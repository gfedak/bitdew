package xtremweb.serv.bench;

import java.io.*;
/**
 * Describe class CallbackFaultTolerantBench here.
 *
 *
 * Created: Thu Jan 17 22:46:49 2008
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
import xtremweb.serv.bench.*;
import xtremweb.core.iface.*;

public class CallbackAllToAllBench extends Callbackbench implements InterfaceRMIbench {

    public CallbackAllToAllBench() {
    }


    public void writeResults() {
	try {
	    File f = new File(new File(bench), bench + "_" + size + "M_" + (workers-1) + "W" );
	    FileWriter fw = new FileWriter(f);
	    for ( int i=1; i< workers; i++) {
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


    public void configure(int w, int r) {
	bench = "alltoallbench";
	workers=w;
	size = 1;
	rounds = r;
	wpool = new WorkerId[workers];
	log.info("Benchmarking Workers: " + workers  + " Loops: " + rounds);
    }


    
}
