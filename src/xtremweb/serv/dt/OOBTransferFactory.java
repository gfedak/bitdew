package xtremweb.serv.dt;

import xtremweb.core.log.*;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.dao.DaoFactory;
import xtremweb.dao.DaoJDOImpl;
//FIXME
import xtremweb.api.transman.TransferType;

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
	
	/**
	 * Class logger
	 */
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
	
	DaoJDOImpl dao  =  (DaoJDOImpl)DaoFactory.getInstance("xtremweb.dao.DaoJDOImpl");
	try {
	    dao.beginTransaction();

	    ll = (Locator) dao.getByUid(xtremweb.core.obj.dc.Locator.class, t.getlocatorlocal());

	    rl = (Locator) dao.getByUid(xtremweb.core.obj.dc.Locator.class, t.getlocatorremote());

	    rp = (Protocol) dao.getByUid(xtremweb.core.obj.dr.Protocol.class, rl.getprotocoluid());

	    lp = (Protocol) dao.getByUid(xtremweb.core.obj.dr.Protocol.class, ll.getprotocoluid());
	    //FIXME to use transfet.getdatauid() instead	    
	    if (! ll.getdatauid().equals(rl.getdatauid()) )
		throw new OOBException("O-O-B Transfers refers to two different data ");

	    d = (Data) dao.getByUid(xtremweb.core.obj.dc.Data.class, ll.getdatauid());
	    log.debug( "OOBTransferFactory create " + t.getuid() + ":" + t.getoob() + ":" + TransferType.toString(t.gettype()));
	    dao.commitTransaction();
	    return createOOBTransfer(d,t,rl,ll,rp,lp);
        } finally {
            if (dao.transactionIsActive())
                dao.transactionRollback();
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
	try {
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
	    return OOBTransferFactory.newInstance(rp.getclassName(),d,t,rl,ll,rp,lp);
	} else  if (rp.getname().toLowerCase().equals("local")) {
	    return OOBTransferFactory.newInstance(rp.getclassName(),d,t,rl,ll,rp,lp);
	}
	} catch (InstantiationException e) {
	     e.printStackTrace();
	     throw new OOBException("There was an error building the transfer");
	} catch (IllegalAccessException e) {		
		e.printStackTrace();
		throw new OOBException("There was an error building the transfer");
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
		throw new OOBException("There was an error building the transfer");
	}
	throw new OOBException("Unable to find the correct OOB Transfers for transfer : " + t.getuid() + "[rl:" + lp.getname() + "|" + "rp:" + lp.getname() + "]"  );
    }
    
    /**
     * This class builds a new transfer in runtime
     * @param clazz the complete class name we want to build
     * @param d the data associated
     * @param t transfer associated
     * @param remote_locator remote locator associated
     * @param local_locator local locator associated
     * @param remote_protocol remote protocol associated
     * @param local_protocol local protocol associated
     * @return a OOBTransfer of the specific class
     * @throws InstantiationException if anything goes wrong when attempting to build this class at runtime
     * @throws IllegalAccessException if anything goes wrong when attempting to build this class at runtime
     * @throws ClassNotFoundException if anything goes wrong when attempting to build this class at runtime
     */
    public static OOBTransfer newInstance(String clazz, Data d, Transfer t, Locator remote_locator, Locator local_locator, Protocol remote_protocol, Protocol local_protocol) throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
	OOBTransferImpl oob = (OOBTransferImpl)Class.forName(clazz).newInstance();
	oob.setData(d);
	oob.setTransfer(t);
	oob.setRemoteLocator(remote_locator);
	oob.setLocalLocator(local_locator);
	oob.setRemoteProtocol(remote_protocol);
	oob.setLocalProtocol(local_protocol);
	return oob;
	
    }

}
