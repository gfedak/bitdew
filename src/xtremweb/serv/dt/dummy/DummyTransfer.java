package xtremweb.serv.dt.dummy;

import xtremweb.core.log.*;
import xtremweb.core.uid.*;
import xtremweb.serv.dt.*;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.conf.*;

import java.io.*;
import java.util.Properties;

public class DummyTransfer  
    extends BlockingOOBTransferImpl 
    implements BlockingOOBTransfer, OOBTransfer {
    

    protected static  int SECOND = 1000; // milli sec
    protected static  int SHORT_TIMEOUT = 1; //1 second
    protected static  int LONG_TIMEOUT = 10; //10 second

    protected static  Logger log = LoggerFactory.getLogger(DummyTransfer.class);

    private boolean transfer_complete = false;

    public static void init() {
	//should overload the default value for timeout
	Properties mainprop;
	try {
	    mainprop = ConfigurationProperties.getProperties();
	    SHORT_TIMEOUT = (Integer.valueOf(mainprop.getProperty("xtremweb.serv.dt.dummy.short", "" + 1))).intValue();
	    LONG_TIMEOUT = (Integer.valueOf(mainprop.getProperty("xtremweb.serv.dt.dummy.long", "" + 10))).intValue();
	} catch (ConfigurationException ce) {
	    log.warn("No Dummy Protocol Information found : " + ce);
	    mainprop = new Properties();
	}
    }

    public DummyTransfer(Data d, Transfer t, Locator rl, Locator ll, 
			 Protocol rp,  Protocol lp ) {	
	super(d,t,rl,ll,rp,lp);
	transfer.setoob(this.getClass().toString());
    } 

    public String toString() {
    	return super.toString() + "/ndummy://" + remote_locator.getdrname() + ":" +  remote_protocol.getport();
    }

    public void connect ()  throws OOBException {	
	log.debug("Dummy connect");
	shorttimeout();
	log.debug("Dummy is connected");
    }	
	
    public void blockingSendSenderSide    ()  throws OOBException {
	log.debug("Dummy Blocking Send Sender Side");
	longtimeout();
	transfer_complete = true;
	log.debug("Dummy send achieved");
    }

    public void blockingSendReceiverSide    ()  throws OOBException {
	log.debug("********** Dummy Blocking Send ReceiverSide *************");
	longtimeout();
	transfer_complete = true;
	log.debug("*********  Dummy send achived   *******");
    }

    public void blockingReceiveSenderSide ()  throws OOBException  {
	log.debug("Dummy Blocking Receive Sender Side");
	longtimeout();
	transfer_complete = true;
	log.debug("Dummy receive finished");
    }

    public void blockingReceiveReceiverSide ()  throws OOBException  {
	log.debug("Dummy Blocking Receive Receiver Side");
	longtimeout();
	transfer_complete = true;
	log.debug("Dummy receive finished");
    }

    public void disconnect() throws OOBException {
	log.debug("Dummy disconnect");
	shorttimeout();
	log.debug("Dummy disconnected");
    }

    public boolean poolTransfer() {
	log.debug("Local Pool : " + (((transfer_complete)?"TRANSFER_COMPLETE":"TRANSFER_ONGOING")));
	return (transfer_complete);
    }


    private void longtimeout() throws OOBException {
	timeout( LONG_TIMEOUT);
    }

    private void shorttimeout() throws OOBException {
	timeout( SHORT_TIMEOUT);
    }

    private void timeout(int timeout) throws OOBException {
	try {
	    Thread.sleep(SECOND * timeout);
	} catch (Exception e ) {	    
	    throw new OOBException("Error during timeout");
	}
    }

    public static void main(String [] args) {

	Data data = new Data();

	//Preparer le local
	Protocol local_proto = new Protocol();
	local_proto.setname("local");

	Locator local_locator = new Locator();
	local_locator.setdatauid(data.getuid());
	local_locator.setdrname("localhost");
	local_locator.setprotocoluid(local_proto.getuid());
	local_locator.setref("/tmp/localcopy");

	// Preparer le proto pour l'acces remote
	Protocol remote_proto = new Protocol();
	remote_proto.setname("http");
	remote_proto.setport(8080);

	Locator remote_locator = new Locator();
	remote_locator.setdatauid(data.getuid());
	remote_locator.setdrname("localhost");
	remote_locator.setprotocoluid(remote_proto.getuid());
	remote_locator.setref("build.xml");

	//prepare
	Transfer t = new Transfer();

	t.setlocatorremote(remote_locator.getuid());
	t.setlocatorlocal(local_locator.getuid());
	
    }

    
} // HttpTransfer
