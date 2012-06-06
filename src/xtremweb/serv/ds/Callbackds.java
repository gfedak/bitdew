package xtremweb.serv.ds;

import java.rmi.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.iface.*;
import xtremweb.core.obj.dc.*;
import xtremweb.core.obj.ds.*;
import xtremweb.core.log.*;
import xtremweb.dao.DaoFactory;
import xtremweb.dao.attribute.DaoAttribute;

import java.util.*;

/**
 * This class implements a data scheduler, it schedule data according to
 * affinity rules Created: Wed Aug 16 16:33:12 2006
 * 
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class Callbackds extends CallbackTemplate implements InterfaceRMIds {
	private DaoAttribute dao;
    /**
     * Class logger
     */
    protected Logger log = LoggerFactory.getLogger("DS Service");

    /**
     * Data scheduler object
     */
    protected DataScheduler ds;

    /**
     * Creates a new <code>Callbackds</code> instance.
     * 
     */
    public Callbackds() {
    	dao = (DaoAttribute) DaoFactory.getInstance("xtremweb.dao.attribute.DaoAttribute");
    	ds = new DataScheduler();
    }

    /**
     * Register an attribute in a DBMS and retrieve a reference in memory
     * 
     * @param attr
     *            the attribute to insert
     * @return a reference to the newly create attribute
     * @exception RemoteException
     *                if anything goes wrong
     */
    public Attribute registerAttribute(Attribute attr) throws RemoteException {
	
	dao.makePersistent(attr, true);
	return attr;
    }

    /**
     * Retrieve an attribute given an uid
     * 
     * @param uid
     *            the attribute uid
     * @return the attribute whose id is uid
     */
    public Attribute getAttributeByUid(String uid) {
	Attribute tmp = null;

	
	try {
	    dao.beginTransaction();
	    tmp = (Attribute) dao.getByUid(Attribute.class, uid);
	    dao.commitTransaction();
	} finally {
	    if (dao.transactionIsActive())
		dao.transactionRollback();

	}

	return tmp;
    }

    /**
     * Retrieve an attribute by its name
     * 
     * @param name
     *            the name to retrieve
     * @return the attribute whose name is name
     */
    public Attribute getAttributeByName(String name) {
	DaoAttribute dao = (DaoAttribute) DaoFactory
		.getInstance("xtremweb.dao.attribute.DaoAttribute");
	Attribute dataStored = null;
	try {
	    dao.beginTransaction();
	    dataStored = (Attribute) dao.getByName(Attribute.class, name);
	    dao.commitTransaction();
	} finally {
	    if (dao.transactionIsActive())
		dao.transactionRollback();
	}

	return dataStored;

    }

    /**
     * Associate a data with one attribute
     * 
     * @param data
     *            the data
     * @param attr
     *            the attr
     * @exception RemoteException
     *                if anything goes wrong
     */
    public void associateDataAttribute(Data data, Attribute attr)
	    throws RemoteException {
	if (attr.getuid() == null)
	    attr = registerAttribute(attr);
	ds.associateDataAttribute(data, attr);
    }

    /**
     * Associates a data to a particular host (the host will own the data)
     * 
     * @param data
     *            data to give the host to own
     * @param host
     *            the host that will own the data
     * @exception RemoteException
     *                if anything goes wrong (rmi exception)
     */
    public void associateDataHost(Data data, Host host) throws RemoteException {
	ds.associateDataHost(data, host);
    }

    /**
     * Associates a data with an attribute an a host
     * 
     * @param data
     *            the data to associate
     * @param attr
     *            the attr to associate
     * @param host
     *            the host to associate
     */
    public void associateDataAttributeHost(Data data, Attribute attr, Host host)
	    throws RemoteException {
	if (attr.getuid() == null)
	    attr = registerAttribute(attr);
	ds.associateDataAttributeHost(data, attr, host);
    }

    /**
     * Removes a data from the Data Scheduler
     * 
     * @param data
     *            the data to remove
     * @exception RemoteException
     *                if anything goes wrong
     */
    public void removeData(Data data) throws RemoteException {
	ds.removeData(data);
    }

    /**
     * This method dont seem to be used
     * 
     * @param datauid
     * @param attruid
     * @throws RemoteException
     */
    public void associateAttribute(String datauid, String attruid)
	    throws RemoteException {
	Attribute attr = null;
	DaoAttribute dao = (DaoAttribute) DaoFactory
		.getInstance("xtremweb.dao.attribute.DaoAttribute");
	try {
	    dao.beginTransaction();
	    Data d = (Data) dao.getByUid(Data.class, datauid);
	    if (d == null) {
		log.debug(" d " + datauid + " is null ");
	    } else {
		d.setattruid(attruid);
		dao.makePersistent(d, true);
	    }
	    dao.commitTransaction();
	} finally {
	    if (dao.transactionIsActive())
		dao.transactionRollback();
	}
    }

    /**
     * Most important method, according to the data contained in a vector V
     * introduced by parameter, it answers with a new list B such that the
     * caller can safely delete the obsolete data and store the newly created
     * data
     * 
     * @param host
     *            the host that is sending the request
     * @param dataList
     *            the data sent to the RMI service
     * @return a new vector containing the data authorized to be kept by the
     *         host
     */
    public Vector sync(Host host, Vector dataList) {
	String sync = "sync [" + host.getuid() + "] ";
	for (int i = 0; i < dataList.size(); i++)
	    sync = sync + (String) dataList.elementAt(i) + " ";
	log.debug(sync);
	return ds.getData(host, dataList);
    }

}
