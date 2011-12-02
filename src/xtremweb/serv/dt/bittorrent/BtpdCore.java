package xtremweb.serv.dt.bittorrent;


import xtremweb.core.exec.*;
import xtremweb.core.log.*;
import xtremweb.core.conf.*;

import java.io.*;
import java.util.Properties;

/**
 *<code>BtpdCore</code>
 *
 *
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class BtpdCore {
    
    /**
     * 
     */
    private String btpdExec;
    
    /**
     * 
     */
    private String btpdPath;
    
    /**
     * 
     */
    private String btpdOptions;
    
    /**
     * 
     */
    public final static String DEFAULT_BTPD_EXEC = "/usr/local/bin/btpd";
    
    /**
     * 
     */
    public final static String DEFAULT_BTPD_OPTIONS = " --max-uploads -1 --empty-start ";
    
    /**
     * 
     */
    protected static  Logger log = LoggerFactory.getLogger(BtpdCore.class);


    /**
     * Creates a new <code>BtpdCore</code> instance.
     *
     */
    public BtpdCore() {
	this(DEFAULT_BTPD_EXEC);
    }


    /**
     * Creates a new <code>BtpdCore</code> instance.
     *
     */
    public BtpdCore(String path) {	
	btpdPath = path;
	//getting bittorrent configuration properties
	Properties mainprop;		
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Bittorrent Protocol Information found : " + ce);
	    mainprop = new Properties();
	}		
	btpdExec = mainprop.getProperty("xtremweb.serv.dt.bittorrent.btpd.exec",DEFAULT_BTPD_EXEC);
	btpdOptions = mainprop.getProperty("xtremweb.serv.dt.bittorrent.btpd.options",DEFAULT_BTPD_OPTIONS);
    }


    /**
     *  <code>startCore</code> start the daemon
     *
     */
    public void startCore() throws BittorrentException {
	File btpdDir = new File(btpdPath);
	btpdDir.mkdir();
	new File(btpdDir, "data").mkdir();
	String btpdCmdLine = btpdExec + " " + btpdOptions;
	log.debug("Sarting Btpd : " + btpdCmdLine );
	try {
	    Executor e = new Executor( btpdCmdLine );	    
	    e.start();
	    Thread.sleep(500); // wait before getting the stream
	} catch (InterruptedException ie) {
	    log.debug("Error when launching " + ie);
	} catch (ExecutorLaunchException ele) {
	    log.debug("Error when launching " + btpdExec + " " + ele);
	    throw new BittorrentException("Error when launching btpd core");
	} // end of try-catch
	
    }


    /**
     *  <code>main</code> test the launch of Azureus
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String [] args) throws Exception {
	BtpdCore az = new BtpdCore();
	if (args[0].equals("--btpd")) {
	    az.startCore();
	    
	}
	System.out.println("Test finished");
    }
}
