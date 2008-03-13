package xtremweb.serv.dt.bittorrent;

/**
 * Describe class BittorrentTools here.
 *
 *
 * Created: Fri Mar 30 17:14:11 2007
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
import xtremweb.core.exec.*;
import xtremweb.core.log.*;
import xtremweb.core.conf.*;

import java.util.Properties;

public class BittorrentTools {

    public static String DEFAULT_TRACKER_EXEC = "/Users/fedak/Desktop/Vaults/BitTornado-CVS/bttrack.py --port 6969 --dfile dstate";
    public static String DEFAULT_TRACKER_OPTION = "--dfile dstate";
    public static String DEFAULT_MAKE_TORRENT_EXEC = "/Users/fedak/Desktop/Vaults/BitTornado-CVS/btmakemetafile.py";

    public static int DEFAULT_TRACKER_PORT = 6969;

    private static String trackerOption;
    private static String trackerExec;
    private static String makeTorrentExec = "/Users/fedak/Desktop/Vaults/BitTornado-CVS/btmakemetafile.py";
    private static int trackerPort = 6969;

    protected static  Logger log = LoggerFactory.getLogger(BittorrentTools.class);

    static {
	init();
    }

    public static void init() {
	//getting bittorrent configuration properties
	Properties mainprop;		
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Bittorrent Protocol Information found : " + ce);
	    mainprop = new Properties();
	}

	//initializing the BittorrentTools
	setTrackerExec(mainprop.getProperty("xtremweb.serv.dt.bittorrent.trackerExec",DEFAULT_TRACKER_EXEC));
	setTrackerOption(mainprop.getProperty("xtremweb.serv.dt.bittorrent.trackerOption",DEFAULT_TRACKER_OPTION));
	setMakeTorrentExec(mainprop.getProperty("xtremweb.serv.dt.bittorrent.makeTorrentExec",DEFAULT_MAKE_TORRENT_EXEC));
	setTrackerPort(Integer.getInteger(mainprop.getProperty("xtremweb.serv.dr.bittorrent.port"),DEFAULT_TRACKER_PORT).intValue());	
    }

    public static void startBittorrentTracker() {
	String execString = trackerExec + " --port " + trackerPort + " "  + trackerOption;
	log.debug("Sarting tracker " + execString);
	Executor e = new Executor( execString ) ;
	try {
	    e.start();	     
	} catch (ExecutorLaunchException ele) {
	    System.out.println("Error when launching " + execString + " " + ele);
	} // end of try-catch
	
    }

    public static void makeTorrent(String trackerURL, String fileName, String torrentName) {

	// /Users/fedak/Desktop/Vaults/BitTornado-CVS/btmakemetafile.py http://129.175.7.234:6969/announce fe3f4f10-db9a-31db-9f4a-492d005f0c4a 
	String execString =  makeTorrentExec + " " + fileName  + " "+ trackerURL + " --target "  + torrentName  ;
	Executor e = new Executor( execString ) ;
	log.debug("Make torrent file  : " + trackerURL + " " + fileName );
	try {
	    e.startAndWait();
	    e.flushPipe();
	} catch (ExecutorLaunchException ele) {
	    System.out.println("Error when launching " + execString + " " + ele);
	} // end of try-catch
    }

    public static String getTrackerExec() {
	return trackerExec;
    }

    public static void setTrackerExec(String tracker) {
	trackerExec = tracker;
    }

    public static int getTrackerPort() {
	return trackerPort;
    }

    public static void setTrackerPort(int port) {
	trackerPort = port;
    }

    public static void setMakeTorrentExec(String torrentExec) {
	makeTorrentExec = torrentExec;
    }

    public static void setTrackerOption(String to) {
	trackerOption = to;
    }

    public static void main(String [] args) {
	//	BittorrentTools bt = new BittorrentTools();
	if (args[0].equals("--tracker"))
	    BittorrentTools.startBittorrentTracker();
	if (args[0].equals("--torrent"))
	    BittorrentTools.makeTorrent(args[1],args[2], ".");
	System.out.println("Test finished");
    }


}
