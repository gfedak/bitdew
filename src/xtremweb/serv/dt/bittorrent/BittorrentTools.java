package xtremweb.serv.dt.bittorrent;


import xtremweb.core.exec.*;
import xtremweb.core.log.*;
import xtremweb.core.conf.*;
import xtremweb.serv.dt.OOBException;

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
     * Command line client tracker daemon options 
     */
    private static String trackerOption;
    
    /**
     * Tracker url
     */
    private static String trackerurl;
    
    /**
     * Path of program that will work as a tracker
     */
    private static String trackerExec;
    
    /**
     * Make torrent executable
     */
    private static String makeTorrentExec;
    
    /**
     * Tracker port
     */
    private static int trackerPort = 6969;
    
    /**
     * Logger 
     */
    protected static  Logger log = LoggerFactory.getLogger(BittorrentTools.class);
    
    /**
     * 
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
	if (mainprop.getProperty("xtremweb.serv.dt.bittorrent.trackerExec")== null)
	    throw new OOBException ("You need to specify the tracker binary path in properties.json");
	if(mainprop.getProperty("xtremweb.serv.dt.bittorrent.makeTorrentExec")== null)
	    throw new OOBException("You need to specify the bin path to the program that makes bittorrent");
	if(mainprop.getProperty("xtremweb.serv.dr.bittorrent.port") ==null)
	    throw new OOBException("You need to specify the port");
	
	 
	//initializing the BittorrentTools
	setTrackerExec(mainprop.getProperty("xtremweb.serv.dt.bittorrent.trackerExec"));
	
	setMakeTorrentExec(mainprop.getProperty("xtremweb.serv.dt.bittorrent.makeTorrentExec"));
	//System.out.println("number is joder ito " + new Integer(mainprop.getProperty("xtremweb.serv.dr.bittorrent.port")) );
	setTrackerPort( new Integer(mainprop.getProperty("xtremweb.serv.dr.bittorrent.port")).intValue());
	setTrackerUrl(mainprop.getProperty("xtremweb.serv.dt.bittorrent.trackerurl"));
	setOptions(mainprop.getProperty("xtremweb.serv.dt.bittorrent.trackerOption"));
    }
    
    /**
     * Sets the tracker url
     * @param property the tracker url
     */
    private static void setTrackerUrl(String property) {
	trackerurl = property;
	
    }
    /**
     * Sets the tracker options
     * @param property the options
     */
    private static void setOptions(String property) {
	trackerOption = property;
	
    }
    
    /**
     * Creates a process to run the bittorrent tracker
     */
    public static void startBittorrentTracker() {
	String execString = trackerExec + " " + trackerOption;
	log.debug("Sarting tracker " + execString);
	System.out.println("Starting tracker " + execString);
	Executor e = new Executor( execString ) ;
	try {
	    e.start();
	} catch (ExecutorLaunchException ele) {
	    System.out.println("Error when launching " + execString + " " + ele);
	}
    }
    
    /**
     * Create a process to make a torrent file from a file
     * @param trackerURL the tracker to announce the .torrent file
     * @param fileName the file name from which the torrent will be created
     * @param torrentName the torrent name
     */
    public static void makeTorrent( String fileName, String torrentName) {
	//todo different make tools have different syntaxis, factory pattrrn
	String execString =  makeTorrentExec + " " +  fileName  + " "+ trackerurl+ " --target "  + torrentName  ;
	System.out.println("execcommand "+ execString);
	Executor e = new Executor( execString ) ;
	log.debug("Make torrent file  : " + trackerurl + " " + fileName );
	try {
	    e.startAndWait();
	    e.flushPipe();
	} catch (ExecutorLaunchException ele) {
	    System.out.println("Error when launching " + execString + " " + ele);
	}
    }
    
    /**
     * Gets the tracker binary file path
     * @return
     */
    public static String getTrackerExec() {
	return trackerExec;
    }
    
    /**
     * Sets the tracker binary file path
     * @param tracker
     */
    public static void setTrackerExec(String tracker) {
	trackerExec = tracker;
    }
    
    /**
     * Gets the tracker port
     * @return
     */
    public static int getTrackerPort() {
	return trackerPort;
    }
    
    /**
     * Sets the tracker port
     * @param port
     */
    public static void setTrackerPort(int port) {
	trackerPort = port;
    }
    
    /**
     * Set the binary path of executable torrent
     * @param torrentExec
     */
    public static void setMakeTorrentExec(String torrentExec) {
	makeTorrentExec = torrentExec;
    }
    
    /**
     * Set tracker options
     * @param to
     */
    public static void setTrackerOption(String to) {
	trackerOption = to;
    }
}
