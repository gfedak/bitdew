package xtremweb.serv.ds;

import java.rmi.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.iface.*;
import xtremweb.core.db.*;
import xtremweb.core.obj.dc.*;
import xtremweb.core.obj.ds.*;
import xtremweb.core.uid.*;
import xtremweb.core.log.*;
import java.util.*;
import java.io.File;
import javax.jdo.PersistenceManager;
import javax.jdo.Extent;
import javax.jdo.Query;
import javax.jdo.Transaction;

/**
 * Describe class Callbackds here.
 *
 *
 * Created: Wed Aug 16 16:33:12 2006
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class Callbackds extends CallbackTemplate implements InterfaceRMIds {

    protected Logger log = LoggerFactory.getLogger("DS Service");

    protected DataScheduler ds;
    /**
     * Creates a new <code>Callbackds</code> instance.
     *
     */
    public Callbackds() {
	ds = new DataScheduler();
	//FIXME 
	//	ds.start();
    }
    
    public Attribute registerAttribute(Attribute attr) throws RemoteException {
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();

	    pm.makePersistent(attr);
	    
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}
	ds.updateAttribute(attr);
	return attr;
    }

    public Attribute getAttributeByUid(String uid) {
	Attribute attr = null;

	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();

	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();

            Extent e=pm.getExtent(Attribute.class,true);
            Query q=pm.newQuery(e, "uid == \"" + uid + "\"");
	    q.setUnique(true);

	    Attribute tmp =(Attribute) q.execute();
	    attr = (Attribute) pm.detachCopy(tmp);
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}	
	
	return attr;
    }

    public void associateDataAttribute(Data data, Attribute attr) throws RemoteException {
	if (attr.getuid() == null) 
	    attr = registerAttribute(attr);
	ds.associateDataAttribute(data, attr);
    }

    public void associateDataHost(Data data, Host host) throws RemoteException {
	ds.associateDataHost(data, host);
    }

    public void associateDataAttributeHost(Data data, Attribute attr, Host host) throws RemoteException {
	if (attr.getuid() == null) 
	    attr = registerAttribute(attr);
	ds.associateDataAttributeHost(data, attr, host);
    }

    //FIXME IT'S BAD
    public void associateAttribute(String datauid, String attruid) throws RemoteException {
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();

	try {
	    tx.begin();
	    Query query = pm.newQuery(xtremweb.core.obj.dc.Data.class, 
				      "uid == \"" + datauid + "\"");
	    query.setUnique(true);
	    Data d = (Data) query.execute();
	    if (d==null) {
		log.debug (" d " + datauid + " is null ");
	    } else {		
		d.setattruid(attruid);
		pm.makePersistent(d);	
	    }
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}
    }

    public Vector sync(Host host, Vector dataList) {
	String sync = "sync [" + host.getuid() + "] ";
	for (int i=0; i< dataList.size(); i++)
	    sync = sync+ (String) dataList.elementAt(i) + " ";
	log.debug( sync );
	return ds.getData(host, dataList);
    }


}
