package xtremweb.role.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.transman.TransferManager;
import xtremweb.core.com.com.CommRMIdc;
import xtremweb.core.com.com.CommRMIdr;
import xtremweb.core.com.com.CommRMIds;
import xtremweb.core.com.com.CommRMIdt;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.serv.dt.OOBTransfer;

/**
 * This class deploy a test in which one node running bitdew services send concurrently files to different hosts running
 * dr services.
 * @author josefrancisco
 *
 */
public class TestDataTransfer {
    
    /**
     * Transfer Manager
     */
    private TransferManager tm;
    
    /**
     * Log
     */
    private Logger log = LoggerFactory.getLogger("TestDataTransfer");
    
    /**
     * Number of http transfers
     */
    private int NUM_HTTP;
    
    /**
     * Number of ftp transfers
     */
    private int NUM_FTP;

    /**
     * Automate test for transfers
     * 
     * @param params
     *            the program parameters : 
     *            params[0] file name with the noden list 
     *            params[1] all nodes between 0 and params[1] positions on
     *            nodelist will be using http protocol 
     *            params[2] all nodes between params[1] and params[2] on the nodelist will be using ftp protocol 
     *            params[3] all nodes between params[2] and params[3] on the nodelist will be using scp protocol 
     *            params[4] BATCH size, each host will be assigned exactly BATCH files.
     */
    public TestDataTransfer(String[] params) {
	log.setLevel("debug");
	int iter = 0;
	NUM_HTTP = Integer.parseInt(params[1]);
	NUM_FTP = Integer.parseInt(params[2]);
	int NUM_SCP = Integer.parseInt(params[3]);
	int BATCH = Integer.parseInt(params[4]);
	String filename = params[5];
	int batchindex = 0;
	Vector v = new Vector();
	OOBTransfer oob = null;
	File f = new File(System.getProperty("user.dir") + File.separator + filename);
	try {
	    BufferedReader buff = new BufferedReader(new FileReader(params[0]));
	    Data d = new Data();
	    Vector uids = new Vector();
	    CommRMIdt dt = (CommRMIdt) ComWorld.getComm("localhost", "RMI",
		    4325, "dt");
	    tm = new TransferManager(dt);
	    tm.start();
	    CommRMIdc dc = (CommRMIdc) ComWorld.getComm("localhost", "RMI",
		    4325, "dc");
	    CommRMIds ds = (CommRMIds) ComWorld.getComm("localhost", "RMI",
		    4325, "ds");
	    String s;
	    s = buff.readLine();
	    s = buff.readLine();

	    for (int beginin = 1; beginin <= NUM_HTTP; beginin++) {
		log.info("Setting http repository in " + s);
		CommRMIdr dr = (CommRMIdr) ComWorld.getComm(s, "RMI", 4325,
			"dr");
		BitDew bd = new BitDew(dc, dr, dt, ds);
		batchindex = 0;
		for (; batchindex < BATCH; batchindex++) {
		    d = bd.createData("bitdew" + beginin + batchindex + ".mf");
		    v.add(d.getuid());
		    oob = bd.put(f, d, "http");// protocol http
		    System.out.println("transfer uid in testdatatransfer "
			    + oob.getTransfer().getuid());
		    tm.registerTransfer(oob);
		}
		s = buff.readLine();
	    }

	    for (int beginin = NUM_HTTP + 1; beginin <= NUM_HTTP + NUM_FTP; beginin++) {
		log.info("Setting ftp repository in " + s);
		CommRMIdr dr = (CommRMIdr) ComWorld.getComm(s, "RMI", 4325,
			"dr");
		BitDew bd = new BitDew(dc, dr, dt, ds);
		batchindex = 0;
		for (; batchindex < BATCH; batchindex++) {
		    d = bd.createData("bitdew" + beginin + batchindex + ".mf");
		    v.add(d.getuid());
		    oob = bd.put(f, d, "ftp");// protocol ftp
		    System.out.println("transfer uid in testdatatransfer "
			    + oob.getTransfer().getuid());
		    tm.registerTransfer(oob);
		}
		s = buff.readLine();
	    }

	    for (int beginin = NUM_HTTP + NUM_FTP + 1; beginin <= NUM_HTTP + NUM_FTP + NUM_SCP; beginin++) {
		log.info("Setting scp repository in " + s + "beginin is " + beginin + " http  "  + NUM_HTTP + " ftp " + NUM_FTP + " SCP " + NUM_SCP );
		CommRMIdr dr = (CommRMIdr) ComWorld.getComm(s, "RMI", 4325,
			"dr");
		BitDew bd = new BitDew(dc, dr, dt, ds);
		batchindex = 0;
		for (; batchindex < BATCH; batchindex++) {
		    d = bd.createData("bitdew" + beginin + batchindex + ".mf");
		    v.add(d.getuid());
		    oob = bd.put(f, d, "scp");// protocol scp
		    tm.registerTransfer(oob);
		}

		s = buff.readLine();
	    }

	    log.info("waiting ...");
	    tm.waitForAllData();
	    log.info("finish waiting ");
	    buff.close();
	    batchindex = 0;
	    BufferedReader sec = new BufferedReader(new FileReader(params[0]));

	    s = sec.readLine();
	    s = sec.readLine();
	    for (int beginin = 1; beginin <= NUM_HTTP + NUM_FTP + NUM_SCP; beginin++) {
		log.info("Setting http repository in " + s);
		CommRMIdr dr = (CommRMIdr) ComWorld.getComm(s, "RMI", 4325,
			"dr");
		BitDew bd = new BitDew(dc, dr, dt, ds);
		batchindex = 0;
		for (; batchindex < BATCH; batchindex++) {
		    d = bd.searchDataByUid((String) v.get(iter));
		    log.info("data for host " + s + " is " + d.getuid());
		    f = new File("results/" + d.getuid());
		    oob = bd.get(d, f);
		    tm.registerTransfer(oob);
		    iter++;
		}

		s = sec.readLine();

	    }
	    log.info("Get " + iter + " files");
	    log.info("waiting for all data");
	    tm.waitForAllData();
	    log.info(" finish waiting ");
	    tm.stop();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }
    
    /**
     * Main method only forwards to TestDatatransfer
     * @param args
     */
    public static void main(String[] args) {

	new TestDataTransfer(args);
    }

}
