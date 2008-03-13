package xtremweb.serv.dt.nio;

/**
 * Ftpsender.java
 *
 *
 * Created: Sun Apr  9 15:02:00 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */

import xtremweb.core.log.*;
import xtremweb.core.uid.*;
import xtremweb.serv.dt.*;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;

import java.io.*;

public class NioTransfer  
    extends NonBlockingOOBTransferImpl 
    implements NonBlockingOOBTransfer, OOBTransfer {
    
    //protected static NioServer nios;
    //protected  NioClient nioc;

    protected static  Logger log = LoggerFactory.getLogger(NioTransfer.class);

    public NioTransfer(Data d, Transfer t, Locator rl, Locator ll, Protocol rp,  Protocol lp ) {
	super(d,t,rl,ll,rp,lp);
    } // Ftpsender constructor

    public String niotoString() {
	return "nio://[" + remote_protocol.getlogin() + ":" +  remote_protocol.getpassword() +  "]@" + remote_locator.getdrname() + ":" +  remote_protocol.getport();
    }

    public void connect ()  throws OOBException {
	/*	nioc = new NioClient();

	try {
	    nioc.connect(remote_locator.getdrname(), remote_protocol.getport());
	} catch (Exception e) {
	    throw new OOBException("Nio Cannot open nio session " + niotoString());
	}
	*/
    }	
	

    public void nonBlockingSendSenderSide   () throws OOBException {
	try {
	    FileInputStream is = new FileInputStream( new File(local_locator.getref()));

	} catch (Exception e) {
	    throw new OOBException("FTP errors when sending  " + "/" + remote_locator.getref() );
	} // end of try-catch
    }

    public void nonBlockingSendReceiverSide   () throws OOBException {
    }

    public void nonBlockingReceiveReceiverSide() throws OOBException  {
	//download
	try {

	    FileOutputStream os = new FileOutputStream( new File(local_locator.getref()));

	} catch (Exception e) {
	    log.debug("Error" + e);
	    throw new OOBException("FTP errors when receiving receive " + "/" + remote_locator.getref() );
	} // end of try-catch
	
	log.debug("FIN du transfer");
    }

    public void nonBlockingReceiveSenderSide() throws OOBException  {
    }

    public void disconnect() throws OOBException {
    }

    public static void main(String [] args) {
    }
    
} // NioTransfer
