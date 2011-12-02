package xtremweb.serv.dt;

import java.rmi.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.iface.*;
import xtremweb.core.log.*;
import xtremweb.serv.dt.http.*;
import xtremweb.serv.dt.dummy.*;
import xtremweb.serv.dt.bittorrent.*;
import xtremweb.api.transman.*;
import xtremweb.core.conf.*;

import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.dao.DaoFactory;
import xtremweb.dao.transfer.DaoTransfer;

//FIXME
import xtremweb.api.transman.TransferStatus;
import java.util.Properties;

/**
 * This class provide a service to monitor multiprotocol transfer status
 * 
 * 
 * Created: Wed Aug 16 16:33:12 2006
 * 
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class Callbackdt extends CallbackTemplate implements InterfaceRMIdt {

    /**
     * Callbackdt logger
     */
    protected Logger log = LoggerFactory.getLogger("DT Service");
    private DaoTransfer dao;
    /**
     * Local transfer manager, this will change in the future as it is violating
     * the previously fixed design rules
     */
    protected TransferManager tm;

    private Properties mainprop;

    /**
     * Creates a new <code>Callbackdt</code> instance.
     * 
     */
    public Callbackdt() {
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException e) {
	    log.debug("problem loading properties in callbackdt");
	    mainprop = new Properties();
	    e.printStackTrace();
	}
	dao = (DaoTransfer) DaoFactory
		.getInstance("xtremweb.dao.transfer.DaoTransfer");
	String add = mainprop.getProperty("xtremweb.serv.dt.embeddedtm");
	boolean b = Boolean.parseBoolean(add);
	if (b) {
	    tm = TransferManagerFactory.getTransferManager();
	    log.debug("tm that callback is using " + tm);
	    //tm.start();
	}

	String temp = mainprop.getProperty("xtremweb.serv.dt.protocols");
	if (temp == null)
	    temp = "dummy http";

	log.debug("list of protocols to initalize :" + temp);
	String[] protocols = temp.split(" ");
	for (int i = 0; i < protocols.length; i++) {
	    String protoName = protocols[i].toLowerCase();

	    try {
		if (protoName == "http")
		    HttpTransfer.init();
		if (protoName.equals("bittorrent"))
		    BittorrentTransfer.init();
		if (protoName == "dummy")
		    DummyTransfer.init();
	    } catch (OOBException oe) {
		log.warn("Was not able to perform BitTorrent initialization"
			+ oe);
	    }
	}
    }

    // sequence to transfert
    // preparer un transfer (virer local remote) -> transfer feasible
    // start transfer -> return a status
    // pool transfer -> return a transfer
    // check
    // end Transfer
    // abort
    /**
     * This method register a transfer in the local transfer manager
     * 
     * @param t
     *            the transfer
     * @param data
     *            the data to associate the transfer with
     * @param rp
     *            remote protocol
     * @param rl
     *            local protocol
     */
    public int registerTransfer(Transfer t, Data data, Protocol rp, Locator rl)
	    throws RemoteException {
	log.debug("register transfer was called in !!!" + tm);
	Protocol local_proto = new Protocol();
	Locator local_locator = new Locator();

	try {
	    t.setdatauid(data.getuid());
	    // No local protocol bizarre
	    local_proto.setname("local");
	    local_locator.setdatauid(data.getuid());
	    local_locator.setref(rl.getref());
	    local_locator.setprotocoluid(local_proto.getuid());

	    t.setlocatorremote(rl.getuid());
	    if (t.gettype() == TransferType.UNICAST_SEND_SENDER_SIDE)
		t.settype(TransferType.UNICAST_SEND_RECEIVER_SIDE);
	    if (t.gettype() == TransferType.UNICAST_RECEIVE_RECEIVER_SIDE)
		t.settype(TransferType.UNICAST_RECEIVE_SENDER_SIDE);
	    t.setlocatorlocal(local_locator.getuid());
	    t.setstatus(TransferStatus.PENDING);
	} catch (Exception e) {
	    log.debug("Exception when registring oob transfer " + e);
	    e.printStackTrace();
	    throw new RemoteException();
	}
	try {
	    OOBTransfer oobt = OOBTransferFactory.createOOBTransfer(data, t,
		    rl, local_locator, rp, local_proto);

	    String tuid = oobt.getTransfer().getuid();
	    if ((oobt.getTransfer() != null)
		    && (oobt.getTransfer().getuid() != null))
		log.debug("Transfer already persisted : "
			+ oobt.getTransfer().getuid());
	    log.debug(" data snapshot just before persisting uid"
		    + oobt.getData().getuid() + "md5 "
		    + oobt.getData().getchecksum() + " size "
		    + oobt.getData().getsize());

	    dao.makePersistent(oobt.getData(), true);
	    dao.makePersistent(oobt.getRemoteProtocol(), true);
	    dao.makePersistent(oobt.getLocalProtocol(), true);

	    oobt.getRemoteLocator().setdatauid(oobt.getData().getuid());
	    oobt.getLocalLocator().setdatauid(oobt.getData().getuid());

	    oobt.getRemoteLocator().setprotocoluid(
		    oobt.getRemoteProtocol().getuid());
	    oobt.getLocalLocator().setprotocoluid(
		    oobt.getLocalProtocol().getuid());

	    dao.makePersistent(oobt.getRemoteLocator(), true);
	    dao.makePersistent(oobt.getLocalLocator(), true);

	    oobt.getTransfer().setlocatorremote(
		    oobt.getRemoteLocator().getuid());
	    oobt.getTransfer().setlocatorlocal(oobt.getLocalLocator().getuid());
	    oobt.getTransfer().setdatauid(oobt.getData().getuid());
	    dao.makePersistent(oobt.getTransfer(), true);

	    // FIXME: should have an assert here
	    if ((tuid != null) && (!tuid.equals(oobt.getTransfer().getuid())))
		log.debug(" Transfer has been incorrectly persisted    " + tuid
			+ "  !=" + oobt.getTransfer().getuid());

	    log.debug("Succesfully created transfer [" + t.getuid()
		    + "] data [" + data.getuid() + "] with remote storage ["
		    + rl.getref() + "] " + rp.getname() + "://["
		    + rp.getlogin() + ":" + rp.getpassword() + "]@"
		    + rl.getdrname() + ":" + rp.getport() + "/" + rp.getpath()
		    + "/" + rl.getref() + "\n" + oobt);

	    String add = mainprop.getProperty("xtremweb.serv.dt.embeddedtm");
	    boolean b = Boolean.parseBoolean(add);
	    if (b)
		tm.registerTransfer(oobt);
	} catch (OOBException e) {
	    log.debug("Exception when registring oob transfer " + e);
	    throw new RemoteException();
	}
	return TransferStatus.PENDING;
    }

    /**
     * This method is not being used
     */
    public int startTransfer(String transferID) throws RemoteException {
	log.debug("start Transfer : Unused function ??!?? Not sure about what to do ???");
	return 0;
    }

    /**
     * This method pools a transfer to check if it is complete
     * 
     * @param transferID
     *            the transfer to pool
     * @return boolean true if this this transfer is complete false if not
     */
    public boolean poolTransfer(String transferID) throws RemoteException {
	log.debug("pooling transfer : " + transferID);
	DaoTransfer daot = (DaoTransfer) DaoFactory
		.getInstance("xtremweb.dao.transfer.DaoTransfer");

	daot.beginTransaction();

	boolean isComplete = false;

	try {
	    Transfer t = (Transfer) daot.getByUid(
		    xtremweb.core.obj.dt.Transfer.class, transferID);
	    log.debug("value of t is " + t + " type of t is " + t.gettype()
		    + " status of t is " + t.getstatus());

	    if (t == null) {
		log.debug(" t " + transferID + " is null ");
	    } else {
		isComplete = (t.getstatus() == TransferStatus.COMPLETE || t
			.getstatus() == TransferStatus.TODELETE);
		log.debug(" t " + t.getuid() + " is status : "
			+ TransferStatus.toString(t.getstatus()));
	    }
	    daot.commitTransaction();
	} finally {
	    if (daot.transactionIsActive())
		daot.transactionRollback();
	    daot.close();
	}
	return isComplete;
    }

    /**
     * This method is doing nothing and maybe it should be erased
     */
    public int endTransfer(String transferID) throws RemoteException {
	log.debug("end Transfer :: Unused Method");
	return 0;
    }

    /**
     * This method is doing nothing and it should be erased
     */
    public int abortTransfer(String transferID) throws RemoteException {
	log.debug("abort Transfert");
	return 0;
    }

    /**
     * This method puts a transfer in the DBMS
     * 
     * @param trans
     *            the transfer to put
     * @return the newly created transfer uid
     * @throws RemoteException
     *             if anything fails
     */
    public String putTransfer(Transfer trans) throws RemoteException {
	dao.makePersistent(trans, true);
	return trans.getuid();
    }

    /**
     * This method changes the transfer status
     * 
     * @param tuid
     *            the transfer uid which status we want to change
     * @param status
     *            the transfer new status
     * @throws RemoteException
     *             if anything fails
     */
    public void setTransferStatus(String tuid, int status)
	    throws RemoteException {

	dao.beginTransaction();
	try {
	    Transfer t = (Transfer) dao.getByUid(
		    xtremweb.core.obj.dt.Transfer.class, tuid);

	    if (t == null) {
		log.debug(" t " + tuid + " is null ");
	    } else {
		t.setstatus(status);
		dao.makePersistent(t, false);
	    }
	    dao.commitTransaction();
	} finally {
	    if (dao.transactionIsActive())
		dao.transactionRollback();

	}
    }

}
