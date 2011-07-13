package xtremweb.dao.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import junit.framework.TestCase;
import xtremweb.core.obj.dc.Data;
import xtremweb.dao.DaoJDOImpl;

public class DaoTest extends TestCase{
    
    private DaoJDOImpl dao;
    private Data data1;
    private Data data2;
    private Data data3;
    private Data data4;
    private Data data5;
    private Collection collection;
    private Collection uids;
    private final int TAM_TOT=5;
    
    //TODO there is an issue with DetachAllOnCommit property, it is not possible to erase one 
    // object in the pm if DetachAllOnCommit is true, i put it in false although i think it will be ok
    //is important to research more on this.
    public void setUp()
    {	System.out.println("setup");
	collection = new ArrayList();
	uids = new ArrayList();
    	data1 = new Data();
	data1.setname("firstdata");
	data1.setattruid(null);
	data1.setuid("uid1");
	data1.setchecksum("AEIOU");
	collection.add(data1);
	
	data2 = new Data();
	data2.setname("seconddata");
	data2.setattruid(null);
	data2.setuid("uid2");
	data2.setchecksum("EIOUA");
	collection.add(data2);
	
	data3 = new Data();
	data3.setname("thirddata");
	data3.setattruid(null);
	data3.setuid("uid3");
	data3.setchecksum("IOUAE");
	collection.add(data3);
	
	data4 = new Data();
	data4.setname("fourthdata");
	data4.setattruid(null);
	data4.setuid("uid4");
	data4.setchecksum("OUAEI");
	collection.add(data4);
	
	data5 = new Data();
	data5.setname("fifthdata");
	data5.setattruid(null);
	data5.setuid("uid5");
	data5.setchecksum("UAEIO");
	collection.add(data5);
	
	dao = new DaoJDOImpl(); 
	
	dao.makePersistent(data1,true);
	
	dao.makePersistent(data2,true);
	
	dao.makePersistent(data3,true);
	
	dao.makePersistent(data4,true);
	
	dao.makePersistent(data5,true);
	dao.beginTransaction();
	Collection col = dao.getAll(Data.class);
	for (Iterator iterator = col.iterator(); iterator.hasNext();) {
	    Data type = (Data) iterator.next();
	    System.out.println("name matafoca " + type.getname());
	    uids.add(type.getuid());
	
	}
	dao.commitTransaction();
	
    }

    @Test 
    public void testPersist()
    {

	dao.beginTransaction();
	String[] arr = {"first","second","third","fourth","fifth"};
	int i =0;
	Collection col = dao.getAll(Data.class);
	assertEquals(TAM_TOT,col.size());
	for (Iterator iterator = col.iterator(); iterator.hasNext();) {
	    Data type = (Data) iterator.next();
	    assertEquals(type.getname(),arr[i] +"data" );
	    uids.add(type.getuid());
	    i++;
	}
	
	dao.commitTransaction();

    }
    
    @Test public void testGetByUid()
    {	Iterator iter = uids.iterator();
	dao.beginTransaction();
	System.out.println("tam de uids " + uids.size());
	Data res = (Data)dao.getByUid(xtremweb.core.obj.dc.Data.class, (String)iter.next());
	System.out.println(" res is "+res);
	assertEquals(res.getchecksum(),"AEIOU");
	
	res = (Data)dao.getByUid(xtremweb.core.obj.dc.Data.class, (String)iter.next());
	assertEquals(res.getchecksum(),"EIOUA");
	
	res = (Data)dao.getByUid(xtremweb.core.obj.dc.Data.class, (String)iter.next());
	assertEquals(res.getchecksum(),"IOUAE");
	
	res = (Data)dao.getByUid(xtremweb.core.obj.dc.Data.class, (String)iter.next());
	assertEquals(res.getchecksum(),"OUAEI");
	
	res = (Data)dao.getByUid(xtremweb.core.obj.dc.Data.class, (String)iter.next());
	assertEquals(res.getchecksum(),"UAEIO");
	
	dao.commitTransaction();
	
    }
    
    @Test public void testGetByName()
    {
	dao.beginTransaction();
	
	Data res = (Data)dao.getByName(xtremweb.core.obj.dc.Data.class, "firstdata");
	assertEquals(res.getchecksum(),"AEIOU");
	
	res = (Data)dao.getByName(xtremweb.core.obj.dc.Data.class, "seconddata");
	assertEquals(res.getchecksum(),"EIOUA");
	
	res = (Data)dao.getByName(xtremweb.core.obj.dc.Data.class, "thirddata");
	assertEquals(res.getchecksum(),"IOUAE");
	
	res = (Data)dao.getByName(xtremweb.core.obj.dc.Data.class, "fourthdata");
	assertEquals(res.getchecksum(),"OUAEI");
	
	res = (Data)dao.getByName(xtremweb.core.obj.dc.Data.class, "fifthdata");
	assertEquals(res.getchecksum(),"UAEIO");
	
	dao.commitTransaction();
    }
    
    public void tearDown()
    {	System.out.println("teardown");
    	dao.beginTransaction();
	Collection col = dao.getAll(Data.class);
	System.out.println("tam joder tio " + col.size());
	col = dao.getAll(Data.class);
	System.out.println("tam joder tio " + col.size());
	dao.deleteAll(col);
	dao.commitTransaction();
	
    }

}
