package xtremweb.serv.dt.amazonS3;

import xtremweb.serv.dt.amazonS3.AmazonS3Utils;
import xtremweb.core.log.*;
import xtremweb.serv.dt.*;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.api.transman.*;

import java.io.*;


import org.jets3t.service.Constants;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.CanonicalGrantee;
import org.jets3t.service.acl.EmailAddressGrantee;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.multithread.DownloadPackage;
import org.jets3t.service.multithread.S3ServiceSimpleMulti;
import org.jets3t.service.security.AWSCredentials;

public class AmazonS3Transfer extends BlockingOOBTransferImpl implements
		BlockingOOBTransfer, OOBTransfer {
	protected static Logger log = LoggerFactory.getLogger(AmazonS3Transfer.class);    
	public S3Service s3Service;
	public S3Bucket testBucket;
	
	
	
/*	try{	
		testBucket = AmazonS3Utils.loadS3Bucket();
	
	}catch (IOException e){
		e.printStackTrace();
	}	*/
	
	


	public AmazonS3Transfer(Data d, Transfer t, Locator rl, Locator ll,
			Protocol rp, Protocol lp) {
		super(d, t, rl, ll, rp, lp);
		transfer.setoob(this.getClass().toString());
	}
	

	public String amazons3toString() {
		return "amazons3://[" + remote_protocol.getlogin() + ":"
				+ remote_protocol.getpassword() + "]@"
				+ remote_protocol.getserver() + ":" + remote_protocol.getport();
	}

	public void connect() throws OOBException {
		log.debug("connect " + amazons3toString());

		try {
			AWSCredentials awsCredentials = AmazonS3Utils.loadAWSCredentials();
			s3Service = new RestS3Service(awsCredentials);
			testBucket = AmazonS3Utils.loadS3Bucket();
		} catch (Exception e) {
			log.debug("" + e);
			throw new OOBException("AmazonS3 Cannot connect"
					+ amazons3toString());
		}
	}

	public void blockingSendSenderSide() throws OOBException {
		try {

			File fileData = new File(local_locator.getref());
			S3Object fileObject = new S3Object(testBucket, fileData);
			s3Service.putObject(testBucket, fileObject);

		} catch (Exception e) {
			log.debug("Error" + e);
			throw new OOBException("AmazonS3 errors when sending  "
					+ amazons3toString() + "/" + remote_locator.getref());
		} // end of try-catch
	}
	   public void blockingSendReceiverSide   () throws OOBException {
	    }
	   public void blockingReceiveReceiverSide() throws OOBException  {
			log.debug("start receive receiver size");
			try {
				
				S3Object objectComplete = s3Service.getObject(testBucket, remote_locator.getref());
				
				String outputfilename=local_locator.getref();
						
				log.debug("going to get " + remote_locator.getref() + "to " + local_locator.getref() );
		     BufferedReader reader = new BufferedReader(
			          new InputStreamReader(objectComplete.getDataInputStream()));
		     BufferedWriter writer = new BufferedWriter(
			          new OutputStreamWriter(new DataOutputStream(new FileOutputStream(outputfilename))));
			 String data;	
			     while ((data = reader.readLine()) != null) {
			        writer.write(data);
			    }

	
				} catch (Exception e) {
			    log.debug("Error" + e);
			    throw new OOBException("AmazonS3 errors when receiving receive " + amazons3toString() + "/" + remote_locator.getref() );
			} // end of try-catch
			
			log.debug("FIN du transfer");
		    }
	   
	   public void blockingReceiveSenderSide() throws OOBException  {
	    }

	   public void disconnect() throws OOBException {

		
	    }

	    public static void main(String [] args) {
	    	//IT4S BROKEN
	    	Data data = new Data();

	    	//Preparer le local
	    	Protocol local_proto = new Protocol();
	    	local_proto.setname("local");

	    	Locator local_locator = new Locator();
	    	local_locator.setdatauid(data.getuid());
	    	local_locator.setdrname("localhost");
	    	local_locator.setprotocoluid(local_proto.getuid());
	    	local_locator.setref("/tmp/testamazon");

	    	// Preparer le proto pour l'acces remote
	    	Protocol remote_proto = new Protocol();
	    	remote_proto.setserver("localhost");
	    	remote_proto.setname("amazons3");
	    	remote_proto.setpath("incoming");
	    	remote_proto.setport(6767);
	    	remote_proto.setlogin("anonymous");
	    	remote_proto.setpassword("fedak@lri.fr");

	    	Locator remote_locator = new Locator();
	    	remote_locator.setdatauid(data.getuid());
	    	remote_locator.setdrname("localhost");
	    	remote_locator.setprotocoluid(remote_proto.getuid());
	    	remote_locator.setref("testamazon");
	    	

	    	//prepar
	    	Transfer t = new Transfer();
	    	t.setlocatorremote(remote_locator.getuid());
	    	t.setlocatorlocal(local_locator.getuid());
	    	
	    	//	Data data = DataUtil.fileToData(file);
	    	
	    	AmazonS3Transfer amazons3 = new AmazonS3Transfer(data, t, remote_locator, local_locator, remote_proto, local_proto);

	    	
	    	
	    	t.settype(TransferType.UNICAST_SEND_SENDER_SIDE);


	    	try {
	    		amazons3.connect();	    
	    		amazons3.sendSenderSide();
	    		amazons3.disconnect();
	    	} catch(OOBException oobe) {
	    	    System.out.println(oobe);
	    	}
	    	
/*	    	remote_locator.setref("testamazon");
//	    	remote_locator.setpath("/tmp");
	    	local_locator.setref("/tmp/testamazon_copy");

	    	t.settype(TransferType.UNICAST_RECEIVE_RECEIVER_SIDE);
	        amazons3= new AmazonS3Transfer(data, t, remote_locator, local_locator, remote_proto, local_proto);
	    	
	    	try {
	    		amazons3.connect();	    
	    		amazons3.receiveReceiverSide();
	    		amazons3.disconnect();
	    	} catch(OOBException oobe) {
	    	    System.out.println(oobe);
	    	}*/

	    	
	        }
}

