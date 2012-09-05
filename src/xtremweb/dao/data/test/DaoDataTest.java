package xtremweb.dao.data.test;

import java.util.Collection;
import java.util.Vector;

import org.junit.Test;

import xtremweb.core.obj.dc.Data;
import xtremweb.dao.data.DaoData;
import xtremweb.serv.dc.DataStatus;
import junit.framework.TestCase;

public class DaoDataTest extends TestCase {
	
	private DaoData daodata;

	public void setUp(){
		daodata = new DaoData();
		
	}
	
	public void tearDown(){
		
	}
	@Test
	public void testAll(){
		
		Data dati1 = new Data() ;
		dati1.setname("name1");
		dati1.setchecksum("checksumdata1");
		dati1.setstatus(DataStatus.ON_LOCAL_CACHE);
		
		Data dati2 = new Data() ;
		dati2.setname("name2");
		dati2.setchecksum("checksumdata2");
		dati2.setstatus(DataStatus.TODELETE);
		
		
		Data dati3 = new Data() ;
		dati3.setname("name3");
		dati3.setchecksum("checksumdata3");
		dati3.setstatus(DataStatus.ON_SCHEDULER);
		
		
		Data dati4 = new Data() ;
		dati4.setname("name4");
		dati4.setchecksum("checksumdata4");
		dati4.setstatus(DataStatus.TODELETE);
		
		daodata.makePersistent(dati1,true);
		daodata.makePersistent(dati2,true);
		daodata.makePersistent(dati3,true);
		daodata.makePersistent(dati4,true);
		daodata.beginTransaction();
		Data returned = daodata.getByUidNotToDelete(dati1.getuid());
		
		assertEquals(returned.getname(),"name1");
		
		Data md5returned = daodata.getDataFromMd5("checksumdata4");
		
		assertEquals(md5returned.getchecksum(),dati4.getchecksum());
		assertEquals(md5returned.getname(),dati4.getname());
		Vector<String> v = new Vector();
		
		v.add(dati1.getuid());
		v.add(dati2.getuid());
		v.add(dati4.getuid());
		
		try {
			Collection datet = daodata.getDataToDelete(v);
			assertEquals(1,datet.size());
		} catch (Exception e) {
			fail();
		}
	daodata.commitTransaction();
	
	}
}
