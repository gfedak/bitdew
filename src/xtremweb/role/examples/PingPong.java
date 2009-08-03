package xtremweb.role.examples;

import java.util.Vector;
import xtremweb.core.log.*;
import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.api.activedata.ActiveData;
import xtremweb.api.activedata.ActiveDataException;
import xtremweb.api.activedata.ActiveDataCallback;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.ds.Attribute;


    /*! \example PingPong.java
     *
     *
     * The PingPong example shows how 2 programs exchange data and
     * communicate following the classical ping pon communication
     * scheme. In addition, it illustrates how to delete Data and how 
     * to react to  Data scheduling.
     *
     * The PigPong example extends the HelloWorld example which should
     * be considered first.
     *
     * You should definitely have a look in the source code, PingPong.java.

     * 
     * Examples have similar structure : the example code is usually a
     * client. You will need to execute the different services to be
     * able to start the exemple

     * <ol>
     * <li>start a server. Open a terminal window and start the
     * following command</li>

     @code
     java -jar bitdew-stand-alone.jar serv dc dt dr ds
     @endcode

     * <li> open a new terminal window and start the example

     @code
     java -cp bitdew-stand-alone.jar xtremweb.role.examples.PingPong ping 
     @endcode

     * First, PingPong creates the Ping Data and schedules it.  It
     * registers a callback and wait for the Pong Data to be scheduled.
     *
     *
     * <li> Output of the pin shell should be similar to : 
     @code
     data Ping [01534a30-7ffe-31de-953d-cfc205e73a32] is successfully created and scheduled with attribute PingAttr = {replicat = -1 } 
     @endcode
   
  * <li> start for the second time the example in a new shell. Wait for a few seconds.

     @code
     java -cp bitdew-stand-alone.jar xtremweb.role.examples.PingPong pong
     @endcode

     * <li> On the Pong shell you should see a message similar to :

     @code
     Received data: Ping, whose uid is 01534a30-7ffe-31de-953d-cfc205e73a32, and with Attribute PingPong
     data Pong [343f2db0-7ffe-31de-9b09-318d297a0db4] is successfully created and scheduled with attribute PingPong
     delete and unschedule data Ping
     @endcode

     When the Ping Data is received, a Pong Data is created and
     scheduled. In addition, the Ping data is deleted.

     * <li> On the ping shell, you can see that Pong Data has been
     * received, which will trigger the deletion of Pong Data.

     @code 
     Received data: Pong, whose uid is 343f2db0-7ffe-31de-9b09-318d297a0db4, and with Attribute PingPong
     delete and unschedule data Pong
     @endcode

     * <li> Finnally, you can observe on both shell that Ping and Pong data has
     * been deleted

     @code
     Data : Ping has been deleted, whose uid is   01534a30-7ffe-31de-953d-cfc205e73a32
     Data : Pong has been deleted, whose uid is 343f2db0-7ffe-31de-9b09-318d297a0db4
     @endcode
    *<li> Source code of the PingPong class:
     */



/**
 * <code>PingPong</code> shows how to create and delete Data and how to react to data schedule and deletion
 *
 * @author <a href="mailto:Gilles.Fedak@inria.fr">Gilles Fedak</a>
 * @version 1.0
 */

public class PingPong {

    Logger log = LoggerFactory.getLogger("PingPong");
    BitDew bitdew;
    ActiveData activeData;
    

    /**
     * Creates a new <code>PingPong</code> instance.
     *
     * @param host  <code>String</code>, the hostName of the dc, dr and ds server
     * @param port <code>int</code>, port on which the server hould be contacted
     * @exception Exception if an error occurs
     */
    public PingPong(String host, int port) throws Exception {

	//intialize the communication vectors which will be used by
	//the API
	Vector comms = ComWorld.getMultipleComms(host, "rmi", port, "dc", "dr",  "ds");

	//now intialize the APIs
	bitdew = new BitDew(comms);
	activeData = new ActiveData(comms);	

    }

    /**
     * <code>Ping</code> create a Data Ping and schedule it. Then  waits for the Pong data and delete it.
     *
     * @exception Exception if an error occurs
     */
    public void Ping() throws Exception{

	//now creates  data named Ping
	Data ping = bitdew.createData("Ping");
	
	//creates an Attribute, which has the property to create a
	//data replica for every host
	Attribute attr = activeData.createAttribute("attr PingPong = {replicat = 1 }");

	//associates the Attribute with the Data
	activeData.schedule(ping, attr);
	log.info(" data "+ ping.getname() + " [" + ping.getuid() + "] is successfully created and scheduled with attribute PingAttr = {replicat = -1 } " );

	//register the PingCallback to handle Data creation 
	activeData.registerActiveDataCallback(new PingCallback());	

	activeData.start();
    }


