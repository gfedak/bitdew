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
import xtremweb.serv.dt.bittorrent.exception.HttpToolsException;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
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
     * Bittorrent transfer properties (trackerurl, daemon path etc)
     */
    private Properties mainprop;

    /**
     * File where download the .torrent (on receiver)
     */
    private static String TORRENT_DIR_RECEIVER;

    /**
     * Directory where a desired .torrent file exists on sender
     */
    private static String TORRENT_DIR_SENDER;

    /**
     * Log
     */
    protected static Logger log = LoggerFactory
	    .getLogger(BittorrentTransfer.class);
    
    /**
     * Constructor
     */
    public BittorrentTransfer(){
	setParams();
    }
    
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
     * Initialize parameters
     */
    public void setParams() {
	try {
	    mainprop = ConfigurationProperties.getProperties();
	    TORRENT_DIR_SENDER = mainprop
		    .getProperty("xtremweb.serv.dr.bittorrent.btpd.torrentDirSender");
	    TORRENT_DIR_RECEIVER = mainprop
		    .getProperty("xtremweb.serv.dr.bittorrent.btpd.torrentDirReceiver");
	    init();
	} catch (ConfigurationException e) {
	    e.printStackTrace();
	} catch (OOBException e) {
	    // TODO Auto-generated catch block
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
	    log.info("Torrent file " + remote_locator.getref() + ".torrent created ");
	    BtpdConnector btcli = new BtpdConnector();
	    Properties mainprop = ConfigurationProperties.getProperties();
	    log.info("Seeding file for the first time ....");
	    btcli.addTorrent(TORRENT_DIR_SENDER, remote_locator.getref() + ".torrent");
	    boolean seeding = btcli.isSeedingComplete(remote_locator.getref()
		    + ".torrent");
	    log.debug(" wait for seeding");
	    log.debug("Seeding ? " + seeding);
	    Properties props = ConfigurationProperties.getProperties();
	    long TIMEOUT = Long.parseLong(props.getProperty("xtremweb.serv.dr.bittorrent.sdrtimeout"));
	    long d = System.currentTimeMillis();
	    long now, elapsed = 0;
	    while (!seeding && elapsed < TIMEOUT) {
		seeding = btcli.isSeedingComplete(remote_locator.getref()
			+ ".torrent");
		now = System.currentTimeMillis();
		elapsed = now - d;

	    }
	    if (!seeding)
		throw new OOBException(
			" Seeding could not be performed, time out reached ");
	    log.info("First seeding done");
	    String httpport = mainprop.getProperty("xtremweb.serv.dr.http.port");
	    String uploadServlet = mainprop.getProperty(
		    "xtremweb.core.http.UploadServlet.url", "/fileupload");
	    String torrentURL = "http://" + remote_protocol.getserver()
		    + ":"+ httpport + uploadServlet + "/";
	    HttpTools.postFileHttp(remote_locator.getref() + ".torrent", torrentURL);
	} catch (ConfigurationException e) {
	    throw new OOBException(
		    "There was a problem when reading the properties file : "
			    + e.getMessage());
	} catch (BittorrentException e) {
	    throw new OOBException(
		    "There was a problem when adding the torrent for first seeding : "
			    + e.getMessage());
	} catch (HttpToolsException e) {
	    e.printStackTrace();
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
	    long timeout = Long.parseLong(mainprop.getProperty("xtremweb.serv.dr.bittorrent.rcvtimeout"));
	    Thread.sleep(timeout);
	    log.debug(" adding torrent " + local_locator.getref() + ".torrent");
	    BtpdConnector.addTorrent(TORRENT_DIR_SENDER, local_locator.getref()
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
	int MAX_TRIES; 
	boolean done = false;
	int theport;
	try {
	    MAX_TRIES = Integer.parseInt(mainprop.getProperty("xtremweb.serv.dr.bittorrent.maxtries"));
	    theport = Integer.parseInt(mainprop.getProperty("xtremweb.serv.dr.http.port"));
	    remote_protocol.setport(theport);
	    String tfName = getData().getuid();
	    String torrentURL = "http://" + remote_protocol.getserver() + ":"
		    + remote_protocol.getport() + "/data/" + tfName
		    + ".torrent";
	    String localTorrent = tfName + ".torrent";
	    while (i < MAX_TRIES && !done){
		try{
		HttpTools.getHttpFile(localTorrent, torrentURL);
		new BtpdConnector().addTorrent(TORRENT_DIR_RECEIVER, localTorrent);
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
	}catch(NumberFormatException e){
	    throw new OOBException("xtremweb.serv.dr.bittorrent.maxtries and xtremweb.serv.dr.http.port must be an integer values");
	}
    }

    /**
     * Disconnect the transfer
     */
    public void disconnect() throws OOBException {
	log.debug("disconnect");
	File target = new File(local_locator.getref());
	File source = new File(remote_locator.getref());
	source.renameTo(target);
    }
} // BittorrentTransfer
