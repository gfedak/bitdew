package xtremweb.serv.dt;

/**
 <code>NonBlockingOOBTransfer</code> is an interface which specifies howto
 * implement an OOB transfer when the API relies on a Non Blocking API.
 *
 * In this case, one has to provide 2 functions for a Send transfer
 * nonBlockingSendSenderSide and nonBblockingSendReceiverSide and 2 functions
 * for a Receive transfer nonBblockingReceiveSenderSide and
 * nonBlockingReceiveReceiverSide
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public interface NonBlockingOOBTransfer extends OOBTransfer {

    /**
     * <code>nonBlockingSendSenderSide</code> implements the non blocking
     * Send method from the Sender side
     *
     * @exception OOBException if an error occurs
     */
    public void nonBlockingSendSenderSide() throws OOBException;

    /**
     * <code>nonBlockingSendReceiverSide</code> implements the non blocking
     * send method from the Receiver side
     *
     * @exception OOBException if an error occurs
     */
    public void nonBlockingSendReceiverSide() throws OOBException;

    /**
     * <code>nonBlockingReceiveSenderSide</code> implements the non blocking
     * receive from the Sender side
     *
     * @exception OOBException if an error occurs
     */
    public void nonBlockingReceiveSenderSide() throws OOBException;

    /**
     * <code>nonBlockingReceiveReceiverSide</code> implements the non blocking
     * receive from the Receiver side
     *
     * @exception OOBException if an error occurs
     */
    public void nonBlockingReceiveReceiverSide() throws OOBException;
}
