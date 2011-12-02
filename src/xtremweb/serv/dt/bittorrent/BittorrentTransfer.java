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
import xtremweb.core.conf.*;
import xtremweb.serv.dt.*;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.serv.dc.*;
import org.apachev3.commons.httpclient.*;
import org.apachev3.commons.httpclient.methods.*;
import org.apachev3.commons.httpclient.methods.multipart.*;
import org.apachev3.commons.httpclient.params.*;

import java.io.*;
import java.util.Properties;

public class BittorrentTransfer extends NonBlockingOOBTransferImpl implements
	NonBlockingOOBTransfer, OOBTransfer {

    /**
     * Btpd daemon encapsulating class
     */
    protected static BtpdCore btpd;

    /**
     * Binary path to daemon directory
     */
    private static String daemonDirName;
    private Properties mainprop;
    private static String dataDirName;
    private static String torrentDirName;
    private static String CLIDIR;
    // private static String fileName;

    private static boolean dirIntialised = false;

    protected static Logger log = LoggerFactory
	    .getLogger(BittorrentTransfer.class);

    public BittorrentTransfer(Data d, Transfer t, Locator rl, Locator ll,
	    Protocol rp, Protocol lp) {	
	super(d, t, rl, ll, rp, lp);
	setParams();
    } // Ftpsender constructor
    
    
    public void setParams(){
	try {
	    mainprop = ConfigurationProperties.getProperties();
	    CLIDIR = mainprop.getProperty("xtremweb.serv.dt.bittorrent.btpd.clidirectory");
	    daemonDirName = mainprop.getProperty("xtremweb.serv.dr.bittorrent.daemonpath");
	    dataDirName = mainprop.getProperty("xtremweb.serv.dr.bittorrent.dirname");
	  
	} catch (ConfigurationException e) {
	  	e.printStackTrace();
	}
    }
    
    public static void init() throws OOBException {


	// intialization of the bittorrent tools
	BittorrentTools.init();

	// start the tracker
	BittorrentTools.startBittorrentTracker();

    }

    public void connect() throws OOBException {
	// start a default bittorrent core
	try {
	    if (btpd == null) {
		btpd = new BtpdCore("");
		btpd.startCore();
	    }

	} catch (BittorrentException be) {
	    log.debug("Was not able to launch an Btpd core  : " + be);
	    throw new OOBException(
		    "Bittorrent Transfer : cannot connect to  Core ");
	}

    }

    public void nonBlockingSendSenderSide() throws OOBException {
	File copy = new File(remote_locator.getref());
	File source = new File(local_locator.getref());
	source.renameTo(copy);
	BittorrentTools.makeTorrent(remote_locator.getref(),
		remote_locator.getref() + ".torrent");
	BtpdConnector btcli = new BtpdConnector();
	HttpClient httpcli = new HttpClient();
	try {
	    Properties mainprop = ConfigurationProperties.getProperties();

	    log.debug("attempting to add torrent");
	    log.debug(" wait for seeding");
	    btcli.addTorrent(CLIDIR, remote_locator.getref() + ".torrent");
	    Thread.sleep(10000);
	    log.debug("Seeding completed");
	    log.debug("torrent added");
	    String tfName = local_locator.getref();
	    log.debug("tfName:" + tfName);

	    String uploadServlet = mainprop.getProperty(
		    "xtremweb.serv.dt.http.uploadServlet", "/fileupload");
	    String torrentURL = "http://" + remote_protocol.getserver() + ":8080" + uploadServlet + "/";
	    PostMethod postMethod = new PostMethod(torrentURL);

	    log.debug("seindinf file to " + torrentURL);
	    File file = new File(remote_locator.getref() + ".torrent");
	    log.debug("sending " + file.getName() + " to " + torrentURL);

	    // Part[] parts = {new FilePart(file.getName(), file)};
	    Part[] parts = { new FilePart(remote_locator.getref() + ".torrent",
		    remote_locator.getref() + ".torrent", file) };

	    // prepare the file upload as a multipart POST request
	    postMethod.setRequestEntity(new MultipartRequestEntity(parts,
		    postMethod.getParams()));

	    // execute the transfer and get the result as a status
	    int status = httpcli.executeMethod(postMethod);
	    log.debug("file sent");
	} catch (HttpException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public void nonBlockingSendReceiverSide() throws OOBException {

	log.debug("Esto solo debe salir en un lado");
	try {
	    new BtpdCore().startCore();
	    setParams();
	    File f = new File(local_locator.getref() + ".torrent");
	    log.debug("construyendo fil e " + local_locator.getref()
		    + ".torrent");
	    // long timeout =
	    // props.getProperty("xtremweb.serv.btpd.btcli.timeout");
	    while (!f.exists()) {
	    }
	    log.debug("el file ya existe, attempting to download");

	    log.debug(" adding torrent " + local_locator.getref() + ".torrent");
	    BtpdConnector.addTorrent(CLIDIR, local_locator.getref()+ ".torrent");
	} catch (BittorrentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} 
    }

    public void nonBlockingReceiveSenderSide() throws OOBException {
    }

    public void nonBlockingReceiveReceiverSide() throws OOBException {
	HttpClient httpClient = new HttpClient();

	remote_protocol.setport(8080);
	String tfName = getData().getuid();
	
	log.debug("tfName:" + tfName);

	String torrentURL = "http://" + remote_protocol.getserver() + ":"
		+ remote_protocol.getport() + "/data/" + tfName + ".torrent";
	String url = torrentURL;

	String localTorrent = dataDirName + "/" + tfName + ".torrent";

	log.debug("getting " + url);
	GetMethod getMethod = new GetMethod(url);

	// Provide custom retry handler is necessary
	getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		new DefaultHttpMethodRetryHandler(3, false));

	try {
	    // Execute the method.
	    int statusCode = httpClient.executeMethod(getMethod);

	    if (statusCode != HttpStatus.SC_OK) {
		log.debug("HttpClient getMethod failed: "
			+ getMethod.getStatusLine());
		throw new OOBException(
			"Http errors when setting retreive from " + url);
	    }

	    InputStream in = getMethod.getResponseBodyAsStream();
	    byte[] buff = new byte[1024];
	    int len;
	    FileOutputStream out = new FileOutputStream(new File(localTorrent));

	    while ((len = in.read(buff)) != -1) {
		// write byte to file
		out.write(buff, 0, len);
	    }

	} catch (HttpException e) {
	    log.debug("Fatal protocol violation: " + e);
	    throw new OOBException("Http errors when receiving receive " + "/"
		    + remote_locator.getref());
	} catch (IOException e) {
	    System.err.println("Fatal transport error: " + e);
	    throw new OOBException("Http errors when receiving receive " + "/"
		    + remote_locator.getref());

	} finally {
	    log.debug("FIN du transfer");
	    getMethod.releaseConnection();
	}

	try {
	    new BtpdConnector().addTorrent(dataDirName, localTorrent);
	} catch (OOBException be) {
	    log.debug("Error when adding new torrents to btpd Core : " + be);
	    throw new OOBException(
		    "Cannot perform Bittorrent nonBlockingReceive");
	}
    }

    public void disconnect() throws OOBException {
	log.debug("disconnect");
    }

    public static void main(String[] args) {

	// Data data = new Data();
	File file = new File(args[1]);
	Data data = DataUtil.fileToData(file);
	// Preparer le local
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

	// prepare
	Transfer t = new Transfer();

	t.setlocatorremote(remote_locator.getuid());
	t.setlocatorlocal(local_locator.getuid());
	// Data data = DataUtil.fileToData(file);

	BittorrentTransfer bt = new BittorrentTransfer(data, t, remote_locator,
		local_locator, remote_proto, local_proto);
	log.debug(bt.toString());
	try {
	    bt.connect();
	    bt.receiveReceiverSide();
	    bt.waitFor();
	    bt.disconnect();
	} catch (OOBException oobe) {
	    System.out.println(oobe);
	}
	/*
	 * remote_locator.setref("copy_test-http");
	 * remote_proto.setpath("fileupload");
	 * 
	 * http = new HttpTransfer(data, t, remote_locator, local_locator,
	 * remote_proto, local_proto);
	 * 
	 * try { http.connect(); http.send(); http.disconnect(); }
	 * catch(OOBException oobe) { System.out.println(oobe); }
	 * System.out.println("upload completed");
	 */

    }

} // BittorrentTransfer
