package xtremweb.serv.dc.test;

import java.rmi.RemoteException;
import java.util.Vector;
import xtremweb.serv.dc.*;
import junit.framework.TestCase;

import org.junit.Test;

import xtremweb.core.com.com.CommRMIdc;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.Interfacedc;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.DataChunk;
import xtremweb.core.obj.dc.DataCollection;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.serv.ServiceLoader;

public class CallbackdcTest  extends TestCase{
    
    //private Interfacedc catalog;
    private final int TOTCHUNKS = 2;
    public void setUp()
    {	String[] str = { "dc"};
	ServiceLoader s = new ServiceLoader("RMI", 4325, str);
	//s.
    }
   
    
    
    @Test 
    public void testCreateData()
    {	try {
	long a=100,b=200,c=300,d=400,e=500;
	Interfacedc catalog = (CommRMIdc) ComWorld.getComm("localhost","rmi",4325,"dc");
	
	Data inserted1 = new Data();
	inserted1.setname("mydata1");
	inserted1.setchecksum("AAA");
	inserted1.setsize(a);
	inserted1.settype(0);
	
	String uid1 = catalog.putData(inserted1);
	assertNotNull(uid1);
	
	Data inserted2 = new Data();
	inserted2.setname("mydata2");
	inserted2.setchecksum("BBB");
	inserted2.setsize(b);
	inserted2.settype(0);			
	
	String uid2 = catalog.putData(inserted2);
	assertNotNull(uid2);
	
	Data inserted3 = new Data(); 
	inserted3.setname("mydata3");
	inserted3.setchecksum("CCC");
	inserted3.setsize(c);
	inserted3.settype(0);
	
	String uid3 = catalog.putData(inserted3);
	assertNotNull(uid3);
	
	Data inserted4 = new Data(); 
	inserted4.setname("mydata4");
	inserted4.setchecksum("DDD");
	inserted4.setsize(d);
	inserted4.settype(0);
	
	String uid4 = catalog.putData(inserted4);	
	assertNotNull(uid4);
	
	
	Data inserted5 = new Data();
	inserted5.setname("mydata5");
	inserted5.setchecksum("EEE");
	inserted5.setsize(e);
	inserted5.settype(0);
	
	String uid5 = catalog.putData(inserted5);	
	assertNotNull(uid5);
	
	Data retrieved =  catalog.getData(uid1);
	//
	assertEquals(inserted1.getsize(),retrieved.getsize());
	assertEquals(inserted1.getname(),retrieved.getname());
	assertEquals(inserted1.getchecksum(),retrieved.getchecksum());
	//
	catalog.browse();
	
	
	Locator loc1 = new Locator();
	loc1.setdatauid(uid1);
	loc1.setpublish(true);
	
	Locator loc2 = new Locator();
	loc2.setdatauid(uid2);
	loc2.setpublish(true);
	
	Locator loc3 = new Locator();
	loc3.setdatauid(uid3);
	loc3.setpublish(true);
	
	catalog.putLocator(loc1);
	catalog.putLocator(loc2);
	catalog.putLocator(loc3);
	
	Locator retrieved1 = catalog.getLocatorByDataUID(uid1);
	assertNotNull(retrieved1);
	Locator retrieved2 = catalog.getLocatorByDataUID(uid2);
	assertNotNull(retrieved2);
	Locator retrieved3 = catalog.getLocatorByDataUID(uid3);
	assertNotNull(retrieved3);
	//The locator retrieved must be equal to the first memory locator
	assertEquals(retrieved1.getdatauid(),loc1.getdatauid());
	assertEquals(retrieved2.getdatauid(),loc2.getdatauid());
	assertEquals(retrieved3.getdatauid(),loc3.getdatauid());
	
	DataCollection dcol = new DataCollection();
	
	String uidcol = catalog.putDataCollection(dcol);
	assertNotNull(uidcol);
	
	DataChunk dch1 = new DataChunk();
	dch1.setdatauid(uid4);
	dch1.setcollectionuid(uidcol);
	dch1.setoffset(10000);
	
	DataChunk dch2 = new DataChunk();
	dch2.setdatauid(uid5);
	dch2.setcollectionuid(uidcol);
	dch2.setoffset(5000);
	//-----DATACHUNK PUT -----------------------
	String idchunk1 = catalog.putDataChunk(dch1);
	String idchunk2 = catalog.putDataChunk(dch2);
	//--------------------------------------------

	
	System.out.println(" id1 is " + idchunk1);
	System.out.println("id2 is " + idchunk2);
	// ----- DATACHUNK GET --------------------------
	DataChunk retdc1 = catalog.getDataChunk(idchunk1);
	DataChunk retdc2 = catalog.getDataChunk(idchunk2);
	
	//assertEquals(retdc1.getdatauid(),retdc2.getdatauid());
	assertEquals(retdc1.getcollectionuid(),retdc2.getcollectionuid());
	
	Vector v = catalog.getAllDataInCollection(uidcol);
	
	assertEquals(v.size(),TOTCHUNKS);
	
	Data member1 = (Data)v.get(0);
	assertNotNull(member1);
	Data member2 = (Data)v.get(1);
	assertNotNull(member2);	
	assertTrue(member1.getuid().equals(dch1.getdatauid()) || member1.getuid().equals(dch2.getdatauid()));

	
	
    } catch (ModuleLoaderException e) {
	e.printStackTrace();
    } catch (Exception e) {
	e.printStackTrace();
    }
    	
    	
	
	
	
	
	
	
	
    }
    
    public void tearDown()
    {
	
    }

}
