package xtremweb.serv.dc;

/**
 * <code>DataStatus</code> is a set of static integer representing the various
 *  status of a transfer.
 *
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class DataStatus {

    /**
     *  <code>LOCK</code> Data is locked by a process.
     *
     */
    public static final int LOCK        = 0;

    /**
     *  <code>RELEASED</code> data is free to be used
     *
     */
    public static final int RELEASED    = 1;

    /**
     * <code>TODELETE</code> data is ready to be deleted.
     *
     */
    public static final int TODELETE    = 2;

    //strings for debuging purpose
    private static final String[] strings = { "LOCK", "RELEASED", "TODELETE" };

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
