package xtremweb.role.bench;
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
import xtremweb.core.iface.*;
import xtremweb.core.log.*;
import xtremweb.core.serv.*;
import xtremweb.core.conf.*;
import xtremweb.serv.bench.*;
import xtremweb.core.com.idl.*;

import xtremweb.api.bitdew.*;
import xtremweb.core.obj.dc.*;

public class BenchBroadCastMaster {

    /**
     * Creates a new <code>BenchBroadCastMaster</code> instance.
     *test
     */
	protected BitDew bitdew;
	public static Logger log = LoggerFactory.getLogger(BenchBroadCastMaster.class);
	public static final String DEFAULT_TEMP_DIR_NAME = ".";
	public static String tempDirName = DEFAULT_TEMP_DIR_NAME;
    public BenchBroadCastMaster(BitDew bd) {
    	bitdew = bd;
    }
  
    public File createFile(int taille) throws BitDewException{
    	String filename = "TEST";
    	
    	File file = new File(tempDirName + File.separator + filename + "_" + taille);
    	//check if the file exists
    	if (!file.exists()) {
    	    byte[] buffer = new byte[1024];
    	    //buffer is filled with random bits
    	    for (int i=0; i< 1024; i++) {
    		buffer[i]=(byte) i;
    	    }
    	    try {
    		FileOutputStream fos = new FileOutputStream( file );
    		// buffer is copied to the file
    		for (int i = 0; i< taille; i++) {
    		    fos.write(buffer);
    		}
    	    } catch (Exception e){ 
    		log.fatal("Cannot create benchmarked file " + e);
    		throw new BitDewException();
    	    }
    	}
    	return file;
        }


    public static void main(String[] args) throws Exception{
    BitDew bitdew=null;
    BenchBroadCastMaster benchbc=null;
//	Logger log = Logger.getLogger(" ");
	
	 CmdLineParser parser = new CmdLineParser();
	
	 CmdLineParser.Option helpOption = parser.addBooleanOption('h', "help");
	 CmdLineParser.Option oobOption = parser.addStringOption("oob");
	 CmdLineParser.Option outputOption = parser.addStringOption("output");
	 CmdLineParser.Option loopOption = parser.addIntegerOption("loop");
	 CmdLineParser.Option sizeOption = parser.addIntegerOption("size");
	 CmdLineParser.Option hostOption = parser.addStringOption("host");
     CmdLineParser.Option workersOption = parser.addIntegerOption("workers");
	 CmdLineParser.Option benchOption = parser.addStringOption("bench");
	 CmdLineParser.Option dirOption = parser.addStringOption("dir");
	 
	//CmdLineParser.Option sizeOption = parser.addIntegerOption("size");
	//CmdLineParser.Option roundsOption = parser.addIntegerOption("rounds");
	//CmdLineParser.Option workersOption = parser.addIntegerOption("workers");
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
    	int loop = ((Integer) parser.getOptionValue(loopOption,new Integer(1))).intValue();
    	int size = ((Integer) parser.getOptionValue(sizeOption,new Integer(0))).intValue();
    

    	tempDirName = (String) parser.getOptionValue(dirOption,DEFAULT_TEMP_DIR_NAME);

    	ServiceLoader sl = new ServiceLoader("RMI", 4322, "dc","dr","dt","ds");
    	CallbackBroadCastBench cbbench = new CallbackBroadCastBench();
    	sl.addSubCallback("RMI", 4322, "bench", cbbench); 
    	
    	
//	cbbench.configure(workers, size, rounds, "ddcbench");
//	cbbench.configure(workers, size, loop,begin, end, inc,output, "bench");
//    	if (help)
//    	    usage(); 
//   	else 
    	    BenchBroadCastMaster.log.info("Benchmark parameters : [ "  + oob + " | " + loop +" ] [ " + size + " ]" ) ;    	
	try {
	    InterfaceRMIdc cdc = (InterfaceRMIdc) ComWorld.getComm( host, "rmi", 4322, "dc" );
	    InterfaceRMIdr cdr = (InterfaceRMIdr) ComWorld.getComm( host, "rmi", 4322, "dr" );
	    InterfaceRMIdt cdt = (InterfaceRMIdt) ComWorld.getComm( host, "rmi", 4322, "dt" );
	    InterfaceRMIds cds = (InterfaceRMIds) ComWorld.getComm( host, "rmi", 4322, "ds" );
	    bitdew=new BitDew(cdc,cdr,cdt,cds);
	    benchbc = new BenchBroadCastMaster(bitdew);
//	    cbbench.createData(); 
	}catch(ModuleLoaderException e) {
		BenchBroadCastMaster.log.warn("Cannot find service " +e);
	}
//create files to transfer by server. 

	String uids = "";
	File fileToBc = benchbc.createFile(size);
	Data dataToBc = bitdew.createData(fileToBc);
	dataToBc.setoob(oob);//set transferring protocol
	bitdew.put(dataToBc,fileToBc);
	uids = uids + dataToBc.getuid() + " "; 

	log.debug("Bench [" + size + "] data created " + uids );
 
  
	cbbench.configureBroadcast(workers,size, loop,output,dataToBc);
    }

}
