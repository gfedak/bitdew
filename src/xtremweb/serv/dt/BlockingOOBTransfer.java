package xtremweb.serv.dt;

/**
 * <code>BlockingOOBTransfer</code> is an interface which specifies howto
 * implement an OOB transfer when the API relies on a Blocking API.
 *
 * In this case, one has to provide 2 functions for a Send transfer
 * blockingSendSenderSide and blockingSendReceiverSide and 2 functions
 * for a Receive transfer blockingReceiveSenderSide and
 * blockingReceiveReceiverSide
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public interface BlockingOOBTransfer extends OOBTransfer {

    /**
     * <code>blockingSendSenderSide</code> implements the blocking
     * Send method from the Sender side
     *
     * @exception OOBException if an error occurs
     */
    public void blockingSendSenderSide() throws OOBException;

    /**
     * <code>blockingSendReceiverSide</code> implements the blocking
     * send method from the Receiver side
     *
     * @exception OOBException if an error occurs
     */
    public void blockingSendReceiverSide() throws OOBException;

    /**
     * <code>blockingReceiveSenderSide</code> implements the blocking
     * receive from the Sender side
     *
     * @exception OOBException if an error occurs
     */
    public void blockingReceiveSenderSide() throws OOBException;

    /**
     * <code>blockingReceiveReceiverSide</code> implements the blocking
     * receive from the Receiver side
     *
     * @exception OOBException if an error occurs
     */
    public void blockingReceiveReceiverSide() throws OOBException;

}