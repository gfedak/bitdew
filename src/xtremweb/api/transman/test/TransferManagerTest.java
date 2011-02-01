package xtremweb.api.transman.test;

import xtremweb.api.bitdew.*;
import xtremweb.api.transman.*;

import xtremweb.serv.dc.*;
import xtremweb.core.iface.*;
import xtremweb.core.log.*;
import xtremweb.core.com.com.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dt.Transfer;

import xtremweb.serv.dt.*;
import xtremweb.serv.dt.ftp.*;
import xtremweb.serv.dc.ddc.*;
import xtremweb.serv.dc.*;


import org.junit.Before; 
import org.junit.Ignore; 
import org.junit.Test; 
import junit.framework.*;
import static org.junit.Assert.*;


import java.io.File;
import java.rmi.RemoteException;

/**
 * Describe class TestTM here.
 *
 *
 * Created: Wed Feb 28 15:16:55 2007
 *
 * @author <a href="mailto:fedak@xtremciel.local">Gilles Fedak</a>
 * @version 1.0
 */
public class TransferManagerTest {

    Logger log = LoggerFactory.getLogger("TransferManagerTest");

    @Test public void start() {
	boolean test = true;
	assertTrue(test);
	log.info("Test Transfer Manager passed");
    }

    public static void main(String [] args) {
	Logger log = LoggerFactory.getLogger("TestTM");
	try {

	    TransferManager tm;
	    BitDew bitdew;
	    InterfaceRMIdc cdc;
	    InterfaceRMIdr cdr;
	    InterfaceRMIdt cdt;
	    InterfaceRMIds cds;
	    Data data;
	    
	    //We are going the scan the directory given by the first argument or the command line
	    String dirName = (args.length==1)?args[0]:".";	

	    log.debug("Dummy Uploading " + dirName );	

	    File file = new File(dirName);
	    	    
	    cdc = (InterfaceRMIdc) ComWorld.getComm( "localhost", "rmi", 4322, "dc" );
	    cdr = (InterfaceRMIdr) ComWorld.getComm( "localhost", "rmi", 4322, "dr" );
	    cdt = (InterfaceRMIdt) ComWorld.getComm( "localhost", "rmi", 4322, "dt" );
	    cds = (InterfaceRMIds) ComWorld.getComm( "localhost", "rmi", 4322, "ds" );

	    bitdew = new BitDew( cdc, cdr, cdt, cds );

	    tm = new TransferManager(cdr, cdt);

	    data = bitdew.createData(file);
	    log.info("created data " + data.getuid());

	    // No local protocol
	    Protocol local_proto = new Protocol();
	    local_proto.setname("local");
	    
	    Locator local_locator = LocatorUtil.localLocator(data,file);
	    log.debug("Local Locator : " + local_locator.getref());

	    Protocol remote_proto;
	    // get a Dummy remote protocol
	    try {
		remote_proto = cdr.getProtocolByName("Dummy");
		log.debug("Remote_proto fetched : " + remote_proto.getuid() + " : " +remote_proto.getname() +"://" + remote_proto.getlogin() + ":" +  remote_proto.getpassword() +  "@" + ((CommRMITemplate) cdr).getHostName() + ":" +  remote_proto.getport() +"/" + remote_proto.getpath() );
	    } catch (RemoteException re) {
		log.debug("Cannot find a protocol dummy " + re);
		throw new BitDewException();
	    }

	    Locator remote_locator = new Locator();
	    remote_locator.setdatauid(data.getuid());
	    remote_locator.setdrname(((CommRMITemplate) cdr).getHostName());
	    remote_locator.setprotocoluid(remote_proto.getuid());
	    
	    try {
		remote_locator.setref( cdr.getRef("" +data.getuid()) );
		log.debug("Remote_reference fetched : " + remote_locator.getref());
	    } catch (RemoteException re) {
		log.debug("Cannot find a protocol dummy " + re);
		throw new BitDewException();
	    }
	    
	    //prepare
	    Transfer t = new Transfer();
	    t.setlocatorremote(remote_locator.getuid());
	    t.settype(TransferType.UNICAST_SEND_SENDER_SIDE);
	    t.setlocatorlocal(local_locator.getuid());
	    //	Data data = DataUtil.fileToData(file);
	
	    //	    FtpTransfer ftp = new FtpTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);
	    OOBTransfer oobt = OOBTransferFactory.createOOBTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);
	    oobt.persist();

	    log.debug("Succesfully created  transfer [" + t.getuid() + "] data [" + data.getuid()+ "] with remote storage [" + remote_locator.getref()  + "] " + remote_proto.getname() +"://[" + remote_proto.getlogin() + ":" +  remote_proto.getpassword() +  "]@" + remote_locator.getdrname() + ":" +  remote_proto.getport() +"/" + remote_proto.getpath() + "/" + remote_locator.getref() );

	    tm.registerTransfer(oobt);

	    /*
	    try {
		.connect();	    
		ftp.send();
		ftp.disconnect();
	} catch(OOBException oobe) {
	   log.debug("Was not able to transfer " + oobe);
	   throw new BitDewException("Error when transfering data to ftp server : " + remote_proto.getname() +"://" + remote_proto.getlogin() + ":" +  remote_proto.getpassword() +  "@" + ((CommRMITemplate) cdr).getHostName() + ":" +  remote_proto.getport() +"/" + remote_proto.getpath() + "/" + remote_locator.getref() );
	}
	    */


	    //Now publish the new locator
	    try {
		cdc.putLocator(remote_locator);
		log.debug("registred new locator");
	    } catch (RemoteException re) {
		log.debug("Cannot register locator " + re);
		throw new BitDewException();
	    }

	    tm.start();

	} catch(ModuleLoaderException e) {
	    log.warn("Cannot find service " +e);
	} catch (BitDewException bde) {
	    log.warn("Oups : " + bde);
	} catch (OOBException oobe) {
	    log.warn("Oups : " + oobe);
	}
    }
}
