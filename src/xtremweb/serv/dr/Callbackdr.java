package xtremweb.serv.dr;

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
import java.net.InetAddress;
import xtremweb.core.com.idl.*;
import xtremweb.core.iface.*;
import xtremweb.core.obj.dc.*;
import xtremweb.core.obj.dr.*;
import xtremweb.serv.dc.*;
import xtremweb.core.conf.*;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.dao.DaoFactory;
import xtremweb.dao.protocol.DaoProtocol;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;



/**
 * This class represents a data repository, this is an abstraction to represent
 * the physical storage, where data resides and the protocols available to
 * access these data. This is performed by implementing a RMI service.
 * 
 * @author josefrancisco
 * 
 */
public class Callbackdr extends CallbackTemplate implements Interfacedr {

    /**
     * Class logger
     */
    protected static Logger log = LoggerFactory.getLogger(Callbackdr.class);
    private DaoProtocol dao;
    private Properties mainprop;
    /**
     * Class constructor, it tries to load a set of protocols and repositories
     * from a json file indicated in System parameters if it is not possible it
     * takes a default json file.
     */
    public Callbackdr() {
    	dao = (DaoProtocol)DaoFactory.getInstance("xtremweb.dao.protocol.DaoProtocol");
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("Not able to find configuration protocols : " + ce);
	    mainprop = new Properties();
	}
	
	String temp = mainprop.getProperty("xtremweb.serv.dr.protocols");
	log.info(" The list of protocols to load " + temp);
	
	if (temp == null) {
	    log.debug(" temp is nulll !!!!! " + temp);
	    temp = "dummy http";
	}

