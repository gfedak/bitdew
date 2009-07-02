package xtremweb.serv.dc;

/**
 * Callbackdc.java
 *
 *
 * Created: Thu Feb 23 14:04:25 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */
import java.rmi.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.iface.*;
import xtremweb.core.db.*;
import xtremweb.core.obj.dc.*;
import xtremweb.core.uid.*;
import xtremweb.core.log.*;

import xtremweb.serv.dc.ddc.jdht.*;
import xtremweb.serv.dc.ddc.*;

import java.util.*;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import javax.jdo.PersistenceManager;
import javax.jdo.Extent;
import javax.jdo.Query;
import javax.jdo.Transaction;

public class Callbackdc extends CallbackTemplate implements InterfaceRMIdc{
   
    protected Logger log = LoggerFactory.getLogger("DC Service");
    protected DistributedDataCatalog ddc = null;

    public Callbackdc() {
	try {
	    ddc = DistributedDataCatalogFactory.getDistributedDataCatalog();
	    ddc.start();
	} catch (DDCException ddce) {
	    log.warn("unable to start a Distributed Data Catalog service");
	    ddc = null;
	}
	log.info("Started DHT service for distributed data catalog");
    } // Callbackobj constructor
    
    public Data createData( String name, String checksum, long size, int type)  throws RemoteException {	
	Data data = new Data();

	data.setname( name );
	data.setchecksum( checksum );
	data.setsize( size );
	data.settype( type );

	putData(data);

	return data;
    }

    public Data createData()  throws RemoteException {
	
	Data data = new Data();
	putData(data);

	return data;
    }


    public void putData(Data data)  throws RemoteException {
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();

	    pm.makePersistent(data);
	    
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}	
    }

    public String getDDCEntryPoint()  throws RemoteException {
	if (ddc==null) return "null";
	else {
	    try {
		return ddc.entryPoint();
	    } catch (DDCException ddce) {
		log.debug("" +ddce);
	    }
	}
	return  "null";
    }

    public Data getData(String uid)  throws RemoteException {
	Data data = null;

	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();

	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();

            Extent e=pm.getExtent(Data.class,true);
            Query q=pm.newQuery(e, "uid == \"" + uid + "\"");
	    q.setUnique(true);

	    Data dataStored =(Data) q.execute();
	    if (dataStored==null) 
		return null;
	    data = (Data) pm.detachCopy(dataStored);
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}	
	
	return data;
    }

    public void deleteData(Data data)  throws RemoteException {
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();

	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();
	    //	    Object id = pm.getObjectId(data);
	    //Data obj = pm.getObjectById(id);
	    pm.makePersistent(data);
	    pm.deletePersistent(data);

	    //	    Data todelete = pm.getObjectById(uid);  // Retrieves the object to delete
	    //	    pm.deletePersistent(todelete);
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}	
    }

    public void deleteData(String uid)  throws RemoteException{
	//	deleteData(data.getuid());
    }

    public void browse() {
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();
            Extent e=pm.getExtent(Data.class,true);
            Iterator iter=e.iterator();
            while (iter.hasNext())
            {
                Data data = (Data) iter.next();
                System.out.println( DataUtil.toString(data) );
            }
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}	
    }

    public void putLocator(Locator locator)  throws RemoteException {
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();

	log.debug("************** protocol **************" + locator.getprotocoluid());
	locator.setpublish(true);
	try {
	    tx.begin();
	    pm.makePersistent(locator);
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}	
    }


    //FIXME TO IMPLEMENT
    public  void setDataStatus( String uid, int status ) throws RemoteException {
    }


    //FIXME THAT'S UGLY !!!!!
    public Locator getLocatorByDataUID(String uid)  throws RemoteException {
	Locator locator = null;
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();

            Extent e=pm.getExtent(Locator.class,true);
	    //            Query q=pm.newQuery(e, "datauid == " + uid);
           Iterator iter=e.iterator();
            while (iter.hasNext())
            {
                Locator ret = (Locator) iter.next();		
		if (ret.getdatauid().equals(uid)) {
		    log.debug("getLocatorByDataUID found one locator : " + ret.getuid() + ":" + ret.getpublish());
		    if (ret.getpublish())
			locator = (Locator) pm.detachCopy(ret);
		}
            }
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}	

	return locator;

    }

    

    public static void main(String [] args) {
	try {
	    Callbackdc dc = new Callbackdc();
	    Data data;
	    String uid=null;
	    
	    // data creation
	    File file = new File("Makefile");
	    uid = (dc.createData(file.getName(), "CHCKSUM", file.length(),0)).getuid();	
	    System.out.println("Data created : " + uid);
	    
	    //data creation from a file
	    data = DataUtil.fileToData(file);
	    System.out.println("Data created : " + DataUtil.toString(data));
	    dc.putData(data);
	    
	    //browse data
	    dc.browse();
	    
	    // getting data
	    Data data_read = dc.getData(data.getuid());
	    System.out.println("readind data UID=" + data.getuid() +" || " +  DataUtil.toString(data_read));
	    
	    //deleting data
	    Data data_delete = data;
	    System.out.println("deleting data UID=" + data_delete.getuid());
	    dc.deleteData(data);
	    dc.browse();

	}catch (RemoteException re){
	    ;
	}
    }
} // Callbackobj
