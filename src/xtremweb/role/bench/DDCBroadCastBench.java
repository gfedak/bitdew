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

import jargs.gnu.CmdLineParser;

public class DDCBroadCastBench {

    protected static DistributedDataCatalog ddc = null;
    protected BitDew bitdew;
    private static  PrintStream os;
    public static Logger log = LoggerFactory.getLogger(DDCBroadCastBench.class);
    public DDCBroadCastBench(BitDew bd) {
    	bitdew = bd;
        } // Bench constructor
    public static final String DEFAULT_TEMP_DIR_NAME = ".";
    public static String tempDirName = DEFAULT_TEMP_DIR_NAME;
   
    
    public boolean verifyFile(int taille, int rang, int loop)  {
    	String filename = "COPY";
    	File file = new File(tempDirName + File.separator + filename + "_" + taille + "_" + loop);
    	//check if the file exists
    	if (file.exists()) {
    	    byte[] buffer = new byte[1024];

    	    try {
    		FileInputStream fis = new FileInputStream( file );
    		// buffer is copied to the file
    		for (int i = 0; i< rang; i++) {
    		    if (fis.read(buffer)  != 1024) return false;
    		    for (int j=0; j< 1024; j++) {
    			if (buffer[j] != ((byte) j)) return false;
    		    }
    		}
    	    } catch (Exception e){ 
    	    	
    		log.fatal("Verifying file " + e);
    		
    		return false;
    	    }
    	} else return false;
    	return true;
        }
    
    public void BroadCastBenchGetData(int size, int loop, String output,Data dataToBc) throws BitDewException{
//       
    	
    	
    	long durations;
//    	int PUT=0;
//    	int GET=1;
    	
    	
    	
    	log.debug("size : " + loop + " " + size);
    	
      	    
    
        long start=System.currentTimeMillis();
        bitdew.get(dataToBc,new File(tempDirName +File.separator+ "COPY_" + size + "_" + loop));
    	long end=System.currentTimeMillis();
    	durations=end-start;
//    if (!verifyFile(s, inc, l)) throw new BitDewException("File COPY_" + s + "_" + inc + " is corrupted" );
  
    

        
        
    	if (output != null) {
    		
    	
    	 try {
    		   File outputfile=new File(output);
    		   if (!outputfile.exists()) {
    	        os = new PrintStream(outputfile);
    		   }
    		 } catch (FileNotFoundException fnfe) {
    		    os = System.out;
    		}
    	    }
    	 else
    		os = System.out;
    	
       
    		os.println(size+ "\t" + durations);
       
    	  
      
}

    
    public static void main(String[] args) throws Exception {

    InterfaceRMIbench ibench=null;
    BitDew bitdew = null;
    DDCBroadCastBench ddcbcbench=null;
    int roundBc=0; //Round of BroadCasting
	
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

  

	long myrank=0;
	String hostName = java.net.InetAddress.getLocalHost().getHostName();
//    log.info("hostname:"+hostName);
	try {
	    InterfaceRMIdc cdc = (InterfaceRMIdc) ComWorld.getComm( host, "rmi", 4322, "dc" );
	    InterfaceRMIdr cdr = (InterfaceRMIdr) ComWorld.getComm( host, "rmi", 4322, "dr" );
	    InterfaceRMIdt cdt = (InterfaceRMIdt) ComWorld.getComm( host, "rmi", 4322, "dt" );
	    InterfaceRMIds cds = (InterfaceRMIds) ComWorld.getComm( host, "rmi", 4322, "ds" );
	    //	    Bench bench = new Bench( new BitDew( cdc, cdr, cdt));
	    bitdew =  new BitDew( cdc, cdr, cdt, cds);	
	    ddcbcbench=new DDCBroadCastBench(bitdew);
//	    log.info("host:"+host);
	    ibench = (InterfaceRMIbench) ComWorld.getComm( host, "rmi", 4322, "bench" );
	    log.info("hostname:"+hostName);	    
	    myrank = ibench.register(hostName);
	    log.info(String.valueOf(myrank));

	} catch(ModuleLoaderException e) {
	    log.warn("Cannot find service " +e);
	    System.exit(0);
	}

	while (true) {
		
	    roundBc++;
	    Data dataToBc = ibench.startExperienceBroadcast();
	    
	    if (dataToBc==null) 
	    	System.exit(0);
	    long start=System.currentTimeMillis();
	    ddcbcbench.BroadCastBenchGetData(size,roundBc,output,dataToBc);
	    long end=System.currentTimeMillis();
//	    if (!verifyFile(s, inc, l)) throw new BitDewException("File COPY_" + s + "_" + inc + " is corrupted" );
//	    effecer le fichier local
	    String [] v=new String[8];
	    ibench.endExperience(myrank,end-start,v);
	}

    }

	    
	
    

}


