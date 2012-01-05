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
import xtremweb.serv.dc.DataUtil;
import xtremweb.serv.dt.*;
import xtremweb.serv.dt.bittorrent.exception.HttpToolsException;
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
 * 
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
     * File where download the .torrent (on receiver)
     */
    private static String torrentDir;

    /**
     * Directory where a desired .torrent file exists on sender
     */
    private static String CLIDIR;

    /**
     * Log
     */
    protected static Logger log = LoggerFactory
	    .getLogger(BittorrentTransfer.class);

    /**
     * Bittorrent transfer constructor
     * 
     * @param d
     *            the data to transfer
     * @param t
     *            transfer object
     * @param rl
     *            remote locator (data destiny location)
     * @param ll
     *            local locator(current data location)
     * @param rp
     *            remote protocol
     * @param lp
     *            local locator
     */
    public BittorrentTransfer(Data d, Transfer t, Locator rl, Locator ll,
	    Protocol rp, Protocol lp) {
	super(d, t, rl, ll, rp, lp);
	setParams();
    }

    /**
     * 
     */
    public void setParams() {
	try {
	    mainprop = ConfigurationProperties.getProperties();
	    CLIDIR = mainprop
		    .getProperty("xtremweb.serv.dr.bittorrent.btpd.torrentDirSender");
	    daemonDirName = mainprop
		    .getProperty("xtremweb.serv.dr.bittorrent.btpd.exec");
	    torrentDir = mainprop
		    .getProperty("xtremweb.serv.dr.bittorrent.btpd.torrentDirReceiver");

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
		btpd = new BtpdCore();
		btpd.startCore();
	    }

	} catch (BittorrentException be) {
	    log.debug("Was not able to launch an Btpd core  : " + be);
	    throw new OOBException(
		    "Bittorrent Transfer : cannot connect to  Core ");
	}

    }

    /**
     * This method perform a point to point torrent transfer. To achieve it, a
     * .torrent file is produced having the bitdew uid, then the .torrent file
     * is sent to a Http repository. and finally the nonBlockingSendReceiverSide
     * is called.
     */
    public void nonBlockingSendSenderSide() throws OOBException {
	File copy = new File(remote_locator.getref());
	File source = new File(local_locator.getref());
	source.renameTo(copy);
	try {
	    log.info("Creating torrent .... ");
	    BittorrentTools.makeTorrent(remote_locator.getref(),
		    remote_locator.getref() + ".torrent");
	    log.info("Torrent created ");
	    BtpdConnector btcli = new BtpdConnector();
	    HttpClient httpcli = new HttpClient();

	    Properties mainprop = ConfigurationProperties.getProperties();
	    log.info("Seeding file for the first time ....");
	    btcli.addTorrent(CLIDIR, remote_locator.getref() + ".torrent");
	    boolean seeding = btcli.isSeedingComplete(remote_locator.getref()
		    + ".torrent");
	    log.debug(" wait for seeding");
	    log.debug("Seeding ? " + seeding);
	    long TIMEOUT = 120000;
	    long d = System.currentTimeMillis();
	    long now, elapsed = 0;
	    while (!seeding && elapsed < TIMEOUT) {
		seeding = btcli.isSeedingComplete(remote_locator.getref()
			+ ".torrent");
		now = System.currentTimeMillis();
		elapsed = now - d;

	    }
	    log.info("First seeding done");
	    if (!seeding)
		throw new OOBException(
			" Seeding could not be performed, time out reached ");
	    String tfName = local_locator.getref();
	    log.debug("Torrent file name :" + tfName);

	    String uploadServlet = mainprop.getProperty(
		    "xtremweb.serv.dt.http.uploadServlet", "/fileupload");
	    String torrentURL = "http://" + remote_protocol.getserver()
		    + ":8080" + uploadServlet + "/";
	    PostMethod postMethod = new PostMethod(torrentURL);

	    log.debug("seindinf file to " + torrentURL);
	    File file = new File(remote_locator.getref() + ".torrent");
	    log.debug("sending " + file.getName() + " to " + torrentURL);

	    // Part[] parts = {new FilePart(file.getName(), file)};
	    Part[] parts = { new FilePart(remote_locator.getref() + ".torrent",
		    remote_locator.getref() + ".torrent", file) };
	    log.info("Sending .torrent file to http repository ....");
	    // prepare the file upload as a multipart POST request
	    postMethod.setRequestEntity(new MultipartRequestEntity(parts,
		    postMethod.getParams()));
	    log.info("File sent, the http repository will try to pull your file using bittorrent, this can take some minutes ....");
	    // execute the transfer and get the result as a status
	    int status = httpcli.executeMethod(postMethod);
	    log.info("Status code for the HTTP multipart post : " + status);
	    if (status != HttpStatus.SC_OK) {
		log.debug("HttpClient getMethod failed: "
			+ postMethod.getStatusLine());
		throw new OOBException(
			"Http errors when setting retreive from " + torrentURL);
	    }

	    log.debug("file sent");
	} catch (HttpException e) {
	    throw new OOBException(
		    "There was a problem when trying to transfer the .torrent file to repository : "
			    + e.getMessage());
	} catch (IOException e) {
	    throw new OOBException(
		    "There was a problem when trying to transfer the .torrent file to repository : "
			    + e.getMessage());
	} catch (ConfigurationException e) {
	    throw new OOBException(
		    "There was a problem when reading the properties file : "
			    + e.getMessage());
	} catch (BittorrentException e) {
	    throw new OOBException(
		    "There was a problem when adding the torrent for first seeding : "
			    + e.getMessage());
	}

    }

    /**
     * This method is executed on http repository side, once it successfully
     * receives the .torrent file with the file to download metadata, it calls a
     * bittorrent daemon to begin the download.
     */
    public void nonBlockingSendReceiverSide() throws OOBException {

	try {
	    new BtpdCore().startCore();
	    setParams();
	    File f = new File(local_locator.getref() + ".torrent");
	    log.debug("building file " + local_locator.getref() + ".torrent");
	    while (!f.exists()) {
	    }
	    log.debug("File exists ! , attempting to download");
	    Thread.sleep(10000);
	    log.debug(" adding torrent " + local_locator.getref() + ".torrent");
	    BtpdConnector.addTorrent(CLIDIR, local_locator.getref()
		    + ".torrent");
	} catch (BittorrentException e) {
	    throw new OOBException(
		    "There was a problem using the btpd library "
			    + e.getMessage());
	}catch(InterruptedException e){
		throw new OOBException("The thread has been interrupted " + e.getMessage());
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
	int i =0;
	int failures = 0;
	BittorrentException exc = null ;
	int MAX_TRIES = 5;
	boolean done = false;
	try {
	    remote_protocol.setport(8080);
	    String tfName = getData().getuid();
	    log.debug("tfName:" + tfName);
	    String torrentURL = "http://" + remote_protocol.getserver() + ":"
		    + remote_protocol.getport() + "/data/" + tfName
		    + ".torrent";
	    String localTorrent = tfName + ".torrent";
	    while (i < MAX_TRIES && !done){
		try{
		HttpTools.getHttpFile(localTorrent, torrentURL);
		new BtpdConnector().addTorrent(torrentDir, localTorrent);
		i++;
		done = true;
		}catch (BittorrentException be) {
		    exc = be;
		    failures++;
		    i++;
		} 
	    }
	    if(failures == MAX_TRIES){
		log.debug("Error when adding new torrents to btpd Core : " + exc);
	    	throw new OOBException("Cannot perform Bittorrent nonBlockingReceive "+ exc.getMessage());
	    }
	} catch (HttpToolsException e) {
	    throw new OOBException(e.getMessage());
	}
    }

    /**
     * Disconnect the transfer
     */
    public void disconnect() throws OOBException {
	log.debug("disconnect");
    }
} // BittorrentTransfer
