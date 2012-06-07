package xtremweb.serv.dc;

/**
 * Test.java
 *
 *
 * Created: Mon Mar  6 16:10:40 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */
import xtremweb.core.com.com.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.obj.dc.Data;
import java.io.*;

import java.util.Timer;
import java.util.TimerTask;

import jargs.gnu.CmdLineParser;


public class Bench {

    public static int iter;
    public static int hits;
    public static long start;

    public Bench() {	
    } // Test constructor

    private static void usage() {
	    System.out.println("Usage : java xtremweb.serv.dc.Bench  [{--db=} database_engine] [{--com=} comunication_layer] [--loop= iteration_number] [--host=hostname] --create filename " );
	    System.exit(2);
    }

    public static void main(String[] args) {

	Bench bench = new Bench();
 
	CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option helpOption = parser.addBooleanOption('h', "help");
        CmdLineParser.Option dbOption = parser.addStringOption("db");
        CmdLineParser.Option comOption = parser.addStringOption("com");
	CmdLineParser.Option loopOption = parser.addIntegerOption("loop");
	CmdLineParser.Option hostOption = parser.addStringOption("host");
        try {
            parser.parse(args);
        }
        catch ( CmdLineParser.OptionException e ) {
            System.err.println(e.getMessage());
            usage();
            System.exit(2);
        }

	String db = (String) parser.getOptionValue(dbOption,"hsqldb");
	String com = (String) parser.getOptionValue(comOption,"local");
	String host = (String) parser.getOptionValue(hostOption,"localhost");
	boolean help = ((Boolean)parser.getOptionValue(helpOption, Boolean.FALSE)).booleanValue();
	int loop = ((Integer) parser.getOptionValue(loopOption,new Integer(1))).intValue();

	if (help) 
	    usage(); 
	else 
	    System.out.println("Benchmark parameters : [ "  + db + " | " + com + " | " + loop +" ]") ;

	Callbackdc dc = new Callbackdc();
	Data data;
	long uid;
	
	iter = loop;
	//data creation 
	data = DataUtil.fileToData(new File("Makefile"));
	start = System.currentTimeMillis();
	Timer timer = new Timer();
	timer.scheduleAtFixedRate(
				  new TimerTask() {
				      public void run() {
					  Bench.iter--;
					  long end=System.currentTimeMillis();
					  System.out.println("Operation per seconds "+ Bench.hits/((end-start)/1000));
					  Bench.hits=0;
					  Bench.start=end;
				      }
				  },
				  10 * 1000,      // run in 10 seconds
				  10 * 1000);     // every  10 seconds
	hits=0;
	
	if (com.equals("local"))  {
	    try {
		while (iter!=0 ) {		    
		    dc.createData(data.getname(), data.getchecksum(), data.getsize(),0);
		    hits++;
		}
	    }catch (Exception re) {
		System.err.println(" n'importe quoi mais bin" );
	    }
	}

	if (com.equals("rmi"))  {
	    CommRMIdc comm = new CommRMIdc();
	    try {
		comm.initComm( host, 4322, "dc");
		while (iter!=0 ) {
		    comm.createData(data.getname(), data.getchecksum(), data.getsize(),0);
		    hits++;
		}
	    } catch(CommException e) {
		System.err.println("Cannot find service " +e + e.lowlevelException);
	    }  catch(Exception e) {
		System.err.println("Interrupted " +e);
	    }
	}
	timer.cancel();
    }
} // Test
