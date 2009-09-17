package xtremweb.core.db;

/**
 * DBInterface.java
 *
 *
 * Created: Mon Feb 27 13:38:06 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */
import xtremweb.core.log.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Extent;
import javax.jdo.Query;
import javax.jdo.JDOHelper;
import javax.jdo.Transaction;
import java.sql.SQLException;

//FIXME is this class used at all ????

public class DBInterface {

    private Logger log = LoggerFactory.getLogger("Data Scheduler Test");

    public void makePersistent(Object obj) {
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();

	boolean persisted = false;
	try {
	    while (!persisted) { 
		tx.begin();
		pm.makePersistent(obj);
		tx.commit();
		persisted=true;
	    }
	} catch (Exception sqle) {
	    //some time there are some error when persisting. In this case, we just wait and try again.
	    log.warn("Error when persisting object : " + sqle + "\ntrying again in 500ms ");
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException ie) 
		  {}
	} finally {
	    if (tx.isActive()) {
		tx.rollback();
	    }    
	    pm.close();
	}
    }


    public Collection getExtent(Class objClass) {
	PersistenceManager pm =  DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	Collection res=null;
	try {
	    tx.begin();
	    Extent e = pm.getExtent(objClass,true);
	    System.out.println("DBT 1: ");
	    res = (Collection) e;
	    System.out.println("DBI: 2");
	    tx.commit();
	} catch (Exception e) {
	    System.out.println("DBI: " + e);
	}
	finally
	    {
		if (tx.isActive())
		    {
			tx.rollback();
		    }
		
		pm.close();
	    }
	return res;
    }

    public Collection getExtent(Class objClass, String jdoql) {
	PersistenceManager pm =  DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	Collection res;
	try {
	    tx.begin();

	    Extent e = pm.getExtent(objClass,true);
	    Query q = pm.newQuery(e,jdoql);
	    //	    q.setOrdering("price ascending");

	    res = (Collection)q.execute();

	    tx.commit();
	}
	finally
	    {
		if (tx.isActive())
		    {
			tx.rollback();
		    }
		
		pm.close();
	    }
	return res;
    }

} // DBInterface
