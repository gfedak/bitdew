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
import xtremweb.core.com.idl.*;
import xtremweb.core.iface.*;
import xtremweb.core.db.*;
import xtremweb.core.obj.dc.*;
import xtremweb.core.obj.dr.*;
import xtremweb.serv.dc.*;
import xtremweb.core.uid.*;
import xtremweb.core.conf.*;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

import java.util.*;
import java.io.File;

import java.util.Collection;
import java.util.Iterator;
import javax.jdo.PersistenceManager;
import javax.jdo.Extent;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.Properties;

public class Callbackdr extends CallbackTemplate implements InterfaceRMIdr{
   
    protected static Logger log = LoggerFactory.getLogger(Callbackdr.class);

    protected Protocol default_ftp_protocol;
    protected Protocol default_http_protocol;
    protected Protocol default_dummy_protocol;
    protected Protocol default_bittorrent_protocol;

    public Callbackdr() {
	Properties mainprop;
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No FTP Protocol Information found : " + ce);
	    mainprop = new Properties();
	}

	try {
	    Protocol protocol = getProtocolByName("FTP");
	    if (protocol != null) {
		default_ftp_protocol = protocol;
	    } else {
		if (mainprop.getProperty("xtremweb.serv.dr.ftp.name").equals("FTP")) {
		    log.debug("Setting FTP potocol from the configuration file " + mainprop.getProperty("xtremweb.serv.dr.ftp.name") );
		    default_ftp_protocol = new Protocol();
		    //yes we overwrite it ;)
		    default_ftp_protocol.setname("FTP");
		    default_ftp_protocol.setserver(mainprop.getProperty("xtremweb.serv.dr.ftp.server","localhost"));
		    default_ftp_protocol.setport((Integer.valueOf(mainprop.getProperty("xtremweb.serv.dr.ftp.port", "21"))).intValue());
		    default_ftp_protocol.setlogin(mainprop.getProperty("xtremweb.serv.dr.ftp.login","anonymous"));
		    default_ftp_protocol.setpassword(mainprop.getProperty("xtremweb.serv.dr.ftp.passwd","bush@whitehouse.gov"));
		    default_ftp_protocol.setpath(mainprop.getProperty("xtremweb.serv.dr.ftp.path","pub/incoming"));
		    registerProtocol(default_ftp_protocol);
		}
	    }
	    log.info("Registred Protocol : ");


	    protocol = getProtocolByName("HTTP");
	    if (protocol != null) {
		default_http_protocol = protocol;
	    } else {
		if (mainprop.getProperty("xtremweb.serv.dr.http.name").equals("HTTP")) {
		    log.debug("Setting HTTP potocol from the configuration file");
		    default_http_protocol = new Protocol();
		    //yes we overwrite it ;)
		    default_http_protocol.setname("HTTP");
		    default_http_protocol.setserver(mainprop.getProperty("xtremweb.serv.dr.http.server","localhost"));
		    default_http_protocol.setport((Integer.valueOf(mainprop.getProperty("xtremweb.serv.dr.http.port", "8080"))).intValue());
		    default_http_protocol.setpath(mainprop.getProperty("xtremweb.serv.dt.http.path","."));
		    registerProtocol(default_http_protocol);
		}
	    }

	    protocol = getProtocolByName("Dummy");
	    if (protocol != null) {
		default_dummy_protocol = protocol;
	    } else {
		if (mainprop.getProperty("xtremweb.serv.dr.dummy.name").equals("Dummy")) {
		    log.debug("Setting Dummy potocol from the configuration file");
		    default_dummy_protocol = new Protocol();
		    //yes we overwrite it ;)
		    default_dummy_protocol.setname("Dummy");
		    default_dummy_protocol.setpath(mainprop.getProperty("xtremweb.serv.dr.dummy.path","."));
		    registerProtocol(default_dummy_protocol);
		}
	    }

	    protocol = getProtocolByName("Bittorrent");
	    if (protocol != null) {
		default_bittorrent_protocol = protocol;
	    } else {
		if (mainprop.getProperty("xtremweb.serv.dr.bittorrent.name").equals("Bittorrent")) {
		    log.debug("Setting Bittorrent potocol from the configuration file");
		    default_bittorrent_protocol = new Protocol();
		    //yes we overwrite it ;)
		    default_bittorrent_protocol.setname("Bittorrent");
		    default_bittorrent_protocol.setpath(mainprop.getProperty("xtremweb.serv.dr.bittorrent.path","torrent"));
		    default_http_protocol.setport(Integer.getInteger(mainprop.getProperty("xtremweb.serv.dr.bittorrent.port"),6969).intValue());
		    registerProtocol(default_bittorrent_protocol);
		}
	    }


	    log.info("Registred Protocol : ");
	    log.info( browse() );
	} catch (RemoteException re){
	    log.warn("unable to record standard protocol");	    
	}
	
    } // Callbackobj constructor
    
    public void sendData( long datauid, long protocol) throws RemoteException {
	
    }    
    //THAT'S UGLY TO
    public String  getRef(String datauid) throws RemoteException {
	return datauid;
    }

    public void registerProtocol(Protocol proto) {
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();

	    pm.makePersistent(proto);
	    
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}	
    }
    
    //FIXME THAT'S UGLY !!!!!
    public Protocol getProtocolByName(String name)  throws RemoteException{
	Protocol ret = null;
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();
            Extent e=pm.getExtent(Protocol.class,true);
            Iterator iter=e.iterator();
            while (iter.hasNext())
            {
                Protocol proto = (Protocol) iter.next();
		if (proto.getname().toLowerCase().equals(name.toLowerCase())) ret= (Protocol) pm.detachCopy(proto);
            }
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}	
	return ret;
    }

    //FIXME THAT'S UGLY !!!!!
    public Protocol getProtocolByUID(String uid)  throws RemoteException{
	Protocol ret = null;
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();
	    Query query = pm.newQuery(xtremweb.core.obj.dr.Protocol.class, 
				      "uid == \"" + uid + "\"");
	    query.setUnique(true);
	    Protocol t = (Protocol) query.execute();
	    if (t==null) {
		log.debug (" proto fetched is null ");
	    } else {
		ret = (Protocol) pm.detachCopy(t);
		log.debug (" proto fetched " + t.getuid());
	    }
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}
	return ret;
    }

        
    public void deleteProtocol(Protocol proto)  throws RemoteException{
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();

	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();
	    //	    Object id = pm.getObjectId(proto);
	    //Protocol obj = pm.getObjectById(id);
	    pm.makePersistent(proto);
	    pm.deletePersistent(proto);

	    //	    Protocol todelete = pm.getObjectById(uid);  // Retrieves the object to delete
	    //	    pm.deletePersistent(todelete);
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}	
    }
    
    public void deleteProtocol(long uid)  throws RemoteException {
	//	deleteProtocol(proto.getuid());
    }

    private String protoToString(Protocol remote_protocol) {
	return remote_protocol.getname() + "://" + remote_protocol.getlogin() + ":(" +  remote_protocol.getpassword() +  ")@" + remote_protocol.getserver() + ":" +  remote_protocol.getport();
    }

    public String browse() {
	String ret = "";
	PersistenceManager pm = DBInterfaceFactory.getPersistenceManagerFactory().getPersistenceManager();
	Transaction tx=pm.currentTransaction();
	try {
	    tx.begin();
            Extent e=pm.getExtent(Protocol.class,true);
            Iterator iter=e.iterator();
            while (iter.hasNext())
            {
                Protocol proto = (Protocol) iter.next();
		ret+=( "Protocol [" + pm.getObjectId(proto) + "]   [" + protoToString(proto) +"]\n" );
            }
	    tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
	}	
	return ret;
    }
    


    public static void main(String [] args) {
	Callbackdc dc = new Callbackdc();
	Callbackdr dr = new Callbackdr();
	Data data;
	Protocol proto = new Protocol();
	long uid=0;
	
	try {
	    // data creation
	    File file = new File("Makefile");
	    data = DataUtil.fileToData(file);

	    System.out.println("Data created : " + DataUtil.toString(data));
	    dc.putData(data);
	    
	    proto = dr.getProtocolByName("FTP");
	    if (proto==null) {
		//		proto.setuid(new UID().getLong());
		proto.setname("FTP");
		proto.setport(22);
		dr.registerProtocol(proto);
	    } else {
		Callbackdr.log.debug("Protocol Registred : " + proto.getname() + " " + proto.getport());
	    }
	    
	    

	} catch(Exception e) {
	    Callbackdr.log.warn("Ooups" +e);
	}
    }

} // Callbackobj
