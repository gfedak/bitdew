package xtremweb.role.blast;
/**
 * Describe class BenchMaster here.
 *
 *
 * Created: Fri Oct  6 22:29:35 2006
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */


import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.BrokenBarrierException;

import jargs.gnu.CmdLineParser;
import xtremweb.core.iface.InterfaceRMIdc;
import xtremweb.core.iface.InterfaceRMIdr;
import xtremweb.core.iface.InterfaceRMIdt;
import xtremweb.core.iface.InterfaceRMIds;
import xtremweb.core.log.*;
import xtremweb.core.serv.*;
import xtremweb.core.conf.*;
import xtremweb.core.com.idl.*;

import xtremweb.api.bitdew.*;
import xtremweb.core.obj.dc.*;

public class Master {

    /**
     * Creates a new <code>BenchBroadCastMaster</code> instance.
     *test
     */
	protected BitDew bitdew;
	public static Logger log = LoggerFactory.getLogger(Master.class);
	public static final String DEFAULT_TEMP_DIR_NAME = ".";
	public static String tempDirName = DEFAULT_TEMP_DIR_NAME;
    public Master(BitDew bd) {
    	bitdew = bd;
    }
  
    

    public static void main(String[] args) throws Exception{
    BitDew bitdew=null;
    Master benchbc=null;
//	Logger log = Logger.getLogger(" ");
	
	 CmdLineParser parser = new CmdLineParser();
	
	 CmdLineParser.Option helpOption = parser.addBooleanOption('h', "help");
	 CmdLineParser.Option oobOption = parser.addStringOption("oob");
	 CmdLineParser.Option outputOption = parser.addStringOption("output");
//	 CmdLineParser.Option loopOption = parser.addIntegerOption("loop");
//	 CmdLineParser.Option sizeOption = parser.addIntegerOption("size");
	 CmdLineParser.Option hostOption = parser.addStringOption("host");
     CmdLineParser.Option workersOption = parser.addIntegerOption("workers");
//	 CmdLineParser.Option benchOption = parser.addStringOption("bench");
	 CmdLineParser.Option dirOption = parser.addStringOption("dir");
	 CmdLineParser.Option fileOption = parser.addStringOption("file");
	 
        try {
            parser.parse(args);
        }
        catch ( CmdLineParser.OptionException e ) {
            log.debug(e.getMessage());
	    //            usage();
        }
    	String oob = (String) parser.getOptionValue(oobOption,"ftp");
    	String host = (String) parser.getOptionValue(hostOption,"localhost");
    	int workers = ((Integer) parser.getOptionValue(workersOption,new Integer(1))).intValue();
    	String output = (String) parser.getOptionValue(outputOption, null);
    	boolean help = ((Boolean)parser.getOptionValue(helpOption, Boolean.FALSE)).booleanValue();
//    	int loop = ((Integer) parser.getOptionValue(loopOption,new Integer(1))).intValue();
//    	int size = ((Integer) parser.getOptionValue(sizeOption,new Integer(0))).intValue();
    

    	tempDirName = (String) parser.getOptionValue(dirOption,DEFAULT_TEMP_DIR_NAME);
    	String filetodl= (String) parser.getOptionValue(fileOption, null);

  
    	String[] modules = {"dc","dr","dt","ds"};
    	ServiceLoader sl = new ServiceLoader("RMI", 4322, modules);
    	CallbackBroadCastBench cbbench = new CallbackBroadCastBench();
    	sl.addSubCallback("RMI", 4322, "bench", cbbench); 
    	
    	
    	 Master.log.info("Benchmark parameters : [ "  + oob + " [ " + filetodl + " ]" ) ;    	
	try {
	    InterfaceRMIdc cdc = (InterfaceRMIdc) ComWorld.getComm( host, "rmi", 4322, "dc" );
	    InterfaceRMIdr cdr = (InterfaceRMIdr) ComWorld.getComm( host, "rmi", 4322, "dr" );
	    InterfaceRMIdt cdt = (InterfaceRMIdt) ComWorld.getComm( host, "rmi", 4322, "dt" );
	    InterfaceRMIds cds = (InterfaceRMIds) ComWorld.getComm( host, "rmi", 4322, "ds" );
	    bitdew=new BitDew(cdc, cdr, cdt, cds);
	    benchbc = new Master(bitdew);
//	    cbbench.createData(); 
	}catch(ModuleLoaderException e) {
		Master.log.warn("Cannot find service " +e);
	}
//create files to transfer by server. 

    String uids = "";
//	File fileToBc = benchbc.createFile(size);
    File f0 = new File(filetodl);
	Data dataToBc = bitdew.createData(f0);
	dataToBc.setoob(oob);//set transferring protocol
	bitdew.put(f0,dataToBc);
	uids = uids + dataToBc.getuid() + " "; 


	    int size=1000;
	    int loop=1;
	    
	    cbbench.configureBroadcast(workers,size, loop,output,dataToBc);

    }

}
