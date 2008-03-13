package xtremweb.core.poxwo;

/**
 * Describe class Callbackpoxwo here.
 *
 *
 * Created: Sun Sep 24 11:07:49 2006
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */

import xtremweb.core.log.*;

public class Callbackpoxwo implements InterfaceRMIpoxwo {
    public static Logger log = LoggerFactory.getLogger("Callbackpoxwo");    

    /**
     * Creates a new <code>Callbackpoxwo</code> instance.
     *
     */
    public Callbackpoxwo() {
	log.debug("Callbackpoxwo created");
    }
    
    public void remotePersist(Poxwo obj) {
    } 

}
