package xtremweb.serv.dt;

import xtremweb.core.log.*;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.serv.dc.DataUtil;
import xtremweb.core.db.*;
import javax.jdo.PersistenceManager;
import javax.jdo.Extent;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.Properties;
import java.io.File;


/**
 * <code>OOBTransferImpl</code>is the abstract class implenting the
 * interface <code>OOBTransfer</code>
 *
 * This class is abstract and could not be used as this. Instead
 * developper should use one of its subclass for instance FtpTransfer,
 * HttpTransfer which implements the desired Transfer Protocol
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public abstract class OOBTransferImpl implements OOBTransfer {

    protected static Logger logfac = LoggerFactory.getLogger(OOBTransferImpl.class);

    /**
     * Data <code>data</code> is the reference to the data transfered
     *
     */
    protected Data data;

    /**
     * Transfer <code>transfer</code> is the reference to the transfer.
     *
     */
    protected Transfer transfer;

    /**
     * Protocol <code>remote_protocol</code> is a reference to the
     * remote protocol used to transfer the data.
     *
     */
    protected Protocol remote_protocol;

    /**
     * Protocol <code>local_protocol</code>  is a reference to the
     * local protocol used to transfer the data. .
     */
    protected Protocol local_protocol;

    /**
     * Locator <code>remote_locator</code> is the remote location of
     * the data.
     */
    protected Locator  remote_locator;

    /**
     * Locator <code>local_locator</code> is the locale location of
     * the data.
     */
    protected Locator  local_locator;


    protected boolean error = false;

    /**
     * Creates a new <code>OOBTransferImpl</code> instance.
     *
     * @param tuid a <code>String</code> value: Transfer unique id
     */
    public OOBTransferImpl(String tuid) {
	logfac.debug(tuid);
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();
	    Query query = pm.newQuery(xtremweb.core.obj.dt.Transfer.class, "uid == \"" + tuid + "\"");
	    query.setUnique(true);
	    transfer = (Transfer) pm.detachCopy(query.execute());
	    logfac.debug( "transfer " + transfer.getuid() + ":" + transfer.getoob() + ":" + transfer.gettype());
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}
    } // OOBTransferImpl constructor

 
    /**
     * Creates a new <code>OOBTransferImpl</code> instance.
     *
     * @param d a <code>Data</code> value
     * @param t a <code>Transfer</code> value
     * @param rl a <code>Locator</code> value
     * @param ll a <code>Locator</code> value
     * @param rp a <code>Protocol</code> value
     * @param lp a <code>Protocol</code> value
     */
    public OOBTransferImpl(Data d, Transfer t, Locator rl, Locator ll, Protocol rp,  Protocol lp ) {
	transfer = t;
	data = d;
	remote_locator = rl;
	local_locator = ll;
	remote_protocol = rp;
	local_protocol = lp;
    } // Ftpsender constructor

    /**
     *  <code>persist</code> the OOBTransfer to the local database
     */
    public void persist() {

	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	String tuid = transfer.getuid();
	if ((transfer!=null)&&(transfer.getuid()!=null)) 
	    logfac.debug("Transfer already persisted : " + transfer.getuid());

	try {
	    tx.begin();
	    pm.makePersistent(data);
	    pm.makePersistent(remote_protocol);
	    pm.makePersistent(local_protocol);

	    remote_locator.setdatauid(data.getuid());
	    local_locator.setdatauid(data.getuid());

	    remote_locator.setprotocoluid(remote_protocol.getuid());
	    local_locator.setprotocoluid(local_protocol.getuid());

	    pm.makePersistent(remote_locator);
	    pm.makePersistent(local_locator);

	    transfer.setlocatorremote(remote_locator.getuid());
	    transfer.setlocatorlocal(local_locator.getuid());
	    transfer.setdatauid(data.getuid());
	    pm.makePersistent(transfer);
	    tx.commit();

        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}

	//FIXME: should have an assert here
	if ( (tuid!=null) && (!tuid.equals( transfer.getuid()))) 
	    logfac.debug(" Transfer has been incorrectly persisted    " + tuid + "  !="  + transfer.getuid());

    }

    /**
     *  <code>poolTransfer</code> pools the local file to determine if
     *  it has the same characteristic, size and md5 checksum, than
     *  the origina data.
     *  returns true if the two files are similar, false otherwise .
     *
     * @return a <code>boolean</code> value
     */
    public boolean poolTransfer() {
	// First get the reference of the local file
	File localFile;
	if (local_protocol.getpath() == null)
	    localFile = new File (local_locator.getref());
	else
	    localFile = new File (new File(local_protocol.getpath()), local_locator.getref());
	if (!localFile.exists()) {
	    logfac.debug("File " + local_protocol.getpath()  + " " + local_locator.getref() + " does not exist ");
	    return false;
	}
	String localChecksum = DataUtil.checksum(localFile);
	logfac.debug("Pooling [static][local]: " + data.getuid() + " size [" + data.getsize() + "][" + localFile.length()  + "] md5sum [" +  data.getchecksum() + "][" + localChecksum + "]" );
	return (( localFile.length() == data.getsize()) && (localChecksum.equals(data.getchecksum())));
    }


    /**
     *  <code>getData</code> return data.
     *
     * @return a <code>Data</code> value
     */
    public Data getData(){
	return data;
    }

    /**
     *  <code>getTransfer</code> return transfer.
     *
     * @return a <code>Transfer</code> value
     */
    public Transfer getTransfer() {
	return transfer;
    }

    /**
     *  <code>getLocalLocator</code> return local locator.
     *
     * @return a <code>Locator</code> value
     */
    public Locator getLocalLocator(){
	return local_locator;
    }

    /**
     * <code>getRemoteLocator</code> return remote locator.
     *
     * @return a <code>Locator</code> value
     */
    public Locator getRemoteLocator(){
	return remote_locator;
    }

    /**
     * <code>getLocalProtocol</code> return local protocol.
     *
     * @return a <code>Protocol</code> value
     */
    public Protocol getLocalProtocol(){
	return local_protocol;
    }

    /**
     * <code>getRemoteProtocol</code> return remote protocol.
     *
     * @return a <code>Protocol</code> value
     */
    public Protocol getRemoteProtocol(){
	return remote_protocol;
    }


    public boolean error() {
	return error;
    }

    /**
     * <code>toString</code> converts to a string
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	return "transfer [" + transfer.getuid() + "]  data [" + data.getuid() + "]  ll [" + local_locator.getuid() + "]  rl [" + remote_locator.getuid() + "]  lp [" + local_protocol.getuid() + "]  rp [" + remote_protocol.getuid() + "]";
    }
} // OOBTransferImpl
