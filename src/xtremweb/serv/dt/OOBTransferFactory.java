package xtremweb.serv.dt;

import xtremweb.serv.dt.ftp.*;
import xtremweb.serv.dt.http.*;
import xtremweb.serv.dt.scp.ScpTransfer;
import xtremweb.serv.dt.dummy.*;
import xtremweb.core.log.*;
import xtremweb.core.db.*;

import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dt.Transfer;
//FIXME
import xtremweb.api.transman.TransferType;

import javax.jdo.PersistenceManager;
import javax.jdo.Extent;
import javax.jdo.Query;
import javax.jdo.Transaction;

/**
 * Class <code>OOBTransferFactory</code> creates the corresponding OOBTransfer
 * subclass according to the transfer and protocols. 
 *
 *
 * Created: Thu Feb 22 12:40:37 2007
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */


public class OOBTransferFactory {

    public static Logger log = LoggerFactory.getLogger(OOBTransferFactory.class);

    /**
     * Creates a new <code>OOBTransferFactory</code> instance.
     *
     */
    public OOBTransferFactory() {
    }

    
    /**
     *  <code>createOOBTransfer</code> creates an OOBTransfer from a
     *  Transfer t. This method will read the data, locators and
     *  protocol from the database. An Exception is throwed when some
     *  values are missing
     *
     * @param t a <code>Transfer</code> value
     * @return an <code>OOBTransfer</code> value
     * @exception OOBException if an error occurs
     */
    public static OOBTransfer createOOBTransfer(Transfer t) throws OOBException {
	Data d = null;
	Locator rl = null;
	Locator ll = null;
	Protocol lp = null;
	Protocol rp = null;
	
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();

	    Query query = pm.newQuery(xtremweb.core.obj.dc.Locator.class, 
				      "uid == \"" + t.getlocatorlocal() + "\"");
	    query.setUnique(true);
	    ll = (Locator) pm.detachCopy(query.execute());

	    query = pm.newQuery(xtremweb.core.obj.dc.Locator.class, 
				"uid == \"" + t.getlocatorremote() + "\"");
	    query.setUnique(true);
	    rl = (Locator) pm.detachCopy(query.execute());

	    query = pm.newQuery(xtremweb.core.obj.dr.Protocol.class, 
				"uid == \"" + rl.getprotocoluid() + "\"");
	    query.setUnique(true);
	    rp = (Protocol) pm.detachCopy(query.execute());

	    query = pm.newQuery(xtremweb.core.obj.dr.Protocol.class, 
				"uid == \"" + ll.getprotocoluid() + "\"");
	    query.setUnique(true);
	    lp = (Protocol) pm.detachCopy(query.execute());
	    //FIXME to use transfet.getdatauid() instead	    
	    if (! ll.getdatauid().equals(rl.getdatauid()) )
		throw new OOBException("O-O-B Transfers refers to two different data ");
	    
	    query = pm.newQuery(xtremweb.core.obj.dc.Data.class, 
				"uid == \"" + ll.getdatauid() + "\"");
	    query.setUnique(true);
	    d = (Data) pm.detachCopy(query.execute());
	    log.debug( "OOBTransferFactory create " + t.getuid() + ":" + t.getoob() + ":" + TransferType.toString(t.gettype()));
	    tx.commit();
	    return createOOBTransfer(d,t,rl,ll,rp,lp);
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}
    }

    /**
     * <code>createOOBTransfer</code> creates an OOBTransfer from the
     * Transfer, Data, Protocol and Locator. The correct class
     * corresponding to the right protocol is selected (Http, Ftp etc..)
     *
     * @param d a <code>Data</code> value
     * @param t a <code>Transfer</code> value
     * @param rl a <code>Locator</code> value
     * @param ll a <code>Locator</code> value
     * @param rp a <code>Protocol</code> value
     * @param lp a <code>Protocol</code> value
     * @return an <code>OOBTransfer</code> value
     * @exception OOBException if an error occurs
     */
    public static OOBTransfer createOOBTransfer(Data d, Transfer t, Locator rl, Locator ll, Protocol rp,  Protocol lp ) throws OOBException {
	if (d==null)
	    throw new OOBException("Unable to find the correct OOB Transfers for transfer : " + t.getuid() + " data (d) is null"  );
	if (t==null)
	    throw new OOBException("Unable to find the correct OOB Transfers for transfer : " + t.getuid() + " transfer (t) is null"  );
	if (rl==null)
	    throw new OOBException("Unable to find the correct OOB Transfers for transfer : " + t.getuid() + " remote locator (rl) is null"  );
	if (ll==null)
	    throw new OOBException("Unable to find the correct OOB Transfers for transfer : " + t.getuid() + " local locator (ll) is null"  );
	if (lp==null)
	    throw new OOBException("Unable to find the correct OOB Transfers for transfer : " + t.getuid() + " local protocol (lp) is null"  );
	if (rp==null)
	    throw new OOBException("Unable to find the correct OOB Transfers for transfer : " + t.getuid() + " remote protocol (rp) is null"  );

	if (lp.getname().toLowerCase().equals("local")) {
	    if (rp.getname().toLowerCase().equals("ftp")) 
		return new FtpTransfer(d,t,rl,ll,rp,lp);
	    if (rp.getname().toLowerCase().equals("http")) 
		return new HttpTransfer(d,t,rl,ll,rp,lp); 
	    if (rp.getname().toLowerCase().equals("dummy")) 
		return new DummyTransfer(d,t,rl,ll,rp,lp); 
	    if (rp.getname().toLowerCase().equals("scp"))
		return new ScpTransfer(d,t,rl,ll,rp,lp);
	} else  if (rp.getname().toLowerCase().equals("local")) {
	    if (lp.getname().toLowerCase().equals("ftp")) 
		return new FtpTransfer(d,t,rl,ll,rp,lp);
	    if (lp.getname().toLowerCase().equals("http")) 
		return new HttpTransfer(d,t,rl,ll,rp,lp); 
	    if (rp.getname().toLowerCase().equals("dummy")) 
		return new DummyTransfer(d,t,rl,ll,rp,lp); 
	}
	throw new OOBException("Unable to find the correct OOB Transfers for transfer : " + t.getuid() + "[rl:" + lp.getname() + "|" + "rp:" + lp.getname() + "]"  );
    }
    
    /**
     * <code>persistOOBTransfer</code> persists an OOBTransfer to the database
     *
     * @param t a <code>Transfer</code> value
     */
    public static void persistOOBTransfer(OOBTransfer t) {
	t.persist();
    }

}
