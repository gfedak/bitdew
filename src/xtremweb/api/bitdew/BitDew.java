package xtremweb.api.bitdew;

/**
 * BitDew.java
 *
 *
 * Created: Thu Mar 23 11:36:22 2006
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

import xtremweb.serv.dc.*;
import xtremweb.core.iface.*;
import xtremweb.core.log.*;
import xtremweb.core.db.*;
import xtremweb.core.com.com.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.ds.Attribute;

import xtremweb.serv.dt.*;
import xtremweb.serv.dt.ftp.*;
import xtremweb.serv.dc.ddc.*;
import xtremweb.serv.dc.*;
import xtremweb.serv.ds.*;

import xtremweb.api.transman.*;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Vector;

public class BitDew {

    public static Logger log = LoggerFactory.getLogger(BitDew.class);
    
    protected InterfaceRMIdc idc;
    protected InterfaceRMIdr idr;
    protected InterfaceRMIdt idt;
    protected InterfaceRMIds ids;

    protected DistributedDataCatalog ddc = null;
    protected String myHost="test_that_dude";

    public BitDew(Vector comms) {

	for (Object o : comms) {
	    if (o instanceof InterfaceRMIdc) idc = (InterfaceRMIdc) o;
	    if (o instanceof InterfaceRMIdr) idr = (InterfaceRMIdr) o;
	    if (o instanceof InterfaceRMIdt) idt = (InterfaceRMIdt) o;
	    if (o instanceof InterfaceRMIds) ids = (InterfaceRMIds) o;
	}
	init();
    }

    public BitDew(InterfaceRMIdc cdc, InterfaceRMIdr cdr, InterfaceRMIdt cdt, InterfaceRMIds cds) {
	idc = cdc;
	idr = cdr;
	idt = cdt;
	ids = cds;

	init();
    } // BitDew constructor

    public void init() {

	try {
	    ddc = DistributedDataCatalogFactory.getDistributedDataCatalog();
	    String entryPoint = idc.getDDCEntryPoint();
	    if (entryPoint != null) {
		ddc.join(entryPoint);
		log.info("Started DHT service for distributed data catalog [entryPoint:" + entryPoint + "]");
	    }
	} catch (Exception ddce) {
	    log.warn("unable to start a Distributed Data Catalog service");
	    ddc = null;		
	}
	//	TransferManagerFactory.init(idr, idt);
	
    }

    public Data createData() throws BitDewException {
	try {
	    Data data = new Data();
	    DBInterfaceFactory.getDBInterface().makePersistent(data);
	    idc.putData(data);  
	    return data;

	} catch (RemoteException re) {
	    log.debug("Cannot find service " + re);
	}
	throw new BitDewException();
    }

    public Data createData(String name) throws BitDewException {
	try {
	    Data data = new Data();
	    data.setname(name);
	    DBInterfaceFactory.getDBInterface().makePersistent(data);
	    idc.putData(data);  
	    return data;

	} catch (RemoteException re) {
	    log.debug("Cannot find service " + re);
	}
	throw new BitDewException();
    }


    public void putData(Data data) throws BitDewException {
	//if data has not been locally serialized, do it now
	if (data.getuid() == null) 
	    DBInterfaceFactory.getDBInterface().makePersistent(data);
	try {
	    idc.putData(data);  
	} catch (RemoteException re) {
	    log.debug("Cannot find service " + re);
	    throw new BitDewException();
	}
    }


    public String ddcSearch( Data data) throws BitDewException {
	try {
	    if (ddc !=null ) 
		return ddc.search(data.getuid());
	} catch (DDCException ddce ) {
	    log.debug("cannot ddc find data : " + data + "\n" + ddce);
	}
	throw new BitDewException();
    }

    public void ddcPublish( Data data, String hostid) throws BitDewException {
	try {
	    if (ddc !=null ) 
		ddc.publish(data.getuid(), hostid);
	    return;
	} catch (DDCException ddce) {
	    log.debug("cannot ddc publish [data|hostid] : [" + data.getuid() + "|" + hostid + "]"  + "\n" + ddce);
	}
	throw new BitDewException();
    }

    public void ddcPublish( String key, String value) throws BitDewException {
	try {
	    if (ddc !=null ) 
		ddc.publish(key, value);
	    return;
	} catch (DDCException ddce) {
	    log.debug("cannot ddc publish [data|hostid] : [" + key + "|" + value + "]"  + "\n" + ddce);
	}
	throw new BitDewException();
    }

    public void cleanup() {
	//TODO
    }

    public void barrier()  throws BitDewException {
	try {
	    TransferManager transman = TransferManagerFactory.getTransferManager(idr, idt);
	    transman.barrier();
	} catch (Exception e) {
	    log.debug("cannot execute the barrier" + e);
	    throw new BitDewException("Unexpexted breeak of the barrier");  
	}
    }

    public Data createData(String name, String protocol, int size)  throws BitDewException {
	try {
	    Data data = new Data();
	    data.setname(name);
	    data.setoob(protocol);
	    data.setsize(size);
	    DBInterfaceFactory.getDBInterface().makePersistent(data);
	    idc.putData(data);  
	    return data;

	} catch (RemoteException re) {
	    log.debug("Cannot find service " + re);
	}
	throw new BitDewException();
    } 

    public Locator createLocator(String ref)  throws BitDewException {
	try {
	    Locator locator = new Locator();
	    locator.setref(ref);
	    DBInterfaceFactory.getDBInterface().makePersistent(locator);
	    return locator;
	} catch (Exception re) {
	    log.debug("Cannot createLocator " + re);
	}
	throw new BitDewException();
    } 

    public void putLocator(Locator loc) throws BitDewException {
	try {
	    idc.putLocator(loc);
	    log.debug (" created locator " + loc.getuid());
	} catch (RemoteException re) {
	    log.debug("Cannot find service " + re);
	} catch (Exception e) {
	    log.debug("Error creating data " + e);
	}
	throw new BitDewException();
    }

    public Data createData(File file) throws BitDewException {
	Data data = DataUtil.fileToData(file);

	try {
	    DBInterfaceFactory.getDBInterface().makePersistent(data);
	    idc.putData(data);
	    log.debug ("uid = " + DataUtil.toString(data));
	    return data;
	} catch (RemoteException re) {
	    log.debug("Cannot find service " + re);
	} catch (Exception e) {
	    log.debug("Error creating data " + e);
	}
	throw new BitDewException();
    }
   

    //convenience method to register a data already present somewhere
    public void put(Data data, Locator remote_locator) throws BitDewException {
	Protocol remote_proto;

	try {
	    if (remote_locator.getuid() == null)
		DBInterfaceFactory.getDBInterface().makePersistent(remote_locator);
	    remote_proto = idr.getProtocolByName(data.getoob());

	    log.debug("Remote_proto fetched : " + remote_proto.getuid() + " : " +remote_proto.getname() +"://" + remote_proto.getlogin() + ":" +  remote_proto.getpassword() +  "@" + ((CommRMITemplate) idr).getHostName() + ":" +  remote_proto.getport() +"/" + remote_proto.getpath() );
	} catch (RemoteException re) {
	    log.debug("Cannot find a oob protocol " + data.getoob() + " " + re);
	    throw new BitDewException();
	}

	remote_locator.setdatauid(data.getuid());
	remote_locator.setdrname(((CommRMITemplate) idr).getHostName());
	remote_locator.setprotocoluid(remote_proto.getuid());	

	try {
	    idc.putLocator(remote_locator);
	    log.debug("registred new locator");
	} catch (RemoteException re) {
	    log.debug("Cannot register locator " + re);
	    throw new BitDewException();
	}
    }

    public void put(Data data, File f) throws BitDewException {
	
	// No local protocol
	Protocol local_proto = new Protocol();
	local_proto.setname("local");

	Locator local_locator = new Locator();
	local_locator.setdatauid(data.getuid());
	//	local_locator.setdrname("localhost");
	//	local_locator.setprotocoluid(local_proto.getuid());
	local_locator.setref(f.getAbsolutePath());
	
	log.debug("Local Locator : " + f.getAbsolutePath());
	Protocol remote_proto;

	// set the default protocol to FTP if there is no
	if (data.getoob() == null)
	    data.setoob("dummy");

	try {
	    remote_proto = idr.getProtocolByName(data.getoob());

	    log.debug("Remote_proto fetched : " + remote_proto.getuid() + " : " +remote_proto.getname() +"://" + remote_proto.getlogin() + ":" +  remote_proto.getpassword() +  "@" + ((CommRMITemplate) idr).getHostName() + ":" +  remote_proto.getport() +"/" + remote_proto.getpath() );
	} catch (RemoteException re) {
	    log.debug("Cannot find a oob protocol " + data.getoob() + " " + re);
	    throw new BitDewException();
	}

	Locator remote_locator = new Locator();
	remote_locator.setdatauid(data.getuid());
	remote_locator.setdrname(((CommRMITemplate) idr).getHostName());
	remote_locator.setprotocoluid(remote_proto.getuid());

	try {
	    remote_locator.setref( idr.getRef("" +data.getuid()) );
	    log.debug("Remote_reference fetched : " + remote_locator.getref());
	} catch (RemoteException re) {
	    log.debug("Cannot find a protocol ftp " + re);
	    throw new BitDewException();
	}

	//prepare
	Transfer t = new Transfer();
    	t.setlocatorremote(remote_locator.getuid());
	t.settype(TransferType.UNICAST_SEND_SENDER_SIDE);
    //t.setlocatorlocal(local_locator.getuid());
	//	Data data = DataUtil.fileToData(file);
	OOBTransfer oobTransfer;
	try {
	    oobTransfer = OOBTransferFactory.createOOBTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);
	    TransferManager transman = TransferManagerFactory.getTransferManager(idr, idt);
	    transman.registerTransfer(t.getuid(), oobTransfer);
	    log.debug("Succesfully created OOB transfer " + oobTransfer);
	} catch(OOBException oobe) {
	   log.debug("Error when creating OOBTransfer " + oobe);
	   throw new BitDewException("Error when transfering data to ftp server : " + remote_proto.getname() +"://" + remote_proto.getlogin() + ":" +  remote_proto.getpassword() +  "@" + ((CommRMITemplate) idr).getHostName() + ":" +  remote_proto.getport() +"/" + remote_proto.getpath() + "/" + remote_locator.getref() );
	}
	//FIXME cannot assume that the data has been fully copied now.
	// should put a status to the locator ????
	//no, data has now status LOCK and UNLOCK
	try {
	    idc.putLocator(remote_locator);
	    log.debug("registred new locator");
	} catch (RemoteException re) {
	    log.debug("Cannot register locator " + re);
	    throw new BitDewException();
	}

	log.debug("Succesfully created data [" + data.getuid()+ "] with remote storage [" + remote_locator.getref()  + "] " + remote_proto.getname() +"://[" + remote_proto.getlogin() + ":" +  remote_proto.getpassword() +  "]@" + ((CommRMITemplate) idr).getHostName() + ":" +  remote_proto.getport() +"/" + remote_proto.getpath() + "/" + remote_locator.getref() );
    }


    public void transfert(Transfer transfer) throws BitDewException {
	

    }

    public void get(Data data, File f) throws BitDewException {

	// No local protocol
	Protocol local_proto = new Protocol();
	local_proto.setname("local");

	Locator local_locator = new Locator();
	local_locator.setdatauid(data.getuid());
	//local_locator.setdrname("localhost");
	//	local_locator.setprotocoluid(local_proto.getuid());
	local_locator.setref(f.getAbsolutePath());
	
	log.debug("Local Locator : " + f.getAbsolutePath());

	// get an FTP remote protocol
	Locator remote_locator = null;

	// set the default protocol to FTP if there is no
	if (data.getoob() == null)
	    data.setoob("FTP");

	try {
	    remote_locator = ( idc.getLocatorByDataUID( data.getuid() ));
	    if (remote_locator==null) throw new BitDewException("Cannot retreive locator for data uid: " + data.getuid());
	    log.debug("Remote_reference fetched : " + remote_locator.getref() + " and protocol " + remote_locator.getprotocoluid() + "@" + remote_locator.getdrname() );
	} catch (RemoteException re) {
	    log.debug("Cannot find a locator associated with data " + data.getuid() + " " + re);
	    throw new BitDewException();
	}

	Protocol remote_proto;

	try {
	     remote_proto = idr.getProtocolByUID(remote_locator.getprotocoluid());
	     log.debug("Remote_proto fetched : " + remote_proto.getuid() + " : " +remote_proto.getname() +"://" + remote_proto.getlogin() + ":" +  remote_proto.getpassword() +  "@" + ((CommRMITemplate) idr).getHostName() + ":" +  remote_proto.getport() +"/" + remote_proto.getpath() );
	} catch (RemoteException re) {
	    log.debug("Cannot find a protocol oob " + re);
	    throw new BitDewException();
	}

	//prepar
	Transfer t = new Transfer();
    	t.setlocatorremote(remote_locator.getuid());
	t.settype(TransferType.UNICAST_RECEIVE_RECEIVER_SIDE);
	//      t.setlocatorlocal(local_locator.getuid());
	//	Data data = DataUtil.fileToData(file);
	
	try {
	    OOBTransfer oobTransfer = OOBTransferFactory.createOOBTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);
	    TransferManager transman = TransferManagerFactory.getTransferManager(idr, idt);
	    transman.registerTransfer(t.getuid(), oobTransfer);
	    log.debug("Succesfully created OOB transfer " + oobTransfer);
	    /*	    oobTransfer.connect();
	    oobTransfer.receiveReceiverSide();
	    oobTransfer.waitFor();
	    oobTransfer.disconnect();
	    */
	} catch(OOBException oobe) {
	   log.debug("Was not able to transfer " + oobe);
	   throw new BitDewException("Error when transfering data from : " + remote_proto.getname() +"://" + remote_proto.getlogin() + ":" +  remote_proto.getpassword() +  "@" + ((CommRMITemplate) idr).getHostName() + ":" +  remote_proto.getport() +"/" + remote_proto.getpath() + "/" + remote_locator.getref() );
	}
	
	log.debug("Succesfully retreived data [" + data.getuid()+ "] to local storage [" + local_locator.getref()  + "] " + remote_proto.getname() +"://[" + remote_proto.getlogin() + ":" +  remote_proto.getpassword() +  "]@" + ((CommRMITemplate) idr).getHostName() + ":" +  remote_proto.getport() +"/" + remote_proto.getpath() + "/" + remote_locator.getref() );
    }




    public static void main(String [] args) {
	try {
	    InterfaceRMIdc cdc = (InterfaceRMIdc) ComWorld.getComm( "localhost", "rmi", 4322, "dc" );
	    InterfaceRMIdr cdr = (InterfaceRMIdr) ComWorld.getComm( "localhost", "rmi", 4322, "dr" );
	    InterfaceRMIdt cdt = (InterfaceRMIdt) ComWorld.getComm( "localhost", "rmi", 4322, "dt" );
	    InterfaceRMIds cds = (InterfaceRMIds) ComWorld.getComm( "localhost", "rmi", 4322, "ds" );

	    BitDew bitdew = new BitDew( cdc, cdr, cdt, cds );

	    Data data = bitdew.createData();
	    BitDew.log.info("created data " + data.getuid());
	} catch(ModuleLoaderException e) {
	    BitDew.log.warn("Cannot find service " +e);
	} catch (BitDewException bde) {
	    BitDew.log.warn("Oups : " + bde);
	}
    }
}
    // BitDew
