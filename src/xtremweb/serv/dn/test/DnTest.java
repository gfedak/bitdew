package xtremweb.serv.dn.test;

import java.rmi.RemoteException;

import org.junit.Test;

import junit.framework.TestCase;

import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.InterfaceRMIdn;
import xtremweb.core.serv.ServiceLoader;

public class DnTest extends TestCase {

    public void setUp() {
	String[] str = { "dn" };
	ServiceLoader s = new ServiceLoader("RMI", 4325, str);
    }

    public void tearDown() {

    }

    @Test
    public void testmain() {
	InterfaceRMIdn dn;
	try {
	    dn = (InterfaceRMIdn) ComWorld.getComm("localhost", "rmi", 4325,
		    "dn");
	    dn.registerService("dt", "lip.ens-lyon.fr");
	    dn.registerService("ds", "lipgerland.ens-lyon.fr");
	    dn.registerService("clacla", "plapla");
	    String host = dn.getServiceAddress("dt");
	    System.out.println(" The host for dt is " + host);

	    host = dn.getServiceAddress("ds");
	    System.out.println("The host for ds is " + host);

	    host = dn.getServiceAddress("clacla");
	    System.out.println("The host for ds is " + host);

	    host = dn.getServiceAddress("claclap");
	    System.out.println("The host for ds is " + host);

	} catch (ModuleLoaderException e) {
	    fail();
	    e.printStackTrace();
	} catch (RemoteException e) {
	    fail();
	    e.printStackTrace();
	}
    }
    /*
     * public void setUp() { } public void tearDown() {
     * 
     * }
     * 
     * public void testdn() { String[] servs = {"dn","dt","dc","dr","ds"};
     * ServiceLoader sl = new ServiceLoader(); InterfaceRMIdn dn; try { dn =
     * (InterfaceRMIdn)ComWorld.getComm("localhost", "rmi", 4325, "dn");
     * dn.registerService("dt","lip.ens-lyon.fr"); dn.registerService("ds",
     * "lipgerland.ens-lyon.fr");
     * 
     * String host = dn.getServiceAddress("dt");
     * System.out.println(" The host for dt is " + host);
     * 
     * host = dn.getServiceAddress("ds");
     * System.out.println("The host for ds is " + host);
     * 
     * } catch (ModuleLoaderException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); } catch (RemoteException e) { // TODO Auto-generated
     * catch block e.printStackTrace(); } }
     */

}
