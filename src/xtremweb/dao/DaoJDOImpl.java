package xtremweb.dao;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Properties;
import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.conf.ConfigurationProperties;
import xtremweb.core.db.DBInterfaceFactory;
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
     * This will initialize the Persistence manager just once
     */
    static {
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
	properties.setProperty("javax.jdo.option.DetachAllOnCommit", "true");
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

    public void beginTransaction() {
	pm.currentTransaction().begin();
    }

    public void commitTransaction() {
	pm.currentTransaction().commit();
	// TODO is it worthy to do this ?
	/*
	 * finally { if (tx.isActive()) tx.rollback(); pm.close(); }
	 */
    }

    /**
     * make persistent using jdo
     * 
     * @param obj
     */
    public void makePersistent(Object obj, boolean autonomous) {
	Transaction tx = pm.currentTransaction();
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
	    if (tx.isActive()) {
		tx.rollback();
	    }
	    pm.close();
	}
    }

    /**
     * detach a copy using jdo
     */
    public Object detachCopy(Object obj) {
	return pm.detachCopy(obj);
    }

    /**
     * getAll using jdo
     */
    public Collection getAll(Class clazz) {
	Collection res = null;
	try {
	    Extent e = pm.getExtent(clazz, true);
	    res = (Collection) e;
	} catch (Exception e) {
	    System.out.println("DBI: " + e);
	} finally {
	    pm.close();
	}
	return res;
    }

    /**
     * get by uid using jdo
     */
    public Object getByUid(Class clazz, String uid) {
	Extent e = pm.getExtent(clazz, true);
	Query q = pm.newQuery(e, "uid == \"" + uid + "\"");
	q.setUnique(true);
	return q.execute();
    }

    /**
     * get by name using jdo
     * 
     * @param clazz
     *            the class to get the name
     * @param name
     *            the name
     * @return the object whose is name is "name"
     */
    public Object getByName(Class clazz, String name) {
	Object ret = null;
	PersistenceManager pm = DBInterfaceFactory
		.getPersistenceManagerFactory().getPersistenceManager();

	try {
	    Extent e = pm.getExtent(clazz, true);
	    Query q = pm.newQuery(e, "name == \"" + name.toLowerCase() + "\"");
	    q.setUnique(true);
	    Object protoStored = q.execute();
	    if (protoStored == null)
		return null;
	    ret = pm.detachCopy(protoStored);
	} finally {
	    pm.close();
	}
	return ret;
    }
}
