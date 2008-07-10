package xtremweb.role.examples;

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

/**
 * Describe class <code>Updater</code> here.
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class Updater {

    Logger log = LoggerFactory.getLogger(Updater.class);
    BitDew bitdew = null;
    ActiveData activeData = null;
    TransferManager transferManager = null;
    boolean master;
    /**
     * Creates a new <code>Updater</code> instance.
     *
     * @param host  <code>String</code> the hostName of the machine hosting the services
     * @param port  <code>int</code> the port to contact the machine hosting the services
     * @exception Exception if an error occurs
     */
    public Updater(String host, int port) throws Exception {

	//master initialisation : loads the service
	if (master) {
	    String[] modules = {"dc","dr","dt","ds"};
	    ServiceLoader sl = new ServiceLoader("RMI", port, modules);
	}

	//intialise the communication and the APIs
	Vector comms = ComWorld.getMultipleComms(host, "rmi", port, "dc", "dr", "dt", "ds");
	//APIFactory.createAPIs(comms, "BitDew", "ActiveData", "TransferManager");
	bitdew = new BitDew(comms);
	activeData = new ActiveData(comms);
	transferManager = new TransferManager(comms);
	
	activeData.start();
	transferManager.start();

	if (master) {
	    //code for the master
	    File fic = new File("/pat/to/master");
	    Data data = bitdew.createData(fic);
	    bitdew.put(fic, data);
	    Attribute attr = activeData.createAttribute("attr update = {replicat = -1, oob = bittorrent, abstime = 43200  }");
	    activeData.schedule(data, attr);
	} else {
	    //code for the client
	    activeData.registerActiveDataCallback(new UpdaterCallback());
	}
    }

    /**
     * Describe class <code>UpdaterCallback</code> here.
     *
     */
    public class UpdaterCallback implements ActiveDataCallback {

	/**
	 * Describe <code>onDataScheduled</code> method here.
	 *
	 * @param data a <code>Data</code> value
	 * @param attr an <code>Attribute</code> value
	 */
	public void onDataScheduled(Data data, Attribute attr) {
	    try {
		if (attr.getname().equals("update")) {
		    File fic = new File("/path_to_data/to/update/");
		    bitdew.get(data, fic);
		    transferManager.waitFor(data);
		}
	    } catch (Exception e) {}
	}
	
	/**
	 * Describe <code>onDataDeleted</code> method here.
	 *
	 * @param data a <code>Data</code> value
	 * @param attr an <code>Attribute</code> value
	 */
	public void onDataDeleted(Data data, Attribute attr) {
	    (new File("/path_to_data/to/update/")).delete();
	}
    }

}
