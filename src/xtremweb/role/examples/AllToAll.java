package xtremweb.role.examples;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.BrokenBarrierException;
import java.util.Vector;
import jargs.gnu.CmdLineParser;

import xtremweb.core.log.*;
import xtremweb.core.db.*;
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

public class AllToAll {

    Logger log = LoggerFactory.getLogger(AllToAll.class);
    BitDew bitdew = null;
    ActiveData activeData = null;
    TransferManager transferManager = null;
    String _dir;

    InterfaceRMIbench ibench=null;
    long myrank;
    String hostName = java.net.InetAddress.getLocalHost().getHostName();
    
    int workers;
    int received=0;

    long start;
    long dataScheduled;
    long end;

    Attribute attr1;
    Attribute attr2;
    Attribute attr3;
    Attribute attr4;

    public AllToAll(String host, int port, boolean master, String dir, int w) throws Exception {
	workers = w;
	_dir=dir;
	Host myHost =  ComWorld.getHost();
	log.debug("my Host " + myHost.getuid());

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

	if (master) {
	    int taille=50000;

	    //code for data1
	    File fic1 = new File(dir,"data1");
	    createFic(fic1,taille);
	    Data data1 = bitdew.createData(fic1);
	    data1.setoob("ftp");
	    Locator loc1 = bitdew.createLocator("data1");
	    bitdew.put(data1,loc1);
	    attr1 = activeData.createAttribute("attr attr1 = {replicat = 1, oob = ftp  }");
	    log.debug("attribute update " + AttributeUtil.toString(attr1));

	    activeData.schedule(data1, attr1);

	    //code for data2
	    File fic2 = new File(dir,"data2");
	    createFic(fic2,taille);
	    Data data2 = bitdew.createData(fic2);
	    data2.setoob("ftp");
	    Locator loc2 = bitdew.createLocator("data2");
	    bitdew.put(data2,loc2);
	    attr2 = activeData.createAttribute("attr attr2 = {replicat = 1, oob = ftp  }");
	    log.debug("attribute update " + AttributeUtil.toString(attr2));

	    activeData.schedule(data2, attr2);
	    /*
	    //code for data3
	    File fic3 = new File(dir,"data3");
	    createFic(fic3,taille);
	    Data data3 = bitdew.createData(fic3);
	    data3.setoob("ftp");
	    Locator loc3 = bitdew.createLocator("data3");
	    bitdew.put(data3,loc3);
	    activeData.schedule(data3, attr);

	    //code for data4
	    File fic4 = new File(dir,"data4");
	    createFic(fic4,taille);
	    Data data4 = bitdew.createData(fic4);
	    data4.setoob("ftp");
	    Locator loc4 = bitdew.createLocator("data4");
	    bitdew.put(data4,loc4);
	    activeData.schedule(data4, attr);
	    */
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
    

    public void createFic(File fic, int taille) {
	if (!fic.exists()) {
	   
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
    }


    public static void main(String[] args) throws Exception {
	CmdLineParser parser = new CmdLineParser();
	CmdLineParser.Option helpOption = parser.addBooleanOption('h', "help");
	CmdLineParser.Option portOption = parser.addIntegerOption("port");
	CmdLineParser.Option workersOption = parser.addIntegerOption("workers");
	CmdLineParser.Option hostOption = parser.addStringOption("host");
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
    	String dir = (String) parser.getOptionValue(dirOption,"/tmp/pub/incoming");
   	int port = ((Integer) parser.getOptionValue(portOption,new Integer(4322))).intValue();
   	int workers = ((Integer) parser.getOptionValue(workersOption,new Integer(1))).intValue();
	boolean master = ((Boolean)parser.getOptionValue(masterOption, Boolean.FALSE)).booleanValue();
	if (help) {
	    System.exit(2);
	}

	if (master) host="localhost";
	else 
	    System.out.println("worker");

	try {
	    AllToAll bc = new AllToAll(host, port, master, dir, workers);
	} catch (Exception e) {
	     System.out.println(e.getMessage());
	}
    }
    /*
    public class AtaThread extends Thread() {

	
    }*/

    public class BroadcastCallback implements ActiveDataCallback {


	public void onDataScheduled(Data data, Attribute attr) {
	    try {
		    log.info("data scheduled  " + data.getname() + " " + attr.getname());
		    File fic = new File(_dir,"test" + received);
		    start=System.currentTimeMillis();
		    bitdew.get(data, fic);
		    transferManager.waitFor(data);
		    end=System.currentTimeMillis();
		    //		    ibench.endExperience(myrank,end-start,null);
		    if ((myrank==0) && (received==0)) {
			//now change attribute
			//ExclIl faut soumattr1
		    }
		    received++;

			//ibench.startExperience();
		    log.info("transfer finished " + hostName + " " + (end-start) + " " + received  );

		    //		    System.exit(0);
		
	    } catch (Exception e) {
		log.warn("finish with error " + e);
		System.exit(0);
	    }
	}

	public void onDataDeleted(Data data, Attribute attr) {}
    }


}
