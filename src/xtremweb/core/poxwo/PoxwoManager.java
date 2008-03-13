package xtremweb.core.poxwo;

/**
 * Describe class PoxwoManager here.
 *
 *
 * Created: Sun Sep 24 09:42:50 2006
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */

import xtremweb.core.db.*;

import java.util.Collection;
import java.util.Iterator;
import javax.jdo.PersistenceManager;
import javax.jdo.Extent;
import javax.jdo.Query;
import javax.jdo.Transaction;

public class PoxwoManager {

    /**
     * Creates a new <code>PoxwoManager</code> instance.
     *
     */
    public PoxwoManager() {
    }

    public static void localPersist(Poxwo obj) {
		PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();
	    pm.makePersistent(obj);
	    System.out.println("local object id : " + obj.getuid() );
	    tx.commit();
	} finally {
	    if (tx.isActive())
		tx.rollback();
	    pm.close();
	}
    }
    
    /*
    public static void remotePersist(Poxwo obj, InterfaceRMIobj iro) {
		comm.registerAnobject(obj);
    }
    */
}
