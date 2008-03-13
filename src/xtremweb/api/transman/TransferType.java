package xtremweb.api.transman;

/**
 * Describe class TransferType here.
 *
 *
 * Created: Tue Feb 20 15:54:09 2007
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

public class TransferType {


    /**
     *  <code>UNICAST_SEND_SENDER_SIDE</code> Point to point send transfer.
     * The sender is local and the receiver is remote.
     */
    public static final int UNICAST_SEND_SENDER_SIDE = 0;

    /**
     * <code>UNICAST_SEND_RECEIVER_SIDE</code> Point to point send transfer.
     * The sender is remote and the receiver is local.
     */
    public static final int UNICAST_SEND_RECEIVER_SIDE = 1;


    /**
     * <code>UNICAST_RECEIVE_SENDER_SIDE</code> Point to point receive transfer.
     * The sender is local and the receiver is remote.
     */    
    public static final int UNICAST_RECEIVE_SENDER_SIDE = 2;


    /**
     * <code>UNICAST_RECEIVE_RECEIVER_SIDE</code> Point to point receive transfer.
     * The sender is remote and the receiver is local.
     */
    public static final int UNICAST_RECEIVE_RECEIVER_SIDE = 3;

    //strings for debuging purpose
    private static final String[] strings = {"UNICAST_SEND_SENDER_SIDE", "UNICAST_SEND_RECEIVER_SIDE", "UNICAST_RECEIVE_SENDER_SIDE", "UNICAST_RECEIVE_RECEIVER_SIDE"};

    /**
     *  <code>isLocal</code> checks if a transfer type is operated locally.
     * true for UNICAST_SEND_SENDER_SIDE and UNICAST_RECEIVE_RECEIVER_SIDE
     *
     * @param type an <code>int</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isLocal(int type) {
	return ((type == UNICAST_SEND_SENDER_SIDE) || (type == UNICAST_RECEIVE_RECEIVER_SIDE));
    }

    /**
     *  <code>isRemote</code> checks if a transfer type is operated remotelly.
     * true for UNICAST_SEND_RECEIVER_SIDE and UNICAST_RECEIVE_SENDER_SIDE
     *
     * @param type an <code>int</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isRemote(int type) {
	return ((type == UNICAST_SEND_RECEIVER_SIDE) || (type == UNICAST_RECEIVE_SENDER_SIDE));
    }

    public static boolean isSender(int type) {
	return ((type == UNICAST_SEND_SENDER_SIDE) || (type == UNICAST_RECEIVE_SENDER_SIDE));
    }
    public static boolean isReceiver(int type) {
	return ((type == UNICAST_SEND_RECEIVER_SIDE) || (type == UNICAST_RECEIVE_RECEIVER_SIDE));
    }


    public static String toString(int type) {
	return strings[type];
    }

}
