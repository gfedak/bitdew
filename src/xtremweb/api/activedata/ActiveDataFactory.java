package xtremweb.api.activedata;

import xtremweb.core.iface.*;

/**
 * <code> ActiveDataFactory</code>  allows to create one single
 * instance of a ActiveData API
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

public class ActiveDataFactory {

	//FIXME une possibilite serait de faire une hashtable avec un TM par couple dr/dt
	// d'ailleurs on peut faire ca pour toutes les API

    private static InterfaceRMIdc dc;
    private static InterfaceRMIds ds;

    private static ActiveData ad = null;

    /**
     * <code>init</code> init the method with the specified runtime services.
     *
     * @param cdc an <code>InterfaceRMIdc</code> value
     * @param cds an <code>InterfaceRMIds</code> value
     */
    public static void init(InterfaceRMIdc cdc, InterfaceRMIds cds) {
	dc = cdc;
	ds = cds;
    }

    /**
     * <code>getActiveData</code> retreive the  <code>ActiveData</code>  instance.
     *
     * @return a <code>ActiveData</code> value
     */
    public static ActiveData getActiveData() {
	if (ad == null) {
	    ad = new ActiveData(dc, ds);
	    ad.start();
	}
	return ad;
    }

}