    /**
     *  <code>PingCallback</code> handles scheduling of Data.
     *
     */
    public class PingCallback implements ActiveDataCallback {

	/**
	 * <code>onDataScheduled</code> will be called when a data is received
	 *
	 * @param data a <code>Data</code> value
	 * @param attr an <code>Attribute</code> value
	 */
	public void onDataScheduled(Data data, Attribute attr) {
	    log.info("Received data: " + data.getname() + ", whose uid is " + data.getuid()  + ", and with Attribute " + attr.getname());
	    try {
		//delete data wether it is Ping or Pong 
		bitdew.deleteData(data);
		activeData.unschedule(data);
		log.info("delete and unschedule data " + data.getname());
	    } catch (BitDewException bde) {
		log.info("cannot delete Pong ");
	    } catch (ActiveDataException ade) {
		log.info("cannot unschedule Pong ");
	    }	    
	}

	/**
	 *  <code>onDataDeleted</code> will be called when a data is deleted
	 *
	 * @param data a <code>Data</code> value
	 * @param attr an <code>Attribute</code> value
	 */
	public void onDataDeleted(Data data, Attribute attr) {
	    log.info("Data : " + data.getname() + " has been deleted, whose uid is " + data.getuid());//  + ", and with Attribute " + attr.getname());
	    //FIXME attr is null, I don't know why
	}
    }

    /**
     *  <code>Pong</code> waits for the Ping Data. When Ping is received, it creates and schedule Pong and delete Ping,
     *
     */
    public void Pong() {
	//register the PongCallback to handle Data creation 
	activeData.registerActiveDataCallback(new PongCallback(bitdew, activeData));	
	activeData.start();
    }

    /**
     *  <code>PingPongCallback</code> handles scheduling of Data.
     *
     */
    public class PongCallback implements ActiveDataCallback {

	BitDew bitdew;
	ActiveData activeData;

	/**
	 *  <code>PongCallback</code> will be called when a data is received
	 *
	 * @param bd a <code>BitDew</code> value
	 * @param ad an <code>ActiveData</code> value
	 */
	public PongCallback(BitDew bd, ActiveData ad) {
	    bitdew = bd;
	    activeData = ad;
	}

	/**
	 *  <code>onDataScheduled</code> will be called when a data is received
	 *
	 * @param data a <code>Data</code> value
	 * @param attr an <code>Attribute</code> value
	 */
	public void onDataScheduled(Data data, Attribute attr) {
	    log.info("Received data: " + data.getname() + ", whose uid is " + data.getuid()  + ", and with Attribute " + attr.getname());

	    try {
		//create pong
		Data pong = bitdew.createData("Pong");
		activeData.schedule(pong, attr);
		log.info(" data "+ pong.getname() + " [" + pong.getuid() + "] is successfully created and scheduled with attribute " +  attr.getname() );

	    } catch (BitDewException bde) {
		log.info("cannot create Pong " + bde);
	    } catch (ActiveDataException ade) {
		log.info("cannot schedule Pong " + ade);
	    }

	    try {
		//delete Ping data 
		bitdew.deleteData(data);
		activeData.unschedule(data);
		log.info("delete and unschedule data " + data.getname());
	    } catch (BitDewException bde) {
		log.info("cannot delete Ping " + bde);
	    } catch (ActiveDataException ade) {
		log.info("cannot unschedule Ping " + ade);
	    } 
	}

	/**
	 *  <code>onDataDeleted</code> will be called when a data is deleted
	 *
	 * @param data a <code>Data</code> value
	 * @param attr an <code>Attribute</code> value
	 */
	public void onDataDeleted(Data data, Attribute attr) {
	    log.info("Data : " + data.getname() + " has been deleted, whose uid is " + data.getuid());//  + ", and with Attribute " + attr.getname());
	    //FIXME attr is null, I don't know why
	}
    }

    /**
     *  <code>usage</code> describes PingPong usage
     *
     */
    static public void usage() {
	System.out.println("Usage : java xtremweb.role.examples.PingPong ping|pong [hostName] [port]");
	System.exit(0);
    }

    /**
     *  <code>main</code> method launches PingPong example
     *
     * @param args a <code>String</code> value
     * @exception Exception if an error occurs
     */
    public static void main(String[] args) throws Exception {
	String role;
	if ((args.length == 0) || (args.length > 2))
	    PingPong.usage();
	role = args[0].toLowerCase();
	if (!(role.equals("ping") || role.equals("pong")))
	    PingPong.usage();

 	String hostName = "localhost";
	if (args.length>1) hostName = args[1];
	int port = 4325;
	if (args.length==3) 
	    port=Integer.parseInt(args[2]);
	PingPong pp = new PingPong( hostName, port);
	if (role.equals("ping"))
	    pp.Ping();
	else 
	    pp.Pong();
    }

}
