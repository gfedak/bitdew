package xtremweb.serv.dt.ibp;

/**
 * Ftpsender.java
 *
 *
 *
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
import xtremweb.serv.dt.ibp.LogisticalTools.*;
import xtremweb.core.log.*;
import xtremweb.serv.dt.*;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.api.transman.*;
import java.io.*;

/**
 * Represents a IBP transfer.
 * @author jsaray
 *
 */
public class ibpTransfer  
    extends BlockingOOBTransferImpl 
    implements BlockingOOBTransfer, OOBTransfer {
	
	/**
	 * Ibp uploader
	 */
    protected LogisticalUpload ibpUpLoader;
    
    /**
     * Ibp downloader
     */
    protected LogisticalDownload ibpDownLoader;
    
    /**
     * Version
     */
    final static String VERSION = "0.01a"; 
    
    /**
     * Server list
     */
    final static String LBONE_SERVER_LIST = 
        "vertex.cs.utk.edu:6767 "     +
        "acre.sinrg.cs.utk.edu:6767 " +
        "galapagos.cs.utk.edu:6767"   ;
    
    /**
     * Copies
     */
    final static int DFLT_COPIES       = 1;
    
    /**
     * Depots
     */
    final static int DFLT_MAX_DEPOTS   = 10;
    
    /**
     * Duration DFLT
     */
    final static int DFLT_DURATION     = 60*60*24; // 1 day
    
    /**
     * Transfer size
     */
    final static int DFLT_TRANSFERSIZE = 512 * 1024;
    
    /**
     * Connections
     */
    final static int DFLT_CONNECTIONS  = 1;
    
    /**
     * Location
     */
    final static String DFLT_LOCATION  = "state= TN";
    
    /**
     * NBMax server
     */
    final static int LBONE_SERVER_NBMAX = 10;
    
    /**
     * Class logger
     */
    protected static  Logger log = LoggerFactory.getLogger(ibpTransfer.class);
    
    /**
     * Class constructor	 
     * @param d
     * @param t
     * @param rl
     * @param ll
     * @param rp
     * @param lp
     */
    public ibpTransfer(Data d, Transfer t, Locator rl, Locator ll, Protocol rp,  Protocol lp ) {
	super(d,t,rl,ll,rp,lp);
	transfer.setoob(this.getClass().toString());
    } // Ftpsender constructor
    
    /**
     * string representation of ibp
     * @return
     */
    public String ibptoString() {
	return "ibp://[" + remote_protocol.getlogin() + ":" +  remote_protocol.getpassword() +  "]@" + remote_protocol.getserver() + ":" +  remote_protocol.getport();
    }
    
    /**
     * Connect to ibp server
     */
    public void connect ()  throws OOBException {
	log.debug("connect " + ibptoString());
	ibpUpLoader = new LogisticalUpload();
	ibpDownLoader = new LogisticalDownload();

	/*try {
	    int reply;
	    ftp.connect(remote_protocol.getserver(), remote_protocol.getport());
	    log.debug(ftp.getReplyString());
	    
	    // After connection attempt, you should check the reply code to verify
	    // success.
	    reply = ftp.getReplyCode();
	    
	    if(!FTPReply.isPositiveCompletion(reply)) {
		ftp.disconnect();		
		log.debug("FTP server refused connection : " + ftptoString()); 
	    }

	    //login as anonymous
	    if (!ftp.login( remote_protocol.getlogin(), remote_protocol.getpassword())) {
		log.debug("FTP server wrong login " + ftptoString());
		
	    } else */
		log.debug("Succesfully logged into " + ibptoString());

/*	    //FIXME, make this configurable
	    ftp.enterLocalPassiveMode();
	} catch (Exception e) {
	    log.debug ("" + e);
	    throw new OOBException("FTP Cannot open ftp session " + ftptoString());
	}
*/    
		}	
	
    /**
     * ibp send sender side
     */
    public void blockingSendSenderSide   () throws OOBException {
	try {
//		FileInputStream is = new FileInputStream( new File(local_locator.getref()));
		File inputfile=new File(local_locator.getref());
//		File inputfile=new File("/tmp/testibp");
		String outputfilename=local_locator.getref()+".xnd";
		int copies=DFLT_COPIES;
		int maxDepots    = DFLT_MAX_DEPOTS;
		int duration     = DFLT_DURATION;
		int transferSize = DFLT_TRANSFERSIZE;
		int connections  = DFLT_CONNECTIONS;
		String location  = DFLT_LOCATION;		
		//log.debug(local_locator.getref()+" "+outputfilename+copies+" "+maxDepots+" "+duration+" "+transferSize+" "+connections+" "+location);
		ibpUpLoader.fill_LBoneServerList("",0);
		ibpUpLoader.upload(inputfile, outputfilename, copies, maxDepots, duration, transferSize, connections, location);
/*	    if (remote_protocol.getpath() != null)
		ftp.changeWorkingDirectory(remote_protocol.getpath());

	    FileInputStream is = new FileInputStream( new File(local_locator.getref()));
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
	    if (! ftp.storeFile (remote_locator.getref(), is)) {
		log.debug("Upload Error");
	    } else {
		log.debug("Upload Success");
	    } // end of else	     
*/	
		} catch (Exception e) {
	    log.debug("Error" + e);
	    throw new OOBException("IBP errors when sending  " + ibptoString() + "/" + remote_locator.getref() );
	} // end of try-catch
    }
    
    /**
     * ibp send receiver side
     */
    public void blockingSendReceiverSide   () throws OOBException {
    }
    
    /**
     * ibp receive receiver side
     */
    public void blockingReceiveReceiverSide() throws OOBException  {
	log.debug("start receive receiver size");
	try {
		int connections  = DFLT_CONNECTIONS;
		String xndfile=remote_locator.getref();
		String outputfilename=local_locator.getref();
		log.debug("going to get " + remote_locator.getref() + "to " + local_locator.getref() );
		ibpDownLoader.download(xndfile, outputfilename, connections);
/*	    if (remote_protocol.getpath() != null)
		ftp.changeWorkingDirectory(remote_protocol.getpath());
	    log.debug("changed directory to " + remote_protocol.getpath());
	    FileOutputStream os = new FileOutputStream( new File(local_locator.getref()));

	    ftp.setFileType(FTP.BINARY_FILE_TYPE);
	    log.debug("going to get " + remote_locator.getref() + "to " + local_locator.getref() );
	    if (! ftp.retrieveFile (remote_locator.getref(), os )) {		
		log.debug("Download Error : " + ftp.getReplyString());
		error=true;
	    } else {
		log.debug("Download Success");
	    } // end of else	     
*/	
		} catch (Exception e) {
	    log.debug("Error" + e);
	    throw new OOBException("IBP errors when receiving receive " + ibptoString() + "/" + remote_locator.getref() );
	} // end of try-catch
	
	log.debug("FIN du transfer");
    }
    
    /**
     * Receive sender side
     */
    public void blockingReceiveSenderSide() throws OOBException  {
    }
    
    /**
     * Disconnect
     */
    public void disconnect() throws OOBException {

/*	if(ftp.isConnected()) {
	    try {
		ftp.logout();
		ftp.disconnect();
	    } catch(IOException ioe) {
		System.out.println("Error" + ioe);
	    }
	}*/
    }
    
    /**
     * Main
     * @param args
     */
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
	local_locator.setref("/tmp/testibp1");

	// Preparer le proto pour l'acces remote
	Protocol remote_proto = new Protocol();
	remote_proto.setserver("localhost");
	remote_proto.setname("ibp");
	remote_proto.setpath("incoming");
	remote_proto.setport(6767);
	remote_proto.setlogin("anonymous");
	remote_proto.setpassword("fedak@lri.fr");

	Locator remote_locator = new Locator();
	remote_locator.setdatauid(data.getuid());
	remote_locator.setdrname("localhost");
	remote_locator.setprotocoluid(remote_proto.getuid());
	remote_locator.setref("binaryFile");

	//prepar
	Transfer t = new Transfer();
	t.setlocatorremote(remote_locator.getuid());
	t.setlocatorlocal(local_locator.getuid());
	
	//	Data data = DataUtil.fileToData(file);
	
	ibpTransfer ibp = new ibpTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);

	
	
	t.settype(TransferType.UNICAST_SEND_SENDER_SIDE);


	try {
	    ibp.connect();	    
	    ibp.sendSenderSide();
	    ibp.disconnect();
	} catch(OOBException oobe) {
	    System.out.println(oobe);
	}
	
	remote_locator.setref("testibp1.xnd");
	remote_proto.setpath("/tmp");

	t.settype(TransferType.UNICAST_RECEIVE_RECEIVER_SIDE);
    ibp = new ibpTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);
	
	try {
	    ibp.connect();	    
	    ibp.receiveReceiverSide();
	    ibp.disconnect();
	} catch(OOBException oobe) {
	    System.out.println(oobe);
	}

	
    }
    
} // Ftpsender