	String[] protocols = temp.split(" ");
	for (int i = 0; i < protocols.length; i++) {
	    String protoName = protocols[i].toLowerCase();

	    try {
		Protocol protocol = getProtocolByName(protoName);
		if (protocol == null) {
		    protocol = new Protocol();
		    protocol.setname(protoName);
		    if (protoName.equals("ftp")) {
			log.debug("Setting FTP potocol from the configuration file "
				+ mainprop
					.getProperty("xtremweb.serv.dr.ftp.name"));
			protocol.setserver(mainprop.getProperty(
				"xtremweb.serv.dr.ftp.server", InetAddress
					.getLocalHost().getHostName()));
			log.debug("the protocol is !!!! " + mainprop.getProperty(
				"xtremweb.serv.dr.ftp.port", "21"));
			protocol.setport((Integer.valueOf(mainprop.getProperty(
				"xtremweb.serv.dr.ftp.port", "21"))).intValue());
			protocol.setlogin(mainprop.getProperty(
				"xtremweb.serv.dr.ftp.login", "anonymous"));
			protocol.setpassword(mainprop.getProperty(
				"xtremweb.serv.dr.ftp.passwd",
				"bush@whitehouse.gov"));
			protocol.setpath(mainprop.getProperty(
				"xtremweb.serv.dr.ftp.path", "pub/incoming"));
			protocol.setclassName(mainprop.getProperty("xtremweb.serv.dr.ftp.className",null));
			registerProtocol(protocol);
		    }
		    if (protoName.equals("http")) {
			log.debug("Setting HTTP protocol from the configuration file");
			// TODO: we should get this from the embedded web server
			// if it runs
			String defaultHost = "localhost";
			try {
			    InetAddress thisIp = InetAddress.getLocalHost();
			    defaultHost = thisIp.getHostAddress();
			} catch (Exception e) {
			    log.debug("cannot determine localhost ip address");
			}
			protocol.setserver(mainprop.getProperty(
				"xtremweb.serv.dr.http.server", defaultHost));
			protocol.setport((Integer.valueOf(mainprop.getProperty(
				"xtremweb.serv.dr.http.port", "8080")))
				.intValue());
			protocol.setpath(mainprop.getProperty(
				"xtremweb.serv.dr.http.path", "."));
			protocol.setclassName(mainprop.getProperty("xtremweb.serv.dr.http.className",null));
			registerProtocol(protocol);
		    }

		    if (protoName.equals("dummy")) {
			log.debug("Setting Dummy protocol from the configuration file");
			protocol.setpath(mainprop.getProperty(
				"xtremweb.serv.dr.dummy.path", "."));
			registerProtocol(protocol);
		    }
		    if (protoName.equals("s3")){
			protocol.setlogin(mainprop.getProperty("xtremweb.serv.dr.s3.key"));
			protocol.setpassword(mainprop.getProperty("xtremweb.serv.dr.s3.key"));
			protocol.setpath(mainprop.getProperty("xtremweb.serv.dr.s3.bucketName"));
			protocol.setclassName(mainprop.getProperty("xtremweb.serv.dr.s3.className",null));
			registerProtocol(protocol);
		    } if (protoName.equals("dropbox")){
			protocol.setlogin(mainprop.getProperty("xtremweb.serv.dr.dropbox.key"));
			protocol.setpassword(mainprop.getProperty("xtremweb.serv.dr.dropbox.secret"));
			protocol.setclassName(mainprop.getProperty("xtremweb.serv.dr.dropbox.className",null));
			registerProtocol(protocol);
		    }
		   
		    if (protoName.equals("scp")) {
			log.debug("Setting scp protocol from the configuration file");
			protocol.setpassphrase(mainprop.getProperty(
				"xtremweb.serv.dr.scp.passphrase", ""));
			protocol.setprivatekeypath(mainprop.getProperty(
				"xtremweb.serv.dr.scp.prkeypath", null));
			protocol.setknownhosts(mainprop.getProperty(
				"xtremweb.serv.dr.scp.knownhosts", null));
			protocol.setlogin(mainprop.getProperty(
				"xtremweb.serv.dr.scp.login", null));
			protocol.setserver(mainprop.getProperty(
				"xtremweb.serv.dr.scp.server", InetAddress
					.getLocalHost().getHostName()));
			protocol.setpassword(mainprop.getProperty(
				"xtremweb.serv.dr.scp.key", null));
			protocol.setpath(mainprop.getProperty(
				"xtremweb.serv.dr.scp.path", null));
			protocol.setport(Integer.parseInt(mainprop.getProperty(
				"xtremweb.serv.dr.scp.port", "22")));
			protocol.setclassName(mainprop.getProperty("xtremweb.serv.dr.scp.className",null));
			registerProtocol(protocol);
		    }
		    if (protoName.equals("bittorrent")) {
			log.debug("Setting Bittorrent protocol from the configuration file");
			protocol.setserver(mainprop.getProperty("xtremweb.serv.dr.bittorrent.server"));
			protocol.setclassName(mainprop.getProperty("xtremweb.serv.dr.bittorrent.className",null));
			registerProtocol(protocol);
		    }
		}
	    } catch (RemoteException re) {
		log.warn("unable to record standard protocol");
	    } catch (java.net.UnknownHostException uhe) {
		log.fatal("There was a problem initializing dr ");
	    }
	}
	log.info("Registred Protocols : ");
	log.info(browse());

    } // Callbackobj constructor

    private String parseProtocols() {
	String res ="";
	Set<Map.Entry<Object,Object>> keys = mainprop.entrySet();
	Iterator it = keys.iterator();
	while(it.hasNext())
	{
	    Map.Entry<String,String> key = (Map.Entry<String,String>)it.next();
	    String keystr = key.getKey();
	    if(keystr.contains("xtremweb.serv.dr")){
		System.out.println("entro " + keystr);
		String[] lengo = keystr.split("\\.");
	    	res = lengo[lengo.length -1]+ " ";
	    }
	}
	
	return res;
    }

    /**
     * This method is doing nothing and is a good idea to erase it
     */
    public String getRef(String datauid) throws RemoteException {
	return datauid;
    }

    /**
     * This method register a protocol in a DBMS and returns the protocol uid
     * 
     * @param proto
     *            the protocol to insert
     * @return the protocol uid in DBMS
     */
    public String registerProtocol(Protocol proto) {

	dao.makePersistent(proto, true);
	return proto.getuid();
    }

    /**
     * Retrieves a protocol according to its name
     * 
     * @param name
     *            protocol name
     * @return the protocol whose name is the parameter
     */
    public Protocol getProtocolByName(String name) throws RemoteException {
	Protocol ret = null;
	Protocol protoStored = null;
	try {
	    dao.beginTransaction();
	    protoStored = (Protocol) dao.getByName(Protocol.class,
		    name.toLowerCase());
	    if (protoStored == null)
		return null;
	    dao.commitTransaction();
	} finally {
	    if (dao.transactionIsActive())
		dao.transactionRollback();
	}
	return protoStored;
    }

    /**
     * Retrieves a protocol according to its uid
     * 
     * @param uid
     *            the protocol uid
     * @return the protocol whose uid is the input parameter
     */
    public Protocol getProtocolByUID(String uid) throws RemoteException {
	Protocol t = null;
	DaoProtocol daop = new DaoProtocol();
	try {
	    daop.beginTransaction();
	    t = (Protocol) daop.getByUid(Protocol.class, uid);
	    if (t == null) {
		log.debug(" proto fetched is null ");
	    } else {
		
		log.debug(" proto fetched " + t.getuid());
	    }
	    daop.commitTransaction();
	} finally {
	    if (daop.transactionIsActive())
		daop.transactionRollback();
	    
	}
	return t;
    }

    /**
     * This method is not called from the API and It should be erased
     * 
     * @param proto
     * @throws RemoteException
     */
    public void deleteProtocol(Protocol proto) throws RemoteException {
	/*PersistenceManager pm = DBInterfaceFactory
		.getPersistenceManagerFactory().getPersistenceManager();

	Transaction tx = pm.currentTransaction();
	try {
	    tx.begin();
	   
	    pm.makePersistent(proto);
	    pm.deletePersistent(proto);

	 
	    tx.commit();
	} finally {
	    if (tx.isActive())
		tx.rollback();
	    pm.close();
	}*/
    }

    /**
     * This method is never called from API and it should be erased
     * 
     * @param uid
     * @throws RemoteException
     */
    public void deleteProtocol(long uid) throws RemoteException {
	// deleteProtocol(proto.getuid());
    }

    /**
     * Helpful method to output protocol information
     * 
     * @param remote_protocol
     *            the protocol from which we want information
     * @return
     */
    private String protoToString(Protocol remote_protocol) {
	return remote_protocol.getname() + "://" + remote_protocol.getlogin()
		+ "@" + remote_protocol.getserver() + ":"
		+ remote_protocol.getport();
    }

    /**
     * Browse all the protocols stored on data repository
     * 
     * @return a string with all protocol information
     */
    public String browse() {
	String ret = "";
	
	try {
	    dao.beginTransaction();

	    Iterator iter = dao.getAll(Protocol.class).iterator();
	    while (iter.hasNext()) {
		Protocol proto = (Protocol) iter.next();
		ret += ("Protocol [" + dao.getObjectId(proto)
			+ "]   [" + protoToString(proto) + "]\n");
	    }
	    dao.commitTransaction();
	} finally {
	    if (dao.transactionIsActive())
		dao.transactionRollback();

	}
	return ret;
    }

    public static void main(String[] args) {
	Callbackdc dc = new Callbackdc();
	Callbackdr dr = new Callbackdr();
	Data data;
	Protocol proto = new Protocol();
	long uid = 0;

	try {
	    // data creation
	    File file = new File("Makefile");
	    data = DataUtil.fileToData(file);

	    System.out.println("Data created : " + DataUtil.toString(data));
	    dc.putData(data);

	    proto = dr.getProtocolByName("FTP");
	    if (proto == null) {
		// proto.setuid(new UID().getLong());
		proto.setname("FTP");
		proto.setport(22);
		dr.registerProtocol(proto);
	    } else {
		Callbackdr.log.debug("Protocol Registred : " + proto.getname()
			+ " " + proto.getport());
	    }

	} catch (Exception e) {
	    Callbackdr.log.warn("Ooups" + e);
	}
    }

} // Callbackobj
