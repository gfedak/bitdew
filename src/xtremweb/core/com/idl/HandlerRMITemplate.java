package xtremweb.core.com.idl;
import java.rmi.*;
import java.util.*;
import java.rmi.server.UnicastRemoteObject;

import xtremweb.core.log.*;
import xtremweb.core.perf.*;
import xtremweb.core.conf.*;

/**
 * HandlerRMI.java
 * Handles RMI communications from servers  
 *
 * Created: Sun Jul  9 17:46:53 2000
 *
 * @author Gilles Fedak
 * @version
 */

public class HandlerRMITemplate extends UnicastRemoteObject {

    protected static Logger log = LoggerFactory.getLogger("HandlerRMI");

    //    private final static PerfMonitor perf = PerfMonitorFactory.createPerfMonitor("ServiceCalls", "hits per second", 3000);
    private final static PerfMonitor perf = PerfMonitorFactory.createPerfMonitor("ServiceCalls");
    public static int rmiBackPort;
    public static final int RMI_DEFAULT_BACKPORT = 4327;

    protected CallbackTemplate callback;

    protected String moduleName;
    protected Vector moduleCall;
    protected static HashMap<String,Integer> perfSet;    
    protected static boolean modulePerf = false;
    protected static int count=0;
    protected static int samplesnb=3000;
    protected static Timer timer;

    static {
	try {
	    Properties prop = ConfigurationProperties.getProperties();
	    modulePerf = (Boolean.valueOf(prop.getProperty("xtremweb.core.handler.perf","false"))).booleanValue();
	} catch(Exception e) {
	    modulePerf = false;
	}
	log.info("Using performance monitor on service call : " +modulePerf);
	if (modulePerf) {
	    perfSet = new HashMap<String, Integer>();
	    if (timer==null) timer=new Timer(); 
	    timer.schedule(new TimerTask() { 
		    public void run() { 
			addSamples();
		    } 
		} , 0, 1000 ); 
	}
	try {
	    Properties prop = ConfigurationProperties.getProperties();
	    rmiBackPort =  (Integer.valueOf(prop.getProperty("xtremweb.core.com.rmi.backPort", "" + RMI_DEFAULT_BACKPORT))).intValue();
	} catch(Exception e) {
	    modulePerf = false;
	}

    }

    public HandlerRMITemplate() throws RemoteException {
	super(rmiBackPort );
    }
    
    public void setRmiBackPort(int port) {
	rmiBackPort = port;
    }

    public void setupPerfMonitor(String module) {
	moduleName = module;
	try {
	    perf.addSerie(moduleName, samplesnb);
	    perfSet.put(moduleName,0);
	} catch (PerfException pe) {
	    log.warn("cannot add the performance monitor for service : " + moduleName);
	}		
    }

    public void registerCallback( CallbackTemplate cb) {
	callback = cb;
    }
    

    public static void addSamples() {
	//	log.debug("adding samples : " + count++);
	if (modulePerf) {
	    for (String m : perfSet.keySet()) {
		perf.addSample(m, perfSet.get(m));
		perfSet.put(m,0);
	    }
	}
    }

    public void perf(String m) {
	perfSet.put(m, perfSet.get(m)+1);
    }
}
