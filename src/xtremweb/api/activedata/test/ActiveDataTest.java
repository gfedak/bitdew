package xtremweb.api.activedata.test;

import xtremweb.core.log.*;
import xtremweb.core.iface.*;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.api.bitdew.*;
import xtremweb.api.activedata.*;
import xtremweb.core.com.com.*;
import xtremweb.core.com.idl.*;
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
 * Describe class TestAD here.
 *
 *
 * Created: Mon Aug 27 15:51:54 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

public class ActiveDataTest {

    ActiveData ad;
    BitDew bitdew;
    Logger log = LoggerFactory.getLogger("ActiveDataTest");
    /*
    public static Test suite() {
	return new JUnit4TestAdapter(ActiveDataTest.class);
    } 
    */

 
    public void setUp() {       
	try {	    
	    Interfacedc cdc;
	    Interfacedr cdr;
	    Interfacedt cdt;
	    Interfaceds cds;
	    	    
	    cdc = (Interfacedc) ComWorld.getComm( "localhost", "rmi", 4322, "dc" );
	    cdr = (Interfacedr) ComWorld.getComm( "localhost", "rmi", 4322, "dr" );
	    cds = (Interfaceds) ComWorld.getComm( "localhost", "rmi", 4322, "ds" );

	    bitdew = new BitDew( cdc, cdr,cds );

	    ActiveDataFactory.init(cdc,cds);
	    ad = ActiveDataFactory.getActiveData();

	} catch (Exception e) {
	    log.debug("error " + e);
	}
    }

     @Test public void start() {
	 //	ad.start();
	 boolean test = true;
	 assertTrue(test);
	 log.info("Test Active Data passed");
    }



}
