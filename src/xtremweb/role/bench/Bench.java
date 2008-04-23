package xtremweb.role.bench;

import xtremweb.api.bitdew.*;
import xtremweb.serv.dc.*;
import xtremweb.core.iface.*;
import xtremweb.core.log.*;
import xtremweb.core.com.com.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.log.*;
import xtremweb.core.http.*;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

import jargs.gnu.CmdLineParser;


/**
 *  <code>Bench</code> is a benchmark class which testes pingPong
 *  communications. 
 *  It requires a service nodes running dr, dc dt and the client runs
 *  the Bench class.
 *  The benchmark is a classical pingPong mesure : data are PUT to the
 *  dr and retreived with GET
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class Bench {

    protected BitDew bitdew;

    public static Logger log = LoggerFactory.getLogger(Bench.class);

    /**
     *  <code>DEFAULT_TEMP_DIR_NAME</code> where files are placed /opt/tmp
     *
     */
    public static final String DEFAULT_TEMP_DIR_NAME = "/opt/tmp";

    public static String tempDirName = DEFAULT_TEMP_DIR_NAME;

    /**
     * Creates a new <code>Bench</code> instance.
     *
     * @param bd a <code>BitDew</code> value
     */
    public Bench(BitDew bd) {
	bitdew = bd;
    } // Bench constructor

    private static void usage() {
	Usage usage = new Usage();
	usage.usage("java xtremweb.role.bench.Bench [Options]");
	usage.option("--oob","transfer_protocol");
	usage.option("--loop","iteration_number");
	usage.option("--burst","number of iration treated concurrently");
	usage.option("--host","hostname" );
	usage.option("--size","size of file");
	usage.option("--begin","size begin");
	usage.option("--end","size end");
	usage.option("--inc","size increase");
	usage.option("--dir","local directory");
	System.exit(2);
    }

    /**
     *  <code>createFile</code> create files whith specified size
     *  named TEST_size_loop
     *
     * @param taille an <code>int</code> value
     * @param loop an <code>int</code> value
     * @return a <code>File</code> value
     * @exception BitDewException if an error occurs
     */
    public File createFile(int taille , int loop) throws BitDewException{
	String filename = "TEST";
	File file = new File(tempDirName + File.separator + filename + "_" + taille + "_" + loop);
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

    /**
     *  <code>verifyFile</code> verifies that the file is correct
     *
     * @param taille an <code>int</code> value
     * @param rang an <code>int</code> value
     * @param loop an <code>int</code> value
     * @return a <code>boolean</code> value
     */
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

    /**
     * Describe <code>benchPingPong</code> method here.
     *
     * @param size_begin an <code>int</code> value
     * @param size_end an <code>int</code> value
     * @param size_inc an <code>int</code> value
     * @param loop an <code>int</code> value
     * @param burst an <code>int</code> value
     * @param oob a <code>String</code> value
     * @param output a <code>String</code> value
     * @exception BitDewException if an error occurs
     */
    public void benchPingPong(int size_begin, int size_end, int size_inc, int loop, int burst, String oob, String output) throws BitDewException{
	Data[] datas = new Data[loop];
	File[] files = new File[loop];
	int PUT=0;
	int GET=1;
	int iter = 0;

	for (int s= size_begin ; s < size_end ; s = s* size_inc ) {
	    log.debug("[iter,size] = [" + iter + "," + s + "]");
	    iter++;
	}

	if ((burst !=1) && (loop%burst !=0)) {
	    log.fatal("When using burst mode, make sure that the number of loop is a multiple of the number of burst.\n For instance with loop=12 and burst=3, there will be 4 rounds of 3 concurrent data transfer");
	    System.exit(3);
	}
	//TODO	bitdew.cleanup();
	log.debug("12");
	long[][][] durations = new long[iter][loop][2];
	log.debug("13");
	for (int s= size_begin,inc=0 ; s < size_end ; s= s*size_inc, inc++) {
	    String uids = "";
	    for (int l = 0; l < loop; l++) {
		files[l] = createFile( s, l );
		log.debug("14" + bitdew);
		datas[l] = bitdew.createData(files[l]);
		log.debug("15");
		datas[l].setoob(oob);
		log.debug("16");
		uids = uids + datas[l].getuid() + " "; 
	    }

	    log.debug("Bench [" + s + "] data created " + uids );
	    System.out.println("Benchmarking pingpong");
	    System.out.print("size : " + s + " ");

	    for (int l = 0; l < loop; l+=burst) {
		long start=System.currentTimeMillis();
		for (int b=0; b<burst;b++) 
		    bitdew.put(files[l+b],datas[l+b]);
		
		//Will wait for all on going transfers to be complete
		//TODO		bitdew.barrier();
		// a remplacer par transfer mnager
		long end=System.currentTimeMillis();
		for (int b=0; b<burst;b++) {
		    durations[inc][l+b][PUT]=end-start;
		    System.out.print(".");
		}
	    }


	    for (int l = 0; l < loop; l+=burst) {
		long start=System.currentTimeMillis();
		for (int b=0; b<burst; b++) 
		    bitdew.get(datas[l],new File(tempDirName +File.separator+ "COPY_" + s + "_" + l));
		//TODO		bitdew.barrier();
		//a remplacer par transfer manager
		long end=System.currentTimeMillis();
		for (int b=0; b<burst;b++) {
		    durations[inc][l+b][GET]=end-start;
		    System.out.print(".");
		    if ( (!oob.equals("dummy")) && (!verifyFile(s, inc, l+b))) throw new BitDewException("File COPY_" + s + "_" + inc + " is corrupted" );
		}


	    }
	    System.out.println();
	}
	long mean[] = new long[2];
	for (int b=0; b<2; b++) {
	    PrintStream os;
	    if (output != null) {
		try {
		    os = new PrintStream(new File(output + "_" + b));
		} catch (FileNotFoundException fnfe) {
		    os = System.out;
		}
	    }
	    else {
		os = System.out;
		if (b==PUT) 
		    log.info("PUT");
		if (b==GET) 
		    log.info("GET");

	    }
	    for (int s= size_begin, inc=0 ; s < size_end ; s = s* size_inc, inc++) {
		mean[b] = 0;
		for (int i=0; i< loop; i++) {
		    mean[b] +=  durations[inc][i][b];
		}
		os.println(s + "\t" + mean[b]/loop);
	    }
	}
    }


    public void warmup( String oob) throws BitDewException {
	log.info("Warmup");
	benchPingPong(100, 101, 2, 50, 25, oob, null);
	log.info("Warmup finished");
    }

    /**
     *  <code>main</code> starts the Benchmark
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String [] args) {

	CmdLineParser parser = new CmdLineParser();

        CmdLineParser.Option helpOption = parser.addBooleanOption('h', "help");
        CmdLineParser.Option warmupOption = parser.addBooleanOption("warmup");
	CmdLineParser.Option oobOption = parser.addStringOption("oob");
	CmdLineParser.Option tmpOption = parser.addStringOption("tmp");
        CmdLineParser.Option outputOption = parser.addStringOption("output");

	CmdLineParser.Option loopOption = parser.addIntegerOption("loop");
	CmdLineParser.Option burstOption = parser.addIntegerOption("burst");
	CmdLineParser.Option sizeOption = parser.addIntegerOption("size");
	CmdLineParser.Option beginOption = parser.addIntegerOption("begin");
	CmdLineParser.Option endOption = parser.addIntegerOption("end");
	CmdLineParser.Option incOption = parser.addIntegerOption("inc");
	CmdLineParser.Option hostOption = parser.addStringOption("host");
	CmdLineParser.Option benchOption = parser.addStringOption("bench");
	CmdLineParser.Option dirOption = parser.addStringOption("dir");
	
        try {
            parser.parse(args);
        }
        catch ( CmdLineParser.OptionException e ) {
            Bench.log.debug(e.getMessage());
            usage();
        }

	String oob = (String) parser.getOptionValue(oobOption,"ftp");
	tempDirName = (String) parser.getOptionValue(tmpOption,DEFAULT_TEMP_DIR_NAME);
	String host = (String) parser.getOptionValue(hostOption,"localhost");
	String output = (String) parser.getOptionValue(outputOption, null);
	boolean help = ((Boolean)parser.getOptionValue(helpOption, Boolean.FALSE)).booleanValue();
	boolean warmup = ((Boolean)parser.getOptionValue(warmupOption, Boolean.FALSE)).booleanValue();
	int loop = ((Integer) parser.getOptionValue(loopOption,new Integer(1))).intValue();
	int burst = ((Integer) parser.getOptionValue(burstOption,new Integer(1))).intValue();
	int size = ((Integer) parser.getOptionValue(sizeOption,new Integer(0))).intValue();
	int begin = ((Integer) parser.getOptionValue(beginOption,new Integer(10))).intValue();
	int end = ((Integer) parser.getOptionValue(endOption,new Integer(100))).intValue();
	int inc = ((Integer) parser.getOptionValue(incOption,new Integer(2))).intValue();
	String benchtype = (String) parser.getOptionValue(benchOption,"pingpong");
	tempDirName = (String) parser.getOptionValue(dirOption,DEFAULT_TEMP_DIR_NAME);

	if (help)
	    usage(); 
	else 
	    Bench.log.info("Benchmark parameters : [ "  + oob + " | " + host  + " | " + loop + " | " + burst + " ] [ " + begin + " | " + end + " ]" ) ;
	
	try {
	    InterfaceRMIdc cdc = (InterfaceRMIdc) ComWorld.getComm( host, "rmi", 4322, "dc" );
	    InterfaceRMIdr cdr = (InterfaceRMIdr) ComWorld.getComm( host, "rmi", 4322, "dr" );
	    InterfaceRMIdt cdt = (InterfaceRMIdt) ComWorld.getComm( host, "rmi", 4322, "dt" );
	    InterfaceRMIds cds = (InterfaceRMIds) ComWorld.getComm( host, "rmi", 4322, "ds" );

	    Bench bench = new Bench( new BitDew( cdc, cdr, cdt, cds));

	    if(true)
		(new HttpServer()).start();
	    log.debug("1 : " + cdc);
	    if(warmup)
		bench.warmup(oob);
	    log.debug("2");
	    bench.benchPingPong(begin, end, inc, loop, burst, oob, output);

	} catch(ModuleLoaderException e) {
	    Bench.log.warn("Cannot find service " +e);
	} catch (BitDewException bde) {
	    Bench.log.warn(" BENCHMARK ERROR BitDew  : " + bde);
	    System.exit(0);
	} catch (Exception e) {
	    Bench.log.warn(" BENCHMARK ERROR   : " + e);
	    System.exit(0);
	}
	Bench.log.info("");
	Bench.log.info("  -------------------------------------------");
	Bench.log.info("  ****  BENCHMARK COMPLETE SUCCESFULL  ******");

	System.exit(0);

    }

}