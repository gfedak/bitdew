package xtremweb.serv.dt.bittorrent;

/**
 * BittorrentTransfer.java
 *
 *
 * Created: Wed May 31 13:18:56 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */

import xtremweb.core.log.*;
import xtremweb.core.uid.*;
import xtremweb.core.conf.*;
import xtremweb.serv.dt.*;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.serv.dc.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.commons.httpclient.params.*;

import java.io.*;
import java.util.Properties;
import java.net.*;

public class BittorrentTransfer 
    extends NonBlockingOOBTransferImpl 
    implements NonBlockingOOBTransfer, OOBTransfer {

    private static boolean azureusFlag=false;
    private static boolean btpdFlag=true;

    protected static AzureusConnector bittorrent;
    protected static BtpdCore btpd;

    private static String daemonDirName;
    private static String dataDirName;
    private static String torrentDirName;

  //  private static String fileName;
    
    private static boolean dirIntialised = false;

    protected static  Logger log = LoggerFactory.getLogger(BittorrentTransfer.class);

    public BittorrentTransfer(Data d, Transfer t, Locator rl, Locator ll, Protocol rp,  Protocol lp ) {
	super(d,t,rl,ll,rp,lp);
	transfer.setoob(this.getClass().toString());
    } // Ftpsender constructor


    private static void prepareDirectories() {

	if (dirIntialised) return;
	//getting bittorrent configuration properties
	Properties mainprop;		
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Bittorrent Protocol Information found : " + ce);
	    mainprop = new Properties();
	}

	//directory initialization
	if  ((daemonDirName = 
	      mainprop.getProperty("xtremweb.serv.dr.bittorrent.daemonpath") 
	      ) == null ) {
	    if (azureusFlag) daemonDirName = "Azureus";
	    else
		if (btpdFlag) daemonDirName = "btpd";
		else 
		    daemonDirName = "bittorrent";
	    log.debug(" daemon dir  " + daemonDirName);
	}
	dataDirName = "data";
	torrentDirName = "torrents";

	File daemonDir = new File( daemonDirName );
	if (!daemonDir.isDirectory())
	    daemonDir.mkdir();
	
	File dataDir = new File(daemonDir, dataDirName);
	if (!dataDir.isDirectory())
	    dataDir.mkdir();
	
	File torrentDir = new File(daemonDir, torrentDirName);
	if (!torrentDir.isDirectory())
	    torrentDir.mkdir();

	daemonDirName = daemonDir.getAbsolutePath();
	dataDirName = dataDir.getAbsolutePath();
	torrentDirName = torrentDir.getAbsolutePath();

	dirIntialised = true;
    }

    public static void init() throws OOBException {
	
	prepareDirectories();

	//intialization of the bittorrent tools
	BittorrentTools.init();

	//start the tracker
	BittorrentTools.startBittorrentTracker();
	
	//start azureus connector
	if (azureusFlag) {
	    //start the Azureus core
	    //start a default bittorrent core
	    try {
		if (bittorrent == null) {
		    bittorrent = new AzureusConnector();
		}
	    } catch (BittorrentException be) {
		log.debug("Was not able to launch an Azureus Connector  : "  + be);
		throw new OOBException("Bittorrent Transfer : cannot connect to Azureus Core ");
	    }
	    
	    int attempts = 3;
	    while (true) {
		try {
		    Socket sck = new Socket("127.0.0.1", 6880);
		    sck.close();
		    break;
		} catch (Exception e) {
		    try {
			Thread.sleep(3000);
		    } catch (Exception ee) {
		    }
		    log.debug("Cannot open a connection to Azureus Core " + attempts + " " +  e);
		    if (attempts-- == 0) 
			throw new OOBException("Bittorrent Transfer : cannot connect to Azureus Core ");
		}
	    }
	}
	
	//start btpd
/*	if (btpdFlag) {
	    try {
		if (btpd == null) {
		    btpd = new BtpdCore(daemonDirName);
		    btpd.startCore();
		}
		
	    } catch (BittorrentException be) {
		log.debug("Was not able to launch an Btpd core  : "  + be);
		throw new OOBException("Bittorrent Transfer : cannot connect to  Core ");
	    }	    
	}*/
	
		//first create .torrent files
	FilenameFilter filter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
		    return !name.endsWith(".torrent");
		}
	    };
	
	File dataDir = new File(dataDirName);
	String[] children = dataDir.list(filter);
	    
	if (children != null) {
	    for (int i=0; i<children.length; i++) {
		 String fileName = children[i];
		//create the .torrent files
		String trackerURL = "";
		try {
			trackerURL = "http://" + InetAddress.getLocalHost().getHostAddress()  + ":6969/announce";
		    } catch (UnknownHostException uhe) {
			log.debug("" + uhe);   
		    }
		log.debug("creating  " + fileName + ".torrent with tracker " + trackerURL );
		BittorrentTools.makeTorrent(trackerURL, dataDirName + "/" + fileName, torrentDirName + "/" + fileName + ".torrent");
	// BittorrentTools.makeTorrent(trackerURL, dataDirName + "/" + fileName, torrentDirName + "/" + rl.getref() + ".torrent");	    
		log.debug("going to add " + torrentDirName + fileName + ".torrent" );
       // log.debug("going to add " + torrentDirName + rl.getref() + ".torrent" );	
 	 String torrentURL = torrentDirName +  "/" + fileName + ".torrent";
         //  String torrentURL = torrentDirName +  "/" + rl.getref() + ".torrent";	
	String urls[] = { torrentURL };
		try {
		    if (azureusFlag)
			bittorrent.sendMsg( urls );
		    if (btpdFlag)
			new BtpdConnector().addTorrent(daemonDirName,  dataDirName, torrentURL);
		} catch (BittorrentException be) {
		    log.debug("Error when adding new torrents to the Azureus Core : " + be);
		    throw new OOBException("Cannot perform Bittorrent nonBlockingReceive");
		}
	    }
	}
    }
    

    public void connect ()  throws OOBException {
	//start a default bittorrent core
	if (!dirIntialised) prepareDirectories();

	if (btpdFlag) {
	    try {
		if (btpd == null) {
		    btpd = new BtpdCore(daemonDirName);
		    btpd.startCore();
		}
		
	    } catch (BittorrentException be) {
		log.debug("Was not able to launch an Btpd core  : "  + be);
		throw new OOBException("Bittorrent Transfer : cannot connect to  Core ");
	    }	    
	}
	
	if (azureusFlag) {
	    try {
		if (bittorrent == null) {
		    bittorrent = new AzureusConnector();
		}
	    } catch (BittorrentException be) {
		log.debug("Was not able to launch an Azureus Connector  : "  + be);
		throw new OOBException("Bittorrent Transfer : cannot connect to Azureus Core ");
	    }
	    
	    int attempts = 3;
	    while (true) {
		try {
		    Socket sck = new Socket("127.0.0.1", 6880);
		    sck.close();
		    break;
		} catch (Exception e) {
		    log.debug("Cannot open a connection to Azureus Core " + attempts + " " +  e);
		    if (attempts-- == 0) 
			throw new OOBException("Bittorrent Transfer : cannot connect to Azureus Core ");
		    try {
			Thread.sleep(3000);
		    } catch (Exception ee) {
		    }
		}
	    }
	}
    }

    public void nonBlockingSendSenderSide   () throws OOBException {

	if (bittorrent != null) {
	    String torrentURL = "http://";
	    
	    String urls[] = { torrentURL };
	    try {
		bittorrent.sendMsg( urls );
	    } catch (BittorrentException be) {
		log.debug("Error when adding new torrents to the Azureus Core : " + be);
		throw new OOBException("Cannot perform Bittorrent nonBlockingReceive");
	    }
	}	
	
    }

    public void nonBlockingSendReceiverSide   () throws OOBException {
    }

    public void nonBlockingReceiveSenderSide() throws OOBException  {
    }

    public void nonBlockingReceiveReceiverSide() throws OOBException  {
	HttpClient httpClient = new HttpClient();
//   set port tempor
         remote_protocol.setport(8080);
	 String tfName=local_locator.getref(); 
	 tfName=tfName.substring(15);//this tfName string is too ugly, modify it later 
	 log.debug("tfName:"+ tfName);
//	String torrentURL = "http://" + remote_locator.getdrname() + ":" + remote_protocol.getport() + "/" + remote_protocol.getpath() + "/" + remote_locator.getref() + ".torrent";
       String torrentURL = "http://" + remote_locator.getdrname() + ":" + remote_protocol.getport() + "/" + remote_protocol.getpath() + "/" + tfName + ".torrent";
	String url = torrentURL;

//	String localTorrent = torrentDirName + "/" + remote_locator.getref() + ".torrent";
	String localTorrent = torrentDirName + "/" + tfName + ".torrent";

	log.debug("getting " + url);
	GetMethod getMethod = new GetMethod(url);	

	// Provide custom retry handler is necessary
	getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
					   new DefaultHttpMethodRetryHandler(3, false));

	try {
	    // Execute the method.
	    int statusCode = httpClient.executeMethod(getMethod);
	    
	    if (statusCode != HttpStatus.SC_OK) {
		log.debug("HttpClient getMethod failed: " + getMethod.getStatusLine());
		throw new OOBException("Http errors when setting retreive from " + url );
	    }
	    
	    InputStream in = getMethod.getResponseBodyAsStream();
	    byte[] buff = new byte[1024];
	    int len;
	    FileOutputStream out = new FileOutputStream( new File(localTorrent));
	    
	    while ((len = in.read(buff)) != -1) {
		//write byte to file
		out.write(buff, 0, len);
	    }
	    
	} catch (HttpException e) {
	    log.debug("Fatal protocol violation: " + e);
	    throw new OOBException("Http errors when receiving receive " + "/" + remote_locator.getref() );
	} catch (IOException e) {
	    System.err.println("Fatal transport error: " + e);
	    throw new OOBException("Http errors when receiving receive " + "/" + remote_locator.getref() );
	    
	} finally {
	    log.debug("FIN du transfer");
	    getMethod.releaseConnection();
	}

	if (btpdFlag) {
	    try {		
		new BtpdConnector().addTorrent(daemonDirName,  dataDirName, localTorrent);
	    } catch (BittorrentException be) {
		log.debug("Error when adding new torrents to the Azureus Core : " + be);
		throw new OOBException("Cannot perform Bittorrent nonBlockingReceive");
	    }
	    
	}

	if (azureusFlag) {
	    if (bittorrent != null) {		
		log.debug("going to fetch : " + torrentURL);
		String urls[] = { torrentURL };
		try {
		    bittorrent.sendMsg( urls );
		} catch (BittorrentException be) {
		    log.debug("Error when adding new torrents to the Azureus Core : " + be);
		    throw new OOBException("Cannot perform Bittorrent nonBlockingReceive");
		}
	    }	
	}
    }

    public void disconnect() throws OOBException {
	log.debug("disconnect");
    }


    public static void main(String [] args) {

	//	Data data = new Data();
	File file = new File(args[1]);
	Data data = DataUtil.fileToData(file);
	//Preparer le local
	Protocol local_proto = new Protocol();
	local_proto.setname("local");
	
	local_proto.setpath(args[2]);

	Locator local_locator = new Locator();
	local_locator.setdatauid(data.getuid());
	local_locator.setdrname("localhost");
	local_locator.setprotocoluid(local_proto.getuid());
	local_locator.setref("");

	// Preparer le proto pour l'acces remote
	Protocol remote_proto = new Protocol();
	remote_proto.setname("bittorrent");
	remote_proto.setpath("torrents");
	remote_proto.setport(8080);

	Locator remote_locator = new Locator();
	remote_locator.setdatauid(data.getuid());
	remote_locator.setdrname(args[0]);
	remote_locator.setprotocoluid(remote_proto.getuid());
	remote_locator.setref("test-bittorrent");

	//prepare
	Transfer t = new Transfer();

	t.setlocatorremote(remote_locator.getuid());
	t.setlocatorlocal(local_locator.getuid());
	//	Data data = DataUtil.fileToData(file);
	
	BittorrentTransfer bt = new BittorrentTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);
	log.debug(bt.toString());
	try {
	    bt.connect();
	    bt.receiveReceiverSide();
	    bt.waitFor();
	    bt.disconnect();
	} catch(OOBException oobe) {
	    System.out.println(oobe);
	}
	/*
	remote_locator.setref("copy_test-http");
	remote_proto.setpath("fileupload");

	http = new HttpTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);

	try {
	    http.connect();	    
	    http.send();
	    http.disconnect();
	} catch(OOBException oobe) {
	    System.out.println(oobe);
	}
	System.out.println("upload completed");
	*/	

    }

} // BittorrentTransfer
