package xtremweb.serv.bench;

import java.rmi.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.iface.*;
import xtremweb.core.obj.dc.*;
import xtremweb.core.uid.*;
import xtremweb.core.log.*;


import xtremweb.serv.dc.ddc.jdht.*;
import xtremweb.serv.dc.ddc.*;

import java.util.*;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import javax.jdo.PersistenceManager;
import javax.jdo.Extent;
import javax.jdo.Query;
import javax.jdo.Transaction;

import java.io.FileWriter;
import java.io.IOException;



/**
 * Callbackbench is a service which is used when performing a benchmark
 * It synchronizes workers at the begining of the experience 
 *
 *
 * Created: Fri Oct  6 21:47:49 2006
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class CallbackBroadCastBench extends Callbackbench implements InterfaceRMIbench {

    int loops;

    int begin; 
    int end;
    int inc;
    String output;
    Data dataToBc;    
    int cd=0 ;//index de donnees

    /**
     * Creates a new <code>Callbackbench</code> instance.
     *
     */
    public CallbackBroadCastBench() {


    }
	
    public void configureBroadcast(int w,int s,  int l, String output,Data datain ) {
	log = LoggerFactory.getLogger("BroadCastBenchMaster Service");
	workers=w;
	size = s;
	loops = l;
	dataToBc=datain;	
	log.debug(" retreived data [" + dataToBc.getuid()+ "]");
	
	wpool = new WorkerId[workers];
	log.info("Benchmarking Workers: " + workers + " Size : " + size + " Loops: " + loops);
    }
    
    public long  register(String name) {
	WorkerId w =  new WorkerId(name, widx ,0, loops);
	log.debug(w.toString());
	synchronized (wpool) {
	    wpool[widx]=w;
	}
	long res=widx;
	widx++;
	return res;
    }


    public synchronized  Data startExperienceBroadcast() {
    	cs++;
        if ( cs==workers ) {
	    // action done by the final thread !
            // notify blocked threads that threshold has been reached
	    ce=0;
	    if ( cr == loops)		
		//log.info("Write Results");
		//writeResults();
		log.info("Start of round :"  + cr);
            notifyAll();	    
        }
        else while ( cs<workers ) {
	     try {
		  wait();
	       } catch (InterruptedException e) {
		     log.fatal("wait start xp" + e);
	      } // end of try-catch
        }
	if ( cr==loops)
	    return null;	     
	return dataToBc;
    }

    public synchronized  void endExperience(long wid,long time, String[] v) {
    ce++;
    if ( ce==workers ) {
	    // action done by the final thread !
        // notify blocked threads that threshold has been reached
	   log.info("End of round :"  + cr);
	   cr++;
	   cs=0;
       notifyAll();	    
        }
     else while ( ce<workers ) {
	 try {
		wait();
	    } catch (InterruptedException e) {
		log.fatal("wait end xp" + e);
	    } // end of try-catch
        }
//	wpool[(int)wid].xp[cr-1]=time;
//	wpool[(int)wid].values=v;
//	log.debug("wid :" + wid + " time : " + time);
    }

    public void writeResults() {
	try {
	    File f = new File(new File(bench), bench + "_" + size + "M_" + workers + "W" );
	    FileWriter fw = new FileWriter(f);
	    for ( int i=0; i< workers; i++) {
		for ( int j=0; j< loops; j++) {
		    fw.write("ftp " + size + " " + wpool[i].name + " "+ j + " " + wpool[i].xp[j] + "\n");
		}
	    } // end of for ()
	    fw.flush();
	    fw.close();
	    System.exit(0);
	} catch (IOException e) {
	    log.fatal("cannot write results" + e);
	    System.exit(1);
	} // end of try-catch
	
    }

    public String[] getValues() {
	//Vector res= new Vector(50*workers);
	String[] res = new String[50*workers];
	int ii=0;
	for (int i=0; i<workers; i++) {
	    for (int j=0; j<50; j++) {
		if (wpool[i].values!=null) res[ii]=wpool[i].values[j];
		ii++;
	    }
	}
	log.info("yop, tout va bien");
	return res;
    }

}
