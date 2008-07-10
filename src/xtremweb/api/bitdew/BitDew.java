package xtremweb.api.bitdew;

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

/**
 *  <code>BitDew</code> programming interface.
 *
 * @author <a href="mailto:fedak@dick">Gilles Fedak</a>
 * @version 1.0
 */
public class BitDew {

    private static Logger log = LoggerFactory.getLogger(BitDew.class);
    
    private InterfaceRMIdc idc;
    private InterfaceRMIdr idr;
    private InterfaceRMIdt idt;
    private InterfaceRMIds ids;

    private DistributedDataCatalog ddc = null;
    private String myHost="test_that_dude";

    /**
     * Creates a new <code>BitDew</code> instance.
     *
     * @param comms a <code>Vector</code> value
     */
    public BitDew(Vector comms) {

	for (Object o : comms) {
	    if (o instanceof InterfaceRMIdc) idc = (InterfaceRMIdc) o;
	    if (o instanceof InterfaceRMIdr) idr = (InterfaceRMIdr) o;
	    if (o instanceof InterfaceRMIdt) idt = (InterfaceRMIdt) o;
	    if (o instanceof InterfaceRMIds) ids = (InterfaceRMIds) o;
	}
	init();
    }

    /**
     * Creates a new <code>BitDew</code> instance.
     *
     * @param cdc an <code>InterfaceRMIdc</code> value
     * @param cdr an <code>InterfaceRMIdr</code> value
     * @param cdt an <code>InterfaceRMIdt</code> value
     * @param cds an <code>InterfaceRMIds</code> value
     */
    public BitDew(InterfaceRMIdc cdc, InterfaceRMIdr cdr, InterfaceRMIdt cdt, InterfaceRMIds cds) {
	idc = cdc;
	idr = cdr;
	idt = cdt;
	ids = cds;

	init();
    } // BitDew constructor


    private void init() {

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

    /**
     * <code>createData</code> creates Data.
     *
     * @return a <code>Data</code> value
     * @exception BitDewException if an error occurs
     */
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

    /**
     * <code>createData</code> creates Data.
     *
     * @param name a <code>String</code> value
     * @return a <code>Data</code> value
     * @exception BitDewException if an error occurs
     */
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


    /**
     * <code>createData</code> creates Data.
     *
     * @param name a <code>String</code> value
     * @param protocol a <code>String</code> value
     * @param size an <code>int</code> value
     * @return a <code>Data</code> value
     * @exception BitDewException if an error occurs
     */
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


    /**
     * <code>createData</code> creates Data from file.
     *
     * @param file a <code>File</code> value
     * @return a <code>Data</code> value
     * @exception BitDewException if an error occurs
     */
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


    private void putData(Data data) throws BitDewException {
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

    /**
     * <code>createLocator</code> creates  a new locator.
     *
     * @param ref a <code>String</code> value
     * @return a <code>Locator</code> value
     * @exception BitDewException if an error occurs
     */
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

    /**
     *  <code>putLocator</code> registers locator
     *
     * @param loc a <code>Locator</code> value
     * @exception BitDewException if an error occurs
     */
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

    /**
     * <code>put</code> convenience method to register a data already
     * present in a data repository without having to copy the data.
     *
     * @param data a <code>Data</code> value
     * @param remote_locator a <code>Locator</code> value
     * @exception BitDewException if an error occurs
     */
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


    /**
     * <code>put</code> file to a data
     *
     * @param file a <code>File</code> value, file to put
     * @param data a <code>Data</code> value, data into which to put the file
     * @param oob a <code>String</code> value, protocol to use to transfer the data
     * @exception BitDewException if an error occurs
     */
    public void put(File file, Data data, String oob) throws BitDewException {
	data.setoob(oob);
	put(file, data);
    }

    /**
     * <code>put</code> file to a data
     *
     * @param file a <code>File</code> value
     * @param data a <code>Data</code> value
     * @exception BitDewException if an error occurs
     */
    public void put(File file, Data data) throws BitDewException {
	
	// No local protocol
	Protocol local_proto = new Protocol();
	local_proto.setname("local");

	Locator local_locator = new Locator();
	local_locator.setdatauid(data.getuid());
	//	local_locator.setdrname("localhost");
	//	local_locator.setprotocoluid(local_proto.getuid());
	local_locator.setref(file.getAbsolutePath());
	
	log.debug("Local Locator : " + file.getAbsolutePath());
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

    /**
     * <code>get</code> data into file.
     *
     * @param data a <code>Data</code> value
     * @param file a <code>File</code> value
     * @exception BitDewException if an error occurs
     */
    public void get(Data data, File file) throws BitDewException {

	// No local protocol
	Protocol local_proto = new Protocol();
	local_proto.setname("local");

	Locator local_locator = new Locator();
	local_locator.setdatauid(data.getuid());
	//local_locator.setdrname("localhost");
	//	local_locator.setprotocoluid(local_proto.getuid());
	local_locator.setref(file.getAbsolutePath());
	
	log.debug("Local Locator : " + file.getAbsolutePath());

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


    /**
     *  <code>searchDataByUid</code> searches data in the central data catalog.
     *
     * @param dataUid a <code>String</code> value
     * @return a <code>Data</code> value
     * @exception BitDewException if an error occurs
     */

    public Data searchDataByUid(String dataUid) throws BitDewException {
	try {
	    return idc.getData(dataUid);
	} catch (RemoteException re ) {
	    log.debug("cannot find data : " + dataUid + " in DC\n" + re);
	}
	throw new BitDewException();
    }

    /**
     *  <code>ddcSearch</code> searches data in the distributed data catalog.
     *
     * @param data a <code>Data</code> value
     * @return a <code>String</code> value
     * @exception BitDewException if an error occurs
     */
    public String ddcSearch( Data data) throws BitDewException {
	try {
	    if (ddc !=null ) 
		return ddc.search(data.getuid());
	} catch (DDCException ddce ) {
	    log.debug("cannot ddc find data : " + data + "\n" + ddce);
	}
	throw new BitDewException();
    }

    /**
     * <code>ddcPublish</code> publishes data and host in the
     * distributed data catalog.
     *
     * @param data a <code>Data</code> value
     * @param hostid a <code>String</code> value
     * @exception BitDewException if an error occurs
     */
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

    /**
     *  <code>ddcPublish</code> publish arbitrary pair key value in
     *  the distributed data catalog
     *
     * @param key a <code>String</code> value
     * @param value a <code>String</code> value
     * @exception BitDewException if an error occurs
     */
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
}
    // BitDew
