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
 * Describe class DataSchedulerTest here.
 *
 *
 * Created: Mon Oct  8 11:35:41 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class DataSchedulerTest {

    Logger log = LoggerFactory.getLogger("Data Scheduler Test");

    DataScheduler ds = new DataScheduler();
    DBInterface dbi = DBInterfaceFactory.getDBInterface();
    Data data = new Data();
    Attribute attr = new Attribute();

    @Test public void addDataAttributeTest() {
	dbi.makePersistent(data);
	dbi.makePersistent(attr);
	ds.addDataAttribute(data,attr);
    }

    @Test public void searchDataAttributeTest() {
	dbi.makePersistent(data);
	dbi.makePersistent(attr);
	ds.addDataAttribute(data,attr);
	int idx = ds.getDataCache().search(data.getuid());
	assertFalse ( idx == -1);
    }


    @Test public void searchWronguid() {
	Data tmp = new Data();
	dbi.makePersistent(tmp);
	int idx = ds.getDataCache().search(tmp.getuid());
	assertEquals(idx, -1);
    }


    @Test public void testRemoveDataFromCache() {
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

	ds.getDataCache().addElement(ce1);
	ds.getDataCache().addElement(ce2);
	ds.getDataCache().addElement(ce3);
	ds.getDataCache().addElement(ce4);

	Host h1 = new Host();
	Host h2 = new Host();

	dbi.makePersistent(h1);	
	dbi.makePersistent(h2);	

	Vector uids = new Vector();
	Vector vector;

	//data which are in the worker cache and which are in the scheduler cache, 
	//are kept in the worker cache
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (0, vector.size());

	uids.add(d1.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (1, vector.size());

	uids.add(d2.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (2, vector.size());

	uids.add(d3.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (3, vector.size());

	uids.add(d4.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (4, vector.size());

	//data which are in the worker cache and which are not in the scheduler cache
	//are removed from the worker cache
	Data d5 = new Data(), d6 = new Data();
	dbi.makePersistent(d5);	
	dbi.makePersistent(d6);
	
	uids.add(d5.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (4, vector.size());
	
	uids = new Vector();
	uids.add(d6.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (0, vector.size());

	uids.add(d4.getuid());
	uids.add(d5.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (1, vector.size());

	//data which are in the scheduler cache but mark as TODELETE should be removed
	//from the worker cache
	d2.setstatus(DataStatus.TODELETE);
	d4.setstatus(DataStatus.TODELETE);
	
	uids = new Vector();

	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (0, vector.size());

	uids.add(d2.getuid());
	uids.add(d4.getuid());

	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (0, vector.size());

	uids.add(d1.getuid());
	uids.add(d3.getuid());

	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (2, vector.size());

	d2.setstatus(0);
	d4.setstatus(0);

	//remove from local cache data which have an attribute with an absolute time life lesser than now();
	long timelife_before = System.currentTimeMillis() - 10000;
	long timelife_after  = System.currentTimeMillis() + 10000;

	Attribute attr_before     = new Attribute();
	Attribute attr_after      = new Attribute();
	Attribute attr_incorrect  = new Attribute();

	dbi.makePersistent(attr_before);	
	dbi.makePersistent(attr_after);	
	dbi.makePersistent(attr_incorrect);	

	AttributeType.setAttributeTypeOn( attr_before, AttributeType.LFTABS );
	attr_before.setlftabs(timelife_before);
	ce2.setAttribute(attr_before);

	AttributeType.setAttributeTypeOn( attr_after, AttributeType.LFTABS );
	attr_after.setlftabs(timelife_after);
	ce3.setAttribute(attr_after);

	//the correct case, the attribute is not set, but the value is set
	AttributeType.setAttributeTypeOff( attr_incorrect, AttributeType.LFTABS );
	attr_incorrect.setlftabs(timelife_before);
	ce4.setAttribute(attr_incorrect);

	uids = new Vector();

	uids.add(d1.getuid());
	uids.add(d2.getuid());
	uids.add(d3.getuid());
	uids.add(d4.getuid());

	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (3, vector.size());

	//data which have attribute LFTREL should be deleted from the cache if the data is not present in the scheduler or have the TODELETE flag
	uids.clear();
	vector.clear();

	Attribute attr_reference   = new Attribute();
	dbi.makePersistent(attr_reference);	
	ce1.setAttribute(attr_reference);

	Attribute attr_relatif    = new Attribute();
	dbi.makePersistent(attr_relatif);	

	//d2 is the relative data
	AttributeType.setAttributeTypeOn( attr_relatif, AttributeType.LFTREL );
	attr_relatif.setlftrel(d1.getuid());
	ce2.setAttribute(attr_relatif);
	d2.setstatus(0);

	assertFalse(  AttributeType.isAttributeTypeSet( attr_relatif, AttributeType.LFTABS ));
	assertTrue(  AttributeType.isAttributeTypeSet( attr_relatif, AttributeType.LFTREL ));

	//d1 is in the cache
	uids.add(d2.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (1, vector.size());

	//d1 has status to delete
	int sav = d1.getstatus();
	d1.setstatus(DataStatus.TODELETE);

	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (0, vector.size());
	d1.setstatus(sav);

	//d5 is not present
	uids.clear();
	attr_relatif.setlftrel(d5.getuid());
	uids.add(d2.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (0, vector.size());
    }

    @Test public void testGetNewDataFromCache() {

	//creates the data cache
	Data d1 = new Data(), d2 = new Data(), d3 = new Data(), d4 = new Data();
	dbi.makePersistent(d1);	
	dbi.makePersistent(d2);	
	dbi.makePersistent(d3);	
	dbi.makePersistent(d4);	

	//	log.info("["+ d1.getuid() + " " + d2.getuid() + " " + d3.getuid() + " " + d4.getuid() + "]");

	Attribute attr = new Attribute();
	dbi.makePersistent(attr);	

	CacheEntry ce1 = new CacheEntry(d1, attr);
	CacheEntry ce2 = new CacheEntry(d2, attr);
	CacheEntry ce3 = new CacheEntry(d3, attr);
	CacheEntry ce4 = new CacheEntry(d4, attr);

	ds.getDataCache().addElement(ce1);
	ds.getDataCache().addElement(ce2);
	ds.getDataCache().addElement(ce3);
	ds.getDataCache().addElement(ce4);

	Host h1 = new Host();
	Host h2 = new Host();
	Host h3 = new Host();
	Host h4 = new Host();

	dbi.makePersistent(h1);	
	dbi.makePersistent(h2);	
	dbi.makePersistent(h3);	
	dbi.makePersistent(h4);	

	Vector uids = new Vector();
	Vector vector;
	
	//get new data from the scheduler cache and data which are not in the worker cache
	vector = ds.getNewDataFromCache(h1, uids);

	assertEquals (1, vector.size());
	assertVectorContains(vector, uids);

	uids.add(d1.getuid());
	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (2, vector.size());
	assertVectorContains(vector, uids);

	uids.add(d2.getuid());
	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (3, vector.size());
	assertVectorContains(vector, uids);

	uids.add(d3.getuid());
	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (4, vector.size());
	assertVectorContains(vector, uids);

	uids.add(d4.getuid());
	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (4, vector.size());
	assertVectorContains(vector, uids);

	// consecutive calls fill the local cache
	ds.resetOwners();
	uids.clear();

	uids = ds.getNewDataFromCache(h1, uids);
	assertEquals (1, uids.size());

	uids = ds.getNewDataFromCache(h1, uids);
	assertEquals (2, uids.size());

	uids = ds.getNewDataFromCache(h1, uids);
	assertEquals (3, uids.size());

	uids = ds.getNewDataFromCache(h1, uids);
	assertEquals (4, uids.size());


	//test several hosts downloading data with different replicat attributes 

	Attribute attr_no_replicat       = new Attribute();
	Attribute attr_default_replicat  = new Attribute();
	Attribute attr_replicat          = new Attribute();
	Attribute attr_replicat_full     = new Attribute();

	dbi.makePersistent(attr_no_replicat);	
	AttributeType.setAttributeTypeOn( attr_no_replicat, AttributeType.REPLICAT );
	attr_no_replicat.setreplicat(0);
	ce1.setAttribute(attr_no_replicat);
	ce2.setAttribute(attr_no_replicat);
	ce3.setAttribute(attr_no_replicat);
	ce4.setAttribute(attr_no_replicat);

	Vector uids1 = new Vector();
	Vector uids2 = new Vector();
	Vector uids3 = new Vector();
	Vector uids4 = new Vector();

	ds.resetOwners();

	//test the replication with no replication
	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(0, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(0, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());

	//test the replication  with the default replication value (that is a replication of value 1)
	ce1.setAttribute(attr_default_replicat);

	ds.resetOwners();

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(0, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());



	//test the replication set to 2
	int rep=2;
	dbi.makePersistent(attr_replicat);
	AttributeType.setAttributeTypeOn( attr_replicat, AttributeType.REPLICAT );
	attr_replicat.setreplicat(rep);
	ce1.setAttribute(attr_replicat);

	uids1.clear();

	ds.resetOwners();

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());

	//test the full replication, that is, when a replicat == -1, the data is present on every node
	dbi.makePersistent(attr_replicat_full);	

	AttributeType.setAttributeTypeOn( attr_replicat_full, AttributeType.REPLICAT );
	attr_replicat_full.setreplicat(-1);
	ce1.setAttribute(attr_replicat_full);
	ce2.setAttribute(attr_replicat_full);

	uids1.clear();
	uids2.clear();
	uids3.clear();
	uids4.clear();

	ds.resetOwners();

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, uids4.size());
	
	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(2, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(2, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(2, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(2, uids4.size());
	
	//test the affinity attribute
	ds.resetOwners();	
	Attribute attr_default = new Attribute();
	Attribute attr_with_replicat = new Attribute();
	Attribute attr_affinity = new Attribute();
	
	dbi.makePersistent(attr_default);
	dbi.makePersistent(attr_with_replicat);
	dbi.makePersistent(attr_affinity);

	//test affinity with data which doesn't exist
	Data d5 = new Data();
	dbi.makePersistent(d5);
	AttributeType.setAttributeTypeOn( attr_affinity, AttributeType.AFFINITY );
	attr_affinity.setaffinity(d5.getuid());

	ce1.setAttribute(attr_affinity);
	ce2.setAttribute(attr_affinity);
	ce3.setAttribute(attr_affinity);
	ce4.setAttribute(attr_affinity);

	uids1.clear();
	uids2.clear();
	uids3.clear();
	uids4.clear();

	ds.resetOwners();

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(0, uids1.size()); 
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(0, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());

	//test affinity with a data DELETED
	AttributeType.setAttributeTypeOn( attr_affinity, AttributeType.AFFINITY );
	attr_affinity.setaffinity(d1.getuid());

	int sav = d1.getstatus();
	d1.setstatus(DataStatus.TODELETE);

	ce1.setAttribute(attr_default);
	ce2.setAttribute(attr_affinity);
	ce3.setAttribute(attr_affinity);
	ce4.setAttribute(attr_affinity);

	uids1.clear();
	uids2.clear();
	uids3.clear();
	uids4.clear();

	ds.resetOwners();

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(0, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(0, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());
	
	d1.setstatus(sav);

	//test affinity with a data with default replicat
	AttributeType.setAttributeTypeOn( attr_affinity, AttributeType.AFFINITY );
	attr_affinity.setaffinity(d1.getuid());

	ce1.setAttribute(attr_default);
	ce2.setAttribute(attr_affinity);
	ce3.setAttribute(attr_affinity);
	ce4.setAttribute(attr_affinity);

	uids1.clear();
	uids2.clear();
	uids3.clear();
	uids4.clear();

	ds.resetOwners();

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(0, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());
	
	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(2, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(0, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());
	
	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(3, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(0, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(0, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, uids1.size());

	//test affinity with a data with replicat n
	AttributeType.setAttributeTypeOn( attr_affinity, AttributeType.AFFINITY );
	attr_affinity.setaffinity(d1.getuid());

	AttributeType.setAttributeTypeOn( attr_replicat, AttributeType.REPLICAT );
	attr_replicat.setreplicat(2);

	ce1.setAttribute(attr_replicat);
	ce2.setAttribute(attr_affinity);
	ce3.setAttribute(attr_affinity);
	ce4.setAttribute(attr_affinity);

	uids1.clear();
	uids2.clear();
	uids3.clear();
	uids4.clear();

	ds.resetOwners();

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(2, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(2, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(3, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(3, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(4, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, uids4.size());

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(4, uids2.size());

	//test affinity with a data with replicat -1
	AttributeType.setAttributeTypeOn( attr_affinity, AttributeType.AFFINITY );
	attr_affinity.setaffinity(d1.getuid());

	AttributeType.setAttributeTypeOn( attr_replicat, AttributeType.REPLICAT );
	attr_replicat.setreplicat(-1);

	ce1.setAttribute(attr_replicat);
	ce2.setAttribute(attr_affinity);
	ce3.setAttribute(attr_affinity);
	ce4.setAttribute(attr_affinity);

	uids1.clear();
	uids2.clear();
	uids3.clear();
	uids4.clear();

	ds.resetOwners();

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, uids4.size());

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(2, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(2, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(2, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(2, uids4.size());

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(3, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(3, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(3, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(3, uids4.size());

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(4, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(4, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(4, uids4.size());

	uids1 = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, uids1.size());
	uids2 = ds.getNewDataFromCache(h2, uids2);
	assertEquals(4, uids2.size());
	uids3 = ds.getNewDataFromCache(h3, uids3);
	assertEquals(4, uids3.size());
	uids4 = ds.getNewDataFromCache(h4, uids4);
	assertEquals(4, uids4.size());


	//tests the FT attribute
	
	//test the affinity attribute
	ds.resetOwners();	

	Attribute attr_no_ft = new Attribute();
	Attribute attr_with_ft = new Attribute();
	
	dbi.makePersistent(attr_no_ft);
	dbi.makePersistent(attr_with_ft);

	AttributeType.setAttributeTypeOn( attr_with_ft, AttributeType.FT );
	Owner.setAliveTimeout(500);

	ce1.setAttribute(attr_no_ft);
	ce2.setAttribute(attr_with_ft);

	uids1.clear();
	uids2.clear();
	uids3.clear();
	uids4.clear();

	ds.resetOwners();

	try {
	    uids1 = ds.getNewDataFromCache(h1, uids1);
	    assertEquals(1, uids1.size());
	    uids2 = ds.getNewDataFromCache(h2, uids2);
	    assertEquals(1, uids2.size());
	    
	    Thread.sleep(750);
	    
	    uids3 = ds.getNewDataFromCache(h3, uids3);
	    assertEquals(1, uids3.size());
	    uids4 = ds.getNewDataFromCache(h4, uids4);
	    assertEquals(0, uids4.size());

	} catch (Exception e) {
	    log.debug("error running test " + e);
	    assertFalse(true);
	}
    }

    @Test public void testSyncData() {

	//creates the data cache
	Data d1 = new Data(), d2 = new Data(), d3 = new Data(), d4 = new Data();
	dbi.makePersistent(d1);	
	dbi.makePersistent(d2);	
	dbi.makePersistent(d3);	
	dbi.makePersistent(d4);	

	//	log.info("["+ d1.getuid() + " " + d2.getuid() + " " + d3.getuid() + " " + d4.getuid() + "]");

	Attribute attr = new Attribute();
	dbi.makePersistent(attr);	

	CacheEntry ce1 = new CacheEntry(d1, attr);
	CacheEntry ce2 = new CacheEntry(d2, attr);
	CacheEntry ce3 = new CacheEntry(d3, attr);
	CacheEntry ce4 = new CacheEntry(d4, attr);

	ds.getDataCache().addElement(ce1);
	ds.getDataCache().addElement(ce2);
	ds.getDataCache().addElement(ce3);
	ds.getDataCache().addElement(ce4);

	Host h1 = new Host();
	Host h2 = new Host();
	Host h3 = new Host();
	Host h4 = new Host();

	dbi.makePersistent(h1);	
	dbi.makePersistent(h2);	
	dbi.makePersistent(h3);	
	dbi.makePersistent(h4);	

	Vector uids = new Vector();
	Vector vector;

	//consucutive calls to getData fills the worker local cache
	uids = ds.getData(h1, uids);
	assertEquals (1, uids.size());

	uids = ds.getData(h1, uids);
	assertEquals (2, uids.size());

	uids = ds.getData(h1, uids);
	assertEquals (3, uids.size());

	uids = ds.getData(h1, uids);
	assertEquals (4, uids.size());

    }

    private void assertVectorEquals(Vector v1, Data... data) {
	Vector<String> v2 = new Vector<String>();
        for ( Data d : data )
	    v2.add(d.getuid());
	boolean result = (v1.size() == v2.size()) && (v1.containsAll(v2)) && (v2.containsAll(v1));
	if (!result) print(v1, v2);
	assertTrue(result); 
    }

    private void assertVectorContains(Vector v1, Data... data) {
	Vector<String> v2 = new Vector<String>();
        for ( Data d : data )
	    v2.add(d.getuid());
	boolean result = (v1.containsAll(v2));
	if (!result) print(v1, v2);
	assertTrue(result); 
    }


    private void assertVectorContains(Vector v1, Vector v2) {
	assertTrue(v1.containsAll(v2)); 
    }

    

    private void print(Vector<String>... vectors) {
	for ( Vector v : vectors )
	    log.info("vector : #" + v.size() + "[" + v + "]");
    }

}
