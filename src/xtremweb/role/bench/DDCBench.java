package xtremweb.role.bench;

import xtremweb.core.log.*;
import xtremweb.serv.dc.ddc.*;
import xtremweb.api.bitdew.*;
import xtremweb.serv.dc.*;
import xtremweb.core.iface.*;
import xtremweb.core.com.com.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.obj.dc.Data;
import java.net.*;
import java.util.*;
import java.io.*;


public class DDCBench {

    protected static DistributedDataCatalog ddc = null;
    
    public static void main(String[] args) throws Exception {
	Logger log = Logger.getLogger("");
	String host = args[0];
	InterfaceRMIbench ibench=null;
	BitDew bitdew = null;
	Random generator=null;
	long myrank=0;
	String hostName = java.net.InetAddress.getLocalHost().getHostName();
	try {
	    InterfaceRMIdc cdc = (InterfaceRMIdc) ComWorld.getComm( host, "rmi", 4322, "dc" );
	    InterfaceRMIdr cdr = (InterfaceRMIdr) ComWorld.getComm( host, "rmi", 4322, "dr" );
	    InterfaceRMIdt cdt = (InterfaceRMIdt) ComWorld.getComm( host, "rmi", 4322, "dt" );
	    InterfaceRMIds cds = (InterfaceRMIds) ComWorld.getComm( host, "rmi", 4322, "ds" );
	    //	    Bench bench = new Bench( new BitDew( cdc, cdr, cdt));
	    bitdew =  new BitDew( cdc, cdr, cdt, cds);	    
	    ibench = (InterfaceRMIbench) ComWorld.getComm( host, "rmi", 4322, "bench" );
	    myrank = ibench.register(hostName);
	    generator = new Random(myrank);
	} catch(ModuleLoaderException e) {
	    log.warn("Cannot find service " +e);
	    System.exit(0);
	}

	while (true) {
	    long size = ibench.startExperience();
	    if (size == -1) 
		break;
	    long start=System.currentTimeMillis();
	    String[] v = new String[50];
	    int k=0;
	    //	log.info("READY TO Bench ");
	    for (int i=0; i< 500;i++) {
		String value = generator.nextInt() + "hostname" + hostName + "_" +  i;
		bitdew.ddcPublish(value, hostName);
		if ((i%10)==0) {
		    v[k]=value;
		    k++;
		}
	    }
	    long end=System.currentTimeMillis();
	    ibench.endExperience(myrank,end-start,v);

	    if (myrank ==0) {
		String[] values = ibench.getValues();
		if (values==null) log.warn("received a wrong values");
		log.warn("ok");
		log.warn("size : " + values.length);
		for (int i=0; i< values.length; i++)
		    if (values[i]!=null) log.debug(" " + i + " " + values[i]);
		File f = new File(new File("ddcbench"), "master"  );
		FileWriter fw = new FileWriter(f);
		for (int r=0; r< 10; r++) {
		     start=System.currentTimeMillis();
		    for (int i=0; i< values.length; i++) {
			if (values[i]!=null) log.debug(ddc.search(values[i]).toString());
		    }
		    end=System.currentTimeMillis();
		    fw.write("ddcbenchmaster " + hostName + " " + values.length + " "+ r + " " + (end-start) + "\n");
		}
		fw.flush();
		fw.close();
	    }
	}
    //System.exit(0);
    }
}