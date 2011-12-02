package xtremweb.serv.dt.bittorrent;

import xtremweb.core.exec.*;
import xtremweb.core.log.*;
import xtremweb.core.conf.*;
import xtremweb.serv.dt.OOBException;

import java.io.*;
import java.util.Properties;

/**
 * <code>BtpdConnector</code>
 * 
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class BtpdConnector {

    /**
     * Path to btpdcli binary file
     */
    public static String btpdCliExec;

    protected static Logger log = LoggerFactory.getLogger(BtpdConnector.class);

    static{
	Properties mainprop;
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Bittorrent Protocol Information found : " + ce);
	    mainprop = new Properties();
	}
	btpdCliExec = mainprop.getProperty("xtremweb.serv.dt.bittorrent.btpd.cliExec");
	if (btpdCliExec == null)
	    btpdCliExec = "/usr/bin/btcli";
    }
   

    /**
     * Add a .torrent file to begin download.
     * 
     * @param daemonDir
     *            dir to btcli binary file
     * @param dataDir
     *            dir to btcli
     * @param torrentURL
     *            url where we can find the torrent
     * @throws BittorrentException
     */
    public static void addTorrent(String folder, String dataDir)
	    throws OOBException {

	String cmdLine = btpdCliExec + " add -d " + folder + " " + dataDir;
	log.debug("Going to add torrent " + cmdLine);
	try {
	    Executor e = new Executor(cmdLine);
	    e.startAndWait();
	    log.info("Download finished");
	} catch (ExecutorLaunchException ele) {
	    log.debug("Error when launching " + btpdCliExec + " " + ele);
	    throw new OOBException("Error when launching btpd core");
	} // end of try-catch

    }

    public boolean isSeedingComplete(String string) throws OOBException {
	String cmd = "btcli list " + string;
	Executor exe = new Executor(cmd);
	try {
	    exe.startAndWait();
	    InputStream is = exe.getStdin();
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    br.readLine();
	    String s = br.readLine();
	    log.debug("output of btcli list " + s);
	    if (s == null)
		throw new OOBException("That torrent do not exist");
	    String[] toks = s.split("\\s");
	    for (String elem : toks) {
		if (elem.contains("100"))
		    return true;
	    }
	} catch (ExecutorLaunchException e) {    
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return false;

    }
}
