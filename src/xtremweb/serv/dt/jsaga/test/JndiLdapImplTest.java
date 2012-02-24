package xtremweb.serv.dt.jsaga.test;

import java.net.URISyntaxException;

import org.junit.Test;

import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.serv.dr.ProtocolUtil;
import xtremweb.serv.dt.jsaga.JndiLdapImpl;
import xtremweb.serv.dt.jsaga.LDAPEngineException;
import junit.framework.TestCase;

public class JndiLdapImplTest extends TestCase{
    
    private JndiLdapImpl impl;
    private String HOST =  "ldap://cclcgtopbdii01.in2p3.fr:2170";
    
    public void setUp()
    {
	try {
	    impl = new JndiLdapImpl();
	    impl.connect(HOST);
	} catch (LDAPEngineException e) {
	   fail();
	}
    }
    
    @Test public void testSearchByService() 
    {
	String url;
	try {
	    url = impl.searchByService("org.glite.RTEPublisher");
	assertNotNull(url);
	Protocol theprot = ProtocolUtil.getProtocol(url);
	assertNotNull(theprot.getserver());
	assertEquals(theprot.getname(),"gsiftp");
	assertNotNull(theprot.getpath());
	assertEquals(theprot.getpath(),"/tmp");
	
	} catch (LDAPEngineException e) {
	   fail();
	} catch (URISyntaxException e) {
	fail();
	}
    }
    
    public void tearDown()
    {
	try {
	    impl.close();
	} catch (LDAPEngineException e) {
	    fail();
	}
    }

}
