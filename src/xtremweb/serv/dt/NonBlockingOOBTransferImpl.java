package xtremweb.serv.dt;

import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;


/**
 * <code>NonBlockingOOBTransferImpl</code> is an abstract class
 * implementing NonBlockingOOBTransfer when the API relies on a non Blocking API.
 *
 * Created: Fri Mar 16 14:28:17 2007
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public abstract class NonBlockingOOBTransferImpl 
    extends OOBTransferImpl 
    implements OOBTransfer, NonBlockingOOBTransfer {
	
	/**
	 * Flag to signal if its transfering
	 */
    protected boolean _isTransfering = false;
    
    /**
     * Class constructor
     */
    public NonBlockingOOBTransferImpl(){
	
    }
    
    /**
     * Creates a new <code>NonBlockingOOBTransferImpl</code> instance.
     *
     * @param d a <code>Data</code> value
     * @param t a <code>Transfer</code> value
     * @param rl a <code>Locator</code> value
     * @param ll a <code>Locator</code> value
     * @param rp a <code>Protocol</code> value
     * @param lp a <code>Protocol</code> value
     */
    public NonBlockingOOBTransferImpl(Data d, Transfer t, Locator rl, Locator ll, 
			 Protocol rp,  Protocol lp ) {	
	super(d,t,rl,ll,rp,lp);
    }

    /**
     * <code>sendSenderSide</code> executes the nonBlockingSend method.
     *
     * @exception OOBException if an error occurs
     */
    public void sendSenderSide() throws OOBException {
	_isTransfering = true;
	nonBlockingSendSenderSide();
    }

    /**
     * <code>sendReceiverSide</code> executes the nonBlockingSend method.
     *
     * @exception OOBException if an error occurs
     */
    public void sendReceiverSide() throws OOBException {
	_isTransfering = true;
	nonBlockingSendReceiverSide();
    }

    /**
     * <code>receiveSenderSide</code>  executes the nonBlockingReceiveSenderSide method
     *
     * @exception OOBException if an error occurs
     */
    public void receiveSenderSide() throws OOBException {
	_isTransfering = true;
	nonBlockingReceiveSenderSide();
    }

    /**
     * <code>receiveReceiverSide</code>  executes the nonBlockingReceive method
     *
     * @exception OOBException if an error occurs
     */
    public void receiveReceiverSide() throws OOBException {
	_isTransfering = true;
	nonBlockingReceiveReceiverSide();
    }
    
    /**
     * Is the transfer done ?
     */
    public boolean isTransfering() {
	_isTransfering = !poolTransfer();
	return _isTransfering;
    }

    /**
     * Wait for this transfer to be done
     */
    public void waitFor() {
	_isTransfering = !poolTransfer();
	while (_isTransfering) {
	    try {
		System.out.println(_isTransfering?"still transfering":"end of transfert");
		_isTransfering = !poolTransfer();
		Thread.sleep(1000);
	    } catch (InterruptedException ie) {
	    	log.fatal("There was an interrupted exception");
	    	ie.printStackTrace();
	    }
	}
    }
}
