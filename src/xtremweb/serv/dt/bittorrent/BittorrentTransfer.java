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
import org.apachev3.commons.httpclient.*;
import org.apachev3.commons.httpclient.methods.*;
import org.apachev3.commons.httpclient.methods.multipart.*;
import org.apachev3.commons.httpclient.params.*;

import java.io.*;
import java.util.Properties;

/**
 * This class implements a Bittorrent transfer, it uses btpd library.
 * @author jsaray
 *
 */
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
    
    /**
     * Bittorrent transfer properties (trackerurl, daemon path etc)
     */
    private Properties mainprop;
    
    /**
     * 
     */
    private static String dataDirName;
    
    /**
     * Directory where a desired .torrent file exists
     */
    private static String CLIDIR;
    
    /**
     * Log
     */
    protected static Logger log = LoggerFactory
	    .getLogger(BittorrentTransfer.class);
    
    /**
     * Bittorrent transfer constructor
     * @param d the data to transfer
     * @param t transfer object
     * @param rl remote locator (data destiny location)
     * @param ll local locator(current data location)
     * @param rp remote protocol
     * @param lp local locator
     */
    public BittorrentTransfer(Data d, Transfer t, Locator rl, Locator ll,
	    Protocol rp, Protocol lp) {	
	super(d, t, rl, ll, rp, lp);
	setParams();
    }
    
    /**
     * 
     */
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
    
    /**
     * 
     * @throws OOBException
     */
    public static void init() throws OOBException {


	// intialization of the bittorrent tools
	BittorrentTools.init();

	// start the tracker
	BittorrentTools.startBittorrentTracker();

    }
    
    /**
     * Connect the bittorrent transfer
     */
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
    
    /**
     * This method perform a point to point torrent transfer. To achieve it,
     * a .torrent file is produced having the bitdew uid, then the .torrent file is sent to a Http repository.
     * and finally the nonBlockingSendReceiverSide is called.
     */
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
	    btcli.addTorrent(CLIDIR, remote_locator.getref() + ".torrent");
	    boolean seeding = btcli.isSeedingComplete(remote_locator.getref() + ".torrent");
	    log.debug(" wait for seeding");
	    log.debug("Seeding ? " + seeding);
	    long TIMEOUT = 120000;
	    long d = System.currentTimeMillis();
	    long now,elapsed=0;
	    while( !seeding && elapsed < TIMEOUT )
	    {
		seeding = btcli.isSeedingComplete(remote_locator.getref() + ".torrent");
		now = System.currentTimeMillis();
		elapsed = now - d;
		log.debug("Performing seeding...");
	    }
	    if (!seeding)
		throw new OOBException(" Seeding could not be performed, time out reached ");
	    
	    log.debug("Seeding completed");
	    log.debug("torrent added");
	    String tfName = local_locator.getref();
	    log.debug("Torrent file name :" + tfName);

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
	    throw new OOBException("There was a problem when trying to transfer the .torrent file to repository : " + e.getMessage());
	} catch (IOException e) {
	    throw new OOBException("There was a problem when trying to transfer the .torrent file to repository : " + e.getMessage());
	} catch (ConfigurationException e) {
 	    throw new OOBException("There was a problem when reading the properties file : " + e.getMessage());
	} catch (BittorrentException e) {
	    throw new OOBException("There was a problem when adding the torrent for first seeding : " + e.getMessage());
	}

    }
    
    /**
     * This method is executed on http repository side, once it successfully receives the .torrent file with
     * the file to download metadata,  it calls a bittorrent daemon to begin the download.
     */
    public void nonBlockingSendReceiverSide() throws OOBException {

	try {
	    new BtpdCore().startCore();
	    setParams();
	    File f = new File(local_locator.getref() + ".torrent");
	    log.debug("building file " + local_locator.getref()
		    + ".torrent");
	    while (!f.exists()) {
	    }
	    log.debug("File exists ! , attempting to download");

	    log.debug(" adding torrent " + local_locator.getref() + ".torrent");
	    BtpdConnector.addTorrent(CLIDIR, local_locator.getref()+ ".torrent");
	} catch (BittorrentException e) {
	    e.printStackTrace();
	} 
    }
    
    /**
     * For this case, this method is empty
     */
    public void nonBlockingReceiveSenderSide() throws OOBException {
    }
    
    /**
     * This method downloads a file from its .torrent description
     */
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
	} catch (BittorrentException be) {
	    log.debug("Error when adding new torrents to btpd Core : " + be);
	    throw new OOBException(
		    "Cannot perform Bittorrent nonBlockingReceive");
	}
    }
    
    /**
     * Disconnect the transfer
     */
    public void disconnect() throws OOBException {
	log.debug("disconnect");
    }
} // BittorrentTransfer
