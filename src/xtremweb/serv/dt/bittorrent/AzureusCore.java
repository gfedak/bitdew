package xtremweb.serv.dt.bittorrent;

import xtremweb.core.exec.*;
import xtremweb.core.log.*;
import xtremweb.core.conf.*;

import java.io.*;
import java.util.Properties;

/**
 * <code>AzureusCore</code> is a class which permits to launh the Azureus software
 * as a daemon
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class AzureusCore {

    private String jarAzureus;
    private String azureusPath;

    public final static String DEFAULT_AZUREUS_JAR = "lib/azureus.jar";

    protected static  Logger log = LoggerFactory.getLogger(AzureusCore.class);

    public AzureusCore() {
	this("./Azureus");
    }

    /**
     * Creates a new <code>AzureusCore</code> instance.
     *
     */
    public AzureusCore(String path) {	
	azureusPath = path;
	//getting bittorrent configuration properties
	Properties mainprop;		
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Bittorrent Protocol Information found : " + ce);
	    mainprop = new Properties();
	}	
	
	jarAzureus = mainprop.getProperty("xtremweb.serv.dt.bittorrent.azureusjar",DEFAULT_AZUREUS_JAR);

    }

    /**
     *  <code>startAzureusCore</code> start the daemon
     *
     */
    public void startAzureusCore() throws BittorrentException {
	File azDir = new File(azureusPath);
	azDir.mkdir();
	new File(azDir, "data").mkdir();

	log.debug("Sarting Azureus " + jarAzureus);
	try {
	    JarExecutor e = new JarExecutor(azureusPath, "lib/log4j.jar:lib/commons-cli.jar", "-Dazureus.config.path=.", jarAzureus, "--ui=telnet" );	    
	    e.start();	    
	    Thread.sleep(3000); // wait before getting the stream
	    log.debug("set \"Use default data dir\" true bool");
	    log.debug("set \"Default save path\" \"data\" string");
	    AzureusConnector.sendMsg("set \"Use default data dir\" true bool");
	    AzureusConnector.sendMsg("set \"Default save path\" \"data\" string");
	} catch (InterruptedException ie) {
	    log.debug("Error when launching " + ie);
	} catch (ExecutorLaunchException ele) {
	    log.debug("Error when launching " + jarAzureus + " " + ele);
	    throw new BittorrentException("Error when launching " + jarAzureus);
	} // end of try-catch
    }

    /**
     *  <code>main</code> test the launch of Azureus
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String [] args) throws Exception {
	AzureusCore az = new AzureusCore();
	if (args[0].equals("--azureus")) {
	    az.startAzureusCore();
	    
	}
	System.out.println("Test finished");
    }

}
