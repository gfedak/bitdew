package xtremweb.core.com.idl;

import xtremweb.core.perf.*;
import xtremweb.core.log.*;
import xtremweb.core.conf.*;
import java.rmi.server.UnicastRemoteObject;

import java.util.Vector;
import java.util.HashMap;
import java.util.Properties;
import java.util.TimerTask;
import java.rmi.RemoteException;
import java.util.Timer;

/**
 * HandlerRMI.java Handles RMI communications from servers
 * 
 * Created: Sun Jul 9 17:46:53 2000
 * 
 * @author Gilles Fedak
 * @version
 */

public class HandlerRMITemplate extends UnicastRemoteObject {

    /**
     * Logger
     */
    protected static Logger log = LoggerFactory.getLogger("HandlerRMI");

    /**
     * Performance Monitor
     */
    private static PerfMonitor perf =null;

    /**
     * Callback template
     */
    protected CallbackTemplate callback;

    /**
     * Module name (dc,dt,dr,ds)
     */
    protected String moduleName;

    /**
     * 
     */
    protected static HashMap<String, Integer> perfSet;

    /**
     * Flag to signal if performance monitoring is enabled
     */
    protected static boolean modulePerf = false;

    /**
     * 
     */
    protected static int samplesnb = 3000;

    /**
     * Timer
     */
    protected static Timer timer;

    /**
     * Initialze hashmap and timer
     */
    static {
	try {
	    Properties prop = ConfigurationProperties.getProperties();
	    modulePerf = (Boolean.valueOf(prop.getProperty(
		    "xtremweb.core.handler.perf", "false"))).booleanValue();
	} catch (Exception e) {
	    modulePerf = false;
	}
	log.info("Using performance monitor on service call : " + modulePerf);
	if (modulePerf) {
	    perf = PerfMonitorFactory.createPerfMonitor("ServiceCalls");
	    perfSet = new HashMap<String, Integer>();
	    if (timer == null)
		timer = new Timer();
	    timer.schedule(new TimerTask() {
		public void run() {
		    addSamples();
		}
	    }, 0, 1000);
	}
    }

    /**
     * Class constructor
     */
    public HandlerRMITemplate() throws RemoteException {
	super(ComWorld.getRmiServerPort());
    }

    /**
     * 
     */
    public void setupPerfMonitor(String module) {
	moduleName = module;
	try {
	    perf.addSerie(moduleName, samplesnb);
	    perfSet.put(moduleName, 0);
	} catch (PerfException pe) {
	    log.warn("cannot add the performance monitor for service : "
		    + moduleName);
	}

    }

    /**
     * Set the associated callback
     */
    public void registerCallback(CallbackTemplate cb) {
	callback = cb;
    }

    /**
     * 
     */
    public static void addSamples() {
	if (modulePerf) {
	    for (String m : perfSet.keySet()) {
		perf.addSample(m, perfSet.get(m));
		perfSet.put(m, 0);
	    }
	}

    }

    /**
     * 
     */
    public void perf(String m) {
	perfSet.put(m, perfSet.get(m) + 1);
    }
}
