package xtremweb.serv.dn;

import java.rmi.RemoteException;

import org.jfree.util.Log;

import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.InterfaceRMIdn;

public class Clientdn {
    
    public static void main(String[] args)
    {	InterfaceRMIdn dn;
        try {
    	dn = (InterfaceRMIdn)ComWorld.getComm("localhost", "rmi", 4325, "dn");
    	dn.registerService("dt","lip.ens-lyon.fr");
    	dn.registerService("ds", "lipgerland.ens-lyon.fr");
    	
    	String host = dn.getServiceAddress("dt");
    	System.out.println(" The host for dt is " + host);
    	
    	host = dn.getServiceAddress("ds");
    	System.out.println("The host for ds is " + host);
    	
        } catch (ModuleLoaderException e) {
    	// TODO Auto-generated catch block
    	e.printStackTrace();
        } catch (RemoteException e) {
    	// TODO Auto-generated catch block
    	e.printStackTrace();
        }
    }

}
