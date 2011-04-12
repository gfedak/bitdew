package xtremweb.role.examples;

import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.serv.dt.BlockingOOBTransferImpl;
import xtremweb.serv.dt.OOBException;
import xtremweb.serv.dt.scp.ScpManager;


/**
 * Scp transfer implementation
 * @author jsaray
 *
 */
public class ScpTransfer extends BlockingOOBTransferImpl {
    
    /*! \example ScpTransfer.java 
     * This example shows how to implement a transfer in BitDew.
     * For this purpose, a scp transfer is implemented using Bitdew's multitransfer management toolbox. 
     * <ol> <li> We use the JSch library that implements transfers in a blocking way, this library is embedded in <em>ScpManager</em> class to provide
     * independence from the library implementation, the class provides <em>send()</em> and <em>receive()</em> methods, its code is complicated and details can be seen if needed in  Bitdew's source code
     * zip file</li>
     * 
     * <li>Two principal interfaces are provided by bitdew, <em>NonBlockingOOBTransfer</em> and <em>BlockingOOBTransfer</em>, moreover, a default implementation of these classes is provided in 
     * <em>NonBlockingOOBTransferImpl</em> and <em>BlockingOOBTransferImpl</em> respectively. Blocking transfers are  those whose we know if either the transfer success
     * or the transfer error code as soon as the methods <em>send</em> and <em>receive</em> of the implementation end (commonly in libraries implementing http,ftp,smtp etc). By this reason, the <em>BlockingOOBTransfer</em> interface 
     * seems more appropriated in implementing scp with JSch.
     * </li>
     * @code
     * public class ScpTransfer extends BlockingOOBTransferImpl {	
     * 
     *     private ScpManager scpm;
     * @endcode
     * 
     * <li>In constructor we initlialize  <em>ScpManager</em> and invoke the parent constructor with the required fields</li>
     * 
     * @code
     * 
     * public ScpTransfer(Data d,Transfer t,Locator l1, Locator l2, Protocol p1, Protocol p2)
     * {	super(d,t,l1,l2,p1,p2);
     *	scpm = new ScpManager(remote_protocol.getlogin(),remote_protocol.getserver(),local_locator.getref(),remote_locator.getref(),remote_protocol.getprivatekeypath(),remote_protocol.getknownhosts(),remote_protocol.getpassphrase());
     * }
     * 
     * @endcode
     * 
     * 
     * <li> Next, four methods needs to be implemented </li>
     * @code
     * 	public void blockingSendSenderSide() throws OOBException {}
     * 	public void blockingReceiveSenderSide() throws OOBException {}
     * 	public void blockingReceiveReceiverSide() throws OOBException {}
     * 	public void blockingSendReceiverSide() throws OOBException {}
     * @endcode 
     * 
     * <li> Depending on the nature of our transfer, we choose the methods to implement. As we can know the final state
     * of the transfer from either the receiver or the sender side, we only implement the methods <em>blockingSendSenderSide</em> and 
     * <em>blockingReceiveReceiverSide</em> , this is not the case of all transfers, for example, in a bittorrent transfer (normally implemented 
     * as non-blocking transfer), the downloader begins the transfer, and seeder do not know if the file was propertly received, in this case we have to implement
     * <em>nonBlockingSendReceiver</em> method.</li>
     * @code 
     *   public void blockingSendSenderSide() throws OOBException {
     *	     scpm.send();
     *   }
     *   
     *   public void blockingReceiveReceiverSide() throws OOBException {
     *	     scpm.receive();
     *   }
     * @endcode 
     * 
     *  <li>Because of scp relies on tcp, we are sure that as soon as the <em>blockingSendSenderSide</em>, <em>blockingReceiveReceiverSide</em> methods end, we know
     * the transfer status, so pooling responsability is only on the internal thread that executes the transfer (<em>TransferThread</em>) , this thread implements the method <em>isTransfering</em> that
     * is called here </li> 
     * @code
     * public boolean poolTransfer()
     * {
     *     return !isTransfering();
     * }
     * @endcode Application code source : </ol>
     */
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
