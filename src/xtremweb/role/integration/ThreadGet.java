package xtremweb.role.integration;

import java.io.File;

import org.jfree.util.Log;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.api.transman.TransferManager;
import xtremweb.api.transman.TransferManagerException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.InterfaceRMIdc;
import xtremweb.core.iface.InterfaceRMIdr;
import xtremweb.core.iface.InterfaceRMIds;
import xtremweb.core.iface.InterfaceRMIdt;
import xtremweb.core.obj.dc.Data;
import xtremweb.serv.dt.OOBTransfer;

public class ThreadGet extends Thread {

    private BitDew bd;
    private TransferManager tm;
    private String duid;
    private int thnum;

    public ThreadGet(String stable, String uid,int filenumber) {
	try {thnum = filenumber;
	    InterfaceRMIdr idr = (InterfaceRMIdr) ComWorld.getComm(stable,
		    "rmi", 4325, "dr");

	    InterfaceRMIdc idc = (InterfaceRMIdc) ComWorld.getComm(stable,
		    "rmi", 4325, "dc");
	    InterfaceRMIds ids = (InterfaceRMIds) ComWorld.getComm(stable,
		    "rmi", 4325, "ds");
	    InterfaceRMIdt idt = (InterfaceRMIdt) ComWorld.getComm(stable,
		    "rmi", 4325, "dt");
	    duid = uid;
	  
	    bd = new BitDew(idc, idr, idt, ids);
	    tm = new TransferManager(idt);
	    tm.start();
	} catch (ModuleLoaderException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void popo() {
	Data d;
	try {
	    System.out.println(" Thread #   " + thnum);
	    d = bd.searchDataByUid(duid);
	    File fofis = new File("fofis" + thnum);
	    OOBTransfer oobt = bd.get(d, fofis);
	    tm.registerTransfer(oobt);
	    tm.waitFor(d);
	    tm.stop();
	    System.out.println("finish thread !!!!!!!!! " + thnum);
	} catch (BitDewException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (TransferManagerException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }
}
