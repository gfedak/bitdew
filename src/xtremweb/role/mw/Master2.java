package xtremweb.role.mw;

/**
 * Describe class Master2 here.
 *
 *
 * Created: Tue Dec 25 18:32:49 2007
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
import xtremweb.core.db.*;
import xtremweb.core.serv.*;
import xtremweb.core.conf.*;
import xtremweb.serv.ds.*;
import xtremweb.core.com.idl.*;

import xtremweb.api.bitdew.*;
import xtremweb.api.activedata.*;
import xtremweb.api.transman.*;
import xtremweb.core.obj.dc.*;
import xtremweb.core.obj.ds.*;
import xtremweb.role.ui.*;

public class Master2 {

    Logger log = LoggerFactory.getLogger(Master2.class);
    BitDew bitdew = null;
    ActiveData activeData = null;
    TransferManager transferManager = null;
    CmdLineParser parser = new CmdLineParser();
    int result=0;
    int params;
    Attribute collectorAttr = null;
    public static void usage() {
	System.out.println(" -h -host -size");
    }


    public Master2(String[] args) throws Exception{

	File binaryFile = new File("tmp/master", "binaryFile");	

	CmdLineParser.Option helpOption = parser.addBooleanOption('h', "help");
	CmdLineParser.Option sizeOption = parser.addIntegerOption("size");
	CmdLineParser.Option portOption = parser.addIntegerOption("port");
	CmdLineParser.Option paramsOption = parser.addIntegerOption("params");
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
   	int size = ((Integer) parser.getOptionValue(sizeOption,new Integer(0))).intValue();
   	int port = ((Integer) parser.getOptionValue(portOption,new Integer(4322))).intValue();
	params = ((Integer) parser.getOptionValue(paramsOption,new Integer(0))).intValue();
	
	if (help) {
	    usage();
	    System.exit(2);
	}
	UIFactory.createUIFactory();
	//assumes that when running on the same host, we start services locally
	if (host.equals("localhost")) {
	    String[] modules = {"dc","dr","dt","ds"};
	    ServiceLoader sl = new ServiceLoader("RMI", port, modules);
	}
	try {
	    Vector comms = ComWorld.getMultipleComms(host, "rmi", port, "dc", "dr", "dt", "ds");
	    bitdew = new BitDew(comms);
	    activeData = new ActiveData(comms);
	    transferManager = new TransferManager(comms);
	} catch(ModuleLoaderException e) {
	    log.warn("Cannot find service " + e);
	}

	/*no creates data and attributes
	Attribute inputAttr      = new Attribute();
	Attribute parameterAttr  = new Attribute();
	Attribute collectorAttr  = new Attribute();
	Attribute resultatAttr   = new Attribute();
	*/


	//with first create a Data with name binaryName, with protocol dummy and size 120 sec
	Data binaryData = bitdew.createData("binaryName", "dummy", 120);

	//next we send this data to the TupleSpace
	bitdew.put(binaryData, bitdew.createLocator("binarydata"));

	//we create an attribute to this data, specifying that the data should be send to every node
	Attribute binaryAttr = activeData.createAttribute("attr binary = { replicat = -1 , oob = dummy}");

	log.debug(" created attributer binaryAttr" + AttributeUtil.toString(binaryAttr));

	//now the data can be schedule
	activeData.schedule(binaryData, binaryAttr);
	/*	
	//same thing for parameters
	Attribute paramAttr = activeData.createAttribute("attr param = { oob = dummy  }");

	for (int i=0; i<params; i++) {
	    Data paramData = bitdew.createData("param" + i, "dummy", 120);
	    bitdew.put(paramData, bitdew.createLocator("param"+i));
	    activeData.schedule(paramData, paramAttr);
	}
	*/
	//TODO the input data which should be everywhere the parameters are

	//create a special data called the collector which will help to gather all the results
	
	Data collectorData = bitdew.createData("collector");
	collectorAttr = activeData.createAttribute("attr collector = { oob=dummy}");
	activeData.scheduleAndPin(collectorData, collectorAttr, ComWorld.getHost());
       
	//ajoute un callback
	//callback sur des data schedul'ees de type result, les downloader 
	//lorsque le dernier result et obtenu faire une barriere sur le transfer manager
	//lorsque la barriere est passee, mettre un temps absolu au collecteur `a now.

	//TODO :
	// change everything from attribute to the active data API
	// evrything to relative to file transfer manager to transfer manager
	// everyhting relative to tuplespace creation to bitdew api

    }

    public static void main(String[] args) throws Exception {
	Master2 master = new Master2(args);
    }

    public class MasterCallback implements ActiveDataCallback {
	
	public void onDataScheduled(Data data, Attribute attr) {
	    try {
		
		if (attr.getname().equals("binary")) {
		    bitdew.get(data, new File("tmp_master",data.getname()));
		}
		/*		if (attr.getname().equals("result")) {
		    bitdew.get(data, new File("tmp_master",data.getname()));
		    if (result++ == params) {
			//at the end we delete all the data refering to this attribute
			AttributeType.setAttributeTypeOn( collectorAttr, AttributeType.LFTABS );
			collectorAttr.setlftabs(0);
			activeData.registerAttribute(collectorAttr);
		    }
		}
		*/
	    } catch (Exception e) {}
	}
	
	public void onDataDeleted(Data data, Attribute attr) {
	    try {
		if (attr.getname().equals("collector")) {
		    //cleanup
		    transferManager.barrier();
		    System.exit(0);
		} 
	    } catch (Exception e) {}
	}
    }
}
