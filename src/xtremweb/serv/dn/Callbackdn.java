package xtremweb.serv.dn;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import xtremweb.core.com.idl.CallbackTemplate;
import xtremweb.core.iface.Interfacedn;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dn.Service;
import xtremweb.dao.DaoFactory;
import xtremweb.dao.service.DaoService;

/**
 * Domain Naming service, this service store information concerning other
 * services, and allows for querying currently it stores its ip address.
 * 
 * @author jsaray
 * 
 */
public class Callbackdn extends CallbackTemplate implements Interfacedn {
    /*! \example Callbackdn.java 
     * <ol>
     * <li> Prerrequesites :
     *    <ol><li>Ant version 1.8.2 or later </li>
     *    <li> Java VM 1.6 or later </li>
     *    </ol>
     * </li>
     * <li> The following example develop a simple domain naming service such that, for a given service name dX, the service
     * answer the ip number of the stable node containing that service. The service uses a database table (SERVICE) to store the mappings <dX name, ip> </li>
     * <li> Download bitdew-sdk-XXX.zip</li>
     * 
     * <li> Unzip, cd into de directory and type, 
     * 
     * @code
     *    java -cp lib/bitdew-stand-alone-XXX.jar xtremweb.gen.service.GenService -s dnaming -o Service
     * @endcode
     * 
     * <li> Last command generate needed files to program a service called dnaming that will use the <a href=\"http://www.oracle.com/technetwork/java/index-jsp-135919.html\">jdo</a> persisten object Service
     * 
     * <ol> 
     *  <li>dnaming.idl</li>
     *  <li>package.jdo</li>
     *  <li>Callbackdnaming.java</li>
     * </ol>
     * 
     * <li> <b>dnaming.idl</b> is a xml file used to describe service method signatures and objects planned to be persistent. You must describe your method signatures here, 
     * you can also discretionally describe the persistent objects that your application will use if any. For our case we define service object, as we are going to
     * manipulate and persist them. You can copy-paste the next example file </li>
     * 
     * @code
     * <Module name="dnaming">
     *   <Object name="Service">
     *	   <Attribute name="uid" type="String" desc="Unique identifier"/>
     *	   <Attribute name="service" type="String" desc="Type of service, format dX"/>
     *	   <Attribute name="bundle" type="String" desc="either a host name of an IP address where the service resides"/>
     *   </Object>
     *   <Method name="registerService">
     *	    <Param name="serviceName" type="String"/>
     *	    <Param name="hostBundle" type="String"/>
     *	    <Return type="void"/>
     *   </Method>
     *   <Method name="getServiceAddress">
     *      <Param name="serviceName" type="String"/>	
     *      <Return type="String"/>
     *   </Method> 
     * </Module>
     * @endcode
     * 
     * <li> Now we generate all the files bitdew needs to load your service </li>
     * 
     * @code
     * 	  ant -lib lib/mtxslt-1.5.jar idl
     * @endcode
     * 
     * <li> The last command will generate several files, if you go to  xtremweb.core.obj.dnaming.Service, you will see the java-object, jdo-ready generated for your .idl</li>
     * 
     * <li> <b>package.jdo</b> is the file used in <a href=""/>JDO</a> to describe the maps between java objects and sql table fields. Copy paste the following xml just
     * below the uid field in the file package.jdo</li>
     * 
     * @code
     * <class name="Service" identity-type="application" table="SERVICE">
     *    <field name="uid" primary-key="true"  value-strategy="auid">
     *       <column name="UID"/>
     *    </field>
     *    <field name="service">
     *       <column name="SERVICE" />
     *    </field>
     *    <field name="bundle">
     *       <column name="BUNDLE" />
     *    </field>
     * </class>
     * @endcode
     * 
     * <li> In this file we are asking to map the service attribute of our jdp-ready java object in the SERVICE column on SERVICE table.</li>
     * 
     * <li> Copy paste, the following code inside the class Callbackdnaming.java </li>
     * @code
     * package xtremweb.serv.dnaming;
     * import java.rmi.RemoteException;
     * import java.util.Collection;
     * import java.util.Iterator;
     * import javax.jdo.PersistenceManager;
     * import javax.jdo.PersistenceManagerFactory;
     * import javax.jdo.Query;
     * import javax.jdo.Transaction;
     * import xtremweb.core.com.idl.CallbackTemplate;
     * import xtremweb.core.db.DBInterfaceFactory;
     * import xtremweb.core.iface.Interfacednaming;
     * import xtremweb.core.log.Logger;
     * import xtremweb.core.log.LoggerFactory;
     * import xtremweb.core.obj.dnaming.Service;
     * 
     * public class Callbackdnaming extends CallbackTemplate implements Interfacednaming {   
     *	  
     *	  public String getServiceAddress(String serviceName) throws RemoteException {
     *		
     *		String str = "";
     *		PersistenceManagerFactory pmf = DBInterfaceFactory
     *				.getPersistenceManagerFactory();
     *	 	PersistenceManager pm = pmf.getPersistenceManager();
     *		Transaction tx = pm.currentTransaction();
     *	        tx.begin();
     *	        Query query = pm.newQuery(xtremweb.core.obj.dnaming.Service.class,
     *			"service == '" + serviceName + "'");
     *	        Collection result = ((Collection) query.execute());
     *	        Iterator iter = result.iterator();
     *	        while (iter.hasNext()) {
     *		   Service s = (Service) iter.next();
     *		   str = s.getbundle();
     *	        }
     *	        tx.commit();
     *	        return str;
     *    }
     *
     *    public void registerService(String serviceName, String hostBundle) throws RemoteException {
     *	
     *	      PersistenceManagerFactory pmf = DBInterfaceFactory.getPersistenceManagerFactory();
     *	      PersistenceManager pm = pmf.getPersistenceManager();
     *	      Transaction tx = pm.currentTransaction();
     *	      tx.begin();
     *	      Service s = new Service();
     *	      s.setservice(serviceName);
     *	      s.setbundle(hostBundle);
     *	      pm.makePersistent(s);
     *	      tx.commit();
     *    }
     * }
     * @endcode
     * 
     * <li> Last code insert a service (registerService) and search for one (getServiceAddress) using jpox jdo implementation </li>
     * 
     * <li> Generate your service jar with </li>
     * 
     * @code
     * 	ant -lib lib/mtxslt-1.5.jar jar-sdk
     * @endcode
     * 
     * <li> Now You are ready to test your brand-new created service, execute
     * 
     * @code
     * java -cp lib/bitdew-stand-alone-0.2.5.jar:dist/myservice.jar xtremweb.role.cmdline.CommandLineTool serv dc dr dnaming
     * @endcode
     * 
     * <li> You should obtain something similar to </li>
     * 
     * @code
     *  java -cp lib/bitdew-stand-alone-0.2.5.jar:dist/myservice.jar xtremweb.role.cmdline.CommandLineTool serv dc dr dnaming
     *	Started DHT service for distributed data catalog
     *	ModuleLoader has registred callback: [dc]
     *	Using performance monitor on service call : true
     *	ModuleLoader has registred handler: [dc,RMI]
     *	ServiceLoader : module dc loaded
     *	Registred Protocols : 
     *  Protocol [6b37c850-66b0-31e0-8ede-df754235fb22]   [dummy://null@null:0]
     *  Protocol [6b3cd160-66b0-31e0-8ede-df754235fb22]   [http://null@10.211.55.3:8080]
     *  Protocol [6b3d94b0-66b0-31e0-8ede-df754235fb22]   [ftp://anonymous@ftp.lip6.fr:21]
     *  ModuleLoader has registred callback: [dr]
     *  ModuleLoader has registred handler: [dr,RMI]
     *  ServiceLoader : module dr loaded
     *  ModuleLoader has registred callback: [dnaming]
     *  ModuleLoader has registred handler: [dnaming,RMI]
     *  ServiceLoader : module dnaming loaded
     *  2011-04-14 18:01:12.146::INFO:  Logging to STDERR via org.mortbay.log.StdErrLog
     *  2011-04-14 18:01:12.286::INFO:  jetty-6.1.x
     *  2011-04-14 18:01:12.364::INFO:  Started SocketConnector @ 0.0.0.0:8080
     *  Http Server started serving files on . with http://localhost:8080//data and uploading files with the servlet http://localhost:8080//fileupload
     * @endcode
     * 
     * <li> You can see that in lines </li>
     * @code
     * ModuleLoader has registred callback: [dnaming]
     * ModuleLoader has registred handler: [dnaming,RMI]
       @endcode
     * 
     * <li> Bitdew has successfully recognized your service, in order to run a test of this service you can copy-paste the following class in a file called <em>CallbackTest.java</em> under the xtremweb.serv.dnaming directory : </li>
     * @code
     * package xtremweb.serv.dnaming;
     * import java.rmi.RemoteException;
     * import java.util.Collection;
     * import java.util.Iterator;
     * import javax.jdo.PersistenceManager;
     * import javax.jdo.PersistenceManagerFactory;
     * import javax.jdo.Query;
     * import javax.jdo.Transaction;
     * import xtremweb.core.com.idl.CallbackTemplate;
     * import xtremweb.core.db.DBInterfaceFactory;
     * import xtremweb.core.iface.Interfacednaming;
     * import xtremweb.core.log.Logger;
     * import xtremweb.core.log.LoggerFactory;
     * import xtremweb.core.obj.dnaming.Service;
     * import xtremweb.core.com.idl.ModuleLoaderException;
     * import xtremweb.core.com.idl.ComWorld;
     * public class CallbackTest {   
     *     public static void main(String[] pon){
     *         String ip1 ="11.11.11.11";
     *         String ip2 ="22.22.22.22";
     *         String ip3 = "33.33.33.33";
     *         try{
     *             String sname1 = "dr";
     *             String sname2 = "ds";
     *             String sname3 = "dt";
     *             Interfacednaming dn = (Interfacednaming) ComWorld.getComm("localhost", "rmi", 4325,"dnaming");
     *             dn.registerService(sname1, ip1);
     *             dn.registerService(sname2, ip2);
     *             dn.registerService(sname3, ip3);
     *             String host = dn.getServiceAddress("dt");
     *             System.out.println(" The host for dt is " + host);
     *             host = dn.getServiceAddress("ds");
     *             System.out.println("The host for ds is " + host);
     *             host = dn.getServiceAddress("dr");
     *             System.out.println("The host for dr is " + host);	
     *             }catch(RemoteException e){
     *                 e.printStackTrace();
     *             }catch(ModuleLoaderException e){
     *                 e.printStackTrace();
     *             }
     *         }
     * }
     * @endcode
     * <li>In other console run again </li> 
     * @code
     *     ant -lib lib/mtxslt-1.5.jar jar-sdk
     * @endcode
     * <li> Then execute the previously copied class :
     * @code
     *     java -cp lib/bitdew-stand-alone-0.2.5.jar:dist/myservice.jar xtremweb.serv.dnaming.CallbackTest
     * @endcode
     * </ol>
     */

	/**
	 * Log
	 */
	protected static Logger log = LoggerFactory.getLogger(Callbackdn.class);
	private DaoService dao;
	public Callbackdn(){
		dao = (DaoService)DaoFactory.getInstance("xtremweb.dao.service.DaoService");
	}
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
		dao.beginTransaction();
		Service name = dao.getServiceByName(serviceName);
		if(name==null)
			return null;
		dao.commitTransaction();
		return name.getbundle();
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
		
		Service s = new Service();
		s.setservice(serviceName);
		s.setbundle(hostBundle);
		dao.makePersistent(s,true);
		
		log.debug("service succesfully registered");
	}
}
