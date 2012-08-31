package xtremweb.serv.dt.jsaga.test;

import java.net.URISyntaxException;

import org.junit.Test;

import xtremweb.core.obj.dr.Protocol;
import xtremweb.serv.dr.ProtocolUtil;
import xtremweb.serv.dt.jsaga.JndiLdapImpl;
import xtremweb.serv.dt.jsaga.LDAPEngineException;
import junit.framework.TestCase;

/**
 * A test to JNDI Ldap 
 * @author jsaray
 *
 */
public class JndiLdapImplTest extends TestCase{
    
	/**
	 * Ldap client
	 */
    private JndiLdapImpl impl;
    
    /**
     * LDAP test interface
     */
    private String HOST =  "ldap://cclcgtopbdii01.in2p3.fr:2170";
    
    /**
     * Setup
     */
    public void setUp()
    {
	try {
	    impl = new JndiLdapImpl();
	    impl.connect(HOST);
	} catch (LDAPEngineException e) {
	   fail();
	}
    }
    
    /**
     * Test case
     */
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
    
    /**
     * Tear down
     */
    public void tearDown()
    {
	try {
	    impl.close();
	} catch (LDAPEngineException e) {
	    fail();
	}
    }

}
