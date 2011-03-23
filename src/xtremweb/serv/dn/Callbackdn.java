package xtremweb.serv.dn;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import xtremweb.core.com.idl.CallbackTemplate;
import xtremweb.core.db.DBInterfaceFactory;
import xtremweb.core.iface.InterfaceRMIdn;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dn.Service;

/**
 * Domain Naming service, this service store information concerning other
 * services, and allows for querying currently it stores its ip address.
 * 
 * @author jsaray
 * 
 */
public class Callbackdn extends CallbackTemplate implements InterfaceRMIdn {

	/**
	 * Log
	 */
	protected static Logger log = LoggerFactory.getLogger(Callbackdn.class);

	/**
	 * This method returns the ip address where a given service on desktop grid
	 * run
	 * 
	 * @param serviceName
	 *            the name of the service ex. ds dn dt dr
	 * @return the machine's ip where the service marked as 'serviceName' run
	 * @throws RemoteException
	 */
	public String getServiceAddress(String serviceName) throws RemoteException {
		log.debug("enter into get service ");
		String str = "";
		PersistenceManagerFactory pmf = DBInterfaceFactory
				.getPersistenceManagerFactory();
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		tx.begin();
		Query query = pm.newQuery(xtremweb.core.obj.dn.Service.class,
				"service == '" + serviceName + "'");
		Collection result = ((Collection) query.execute());
		Iterator iter = result.iterator();
		while (iter.hasNext()) {
			Service s = (Service) iter.next();
			str = s.getbundle();
		}
		tx.commit();
		return str;
	}

	/**
	 * register a service and its ip address on the desktop grid
	 * 
	 * @param serviceName
	 *            the name of the service i.e ds, dt, dr, dc
	 * @param hostBundle
	 *            how we get the service (actually this means ip address)
	 * @throws RemoteException
	 *             if any goes wrong on rmi infrastructure
	 */
	public void registerService(String serviceName, String hostBundle)
			throws RemoteException {
		log.debug("enter into register service ");
		PersistenceManagerFactory pmf = DBInterfaceFactory
				.getPersistenceManagerFactory();
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		tx.begin();
		Service s = new Service();
		s.setservice(serviceName);
		s.setbundle(hostBundle);
		pm.makePersistent(s);
		tx.commit();
		log.debug("service succesfully registered");
	}
}
