package xtremweb.role.mw;

/**
 * Describe class Worker2 here.
 *
 *
 * Created: Tue Dec 25 18:33:12 2007
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */


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

import xtremweb.api.bitdew.*;
import xtremweb.api.activedata.*;
import xtremweb.api.transman.*;
import xtremweb.core.obj.dc.*;
import xtremweb.core.obj.ds.*;


public class Worker2 {

    Logger log = LoggerFactory.getLogger(Worker2.class);
    BitDew bitdew = null;
    ActiveData activeData = null;
    TransferManager transferManager = null;
    CmdLineParser parser = new CmdLineParser();
    Attribute attrResult = null;
    File binaryFile;
    Vector<File> paramsFile;

    public static int EXECUTION_TIME = 10000;
    public int test;

    public Worker2(String[]args) {

	CmdLineParser.Option helpOption = parser.addBooleanOption('h', "help");
	CmdLineParser.Option portOption = parser.addIntegerOption("port");
	CmdLineParser.Option hostOption = parser.addStringOption("host");
	
        try {
            parser.parse(args);
        }
        catch ( CmdLineParser.OptionException e ) {
            log.debug(e.getMessage());
	    //            usage();
        }
    	boolean help = ((Boolean)parser.getOptionValue(helpOption, Boolean.FALSE)).booleanValue();
    	String host = (String) parser.getOptionValue(hostOption,"localhost");
   	int port = ((Integer) parser.getOptionValue(portOption,new Integer(4322))).intValue();
	
	if (help) {
	    usage();
	    System.exit(2);
	}

	//Open the good API's
	try {
	    Vector comms = ComWorld.getMultipleComms(host, "rmi", port, "dc", "dr", "dt", "ds");
	    bitdew = new BitDew(comms);
	    activeData = new ActiveData(comms);
	    activeData.start();
	    transferManager = new TransferManager(comms);
	} catch(ModuleLoaderException e) {
	    log.warn("Cannot find service " +e);
	}	
	
	activeData.registerActiveDataCallback(new WorkerCallback());
	
    }

    public static void usage() {
	System.out.println(" -h -host -size");
    }

    public static void main(String[] args) throws Exception {
	Worker2 worker = new Worker2(args);	
    }

    public class WorkerCallback implements ActiveDataCallback {
      
	public void onDataScheduled(Data data, Attribute attr) {
	    try {
		if (attr.getname().equals("binary")) {
		    log.debug("binary data scheduled " + data.getname());
		    binaryFile = new File("tmp_worker", "myApp.exe");
		    bitdew.get(data, binaryFile);
		    transferManager.waitFor(data);
		    Thread.sleep(EXECUTION_TIME);
		    Data result = bitdew.createData("result-" + data.getname(), "dummy", 10 );
		    bitdew.put(result,  bitdew.createLocator("result"));
		    activeData.schedule(result, attrResult);
		}	    
	    } catch (Exception bde) {
		log.debug("exception happened when treating new data arrival: " + bde);
	    }
	}
	
	public void onDataDeleted(Data data, Attribute attr) {
	    System.exit(2);
	}
    }
}
