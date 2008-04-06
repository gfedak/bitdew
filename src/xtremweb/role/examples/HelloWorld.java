package xtremweb.role.examples;

import java.util.Vector;
import xtremweb.core.log.*;
import xtremweb.api.bitdew.BitDew;
import xtremweb.api.activedata.ActiveData;
import xtremweb.api.activedata.ActiveDataCallback;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.serv.ServiceLoader;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.ds.Attribute;

/**
 * <code>HelloWorld</code> here.
 *
 *
 * Created: Mon Feb 18 21:23:00 2008
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class HelloWorld extends Example {

    /*! @mainpage BitDew: An Open Source Middleware for Large Scale Data Management
     *
     * This is a short introduction to run and program BitDew
     *
     * @section download Downloading BitDew
     *
     * Please Download BitDew from the BitDew web site
     * hppt://www.BitDew.net
     * 
     * @section run_sec Running the examples
     *
     * To install the BitDew software follow the steps :
     * <ol>
     *  <li> deflate the BitDew archive HelloWorld</li>
     *  <li> move in the BitDew directory</li>
     * </ol>
     * All the examples have similar structure : the same code can be run either  as the server ot as a client.
     *
     * - start a server
     @code
     
     @endcode
     *
     * @section program_sec A look at the examples
     *
     * @subsection step1 the HelloWorld example
     *  
     * etc...
     */

    /*! \example AllToAll.java
     * This is an example of how to use the BitDew class.
     * More details about this example.
     */

    Logger log = LoggerFactory.getLogger(Updater.class);
    BitDew bitdew;
    ActiveData activeData;

    /**
     * Creates a new <code>HelloWorld</code> instance.
     *
     * @param host a <code>String</code> value
     * @param port an <code>int</code> value
     */
    public HelloWorld(String host, int port) throws Exception {

	//master initialisation : loads the service
	if (host.equals("localhost")) {
	    String[] modules = {"dc","dr","ds"};
	    ServiceLoader sl = new ServiceLoader("RMI", port, modules);
	}

	//intialise the communication and the APIs
	Vector comms = ComWorld.getMultipleComms(host, "rmi", port, "dc", "dr",  "ds");
	//APIFactory.createAPIs(comms, "BitDew", "ActiveData", "TransferManager");
	bitdew = new BitDew(comms);
	activeData = new ActiveData(comms);
	
	activeData.start();

	if (host.equals("localhost")) {
	    Data data = bitdew.createData("hellowWorldData");
	    Attribute attr = activeData.createAttribute("attr helloWorldAttr = {replicat = -1 }");
	    activeData.schedule(data, attr);
	} else {
	    activeData.registerActiveDataCallback(new HelloWorldCallback());
	}
    }
    /**
     * Describe class <code>HelloWorldCallback</code> here.
     *
     */
    public class HelloWorldCallback implements ActiveDataCallback {

	/**
	 * Describe <code>onDataScheduled</code> method here.
	 *
	 * @param data a <code>Data</code> value
	 * @param attr an <code>Attribute</code> value
	 */
	public void onDataScheduled(Data data, Attribute attr) {
	    System.out.println("Received data : " + data.getname() + "with Attribute : " + attr.getname());
	}

	public void onDataDeleted(Data data, Attribute attr) {}
    }
}
