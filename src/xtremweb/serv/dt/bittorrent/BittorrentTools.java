package xtremweb.serv.dt.bittorrent;


import xtremweb.core.exec.*;
import xtremweb.core.log.*;
import xtremweb.core.conf.*;
import xtremweb.serv.dt.OOBException;

import java.io.File;
import java.util.Properties;

/**
 * Helper class to bittorrent transfer
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class BittorrentTools {
    
    /**
     * Default tracker port
     */
    public static int DEFAULT_TRACKER_PORT = 6969;
    
    /**
     * Tracker url
     */
    private static String trackerurl;
    
    /**
     * Make torrent executable
     */
    private static String makeTorrentExec;
    
    /**
     * Logger 
     */
    protected static  Logger log = LoggerFactory.getLogger(BittorrentTools.class);
    
    /**
     * Static block to initialize class
     */
    static {
	try {
	    init();
	} catch (OOBException e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Init different variables from properties file
     * @throws OOBException
     */
    public static void init() throws OOBException{
	//getting bittorrent configuration properties
	Properties mainprop;		
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Bittorrent Protocol Information found : " + ce);
	    mainprop = new Properties();
	}
	if(mainprop.getProperty("xtremweb.serv.dr.bittorrent.makeTorrentExec")== null)
	    throw new OOBException("You need to specify the bin path to the program that makes bittorrent");
	setMakeTorrentExec(mainprop.getProperty("xtremweb.serv.dr.bittorrent.makeTorrentExec"));
	setTrackerUrl(mainprop.getProperty("xtremweb.serv.dr.bittorrent.trackerurl"));
    }
    
    /**
     * Sets the tracker url
     * @param property the tracker url
     */
    private static void setTrackerUrl(String property) {
	trackerurl = property;
	
    }
    
    /**
     * Create a process to make a torrent file from a file
     * @param trackerURL the tracker to announce the .torrent file
     * @param fileName the file name from which the torrent will be created
     * @param torrentName the torrent name
     */
    public static void makeTorrent( String fileName, String torrentName)throws BittorrentException {
	//todo different make tools have different syntaxis, factory pattrrn
	String execString =  makeTorrentExec + " " +  fileName  + " "+ trackerurl+ " --target "  + torrentName  ;
	log.info("Executing command  "+ execString + " to build .torrent file");
	Executor e = new Executor( execString ) ;
	log.debug("Make torrent file  : " + trackerurl + " " + fileName );
	try {
	    e.startAndWait();
	    e.flushPipe();
	} catch (ExecutorLaunchException ele) {
	    File bin = new File(makeTorrentExec);
	    if (!bin.exists())
		throw new BittorrentException("The binary file used to produce a .torrent " + makeTorrentExec + "do not exist");
	    else
		throw new BittorrentException("There was a problem making the .torrent file " + ele.getMessage());
	}
    }
    /**
     * Set the binary path of executable torrent
     * @param torrentExec
     */
    public static void setMakeTorrentExec(String torrentExec) {
	makeTorrentExec = torrentExec;
    }
}
