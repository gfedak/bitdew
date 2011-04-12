package xtremweb.serv.dt.scp;

import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.serv.dt.BlockingOOBTransferImpl;
import xtremweb.serv.dt.OOBException;


/**
 * Scp transfer implementation
 * @author jsaray
 *
 */
public class ScpTransfer extends BlockingOOBTransferImpl {
    
    /**
     * Log
     */
    protected static  Logger log = LoggerFactory.getLogger(ScpTransfer.class);
    
    /**
     * Object to interface bitdew with a scp transfer library
     */
    private ScpManager scpm;
    
    /**
     * Scp transfer contructor
     * @param d data, the data to transfer
     * @param t transfer, the transfer 
     * @param l1 Local locator
     * @param l2 Remote locator
     * @param p1 Local protocol
     * @param p2 Remote protocol
     */
    public ScpTransfer(Data d,Transfer t,Locator l1, Locator l2, Protocol p1, Protocol p2)
    {	super(d,t,l1,l2,p1,p2);
	scpm = new ScpManager(remote_protocol.getlogin(),remote_protocol.getserver(),local_locator.getref(),remote_locator.getref(),
		remote_protocol.getprivatekeypath(),remote_protocol.getknownhosts(),remote_protocol.getpassphrase());
    }
    
    /**
     * String representation
     */
    public String toString()
    {
	return "Send : " + remote_protocol.getserver() +" user : "+ remote_protocol.getlogin() + " key: "+ remote_protocol.getpassword();
    }
    
    /**
     * Securely send a file
     */
    public void blockingSendSenderSide() throws OOBException {	
	scpm.send();
    }

    /**
     * This method is unimplemented due to the scp transfer's nature
     */
    public void blockingReceiveSenderSide() throws OOBException {}

    /**
     * Securely connect 
     */
    public void blockingReceiveReceiverSide() throws OOBException {
	scpm.receive();
    }

    /**
     * Securely connect to the host
     */
    public void connect() throws OOBException {
	scpm.connect();
    }
    
    /**
     * Securely disconnect from the host
     */
    public void disconnect() throws OOBException {
	scpm.disconnect();

    }
    
    /**
     * Pool the transfer (blocking transfer, so it returns inmediatly
     */
    public boolean poolTransfer()
    {
    	return !isTransfering();
    }

    public void blockingSendReceiverSide() throws OOBException {
    }
}
