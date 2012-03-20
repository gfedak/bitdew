package xtremweb.serv.dt.bittorrent;

import xtremweb.core.exec.*;
import xtremweb.core.log.*;
import xtremweb.core.conf.*;
import xtremweb.serv.dt.OOBException;

import java.io.*;
import java.util.Properties;

/**
 * This class allows to add a torrent and probe for successfully seeding using btcli 
 * implementation
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
    
    /**
     * Logger
     */
    protected static Logger log = LoggerFactory.getLogger(BtpdConnector.class);
    
    /**
     * Static block to initialize btpd client binary path
     */
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
     * @param folder the folder where .torrent file is
     * @param torrentFile the .torrent file
     * @throws BittorrentException if a problem launching btcli add command happens
     */
    public static void addTorrent(String folder, String torrentFile)
	    throws BittorrentException {

	String cmdLine = btpdCliExec + " add -d " + folder + " " + torrentFile;
	log.debug("Going to add torrent " + cmdLine);
	try {
	    Executor e = new Executor(cmdLine);
	    int returnCode = e.startAndWait();
	    log.debug("Error code is " + returnCode);
	    if (returnCode != 0)
		throw new BittorrentException("There was a problem when executing btcli, to find more information add -v option : ");
	    log.info("Download finished");
	} catch (ExecutorLaunchException ele) {
	    log.debug("Error when launching " + btpdCliExec + " " + ele);
	    throw new BittorrentException("Error when launching btpd core " + ele.getMessage());
	} // end of try-catch

    }
    
    /**
     * Checks if the first seeding is complete
     * @param torrentFile the torrent file we previously add
     * @return true if the file to download was first-time seeded, else false.
     * @throws OOBException
     */
    public boolean isSeedingComplete(String torrentFile) throws BittorrentException {
	log.debug("enter in seeding complete");
	String cmd = btpdCliExec+" list " + torrentFile;
	
	try {
	    Process  p = Runtime.getRuntime().exec(cmd);
	    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    log.debug("after startwait");
	    String firs = br.readLine();
	    log.debug("after startwait " + firs);
	    String s = br.readLine();
	    log.debug("output of btcli list " + s);
	    if (s == null)
		throw new BittorrentException("That torrent do not exist");
	    String[] toks = s.split("\\s");
	    for (String elem : toks) {
		if (elem.contains("100"))
		    return true;
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return false;
    }
}
