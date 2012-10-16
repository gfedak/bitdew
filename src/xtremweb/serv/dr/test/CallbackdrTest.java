package xtremweb.serv.dr.test;

import java.rmi.RemoteException;

import org.junit.Test;

import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.Interfacedr;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.serv.ServiceLoader;
import xtremweb.serv.dr.Callbackdr;
import junit.framework.TestCase;

public class CallbackdrTest extends TestCase {

    public void setUp() {
    }

    @Test
    public void testObject() {
	System.setProperty("PROPERTIES_FILE", "conf/testdr.json");
	Callbackdr dr = new Callbackdr();
	Protocol http;
	
	    http = dr.getProtocolByName("http");

	    assertNotNull(http);
	    assertEquals(http.getpath(), "data");
	    dr.browse();
	    Protocol scp = dr.getProtocolByName("scp");

	    assertNotNull(scp);
	    assertEquals(scp.getpath(), "scp/path");
	    assertEquals(scp.getprivatekeypath(), "/home/user/privete/key/path");
	    assertEquals(scp.getlogin(), "test");

	    Protocol ftp = dr.getProtocolByName("ftp");

	    assertEquals(ftp.getpath(), "ftp/path");
	    assertNotNull(ftp);

	    Protocol httpbyuid = dr.getProtocolByUID(http.getuid());
	    assertEquals(httpbyuid.getname(), http.getname());

	    Protocol scpbyuid = dr.getProtocolByUID(scp.getuid());
	    assertEquals(scpbyuid.getname(), scp.getname());

	    Protocol ftpbyuid = dr.getProtocolByUID(ftp.getuid());
	    assertEquals(ftpbyuid.getname(), ftp.getname());


    }

    @Test
    public void testRMI() {
	String[] str = { "dr" };
	System.setProperty("PROPERTIES_FILE", "testdr.json");
	ServiceLoader s = new ServiceLoader("RMI", 4325, str);

	Interfacedr dr;
	try {
	    dr = (Interfacedr) ComWorld.getComm("localhost", "rmi", 4325,
		    "dr");

	    Protocol http = new Protocol();
	    http.setpath("data");
	    http.setport(8080);
	    http.setname("tuhttp");
	    String htuid = dr.registerProtocol(http);

	    Protocol ftp = new Protocol();
	    ftp.setname("tuftp");
	    ftp.setport(23);
	    ftp.setpath("ftp/path");
	    ftp.setserver("ftp.server.com");
	    String ftpui = dr.registerProtocol(ftp);

	    Protocol scp = new Protocol();
	    scp.setname("tuscp");
	    scp.setlogin("test");
	    scp.setpublickeypath("/home/user/public/key/path");
	    scp.setprivatekeypath("/home/user/private/key/path");
	    scp.setport(8080);
	    scp.setpath("path");
	    String scui = dr.registerProtocol(scp);

	    Protocol retrhttp = dr.getProtocolByName("tuhttp");

	    assertNotNull(retrhttp);
	    assertEquals(retrhttp.getpath(), http.getpath());

	    Protocol retrscp = dr.getProtocolByName("tuscp");

	    assertNotNull(retrscp);
	    assertEquals(retrscp.getpath(), scp.getpath());

	    Protocol retrftp = dr.getProtocolByName("tuftp");

	    assertEquals(retrftp.getpath(), ftp.getpath());
	    assertNotNull(retrftp);

	    Protocol httpbyuid = dr.getProtocolByUID(htuid);
	    assertNotNull(httpbyuid);
	    assertEquals(httpbyuid.getname(), http.getname());

	    Protocol scpbyuid = dr.getProtocolByUID(scui);
	    assertNotNull(scpbyuid);
	    assertEquals(scpbyuid.getname(), scp.getname());

	    Protocol ftpbyuid = dr.getProtocolByUID(ftpui);
	    assertNotNull(ftpbyuid);
	    assertEquals(ftpbyuid.getname(), ftp.getname());

	} catch (ModuleLoaderException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public void tearDown() {

    }

}
