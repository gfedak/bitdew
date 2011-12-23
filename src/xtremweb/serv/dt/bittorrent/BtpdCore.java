package xtremweb.serv.dt.bittorrent;


import xtremweb.core.exec.*;
import xtremweb.core.log.*;
import xtremweb.core.conf.*;
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
     * Path to btpd daemon executable
     */
    private String btpdExec;
    
    /**
     * Options added to btpd
     */
    private String btpdOptions;
    
    /**
     * Default path to btpd
     */
    public final static String DEFAULT_BTPD_EXEC = "/usr/bin/btpd";
    
    /**
     * btpd default options
     */
    public final static String DEFAULT_BTPD_OPTIONS = " --max-uploads -1 --empty-start ";
    
    /**
     * Log
     */
    protected static  Logger log = LoggerFactory.getLogger(BtpdCore.class);

    /**
     * Creates a new <code>BtpdCore</code> instance.
     *
     */
    public BtpdCore() {	
	//getting bittorrent configuration properties
	Properties mainprop;		
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Bittorrent Protocol Information found : " + ce);
	    mainprop = new Properties();
	}		
	btpdExec = mainprop.getProperty("xtremweb.serv.dr.bittorrent.btpd.exec",DEFAULT_BTPD_EXEC);
	btpdOptions = mainprop.getProperty("xtremweb.serv.dr.bittorrent.btpd.options",DEFAULT_BTPD_OPTIONS);
    }


    /**
     * Start the btpd daemon
     *
     */
    public void startCore() throws BittorrentException {
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
}
