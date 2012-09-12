package xtremweb.role.integration;

import java.io.File;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.api.transman.TransferManager;
import xtremweb.api.transman.TransferManagerException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.Interfacedc;
import xtremweb.core.iface.Interfacedr;
import xtremweb.core.iface.Interfaceds;
import xtremweb.core.iface.Interfacedt;
import xtremweb.core.obj.dc.Data;
import xtremweb.serv.dt.OOBTransfer;

/**
 * This class throws a process with a single BitDew put 
 * @author jose
 *
 */
public class PutProcess {
    
    /**
     * Bitdew API
     */
    private BitDew bd;
    
    /**
     * The file name to put
     */
    private String getfilename;
    
    /**
     * Transfer Manager API
     */
    private TransferManager tm;
    
    /**
     * Process number , it will be added on the filename get by this process
     * 
     */
    private int prnum;
    
    /**
     * PutProcess constructor
     * @param stable stable node where bitdew services run
     * @param uid data uid to get
     * @param filenumber
     */
    public PutProcess(String stable, String getfname) {
	try {
	    this.getfilename= getfname;
	    Interfacedr idr = (Interfacedr) ComWorld.getComm(stable,
		    "rmi", 4325, "dr");

	    Interfacedc idc = (Interfacedc) ComWorld.getComm(stable,
		    "rmi", 4325, "dc");
	    Interfaceds ids = (Interfaceds) ComWorld.getComm(stable,
		    "rmi", 4325, "ds");
	    Interfacedt idt = (Interfacedt) ComWorld.getComm(stable,
		    "rmi", 4325, "dt");

	  
	    bd = new BitDew(idc, idr,ids);
	    tm = new TransferManager(idt);
	    tm.start();
	} catch (ModuleLoaderException e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Perform a put
     */
    public void execute() {
	Data d;
	try {
	    System.out.println(" Process #   " + prnum);
	    File fofis = new File(getfilename);
	    d = bd.createData(fofis);
	    OOBTransfer oobt = bd.put(fofis,d,"http");
	    tm.registerTransfer(oobt);
	    tm.waitFor(d);
	    tm.stop();
	    System.out.println("Finish process !!!!!!!!! " + prnum);
	} catch (BitDewException e) {
	    e.printStackTrace();
	} catch (TransferManagerException e) {
	    e.printStackTrace();
	}

    }

}
