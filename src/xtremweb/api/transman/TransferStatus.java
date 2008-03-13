package xtremweb.api.transman;

/**
 *  TransferStatus is a set of static integer representing the various
 *  status of a transfer. 
 *
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class TransferStatus {

    
    /**
     * <code>PENDING</code> is the initial state of the transfer.
     * After being checked with static informations, the transfer
     * can be either declared as INVALID or READY
     */
    public static final int PENDING     = 0;

    /**
     * <code>READY</code> is the state after a transfer has been 
     * statically checked and validated. The transfer is now ready 
     * to start.
     */
    public static final int READY       = 1;

    /**
     * <code>INVALID</code> is the state reached by a transfert when is has
     * not been checked successfully.
     */

    public static final int INVALID     = 2;

    /**
     * <code>TRANSFERING</code> is the state of transfert actually 
     * being processed. The transfer can be either cancelled by the system
     * (ABORTED), or complete (COMPLETE) when the transfert is done.
     */
    public static final int TRANSFERING = 3;

    /**
     * <code>ABORTED</code> is the state of a transfer when the system cancels
     * the transfer.
     */
    public static final int ABORTED     = 4;

    /**
     * <code>COMPLETE</code> is the state of a transfer when achieved correctly.
     *
     */
    public static final int COMPLETE    = 5;

    /**
     * <code>TODELETE</code> is the state of a process indicating that
     * it is ready to be safully removed from database.
     */
    public static final int TODELETE    = 6;

     //strings for debuging purpose
    private static final String[] strings = { "PENDING", "READY", "INVALID", "TRANSFERING", "ABORTED", "COMPLETE", "TODELETE" };

    /**
     * <code>toString</code> returns the name of the status.
     *
     * @param type an <code>int</code> value
     * @return a <code>String</code> value
     */
    public static String toString(int type) {
	return strings[type];
    }
}
