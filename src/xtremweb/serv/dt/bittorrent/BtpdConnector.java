package xtremweb.serv.dt.bittorrent;

import xtremweb.core.exec.*;
import xtremweb.core.log.*;
import xtremweb.core.conf.*;

import java.io.*;
import java.util.Properties;

/**
 *  <code>BtpdConnector</code>
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class BtpdConnector {

    public String btpdCliExec;
    public final static String DEFAULT_BTPDCLI_EXEC = "/usr/local/bin/btcli";

    protected static  Logger log = LoggerFactory.getLogger(AzureusCore.class);

    /**
     * Creates a new <code>BtpdConnector</code> instance.
     *
     */
    public BtpdConnector() {

	//getting bittorrent configuration properties
	Properties mainprop;		
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Bittorrent Protocol Information found : " + ce);
	    mainprop = new Properties();
	}		
	btpdCliExec = mainprop.getProperty("xtremweb.serv.dt.bittorrent.btpd.cliExec",DEFAULT_BTPDCLI_EXEC);
    }

    //btcli -d btpd add  -d data btpd/torrents/test-bittorrent.torrent
    public void addTorrent(String daemonDir, String dataDir, String torrentURL) throws BittorrentException {

	String cmdLine = btpdCliExec + " -d " + daemonDir + " add -d " + dataDir + " " + torrentURL; 
	log.debug("Going to add torrent " + cmdLine );
	try {
	    Executor e = new Executor( cmdLine );	    
	    e.startAndWait();
	} catch (ExecutorLaunchException ele) {
	    log.debug("Error when launching " + btpdCliExec + " " + ele);
	    throw new BittorrentException("Error when launching btpd core");
	} // end of try-catch
	
    }
}


