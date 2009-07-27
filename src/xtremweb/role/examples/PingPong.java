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

/**
 * <code>PingPong</code>.
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
     * @param host a <code>String</code> value
     * @param port an <code>int</code> value
     */
    public PingPong(String host, int port) throws Exception {

	//intialize the communication vectors which will be used by
	//the API
	Vector comms = ComWorld.getMultipleComms(host, "rmi", port, "dc", "dr",  "ds");

	//now intialize the APIs
	bitdew = new BitDew(comms);
	activeData = new ActiveData(comms);	

    }

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

	public void onDataDeleted(Data data, Attribute attr) {
	    log.info("Data : " + data.getname() + " has been deleted, whose uid is " + data.getuid());//  + ", and with Attribute " + attr.getname());
	    //FIXME attr is null, I don't know why
	}
    }

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

	public PongCallback(BitDew bd, ActiveData ad) {
	    bitdew = bd;
	    activeData = ad;
	}

	/**
	 * Describe <code>onDataScheduled</code> method here.
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

	public void onDataDeleted(Data data, Attribute attr) {
	    log.info("Data : " + data.getname() + " has been deleted, whose uid is " + data.getuid());//  + ", and with Attribute " + attr.getname());
	    //FIXME attr is null, I don't know why
	}
    }



    static public void usage() {
	System.out.println("Usage : java xtremweb.role.examples.PingPong ping [hostName] [port]");
	System.exit(0);
    }


    //usage is localhost if your services (dc, ds) run on the same host
    //otherwise
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
