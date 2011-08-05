package xtremweb.role.examples;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.BrokenBarrierException;
import java.util.Vector;
import jargs.gnu.CmdLineParser;

import xtremweb.core.log.*;
import xtremweb.core.serv.*;
import xtremweb.core.conf.*;
import xtremweb.serv.ds.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.iface.*;

import xtremweb.api.bitdew.*;
import xtremweb.api.activedata.*;
import xtremweb.api.transman.*;
import xtremweb.core.obj.dc.*;
import xtremweb.core.obj.ds.*;
import xtremweb.role.ui.*;
import xtremweb.serv.bench.*;

public class Broadcast {

    Logger log = LoggerFactory.getLogger(Broadcast.class);
    BitDew bitdew = null;
    ActiveData activeData = null;
    TransferManager transferManager = null;
    String _dir;

    InterfaceRMIbench ibench=null;
    long myrank;

    String hostName = "prout";
    int workers;

    long start;
    long dataScheduled;
    long end;

    public Broadcast(String host, int port, boolean master, String dir, int w, String mh, int t) throws Exception {
	workers = w;
	_dir=dir;
	Host myHost =  ComWorld.getHost();
	log.debug("my Host " + myHost.getuid() + " "+ mh);

	if (!mh.equals("localhost")) {
	    hostName = mh;
	} else {
	    try	 {
		hostName = java.net.InetAddress.getLocalHost().getHostName();
	    } catch(Exception e ){
		log.fatal("WTF " + e);
	    }
	}

	//master initialisation : loads the service
	if (master) {
	    String[] modules = {"dc","dr","dt","ds"};
	    ServiceLoader sl = new ServiceLoader("RMI", port, modules);
	    CallbackFaultTolerantBench cbbench = new CallbackFaultTolerantBench();
	    sl.addSubCallback("RMI", port, "bench", cbbench); 
	    cbbench.configure(workers, 1);
	    log.info("bench installed");
	}

	//intialise the communication and the APIs
	Vector comms = ComWorld.getMultipleComms(host, "rmi", port, "dc", "dr", "dt", "ds");

	bitdew = new BitDew(comms);

	activeData = new ActiveData(comms);
	activeData.settimeout(t);
	if (master) {
	    //code for the master
	    File fic = new File(dir,"binaryFile");
	    if (!fic.exists()) {
		int taille=5000;
		byte[] buffer = new byte[1024];
		//buffer is filled with random bits
		for (int i=0; i< 1024; i++) {
		    buffer[i]=(byte) i;
		}
		try {
		    FileOutputStream fos = new FileOutputStream( fic );
		    // buffer is copied to the file
		    for (int i = 0; i< taille; i++) {
			fos.write(buffer);
		    }
		} catch (Exception e){ 
		    log.fatal("Cannot create benchmarked file " + e);
		    System.exit(0);
		}
	    }


	    Data data = bitdew.createData(fic);
	    //FIXME
	    data.setoob("dummy");
	    Locator loc = bitdew.createLocator("binaryFile");

	    bitdew.put(data,loc);

	    Attribute attr = activeData.createAttribute("attr update = {replicat = -1, oob = dummy, ft = true  }");

	    log.info("attribute update " + AttributeUtil.toString(attr));
	    activeData.scheduleAndPin(data, attr, myHost);
	    log.info("      READY    READY    ");
	} else {

	    //code for the client
	    transferManager = TransferManagerFactory.getTransferManager(comms);
	    activeData.registerActiveDataCallback(new BroadcastCallback());

	    ibench = (InterfaceRMIbench) ComWorld.getComm( host, "rmi", port, "bench" );
	    myrank = ibench.register(hostName);
	    log.info( hostName +  "rank is " + myrank );
	    transferManager.start();
	    ibench.startExperience();
	    activeData.start();

	}

    }

    public static void main(String[] args) throws Exception {
	CmdLineParser parser = new CmdLineParser();
	CmdLineParser.Option helpOption = parser.addBooleanOption('h', "help");
	CmdLineParser.Option portOption = parser.addIntegerOption("port");
	CmdLineParser.Option timeoutOption = parser.addIntegerOption("timeout");
	CmdLineParser.Option workersOption = parser.addIntegerOption("workers");
	CmdLineParser.Option hostOption = parser.addStringOption("host");
	CmdLineParser.Option myHostOption = parser.addStringOption("myHost");
	CmdLineParser.Option dirOption = parser.addStringOption("dir");
	CmdLineParser.Option masterOption = parser.addBooleanOption("master");
	
        try {
            parser.parse(args);
        }
        catch ( CmdLineParser.OptionException e ) {
            System.out.println(e.getMessage());
        }

    	boolean help = ((Boolean)parser.getOptionValue(helpOption, Boolean.FALSE)).booleanValue();
    	String host = (String) parser.getOptionValue(hostOption,"localhost");
    	String myHost = (String) parser.getOptionValue(myHostOption,"localhost");
    	String dir = (String) parser.getOptionValue(dirOption,"/tmp/pub/incoming");
   	int port = ((Integer) parser.getOptionValue(portOption,new Integer(4322))).intValue();
   	int timeout = ((Integer) parser.getOptionValue(timeoutOption,new Integer(1000))).intValue();
   	int workers = ((Integer) parser.getOptionValue(workersOption,new Integer(1))).intValue();
	boolean master = ((Boolean)parser.getOptionValue(masterOption, Boolean.FALSE)).booleanValue();
	if (help) {
	    System.exit(2);
	}

	if (master) host="localhost";
	else 
	    System.out.println("worker");

	try {
	    Broadcast bc = new Broadcast(host, port, master, dir, workers, myHost,  timeout);
	} catch (Exception e) {
	     System.out.println(e.getMessage());
	}
    }

    public class BroadcastCallback implements ActiveDataCallback {

	public void onDataScheduled(Data data, Attribute attr) {
	    try {
		if (attr.getname().equals("update")) {
		    log.info("data scheduled  " + data.getname() + " " + attr.getname());
		    File fic = new File(_dir,"test");
		    dataScheduled=System.currentTimeMillis();
		    bitdew.get(data, fic);
		    transferManager.waitFor(data);
		    end=System.currentTimeMillis();
		    log.info("transfer finished " + hostName + " "  + (end-dataScheduled)  );
		    ibench.endExperience(myrank,end-dataScheduled,null);
		    //the last one is to stop experience  

		    //		    ibench.startExperience();

		    Thread.sleep(2000);

		    System.exit(0);
		}
	    } catch (Exception e) {
		//		log.info("transfer finished");
		System.exit(0);
	    }
	}

	public void onDataDeleted(Data data, Attribute attr) {}
    }

}
