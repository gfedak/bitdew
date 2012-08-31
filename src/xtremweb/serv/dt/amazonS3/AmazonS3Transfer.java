package xtremweb.serv.dt.amazonS3;

import xtremweb.serv.dt.amazonS3.AmazonS3Utils;
import xtremweb.core.log.*;
import xtremweb.serv.dt.*;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;

import java.io.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
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
	
	/**
	 * Class logger
	 */
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
     * Constructor default
     */
    public AmazonS3Transfer(){}
    
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
	System.setProperty("com.amazonaws.sdk.disableCertChecking","true");
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
	    s3.putObject(new PutObjectRequest(bucketName, data.getuid(), file));
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
	
	log.debug("Listing buckets");
        for (Bucket bucket : s3.listBuckets()) {
            log.debug(" - " + bucket.getName());
        }
	log.debug("attempting to download object with bucket " + bucketName + "and key " + data.getuid());
	S3Object object = s3.getObject(new GetObjectRequest(bucketName,
		data.getuid()));
	log.info("Content-Type: " + object.getObjectMetadata().getContentType());
	InputStream reader = new BufferedInputStream(object.getObjectContent());
	File file = new File(local_locator.getref());      
	

	int read = -1;

	try {
	    OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
	    while ( ( read = reader.read() ) != -1 ) {
	        writer.write(read);
	    }
	    writer.flush();
	    writer.close();
	    reader.close();
	} catch (IOException e) {
	    throw new OOBException("Problem in blockingReceiveReceiverSide of AmazonS3 transfer : " + e.getMessage());
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
