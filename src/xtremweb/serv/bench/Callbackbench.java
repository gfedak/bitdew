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
 * It synchronizes workers at the begining of the experience and at
 * the end of the experience
 *
 *
 * Created: Fri Oct  6 21:47:49 2006
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class Callbackbench extends CallbackTemplate implements InterfaceRMIbench {

    protected Logger log = LoggerFactory.getLogger("BenchMaster Service");
    protected int workerIndex=0;
    protected int workers;
    protected int rounds;
    protected int size;
    protected String bench;

    int cs=0;
    int ce=0;
    int cr = 0;
    Data dataToGo;

    protected WorkerId wpool[];
    protected int widx=0;
    /**
     * Creates a new <code>Callbackbench</code> instance.
     *
     */
    public Callbackbench() {
    }

    public void configure(int w, int s, int r, String b ) {
	workers = w;
	rounds = r;
	size = s;
	bench=b;
	wpool = new WorkerId[workers];
	log.info("Benchmarking Workers: " + workers + " Size : " + size + " Rounds: " + rounds);
    }
    
    public long  register(String name) {
	WorkerId w =  new WorkerId(name, widx ,0, rounds);
	log.debug(w.toString());
	synchronized (wpool) {
	    wpool[widx]=w;
	}
	long res=widx;
	widx++;
	return res;
    }

    public synchronized  Data startExperienceBroadcast() {
    	return dataToGo;
    }

    public synchronized  long startExperience() {
	cs++;
        if ( cs==workers ) {
	    // action done by the final thread !
            // notify blocked threads that threshold has been reached
	    ce=0;
	    if ( cr == rounds)		
		//log.info("Write Results");
		writeResults();
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
	if ( cr==rounds)
	    return -1;	
	return size;
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
	wpool[(int)wid].xp[cr-1]=time;
	wpool[(int)wid].values=v;
	log.debug("wid :" + wid + " time : " + time);
    }

    public void writeResults() {
	try {
	    File f = new File(new File(bench), bench + "_" + size + "M_" + workers + "W" );
	    FileWriter fw = new FileWriter(f);
	    for ( int i=0; i< workers; i++) {
		for ( int j=0; j< rounds; j++) {
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

    //
    public Data startBroadcastExperience() {
	return null;
    }

    class WorkerId {
	public String name;
	public int index;
	public long timeShift;
	public String[] values=null;
	public long[] xp;

	WorkerId (String n , int i, long ts, int rounds) {
	    name = n;
	    index = i;
	    timeShift = ts;
	    xp = new long[rounds];
	}

	public void setResults(int rounds, long time, Vector v ) {
	    xp[rounds] = time;
	    //	    values=v;
	}

	public String toString() {
	    return name + " [" + index + "] ";
	}
    }    

}
