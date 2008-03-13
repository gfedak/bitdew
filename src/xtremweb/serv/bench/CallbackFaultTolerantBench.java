package xtremweb.serv.bench;


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

public class CallbackFaultTolerantBench extends Callbackbench implements InterfaceRMIbench {


    /**
     * Creates a new <code>CallbackFaultTolerantBench</code> instance.
     *
     */
    public CallbackFaultTolerantBench() {

    }

    public void configure(int w, int r) {
	bench = "ftbench";
	workers=w;
	size = 1;
	rounds = r;
	wpool = new WorkerId[workers];
	log.info("Benchmarking Workers: " + workers  + " Loops: " + rounds);
    }


    
}
