package xtremweb.serv.ds.test;

import  xtremweb.serv.ds.*;

import xtremweb.core.log.*;
import xtremweb.core.db.*;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.ds.Attribute;
import xtremweb.core.obj.ds.Host;
import xtremweb.serv.dc.DataStatus;

import org.junit.Before; 
import org.junit.Ignore; 
import org.junit.Test; 
import junit.framework.*;
import static org.junit.Assert.*;

import java.util.*;

/**
 * Describe class CacheEntryTest here.
 *
 *
 * Created: Mon Oct 22 12:12:12 2007
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class CacheEntryTest {

    Logger log = LoggerFactory.getLogger("Cache Entry Test");
    DBInterface dbi = DBInterfaceFactory.getDBInterface();

    @Test public void testOwners() {

	//creates the data cache
	Data d1 = new Data(), d2 = new Data(), d3 = new Data(), d4 = new Data();
	dbi.makePersistent(d1);	
	dbi.makePersistent(d2);	
	dbi.makePersistent(d3);	
	dbi.makePersistent(d4);

	Attribute attr = new Attribute();
	dbi.makePersistent(attr);	

	CacheEntry ce1 = new CacheEntry(d1, attr);
	CacheEntry ce2 = new CacheEntry(d2, attr);
	CacheEntry ce3 = new CacheEntry(d3, attr);
	CacheEntry ce4 = new CacheEntry(d4, attr);

	Host h1 = new Host();
	Host h2 = new Host();
	Host h3 = new Host();

	dbi.makePersistent(h1);	
	dbi.makePersistent(h2);	
	dbi.makePersistent(h3);	

	//add signle host
	ce1.setOwner(h1);

	// add multiple hosts
	ce3.setOwner(h2);
	ce3.setOwner(h1);

	assertEquals(1, ce1.getOwnersNumber());
	assertEquals(0, ce2.getOwnersNumber());
	assertEquals(2, ce3.getOwnersNumber());
	assertEquals(0, ce4.getOwnersNumber());


	//test reset
	//	ds.resetOwners();
	ce1.resetOwners();
	ce2.resetOwners();
	ce3.resetOwners();
	ce4.resetOwners();

	assertEquals(0, ce1.getOwnersNumber());
	assertEquals(0, ce2.getOwnersNumber());
	assertEquals(0, ce3.getOwnersNumber());
	assertEquals(0, ce4.getOwnersNumber());
	

	ce1.setOwner(h1);
	assertEquals(1, ce1.getOwnersNumber());
	ce1.setOwner(h2);
	assertEquals(2, ce1.getOwnersNumber());
	ce1.setOwner(h3);
	assertEquals(3, ce1.getOwnersNumber());

	//test if we add 2 times the same host
	ce1.setOwner(h3);
	assertEquals(3, ce1.getOwnersNumber());

	//test if we remove an existing host
	ce1.removeOwner(h1);
	assertEquals(2, ce1.getOwnersNumber());

	//test if we remove a non existing host
	ce1.removeOwner(h1);
	assertEquals(2, ce1.getOwnersNumber());
    }

    @Test public void testLastAlive() {

	//creates the data cache
	Data data = new Data();
	dbi.makePersistent(data);	

	Attribute attr = new Attribute();
	dbi.makePersistent(attr);	

	CacheEntry ce = new CacheEntry(data, attr);

	Host h1 = new Host();
	Host h2 = new Host();
	Host h3 = new Host();

	dbi.makePersistent(h1);	
	dbi.makePersistent(h2);	
	Owner.setAliveTimeout(1000);

	try {
	    ce.setOwner(h1);
	    ce.setOwner(h2);
	    assertEquals(2,ce.getOwnersNumber());

	    Thread.sleep(750);
	    ce.updateOwner(h1);
	    Thread.sleep(750);
	    ce.updateOwners();
	    assertEquals(1,ce.getOwnersNumber());
	    
	} catch (Exception e) {
	    log.debug("error running test " + e);
	    assertFalse(true);
	}
	

    }

}
