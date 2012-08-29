package xtremweb.serv.dt;

/**
 * <code>TransferThread</code> is a helper class to implement
 * non-blocking transfer from a blocking API.
 *
 * With this code, the blockingSend() and blockingReceive() methods
 * will be run in a separate thread.
 *
 * Created: Fri Mar  9 11:41:36 2007
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
import xtremweb.core.log.*; //FIXME
import xtremweb.api.transman.TransferType;

/**
 * This class represents a thread performing a transfer
 * @author jsaray
 *
 */
public class TransferThread extends Thread {

    /**
     * The blocking transfer
     */
    private BlockingOOBTransfer _oob;

    /**
     * Log
     */
    protected static Logger log = LoggerFactory.getLogger(TransferThread.class);

    /**
     * flag of transferring, true if its being done; false otherwise
     */
    protected boolean isTransfering = true;

    /**
     * Creates a new <code>TransferThread</code> instance
     * 
     * @param oob
     *            an <code>BlockingOOBTransfer</code> value
     */
    public TransferThread(BlockingOOBTransfer oob) {
	_oob = oob;
    }

    /**
     * <code>run</code> starts the new thread.
     */
    public synchronized void run() {
	log.debug("start of transfer run : "
		+ TransferType.toString(_oob.getTransfer().gettype()));
	try {
	    switch (_oob.getTransfer().gettype()) {
	    case TransferType.UNICAST_SEND_SENDER_SIDE:
		_oob.blockingSendSenderSide();
		break;
	    case TransferType.UNICAST_SEND_RECEIVER_SIDE:
		_oob.blockingSendReceiverSide();
		break;
	    case TransferType.UNICAST_RECEIVE_SENDER_SIDE:
		_oob.blockingReceiveSenderSide();
		break;
	    case TransferType.UNICAST_RECEIVE_RECEIVER_SIDE:
		_oob.blockingReceiveReceiverSide();
		break;
	    }

	} catch (OOBException oobe) {
	    log.info("Error when performing "
		    + TransferType.toString(_oob.getTransfer().gettype()) + " "
		    + oobe);
	    _oob.setError();
	} finally {
	    isTransfering = false;
	    notifyAll();
	}
	log.debug("end of transfer run");
    }
    
    /**
     * Is the transfer still active ?
     * @return
     */
    public boolean isTransfering() {
	return isTransfering;
    }	
    
    /**
     * Wait for this transfer
     */
    public synchronized void waitFor() {
	log.debug("starting waitFor");
	while (isTransfering) {
	    log.debug("still Waiting");
	    try {
		wait();
	    } catch (InterruptedException e) {
		log.fatal("wait end xp" + e);
	    } // end of try-catch
	}
	log.debug("endWaitFor");
    }
}
