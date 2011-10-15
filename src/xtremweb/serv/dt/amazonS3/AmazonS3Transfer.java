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
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

/**
 * This class implements a file transfer using the amazon S3 protocol
 * @author josefrancisco
 *
 */
public class AmazonS3Transfer extends BlockingOOBTransferImpl implements
	BlockingOOBTransfer, OOBTransfer {

    protected static Logger log = LoggerFactory.getLogger(AmazonS3Transfer.class);
    
    /**
     * Amazon S3 api
     */
    private AmazonS3 s3;
    
    /**
     * Helper class to delegate some tasks
     */
    private AmazonS3Utils s3utils;
    
    /**
     * Amazon S3 bucket name
     */
    private String bucketName;
    
    /**
     * Amazon S3 object key
     */
    private String objectKey;
    
    /**
     * Class constructor
     * @param d
     * @param t
     * @param rl
     * @param ll
     * @param rp
     * @param lp
     */
    public AmazonS3Transfer(Data d, Transfer t, Locator rl, Locator ll,
	    Protocol rp, Protocol lp) {
	super(d, t, rl, ll, rp, lp);
	transfer.setoob(this.getClass().toString());
    }
    
    /**
     * Converts the Amazon S3 protocol to string
     * @return
     */
    public String amazons3toString() {
	return "amazons3://[" + remote_protocol.getlogin() + ":"
		+ remote_protocol.getpassword() + "]@"
		+ remote_protocol.getserver() + ":" + remote_protocol.getport();
    }
    
    /**
     * Connect to amazon S3 cloud
     */
    public void connect() throws OOBException {
	log.info("connect " + amazons3toString());

	try {
	    s3utils = new AmazonS3Utils();
	    s3 = new AmazonS3Client(s3utils.loadAWSCredentials());
	    bucketName = s3utils.loadS3Bucket();
	    objectKey = s3utils.loadObjectKey();
	} catch (Exception e) {
	    log.info("" + e);
	    throw new OOBException("AmazonS3 Cannot connect"
		    + amazons3toString());
	}
    }
    
    /**
     * Performs a send action on the sender side
     */
    public void blockingSendSenderSide() throws OOBException {
	try {
	    log.info("blocking send sender side ");
	    s3.createBucket(bucketName);
	    File file = new File(local_locator.getref());
	    log.info("Uploading a new object to S3 from a file\n");
	    s3.putObject(new PutObjectRequest(bucketName, objectKey, file));
	} catch (Exception e) {
	    log.debug(" an exception has occured sendsender on amazonS3");
	    throw new OOBException(
		    "A problem has occured in blockingSendSenderSide of AmazonS3 transfer"
			    + e.getMessage());
	}
    }
    
    /**
     * Performs a receive action on sender side
     */
    public void blockingSendReceiverSide() throws OOBException {
    }
    
    /**
     * Tells is transfer has finished
     */
    public boolean poolTransfer() {
	return !isTransfering();
    }
    
    /**
     * Performs a receive action on receiver side
     */
    public void blockingReceiveReceiverSide() throws OOBException {
	int line;
	S3Object object = s3.getObject(new GetObjectRequest(bucketName,
		objectKey));
	log.info("Content-Type: " + object.getObjectMetadata().getContentType());
	BufferedReader reader = new BufferedReader(new InputStreamReader(
		object.getObjectContent()));
	try {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(local_locator.getref())));
	    while ((line = reader.read())!=-1) {
		writer.write(line);
		log.debug("    " + line);
	    }
	    writer.close();
	} catch (IOException e) {
	    throw new OOBException(
		    "A problem has occured in blockingReceiveReceiverSide of AmazonS3 transfer "
			    + e.getMessage());
	}
    }
    
    /**
     * Performs a receive action on the sender side
     */
    public void blockingReceiveSenderSide() throws OOBException {
    }
    
    /**
     * Disconnect from Amazon S3 transfer.
     */
    public void disconnect() throws OOBException {

    }
}
