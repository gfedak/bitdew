package xtremweb.serv.ds.test;
import java.rmi.RemoteException;

import junit.framework.TestCase;

import org.junit.Test;
import xtremweb.core.obj.ds.Attribute;
import xtremweb.dao.DaoFactory;
import xtremweb.dao.attribute.DaoAttribute;
import xtremweb.serv.ds.Callbackds;

public class TestCallbackds extends TestCase {
    
    public void setUp()
    {
	
    }
    
    public void testGetAttrByName()
    {
	Attribute att1 = new Attribute();
	Attribute att2 = new Attribute();
	Callbackds ds = new Callbackds();
	att1.setuid("uid1");
	att1.setname("att1");
	att1.setlftrel("myself");
	att2.setname("att2");
	att2.setlftrel("byself");
	att2.setuid("uid2");
	DaoAttribute dao = (DaoAttribute)DaoFactory.getInstance("xtremweb.dao.attribute.DaoAttribute");
	
	try {
	    
	    Attribute uid1 = ds.registerAttribute(att1);
	    Attribute uid2 = ds.registerAttribute(att2);
	    System.out.println("uid is " + uid1.getname());
	    Attribute retr1 = ds.getAttributeByName(uid1.getname());
	    assertNotNull(retr1);
	    assertEquals(retr1.getlftrel(),"myself");
	    Attribute retr2 = ds.getAttributeByName(uid2.getname());
	    assertNotNull(retr2);
	    assertEquals(retr2.getlftrel(),"byself");
		
	    Attribute retr3 = ds.getAttributeByUid(uid1.getuid());
	    assertNotNull(retr3);
	    assertEquals(retr3.getlftrel(),uid1.getlftrel());
	
	    
	} catch (RemoteException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	
	
	
	
    }
    
   
    public void tearDown()
    {
	
    }

}
