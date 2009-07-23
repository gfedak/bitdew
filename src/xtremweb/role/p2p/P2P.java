package xtremweb.role.p2p;


import xtremweb.core.log.*;
import xtremweb.api.bitdew.BitDew;
import xtremweb.serv.dc.DataUtil;
import xtremweb.api.transman.TransferManager;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.obj.dc.Data;

import java.util.Vector;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
/**
 *  <code>P2P</code>.
 *
 * @author <a href="mailto:Gilles.Fedak@inria.fr">Gilles Fedak</a>
 * @version 1.0
 */

public class P2P {

    private static int DEFAULT_PORT = 4325;

    private BitDew bitdew;
    private TransferManager transferManager;

    Logger log = LoggerFactory.getLogger("PutGet");

    private String hostName;
    private int port;

    private String dir;

    public P2P(String [] args) {
	//determine hostName
	try {
	    InetAddress thisIp =InetAddress.getLocalHost();
	    hostName = thisIp.getHostAddress();
	}
	catch(Exception e) {
	    log.fatal("cannot determine localhost ip address");
	    System.exit(1);
	}

	if ( (args.length != 1) &&  (args.length != 2) ) {
	    log.info("usage: p2p [port] directory");
	    System.exit(0);
	}

	if (args.length == 1)
	    dir = args[0];

	if (args.length == 2) {
	    //determine port
	    try {
		port = Integer.parseInt(args[0]);
	    } catch (Exception e) {
		port = DEFAULT_PORT;
	    }
	    dir = args[1];
	}


    }

    public void mainLoop() {
	log.info("starting BitDew P2P on host " + hostName + " on port " + port + " using directory " + dir);
	log.info("scanning directory " + dir);


	File fic = new File(dir,"binaryFile");
	Data data = bitdew.createData(fic);
	Locator loc = bitdew.createLocator("binaryFile");
	bitdew.put(data,loc);

	System.out.print("> ");
	try {
	    while (true) {
		int lu = System.in.read();

		switch (lu) {
		    //\r
		case (10) :
		    System.out.print("> ");
		    break;
		    //l
		case (108) :
		    log.info("list current catalog");
		    break;
		    //s
		case (115) :
		    log.info("search string");
		    break;
		    //d
		case (100) :
		    log.info("download");
		    break;
		    //p
		case (112) :
		    log.info("current file transfer");
		    break;
		    //q
		case (113) :
		    log.info("exit now !");
		    // (confirm with y)
		    //if (System.in.read() == 121) 
		    System.exit(0);
		    break;
		    //s
		default :
		    log.debug(" code " + lu);
		    break;
			

		}

	    }
	} catch (IOException ioe) {
	    log.fatal("error reading shell");
	}
    } 

    /*
    public void publish(File f) {
    }

    public void index(Data data, String Url) {
    }

    public ArrayList<Data> search(String key) {
    }

    //return host Url
    public String select(ArrayList<Data> files) {
    } 

    public void download(Data data, String Url) {
    }

    */

    public static void main(String[] args) {
	P2P p2p = new P2P(args);
	p2p.mainLoop();
    }

}