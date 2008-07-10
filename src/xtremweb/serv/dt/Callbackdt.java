package xtremweb.serv.dt;

import java.rmi.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.iface.*;
import xtremweb.core.db.*;
import xtremweb.core.obj.dc.*;
import xtremweb.core.obj.dt.*;
import xtremweb.core.uid.*;
import xtremweb.core.log.*;
import xtremweb.serv.dt.http.*;
import xtremweb.serv.dt.dummy.*;
import xtremweb.serv.dt.bittorrent.*;
import xtremweb.api.transman.*;
import java.util.*;
import java.io.File;

import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dt.Transfer;

//FIXME
import xtremweb.api.transman.TransferStatus;

import javax.jdo.PersistenceManager;
import javax.jdo.Extent;
import javax.jdo.Query;
import javax.jdo.Transaction;


/**
 * Describe class Callbackdt here.
 *
 *
 * Created: Wed Aug 16 16:33:12 2006
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class Callbackdt extends CallbackTemplate implements InterfaceRMIdt {

    protected Logger log = LoggerFactory.getLogger("DT Service");

    //FIXME it brokes our design rules !!!!
    protected TransferManager tm;

    /**
     * Creates a new <code>Callbackdt</code> instance.
     *
     */
    public Callbackdt() {
	//recupere les properties
	//FIXME : bad design, is it necessary ?
	tm = TransferManagerFactory.getTransferManager();
	tm.start();
	try {
	    HttpTransfer.init();
	    BittorrentTransfer.init();
	    DummyTransfer.init();
	} catch (OOBException oe) {
	    log.warn("Was not able to perform BitTorrent initialization" + oe);
	}
    }

    //sequence to transfert
    //preparer un transfer (virer local remote) -> transfer feasible
    //start transfer -> return a status
    //pool transfer -> return a transfer
    //check 
    //end  Transfer
    //abort
    
    public int registerTransfer(Transfer t, Data data, Protocol rp, Locator rl) throws RemoteException {
	//renregistrer le transfert et faire les bonnes verif.
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();

	Transfer trans = t;

	trans.setdatauid(data.getuid());
	// No local protocol bizarre
	Protocol local_proto = new Protocol();
	local_proto.setname("local");
	    
	Locator local_locator = new Locator();
	local_locator.setdatauid(data.getuid());
	local_locator.setref(rl.getref());
	local_locator.setprotocoluid(local_proto.getuid());
	log.debug("Local Locator : " + local_locator.getref());

	Protocol remote_proto = rp;

	Locator remote_locator = rl;
	
	t.setlocatorremote(remote_locator.getuid());
	if (t.gettype () == TransferType.UNICAST_SEND_SENDER_SIDE)
	    t.settype(TransferType.UNICAST_SEND_RECEIVER_SIDE);
	if (t.gettype () == TransferType.UNICAST_RECEIVE_RECEIVER_SIDE)
	    t.settype(TransferType.UNICAST_RECEIVE_SENDER_SIDE);
	t.setlocatorlocal(local_locator.getuid());
	t.setstatus(TransferStatus.PENDING);
	
	try {
	    OOBTransfer oobt = OOBTransferFactory.createOOBTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);
	    oobt.persist();
	    log.debug("Succesfully created transfer [" + t.getuid() + "] data [" + data.getuid()+ "] with remote storage [" + remote_locator.getref()  + "] " + remote_proto.getname() +"://[" + remote_proto.getlogin() + ":" +  remote_proto.getpassword() +  "]@" + remote_locator.getdrname() + ":" +  remote_proto.getport() +"/" + remote_proto.getpath() + "/" + remote_locator.getref() + "\n" + oobt);
	    
	    tm.registerTransfer(t.getuid(), oobt);
	} catch( OOBException e) {
	    log.debug("Exception when registring oob transfer " + e);
	    throw new RemoteException();
	}
	return TransferStatus.PENDING;
    }

    public int startTransfer(String transferID) throws RemoteException {
	log.debug("start Transfer : Unused function ??!?? Not sure about what to do ???");
	/*
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();

	try {
	    tx.begin();
	    Query query = pm.newQuery(xtremweb.core.obj.dt.Transfer.class, 
				      "uid == \"" + transferID + "\"");
	    query.setUnique(true);
	    Transfer t = (Transfer) query.execute();
	    if (t==null) {
		log.debug (" t " + t.getuid() + " is null ");
	    } else {
		OOBTransfer oobt = OOBTransferFactory.createOOBTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);
		
		log.debug (" t " + t.getuid() + " is status : " + TransferStatus.toString( t.getstatus()) );
	    }
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}
	*/
	return 0;
    }

    public boolean poolTransfer(String transferID) throws RemoteException {
	log.debug("pooling transfer : " + transferID);
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	
	boolean isComplete = false;
	
	try {
	    tx.begin();
	    Query query = pm.newQuery(xtremweb.core.obj.dt.Transfer.class, 
				      "uid == \"" + transferID + "\"");
	    query.setUnique(true);
	    Transfer t = (Transfer) query.execute();
	    if (t==null) {
		log.debug (" t " + t.getuid() + " is null ");
	    } else {
		isComplete = (t.getstatus() == TransferStatus.COMPLETE);
		log.debug (" t " + t.getuid() + " is status : " + TransferStatus.toString( t.getstatus()) );
	    }
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}

	return isComplete;
    }

    public int endTransfer(String transferID) throws RemoteException {
	log.debug("end Transfer :: Unused Method");
	return 0;
    }

    public int abortTransfer(String transferID) throws RemoteException {
	log.debug("abort Transfert");
	return 0;
    }

    public void setTransferStatus(String tuid, int status) throws RemoteException {
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();

	try {
	    tx.begin();
	    Query query = pm.newQuery(xtremweb.core.obj.dt.Transfer.class, 
				      "uid == \"" + tuid + "\"");
	    query.setUnique(true);
	    Transfer t = (Transfer) query.execute();
	    if (t==null) {
		log.debug (" t " + tuid + " is null ");
	    } else {		
		t.setstatus(status);
		pm.makePersistent(t);	
	    }    
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}
    }

}
