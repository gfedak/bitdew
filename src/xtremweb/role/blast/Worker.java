package xtremweb.role.blast;

import xtremweb.core.log.*;
import xtremweb.serv.dc.ddc.*;
import xtremweb.api.bitdew.*;
import xtremweb.serv.dc.*;
import xtremweb.core.iface.*;
import xtremweb.core.com.com.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.conf.ConfigurationProperties;
import xtremweb.core.obj.dc.Data;
import java.net.*;
import java.util.*;
import java.io.*;
import xtremweb.core.exec.*;

import jargs.gnu.CmdLineParser;

public class Worker {
	
	 public static String DEFAULT_APP_EXEC = "/users/parall/hehaiwu/blast/blast-2.2.14/bin/blastall";
	 public static String DEFAULT_APP_OPTION = "-p blastn -i sequence1 -d sts";
	 public static String DEFAULT_UNZIP_EXEC = "tar";
	 public static String DEFAULT_UNZIP_OPTION= "zvxf";
	 public static String DEFAULT_UNZIP_DIR="/tmp/btpd/data/";
	 public static String DEFAULT_APPEXEC_DIR="/tmp/btpd/data/sts/";
	 
	 private static String appExec;
	 private static String appOption;
	 private static String unzipExec;
	 private static String unzipOption;
	 private static String unzipDir;
	 private static String appexecDir;
	 
    protected static DistributedDataCatalog ddc = null;
    protected BitDew bitdew;
    private static  PrintStream os;
    public static Logger log = LoggerFactory.getLogger(Worker.class);
    
    static {
    	init();
        }
    
    public static void setAppExec(String app) {
    	appExec = app;
        }
    public static void setAppOption(String appop) {
    	appOption = appop;
        }
    public static void setUnzipExec(String unzip) {
    	unzipExec = unzip;
        }
    public static void setUnzipOption(String unzipop) {
    	unzipOption = unzipop;
        }
    
    public static void setUnzipDir(String unzipd) {
    	unzipDir = unzipd;
        }
    public static void setAppExecDir(String appexecd) {
    	appexecDir = appexecd;
        }
    
    public static void init() {
    	//getting unzip and apps configuration properties
    	Properties mainprop;		
    	try {
    	    mainprop = ConfigurationProperties.getProperties();
    	} catch (ConfigurationException ce) {
    	    log.warn("No Bittorrent Protocol Information found : " + ce);
    	    mainprop = new Properties();
    	}

    	//initializing the BittorrentTools
    	setAppExec(mainprop.getProperty("xtremweb.role.mw.appExec",DEFAULT_APP_EXEC));
    	setAppOption(mainprop.getProperty("xtremweb.role.mw.appOption",DEFAULT_APP_OPTION));
    	setUnzipExec(mainprop.getProperty("xtremweb.role.mw.unzipExec",DEFAULT_UNZIP_EXEC));
    	setUnzipOption(mainprop.getProperty("xtremweb.role.mw.unzipOption",DEFAULT_UNZIP_OPTION));
    	setUnzipDir(mainprop.getProperty("xtremweb.role.mw.unzipDir",DEFAULT_UNZIP_DIR));
    	setAppExecDir(mainprop.getProperty("xtremweb.role.mw.appexecDir",DEFAULT_APPEXEC_DIR));
        }
    public Worker(BitDew bd) {
    	bitdew = bd;
        } // Bench constructor
    public static final String DEFAULT_TEMP_DIR_NAME = ".";
    public static String tempDirName = DEFAULT_TEMP_DIR_NAME;
    
    
   
    
       
    public void BroadCastWorkerGetData(String output,Data dataToBc,String filetodl) throws BitDewException{
//       
    	
    	
    	long durations;
//    	int PUT=0;
//    	int GET=1;
    	
    	
    	
//    	log.debug("size : " + loop + " " + size);
    	
      	    
    
        long start=System.currentTimeMillis();
        
        bitdew.get(dataToBc,new File(tempDirName +File.separator+ filetodl));
        
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
    	
       
    		os.println(durations);
       
    	  
      
}
    public static void UnzipFile(String fileName) {
 
 //   	String execString ="tar"  + " " +"zvxf"+" "+"/tmp/btpd/data/"+fileName;
       	String execString =unzipExec+ " " +unzipOption+" "+unzipDir+fileName;
//      Executor e = new Executor( execString,"/tmp/btpd/data/") ;
    	Executor e = new Executor( execString,unzipDir) ;
    	log.debug("Untar file  : "+ fileName );
    	try {
    	    e.startAndWait();
    	    e.flushPipe();
    	} catch (ExecutorLaunchException ele) {
    	    System.out.println("Error when launching " + execString + " " + ele);
    	} // end of try-catch
        }
    
    public static void RunApplication() { 
    	 
//    	String execString ="/users/parall/hehaiwu/blast/blast-2.2.14/bin/blastall -p blastn -i sequence1 -d"+" "+DBName;
    	String execString =appExec+" "+appOption;
//    	Executor e = new Executor( execString,"/tmp/btpd/data/"+DBName) ;
    	Executor e = new Executor( execString,appexecDir) ;
    	log.debug("Run  : "+ execString);
    	try {
    	    e.startAndWait();
    	    e.flushPipe();
    	} catch (ExecutorLaunchException ele) {
    	    System.out.println("Error when launching " + execString + " " + ele);
    	} // end of try-catch
        }

    
    public static void main(String[] args) throws Exception {

    InterfaceRMIbench ibench=null;
    BitDew bitdew = null;
    Worker ddcworker=null;
    int roundBc=0; //Round of BroadCasting
	
	 CmdLineParser parser = new CmdLineParser();
	
	 CmdLineParser.Option helpOption = parser.addBooleanOption('h', "help");
	 CmdLineParser.Option oobOption = parser.addStringOption("oob");
	 CmdLineParser.Option outputOption = parser.addStringOption("output");
//	 CmdLineParser.Option loopOption = parser.addIntegerOption("loop");
//	 CmdLineParser.Option sizeOption = parser.addIntegerOption("size");
	 
	 
	 
	 CmdLineParser.Option hostOption = parser.addStringOption("host");
     CmdLineParser.Option workersOption = parser.addIntegerOption("workers");
	 CmdLineParser.Option benchOption = parser.addStringOption("bench");
	 CmdLineParser.Option dirOption = parser.addStringOption("dir");
	 
	 CmdLineParser.Option fileOption = parser.addStringOption("file");
	 
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
 //  	int loop = ((Integer) parser.getOptionValue(loopOption,new Integer(1))).intValue();
 //  	int size = ((Integer) parser.getOptionValue(sizeOption,new Integer(0))).intValue();
   	
   	String filetodl= (String) parser.getOptionValue(fileOption, null);
   	
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
	    ddcworker=new Worker(bitdew);
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
	    ddcworker.BroadCastWorkerGetData(output,dataToBc,filetodl);
	    UnzipFile(filetodl);
	    RunApplication();
	    long end=System.currentTimeMillis();
	    
//	    if (!verifyFile(s, inc, l)) throw new BitDewException("File COPY_" + s + "_" + inc + " is corrupted" );
//	    effecer le fichier local
	    
	    String [] v=new String[8];
	    ibench.endExperience(myrank,end-start,v);
	}

    }

	    
	
    

}


