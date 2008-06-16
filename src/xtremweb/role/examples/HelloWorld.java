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
 * <code>HelloWorld</code>.
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

public class HelloWorld {

    /*! @mainpage BitDew: An Open Source Middleware for Large Scale Data Management
     *
     * This is a short introduction to run the exemple in the BitDew
     * source package 
     *
     * @section download Downloading BitDew
     *
     * Please Download BitDew from the BitDew web site
     * hppt://www.BitDew.net
     * 
     * You can get a complete documentation on the web site.
     *
     * @section run_sec Running the examples
     *
     * To install the BitDew software follow the steps :
     * <ol>
     *  <li> deflate the BitDew archive</li>
     *  <li> move in the BitDew directory</li>
     * </ol>
     *
     * If you modify the source and if you need to compile again
     * BitDew, run the following command :
     @code
     ./build.sh
     @endcode
     * 
     * Examples have similar structure : the example code is usually a
     * client. You will need to execte the different services to be
     * able to start the exemples
     * <ol>
     * <li>start a server. Open a terminal window and start the
     * following command</li>

     @code
     java -jar bitdew-standalone.jar serv dc dt dr ds
     @endcode

     * <li> start the example

     @code
     java -cp bitdew-standalone.jar xtremweb.role.examples.HelloWorld localhost
     @endcode

     * etc...
     */

    /*! \example HelloWorld.java
     * This is an example of how to use the BitDew class.
     * The example creates a data.
     */

    Logger log = LoggerFactory.getLogger("HelloWorld");
    BitDew bitdew;
    ActiveData activeData;

    /**
     * Creates a new <code>HelloWorld</code> instance.
     *
     * @param host a <code>String</code> value
     * @param port an <code>int</code> value
     */
    public HelloWorld(String host, int port) throws Exception {

	//intialize the communication vectors which will be used by
	//the API
	Vector comms = ComWorld.getMultipleComms(host, "rmi", port, "dc", "dr",  "ds");

	//now intialize the APIs
	bitdew = new BitDew(comms);
	activeData = new ActiveData(comms);

	//register the HelloWorldCallback to handle Data creation 
	activeData.registerActiveDataCallback(new HelloWorldCallback());	
	activeData.start();

	//now creates a Data
	Data data = bitdew.createData("hellowWorldData");
	
	//creates an Attribute
	Attribute attr = activeData.createAttribute("attr helloWorldAttr = {replicat = -1 }");

	//associates the Attribute with the Data
	activeData.schedule(data, attr);
    }

    /**
     *  <code>HelloWorldCallback</code> handles scheduling of Data.
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


    public static void main(String[] args) throws Exception {
 	String hostName = "localhost";
	if (args.length>0) hostName = args[0];
	int port = 4325;
	if (args.length==2) Integer.parseInt(args[1]);
	HelloWorld hw = new HelloWorld(hostName, port);
    }

}
