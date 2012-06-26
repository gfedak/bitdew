package xtremweb.api.transman;

import xtremweb.core.iface.*;
import java.util.*;
/**
 * <code>TransferManagerFactory</code> allows to create one single
 * instance of a TransferManager. 
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */


public class TransferManagerFactory {


	//FIXME une possibilite serait de faire une hashtable avec un TM par couple dr/dt
	// d'ailleurs on peut faire ca pour toutes les API

    private static TransferManager tm = null;



    /**
     * <code>getTransferManager</code> retreive the  <code>TransferManager</code>  instance.
     *
     * @return a <code>TransferManager</code> value
     */
    public static TransferManager getTransferManager() {
	if (tm==null) tm = new TransferManager();
	return tm;
    }

    /**
     * <code>getTransferManager</code> retreive the  <code>TransferManager</code>  instance.
     *
     * @return a <code>TransferManager</code> value
     */
    public static TransferManager getTransferManager(Interfacedt dt) {
	if (tm == null) {
	    tm = new TransferManager(dt);
	   tm.start();
	}
	return tm;
    }

    public static TransferManager getTransferManager(Vector comms) {
	if (tm == null) {
	    tm = new TransferManager(comms);
	    tm.start();
	}
	return tm;
    }

}
