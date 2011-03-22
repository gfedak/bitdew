package xtremweb.serv.dt;

import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;

/**
 * <code>BlockingOOBTransferImpl</code> is an abstract class implementing
 * BlockingOOBTransfer when the API relies on a Blocking API. In this case send
 * and receive operations are executed in a separate thread.
 * 
 * Created: Fri Mar 9 11:22:15 2007
 * 
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public abstract class BlockingOOBTransferImpl extends OOBTransferImpl implements
		OOBTransfer, BlockingOOBTransfer {

	/**
	 * <code>runner</code> is a TransferThread which helps to implements non
	 * blocking method on top of blocking method.
	 * 
	 */
	protected TransferThread runner;

	/**
	 * Creates a new <code>BlockingOOBTransferImpl</code> instance.
	 * 
	 * @param d
	 *            a <code>Data</code> value
	 * @param t
	 *            a <code>Transfer</code> value
	 * @param rl
	 *            a <code>Locator</code> value
	 * @param ll
	 *            a <code>Locator</code> value
	 * @param rp
	 *            a <code>Protocol</code> value
	 * @param lp
	 *            a <code>Protocol</code> value
	 */
	public BlockingOOBTransferImpl(Data d, Transfer t, Locator rl, Locator ll,
			Protocol rp, Protocol lp) {
		super(d, t, rl, ll, rp, lp);
		runner = new TransferThread(this);

	}

	/**
	 * <code>sendSenderSide</code> executes the blockingSendSenderSide method in
	 * a separate Thread.
	 * 
	 * @exception OOBException
	 *                if an error occurs
	 */
	public void sendSenderSide() throws OOBException {
		// runner.setTransferType(TransferThread.SEND);
		runner.start();
	}

	/**
	 * <code>sendReceiverSide</code> is a void method
	 * 
	 * @exception OOBException
	 *                if an error occurs
	 */
	public void sendReceiverSide() throws OOBException {
		runner.start();
	}

	/**
	 * <code>receiveSenderSide</code> is a void method
	 * 
	 * @exception OOBException
	 *                if an error occurs
	 */
	public void receiveSenderSide() throws OOBException {
		runner.start();
	}

	/**
	 * <code>receiveReceiverSide</code> executes the blockingReceiveReceiver
	 * method in a separate Thread.
	 * 
	 * @exception OOBException
	 *                if an error occurs
	 */
	public void receiveReceiverSide() throws OOBException {
		runner.start();
	}

	public boolean isTransfering() {
		return runner.isTransfering();
	}

	public void waitFor() {
		runner.waitFor();
	}

}
