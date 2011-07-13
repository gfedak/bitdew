package xtremweb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.conf.ConfigurationProperties;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

/**
 * This Dao implementation uses jdo to perform the operations declared on the
 * interface (see InterfaceDao )
 * 
 * @author jsaray
 * 
 */
public class DaoJDOImpl implements InterfaceDao {

    /**
     * Log
     */
    public static Logger log = LoggerFactory.getLogger(DaoJDOImpl.class);

    /**
     * JDO Persistence manager
     */
    protected static PersistenceManager pm;

    /**
     * This will initialize the Persistence manager, one DAO instance is
     * equivalent to one persistence manager and thus one transaction
     */
    public DaoJDOImpl() {
	Properties mainprop;
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.debug("No Database configuratioin found for DBInterfaceFactory : "
		    + ce);
	    mainprop = new Properties();
	}

	Properties properties = new Properties();
	properties.setProperty("javax.jdo.PersistenceManagerFactoryClass",
		"org.jpox.PersistenceManagerFactoryImpl");
	properties.setProperty("javax.jdo.option.ConnectionDriverName",
		mainprop.getProperty("xtremweb.core.db.driver",
			"org.hsqldb.jdbcDriver"));
	properties.setProperty("javax.jdo.option.ConnectionURL", mainprop
		.getProperty("xtremweb.core.db.url", "jdbc:hsqldb:mem:test"));
	properties.setProperty("javax.jdo.option.ConnectionUserName",
		mainprop.getProperty("xtremweb.core.db.user", "sa"));
	properties.setProperty("javax.jdo.option.ConnectionPassword",
		mainprop.getProperty("xtremweb.core.db.password", ""));

	properties.setProperty("org.jpox.autoCreateSchema", "true");
	properties.setProperty("org.jpox.validateTables", "false");
	properties.setProperty("org.jpox.validateConstraints", "false");
	properties.setProperty("javax.jdo.option.DetachAllOnCommit", "false");
	if (mainprop.getProperty("xtremweb.core.db.connectionPooling") != null) {
	    properties.setProperty("org.jpox.connectionPoolingType",
		    mainprop.getProperty("xtremweb.core.db.connectionPooling"));
	    String dbcpPropertiesFile = mainprop
		    .getProperty("xtremweb.core.db.dbcp.propertiesFile");
	    if (dbcpPropertiesFile != null)
		properties.setProperty(
			"org.jpox.connectionPoolingConfigurationFile",
			dbcpPropertiesFile);
	}

	pm = JDOHelper.getPersistenceManagerFactory(properties)
		.getPersistenceManager();
    }

    /**
     * Ask the persistence manager to begin a transaction
     */
    public void beginTransaction() {
	pm.currentTransaction().begin();
    }

    /**
     * Ask the persistence manager to commit a transaction
     */
    public void commitTransaction() {
	pm.currentTransaction().commit();
    }

    /**
     * Make persistent using jdo
     * 
     * @param obj
     *            the object to make persistent
     * @param autonomous
     *            true if you want the storing to be made in a transaction
     *            launched by this method, false if you want to manage the
     *            transaction begin and commit
     */
    public void makePersistent(Object obj, boolean autonomous) {

	Transaction tx = null;
	if (autonomous)
	    tx = pm.currentTransaction();
	boolean persisted = false;

	try {
	    while (!persisted) {
		if (autonomous)
		    tx.begin();
		pm.makePersistent(obj);
		if (autonomous)
		    tx.commit();
		persisted = true;
	    }
	} catch (Exception sqle) {
	    log.warn("Error when persisting object : " + sqle
		    + "\ntrying again in 500ms ");
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException ie) {
		ie.printStackTrace();
	    }
	} finally {
	    if (autonomous) {
		if (tx.isActive()) {
		    tx.rollback();
		}
	    }
	}
    }

    /**
     * detach a copy using the persistence manager
     * 
     * @param obj
     *            the copy we want to be detached
     * @return the detached copy
     */
    public Object detachCopy(Object obj) {
	return pm.detachCopy(obj);
    }

    /**
     * getAll using jdo persistence manager
     * 
     * @param clazz
     *            the class which objects we want to retrieve
     * @return a collection of all the objects of a given class stored in jdo
     */
    public Collection getAll(Class clazz) {
	Collection res = new ArrayList();
	Extent ex = null;
	try {
	    ex = pm.getExtent(clazz, true);
	    for (Iterator iterator = ex.iterator(); iterator.hasNext();) {
		Object object = (Object) iterator.next();
		res.add(object);
	    }
	} catch (Exception e) {
	    System.out.println("DBI: " + e);
	}
	return res;
    }

    /**
     * get by uid using jdo persistence manager
     * 
     * @param clazz
     *            , the class we want to search
     * @param uid
     *            the uid we are searching for
     * @return the object whose uid is equals to parameter uid
     */
    public Object getByUid(Class clazz, String uid) {
	Extent e = pm.getExtent(clazz, true);
	Query q = pm.newQuery(e, "uid == \'" + uid + "\'");
	q.setUnique(true);
	return q.execute();
    }

    /**
     * Delete one object from the persistence manager
     * 
     * @param o
     *            the object we want to delete
     */
    public void deleteOne(Object o) {
	pm.deletePersistent(o);
    }

    /**
     * Delete all objects belonging to a collection
     * 
     * @param col
     *            the objects we want to delete
     */
    public void deleteAll(Collection col) {

	pm.deletePersistentAll(col);
    }

    /**
     * get by name using jdo, Warning !!! is actually searching by lowercase
     * 
     * @param clazz
     *            the class to get the name
     * @param name
     *            the name
     * @return the object whose is name is "name"
     */
    public Object getByName(Class clazz, String name) {
	Object ret = null;
	try {
	    Extent e = pm.getExtent(clazz, true);
	    Query q = pm.newQuery(e, "name == \"" + name.toLowerCase() + "\"");
	    q.setUnique(true);
	    Object protoStored = q.execute();
	    if (protoStored == null)
		return null;
	    ret = pm.detachCopy(protoStored);
	} finally {

	}
	return ret;
    }

    /**
     * Tells if a transaction is active
     * 
     * @return true if the current transaction is active, else false
     */
    public boolean transactionIsActive() {
	return pm.currentTransaction().isActive();
    }

    /**
     * Perform a rollback on the transaction
     */
    public void transactionRollback() {
	pm.currentTransaction().rollback();
    }

    /**
     * Close the persistence manager
     */
    public void close() {
	pm.close();
    }
}
