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
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class DataSchedulerTest {

    Logger log = LoggerFactory.getLogger("Data Scheduler Test");

    DataScheduler ds = new DataScheduler();
    DBInterface dbi = DBInterfaceFactory.getDBInterface();
    Data data = new Data();
    Attribute attr = new Attribute();

    private void printVector(Vector in, Vector out, DataScheduler dspace){
	int in_size = in.size();
	int out_size = out.size();
        if (in_size==0){
	    System.out.print("In:{");
	    System.out.print("} ");
	}
        else{
	    System.out.print("In:{");
            int notin = 0;
	    int dsnum = dspace.getDataCache().size();
	    notin = notin + dsnum;
	    for (int i=0; i<=in_size-1; i++){
		int idx=0;
		idx = dspace.getDataCache().search(in.elementAt(i));
		if (idx!=-1)
		    System.out.print("d"+(idx+1)+",");
		else{
		    notin = notin + 1;
		    System.out.print("d"+notin+",");
		}

	    }
	    System.out.print("} ");
	}

        if (out_size==0){
	    System.out.print(" Out:{");
	    System.out.println("}");
	}
	else{
	    System.out.print(" Out:{");
	    for (int j=0; j<=out_size-1; j++){
		int jdx=0;
		jdx = dspace.getDataCache().search(out.elementAt(j));
		if (jdx!=-1)
		    System.out.print("d"+(jdx+1)+",");
	    }
	    System.out.println("}");
	}

    }    


    public void assertVectorEquals(Vector v, String str, DataScheduler dspace){
	boolean result = false;
	if (v.size()==0){
	    if (str.equals("{}"))
		result=true;
	    else
		result=false;
	}else{
	    if (str.equals("{}"))
		result=false;
	    else{
		Vector hid= new Vector();
		String sub="";
		int length=str.length();
		for (int i=1;i<length;i++){
		    char ch = str.charAt(i);
		    if ( (ch==',')||(ch=='}') ){
			String e=sub;
			hid.addElement(e);
			sub="";
		    }else{
			sub=sub+str.substring(i,i+1);
		    }
		}

		int notin = 0;
	        int dsnum = dspace.getDataCache().size();
	        notin = notin + dsnum;
		Vector fid = new Vector();
		for (int j=0; j<=v.size()-1; j++){
		    int idx=0;
		    idx = dspace.getDataCache().search(v.elementAt(j));
		    if (idx!=-1){
		        String f="d"+Integer.toString(idx+1);
		        fid.addElement(f);
		    }
		    else{
			notin = notin + 1;
			String f="d"+Integer.toString(notin);
			fid.addElement(f);
		    }
		}

		if (hid.equals(fid))
		    result=true;
		else
		    result=false;
	    }
	}
	assertTrue(result);
    }

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

	//set the maxumum number if data to Schedule
	ds.setNumberOfDataToSchedule(1);

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
        assertVectorEquals(uids, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

	uids.add(d1.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (1, vector.size());
	assertVectorEquals(uids, "{d1}", ds);
	assertVectorEquals(vector, "{d1}", ds);

	uids.add(d2.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (2, vector.size());
	assertVectorEquals(uids, "{d1,d2}", ds);
	assertVectorEquals(vector, "{d1,d2}", ds);

	uids.add(d3.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (3, vector.size());
	assertVectorEquals(uids, "{d1,d2,d3}", ds);
	assertVectorEquals(vector, "{d1,d2,d3}", ds);

	uids.add(d4.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (4, vector.size());
	assertVectorEquals(uids, "{d1,d2,d3,d4}", ds);
	assertVectorEquals(vector, "{d1,d2,d3,d4}", ds);

	//data which are in the worker cache and which are not in the scheduler cache
	//are removed from the worker cache
	Data d5 = new Data(), d6 = new Data();
	dbi.makePersistent(d5);	
	dbi.makePersistent(d6);
	
	uids.add(d5.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (4, vector.size());
	assertVectorEquals(uids, "{d1,d2,d3,d4,d5}", ds);
	assertVectorEquals(vector, "{d1,d2,d3,d4}", ds);

	uids = new Vector();
	uids.add(d5.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (0, vector.size());
	assertVectorEquals(uids, "{d5}", ds);
	assertVectorEquals(vector, "{}", ds);

	uids.add(d6.getuid());
	uids.add(d4.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (1, vector.size());
	assertVectorEquals(uids, "{d5,d6,d4}", ds);
	assertVectorEquals(vector, "{d4}", ds);

	//data which are in the scheduler cache but mark as TODELETE should be removed
	//from the worker cache
	d2.setstatus(DataStatus.TODELETE);
	d4.setstatus(DataStatus.TODELETE);
	
	uids = new Vector();

	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (0, vector.size());
	assertVectorEquals(uids, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

	uids.add(d2.getuid());
	uids.add(d4.getuid());

	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (0, vector.size());
	assertVectorEquals(uids, "{d2,d4}", ds);
	assertVectorEquals(vector, "{}", ds);

	uids.add(d1.getuid());
	uids.add(d3.getuid());

	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (2, vector.size());
	assertVectorEquals(uids, "{d2,d4,d1,d3}", ds);
	assertVectorEquals(vector, "{d1,d3}", ds);

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
	ce2.setAttribute(attr_before);    //d2      deleted!

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
	assertVectorEquals(uids, "{d1,d2,d3,d4}", ds);
	assertVectorEquals(vector, "{d1,d3,d4}", ds);

        uids = vector;
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals(3, vector.size());
	assertVectorEquals(uids, "{d1,d3,d4}", ds);
	assertVectorEquals(vector, "{d1,d3,d4}", ds);

	//data which have attribute LFTREL should be deleted from the cache if the data is
        // not present in the scheduler or have the TODELETE flag        
	uids.clear();
	vector.clear();

	Attribute attr_reference   = new Attribute();
	dbi.makePersistent(attr_reference);	
	ce1.setAttribute(attr_reference);

	Attribute attr_relatif    = new Attribute();
	dbi.makePersistent(attr_relatif);	

	//d2 is the relative data
	AttributeType.setAttributeTypeOn( attr_relatif, AttributeType.LFTREL );
	attr_relatif.setlftrel(d1.getuid());   //d2's relative data -----is d1
	ce2.setAttribute(attr_relatif);
	d2.setstatus(0);

	assertFalse(  AttributeType.isAttributeTypeSet( attr_relatif, AttributeType.LFTABS ));
	assertTrue(  AttributeType.isAttributeTypeSet( attr_relatif, AttributeType.LFTREL ));

	//d1 is in the cache
	uids.add(d2.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (1, vector.size());
	assertVectorEquals(uids, "{d2}", ds);
	assertVectorEquals(vector, "{d2}", ds);

	//d1 has status to delete
	int sav = d1.getstatus();
	d1.setstatus(DataStatus.TODELETE);

	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (0, vector.size());
	assertVectorEquals(uids, "{d2}", ds);
	assertVectorEquals(vector, "{}", ds);
	d1.setstatus(sav);

	//d5 is not present
	uids.clear();
	attr_relatif.setlftrel(d5.getuid());
	uids.add(d2.getuid());
	vector = ds.removeDataFromCache(h1, uids);
	assertEquals (0, vector.size());
	assertVectorEquals(uids, "{d2}", ds);
	assertVectorEquals(vector, "{}", ds);
    }

    @Test public void testGetNewDataFromCache() {

	//set the maxumum number if data to Schedule
	ds.setNumberOfDataToSchedule(1);

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
	Host h3 = new Host();
	Host h4 = new Host();

	dbi.makePersistent(h1);	
	dbi.makePersistent(h2);	
	dbi.makePersistent(h3);	
	dbi.makePersistent(h4);	

	Vector uids = new Vector();
	Vector vector = new Vector();
	
	//get new data from the scheduler cache and data which are not in the worker cache
	vector = ds.getNewDataFromCache(h1, uids);

	assertEquals (1, vector.size());      
	assertVectorEquals(uids, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);

	uids.add(d1.getuid());
	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (2, vector.size());
       	assertVectorEquals(uids, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);

	uids.add(d2.getuid());
	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (3, vector.size());
	assertVectorEquals(uids, "{d1,d2}", ds);
	assertVectorEquals(vector, "{d3,d1,d2}", ds);

	uids.add(d3.getuid());
	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (4, vector.size());
	assertVectorEquals(uids, "{d1,d2,d3}", ds);
	assertVectorEquals(vector, "{d4,d1,d2,d3}", ds);

	uids.add(d4.getuid());
	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (4, vector.size());
	assertVectorEquals(uids, "{d1,d2,d3,d4}", ds);
	assertVectorEquals(vector, "{d1,d2,d3,d4}", ds);
	
	// consecutive calls fill the local cache
	ds.resetOwners();
	uids.clear();

	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (1, vector.size());
	assertVectorEquals(uids, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids=vector;

	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (2, vector.size());
	assertVectorEquals(uids, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds); 
	uids=vector;

	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (3, vector.size());
	assertVectorEquals(uids, "{d2,d1}", ds);
	assertVectorEquals(vector, "{d3,d2,d1}", ds);
	uids=vector;

	vector = ds.getNewDataFromCache(h1, uids);
	assertEquals (4, vector.size());
	assertVectorEquals(uids, "{d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids=vector;

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
	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(0, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(0, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

	//test the replication  with the default replication value (that is a replication of value 1)
	ce1.setAttribute(attr_default_replicat);

	ds.resetOwners();

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(0, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

	//test the replication set to 2
	int rep=2;
	dbi.makePersistent(attr_replicat);
	AttributeType.setAttributeTypeOn( attr_replicat, AttributeType.REPLICAT );
	attr_replicat.setreplicat(rep);
	ce1.setAttribute(attr_replicat);

	uids1.clear();

	ds.resetOwners();

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

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

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids4=vector;
	
	vector = ds.getNewDataFromCache(h1, uids1);   
	assertEquals(2, vector.size());
	assertVectorEquals(uids1, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(2, vector.size());
	assertVectorEquals(uids2, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(2, vector.size());
	assertVectorEquals(uids3, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(2, vector.size());
	assertVectorEquals(uids4, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);
		
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

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{d2}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{d4}", ds);
	uids4=vector;

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{d1}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{d2}", ds);
	assertVectorEquals(vector, "{d2}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{d4}", ds);
	assertVectorEquals(vector, "{d4}", ds);
	uids4=vector;

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

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d2}", ds);
	uids1=vector;
	
	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{d4}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{}", ds);
	uids4=vector;

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{d2}", ds);
	assertVectorEquals(vector, "{d2}", ds);
	
	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{d4}", ds);
	assertVectorEquals(vector, "{d4}", ds);

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(0, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{}", ds);

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

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{d2}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{d4}", ds);
        uids4=vector;

	
	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(2, vector.size());
	assertVectorEquals(uids1, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{d2}", ds);
	assertVectorEquals(vector, "{d2}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{d4}", ds);
	assertVectorEquals(vector, "{d4}", ds);
	uids4=vector;
	
	vector = ds.getNewDataFromCache(h1, uids1);
        assertEquals(3, vector.size());
	assertVectorEquals(uids1, "{d2,d1}", ds);
	assertVectorEquals(vector, "{d3,d2,d1}", ds);
	uids1=vector;
	
	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{d2}", ds);
	assertVectorEquals(vector, "{d2}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{d4}", ds);
	assertVectorEquals(vector, "{d4}", ds);
	uids4=vector;

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, vector.size());
	assertVectorEquals(uids1, "{d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{d2}", ds);
	assertVectorEquals(vector, "{d2}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{d4}", ds);
	assertVectorEquals(vector, "{d4}", ds);
	uids4=vector;

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, vector.size());
	assertVectorEquals(uids1, "{d4,d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	
	
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

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{d2}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids4=vector;

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(2, vector.size());
	assertVectorEquals(uids1, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(2, vector.size());
	assertVectorEquals(uids2, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(2, vector.size());
	assertVectorEquals(uids3, "{d2}", ds);
	assertVectorEquals(vector, "{d4,d2}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids4=vector;

	
	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(3, vector.size());
	assertVectorEquals(uids1, "{d2,d1}", ds);
	assertVectorEquals(vector, "{d3,d2,d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(3, vector.size());
	assertVectorEquals(uids2, "{d2,d1}", ds);
	assertVectorEquals(vector, "{d3,d2,d1}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(2, vector.size());
	assertVectorEquals(uids3, "{d4,d2}", ds);
	assertVectorEquals(vector, "{d4,d2}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids4=vector;

		
	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, vector.size());
	assertVectorEquals(uids1, "{d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(4, vector.size());
	assertVectorEquals(uids2, "{d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(2, vector.size());
	assertVectorEquals(uids3, "{d4,d2}", ds);
	assertVectorEquals(vector, "{d4,d2}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids4=vector;

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, vector.size());
	assertVectorEquals(uids1, "{d4,d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(4, vector.size());
	assertVectorEquals(uids2, "{d4,d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids2=vector;

	
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

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
       	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids4=vector;

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(2, vector.size());
	assertVectorEquals(uids1, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(2, vector.size());
	assertVectorEquals(uids2, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(2, vector.size());
	assertVectorEquals(uids3, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(2, vector.size());
	assertVectorEquals(uids4, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);
	uids4=vector;

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(3, vector.size());
	assertVectorEquals(uids1, "{d2,d1}", ds);
	assertVectorEquals(vector, "{d3,d2,d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(3, vector.size());
	assertVectorEquals(uids2, "{d2,d1}", ds);
	assertVectorEquals(vector, "{d3,d2,d1}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(3, vector.size());
	assertVectorEquals(uids3, "{d2,d1}", ds);
	assertVectorEquals(vector, "{d3,d2,d1}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(3, vector.size());
	assertVectorEquals(uids4, "{d2,d1}", ds);
	assertVectorEquals(vector, "{d3,d2,d1}", ds);
	uids4=vector;

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, vector.size());
	assertVectorEquals(uids1, "{d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(4, vector.size());
	assertVectorEquals(uids2, "{d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(4, vector.size());
	assertVectorEquals(uids3, "{d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(4, vector.size());
	assertVectorEquals(uids4, "{d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids4=vector;

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(4, vector.size());
	assertVectorEquals(uids1, "{d4,d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids1=vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(4, vector.size());
	assertVectorEquals(uids2, "{d4,d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids2=vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(4, vector.size());
	assertVectorEquals(uids3, "{d4,d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids3=vector;

	vector = ds.getNewDataFromCache(h4, uids4);
	assertEquals(4, vector.size());
	assertVectorEquals(uids4, "{d4,d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids4=vector;
	
	//test AFFINITY+REPLICA together
	uids1.clear();
	uids2.clear();
	uids3.clear();
	uids4.clear();

	ds.resetOwners();

	Attribute attr_aff_and_rep = new Attribute();
	Attribute attr_normal = new Attribute();
	
	dbi.makePersistent(attr_aff_and_rep);
	dbi.makePersistent(attr_normal);

	AttributeType.setAttributeTypeOn( attr_aff_and_rep, AttributeType.AFFINITY );
	attr_aff_and_rep.setaffinity(d2.getuid());
	
	AttributeType.setAttributeTypeOn( attr_aff_and_rep, AttributeType.REPLICAT );
	attr_aff_and_rep.setreplicat(2);

	int sd3=d3.getstatus();
	int sd4=d4.getstatus();
	
	d3.setstatus(DataStatus.TODELETE);
	d4.setstatus(DataStatus.TODELETE);

	ce1.setAttribute(attr_aff_and_rep);
	ce2.setAttribute(attr_normal);

	uids2.addElement(d2.getuid());
	ce2.setOwner(h2);

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids1 = vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(2, vector.size());
	assertVectorEquals(uids2, "{d2}", ds);
	assertVectorEquals(vector, "{d1,d2}", ds);
	uids2 = vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(0, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{}", ds);
	uids3 = vector;

	uids1.clear();
	uids2.clear();
	uids3.clear();	
	ds.resetOwners();

	uids3.addElement(d2.getuid());
	ce2.setOwner(h3);

	vector = ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids1 = vector;

	vector = ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids2 = vector;

	vector = ds.getNewDataFromCache(h3, uids3);
	assertEquals(2, vector.size());
	assertVectorEquals(uids3, "{d2}", ds);
	assertVectorEquals(vector, "{d1,d2}", ds);
	uids3 = vector;

	d3.setstatus(sd3);
	d4.setstatus(sd4);
	
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
	    vector = ds.getNewDataFromCache(h1, uids1);  //?
	    assertEquals(1, vector.size());
	    assertVectorEquals(uids1, "{}", ds);
	    assertVectorEquals(vector, "{d1}", ds);

	    vector = ds.getNewDataFromCache(h2, uids2);
	    assertEquals(1, vector.size());
	    assertVectorEquals(uids2, "{}", ds);
	    assertVectorEquals(vector, "{d2}", ds);
	    
	    Thread.sleep(750);
	    
	    vector = ds.getNewDataFromCache(h3, uids3);  //?
	    assertEquals(1, vector.size());
	    assertVectorEquals(uids3, "{}", ds);
	    assertVectorEquals(vector, "{d2}", ds);

	    vector = ds.getNewDataFromCache(h4, uids4);
	    assertEquals(1, vector.size());
	    assertVectorEquals(uids4, "{}", ds);
	    assertVectorEquals(vector, "{d3}", ds);

	} catch (Exception e) {
	    log.debug("error running test " + e);
	    assertFalse(true);
	}
    }

    @Test public void testSyncData() {

	//set the maxumum number if data to Schedule
	ds.setNumberOfDataToSchedule(1);

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
	Host h3 = new Host();
	Host h4 = new Host();

	dbi.makePersistent(h1);	
	dbi.makePersistent(h2);	
	dbi.makePersistent(h3);	
	dbi.makePersistent(h4);	

	Vector uids = new Vector();
	Vector vector = new Vector();

	//consucutive calls to getData fills the worker local cache
	vector = ds.getData(h1, uids);
	assertEquals (1, vector.size());
	assertVectorEquals(uids, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids=vector;

	vector = ds.getData(h1, uids);
	assertEquals (2, vector.size());
	assertVectorEquals(uids, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);
	uids=vector;

	vector = ds.getData(h1, uids);
	assertEquals (3, vector.size());
	assertVectorEquals(uids, "{d2,d1}", ds);
	assertVectorEquals(vector, "{d3,d2,d1}", ds);
	uids=vector;

	vector = ds.getData(h1, uids);
	assertEquals (4, vector.size());
	assertVectorEquals(uids, "{d3,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d2,d1}", ds);
	uids=vector;

    }

    @Test public void testDistrib(){

	//set the maxumum number if data to Schedule
	ds.setNumberOfDataToSchedule(1);

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
	Host h3 = new Host();
	Host h4 = new Host();

	dbi.makePersistent(h1);	
	dbi.makePersistent(h2);	
	dbi.makePersistent(h3);	
	dbi.makePersistent(h4);

	Vector uids = new Vector();
	Vector vector = new Vector();

	//test distrib
	Attribute attr_with_distrib = new Attribute();
	Attribute attr_without_distrib = new Attribute();
	dbi.makePersistent(attr_with_distrib);
	dbi.makePersistent(attr_without_distrib);

	AttributeType.setAttributeTypeOn( attr_with_distrib, AttributeType.DISTRIB );
	attr_with_distrib.setdistrib(2);     // max=2 data on each node
	ce1.setAttribute(attr_with_distrib);
	ce2.setAttribute(attr_with_distrib);
	ce3.setAttribute(attr_with_distrib);
	ce4.setAttribute(attr_without_distrib);

	vector=ds.removeDataFromCache(h1, uids);
	assertEquals(0, vector.size());
	assertVectorEquals(uids, "{}", ds);
	assertVectorEquals(vector, "{}", ds);
	uids = vector;

	vector=ds.getNewDataFromCache(h1, uids);
	assertEquals(1, vector.size());
	assertVectorEquals(uids, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids = vector;

	vector=ds.getNewDataFromCache(h1, uids);
	assertEquals(2, vector.size());
	assertVectorEquals(uids, "{d1}", ds);
	assertVectorEquals(vector, "{d2,d1}", ds);
	uids = vector;

	vector=ds.getNewDataFromCache(h1, uids);
	assertEquals(3, vector.size());
	assertVectorEquals(uids, "{d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d2,d1}", ds);
	uids = vector;

	vector=ds.getNewDataFromCache(h1, uids);
	assertEquals(3, vector.size());
	assertVectorEquals(uids, "{d4,d2,d1}", ds);
	assertVectorEquals(vector, "{d4,d2,d1}", ds);
	uids = vector;
		
	uids.clear();
	vector.clear();
	ds.resetOwners();

	//combine affinity and distrib together
	Attribute attr_with_affinity = new Attribute();
	Attribute attr_without_affinity = new Attribute();
	dbi.makePersistent(attr_with_affinity);
	dbi.makePersistent(attr_without_affinity);

	AttributeType.setAttributeTypeOn( attr_with_affinity, AttributeType.DISTRIB );
	attr_with_affinity.setdistrib(2);     
	AttributeType.setAttributeTypeOn( attr_with_affinity, AttributeType.AFFINITY );
	attr_with_affinity.setaffinity(d1.getuid());

	AttributeType.setAttributeTypeOn( attr_without_affinity, AttributeType.DISTRIB );
	attr_without_affinity.setdistrib(1);

	ce1.setAttribute(attr_without_affinity);
	ce2.setAttribute(attr_without_affinity);
	ce3.setAttribute(attr_with_affinity);
	ce4.setAttribute(attr_with_affinity);

	vector=ds.getNewDataFromCache(h1, uids);
	assertEquals(1, vector.size());
	assertVectorEquals(uids, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	//printVector(uids, vector, ds);
	uids = vector;

	vector=ds.getNewDataFromCache(h1, uids);
	assertEquals(2, vector.size());
	assertVectorEquals(uids, "{d1}", ds);
	assertVectorEquals(vector, "{d3,d1}", ds);
	//printVector(uids, vector, ds);
	uids = vector;

	vector=ds.getNewDataFromCache(h1, uids);
	assertEquals(3, vector.size());
	assertVectorEquals(uids, "{d3,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d1}", ds);
	//printVector(uids, vector, ds);
	uids = vector;

	vector=ds.getNewDataFromCache(h1, uids);
	assertEquals(3, vector.size());
	assertVectorEquals(uids, "{d4,d3,d1}", ds);
	assertVectorEquals(vector, "{d4,d3,d1}", ds);
	//printVector(uids, vector, ds);
	uids = vector;
	
	uids.clear();
	vector.clear();
	ds.resetOwners();

	//combine replica and distrib together
	Vector uids1 = new Vector();
	Vector uids2 = new Vector();
	Vector uids3 = new Vector();
	Vector uids4 = new Vector();

	Attribute attr_with_replicat = new Attribute();
	Attribute attr_without_replicat = new Attribute();
	dbi.makePersistent(attr_with_replicat);
	dbi.makePersistent(attr_without_replicat);

	AttributeType.setAttributeTypeOn( attr_with_replicat, AttributeType.DISTRIB );
	attr_with_replicat.setdistrib(1);     
	AttributeType.setAttributeTypeOn( attr_with_replicat, AttributeType.REPLICAT );
	attr_with_replicat.setreplicat(2);

	AttributeType.setAttributeTypeOn( attr_without_affinity, AttributeType.DISTRIB );
	attr_without_replicat.setdistrib(1);

	ce1.setAttribute(attr_without_replicat);
	ce2.setAttribute(attr_without_replicat);
	ce3.setAttribute(attr_with_replicat);
	ce4.setAttribute(attr_with_replicat);

	vector=ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids1 = vector;

	vector=ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{d2}", ds);
	uids2 = vector;

	vector=ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids3 = vector;

	vector=ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids4 = vector;
		
	vector=ds.getNewDataFromCache(h1, uids1);
	assertEquals(2, vector.size());
	assertVectorEquals(uids1, "{d1}", ds);
	assertVectorEquals(vector, "{d4,d1}", ds);
	uids1 = vector;

	vector=ds.getNewDataFromCache(h2, uids2);
	assertEquals(2, vector.size());
	assertVectorEquals(uids2, "{d2}", ds);
	assertVectorEquals(vector, "{d4,d2}", ds);
	uids2 = vector;

	vector=ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids3 = vector;

	vector=ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids4 = vector;
	
	vector=ds.getNewDataFromCache(h1, uids1);
	assertEquals(2, vector.size());
	assertVectorEquals(uids1, "{d4,d1}", ds);
	assertVectorEquals(vector, "{d4,d1}", ds);
	uids1 = vector;

	vector=ds.getNewDataFromCache(h2, uids2);
	assertEquals(2, vector.size());
	assertVectorEquals(uids2, "{d4,d2}", ds);
	assertVectorEquals(vector, "{d4,d2}", ds);
	uids2 = vector;

	vector=ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids3 = vector;

	vector=ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids4 = vector;


	uids.clear();
	vector.clear();
	ds.resetOwners();

	//combine replica and distrib together distrib=-1
	uids1.clear();
	uids2.clear();
	uids3.clear();
	uids4.clear();

	Attribute attr_distrib1 = new Attribute();
	Attribute attr_distrib2 = new Attribute();
	dbi.makePersistent(attr_distrib1);
	dbi.makePersistent(attr_distrib2);

	AttributeType.setAttributeTypeOn( attr_distrib1, AttributeType.DISTRIB );
	attr_distrib1.setdistrib(-1);     

	AttributeType.setAttributeTypeOn( attr_distrib2, AttributeType.REPLICAT );
	attr_distrib2.setreplicat(2);
	AttributeType.setAttributeTypeOn( attr_distrib2, AttributeType.DISTRIB );
	attr_distrib2.setdistrib(-1);
	
	ce1.setAttribute(attr_distrib1);
	ce2.setAttribute(attr_distrib1);
	ce3.setAttribute(attr_distrib2);
	ce4.setAttribute(attr_distrib2);

	vector=ds.getNewDataFromCache(h1, uids1);
	assertEquals(1, vector.size());
	assertVectorEquals(uids1, "{}", ds);
	assertVectorEquals(vector, "{d1}", ds);
	uids1 = vector;

	vector=ds.getNewDataFromCache(h2, uids2);
	assertEquals(1, vector.size());
	assertVectorEquals(uids2, "{}", ds);
	assertVectorEquals(vector, "{d2}", ds);
	uids2 = vector;

	vector=ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids3 = vector;

	vector=ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids4 = vector;
	
	vector=ds.getNewDataFromCache(h1, uids1);
	assertEquals(2, vector.size());
	assertVectorEquals(uids1, "{d1}", ds);
	assertVectorEquals(vector, "{d4,d1}", ds);
	uids1 = vector;
	
	vector=ds.getNewDataFromCache(h2, uids2);
	assertEquals(2, vector.size());
	assertVectorEquals(uids2, "{d2}", ds);
	assertVectorEquals(vector, "{d4,d2}", ds);
	uids2 = vector;

	vector=ds.getNewDataFromCache(h3, uids3);
	assertEquals(1, vector.size());
	assertVectorEquals(uids3, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids3 = vector;

	vector=ds.getNewDataFromCache(h4, uids4);
	assertEquals(1, vector.size());
	assertVectorEquals(uids4, "{d3}", ds);
	assertVectorEquals(vector, "{d3}", ds);
	uids4 = vector;
	
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
