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

import java.util.Collection;
import java.util.Iterator;
import javax.jdo.PersistenceManager;
import javax.jdo.Extent;
import javax.jdo.Query;
import javax.jdo.Transaction;

import xtremweb.dao.DaoFactory;
import xtremweb.dao.data.DaoData;

public class PoxwoManager {
	private static DaoData dao = (DaoData)DaoFactory.getInstance("xtremweb.dao.data.DaoData");
    /**
     * Creates a new <code>PoxwoManager</code> instance.
     *
     */
    public PoxwoManager() {
    }
    
    public static void localPersist(Poxwo obj) {
		
	
	   
	    dao.makePersistent(obj,true);
	    System.out.println("local object id : " + obj.getuid() );
	   
	} 
  
    
    /*
    public static void remotePersist(Poxwo obj, InterfaceRMIobj iro) {
		comm.registerAnobject(obj);
    }
    */
}
