package xtremweb.core.uid;

import java.util.Random;
import org.jpox.poid.AUID;

/**
 * UID.java
 *
 *
 * Created: Tue Apr 29 10:56:17 2003
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class UID {

    long uid;

    static Random rand = new Random(System.currentTimeMillis()) ;
    /**
     * Creates a new <code>UID</code> instance.
     *
     * the unique id is computed by a combination of a date and a random
     * number
     */
    public UID() {
	long time = System.currentTimeMillis();
	long r = rand.nextLong();
	uid = Math.abs(time + r);
    } // UID constructor

    /**
     * Creates a new <code>UID</code> instance from a String.
     *
     */

    public UID(String suid) {
	uid = Long.parseLong(suid);
    }

    /**
     * <code>getUID</code> return the uid
     *
     * @return an <code>int</code> value
     */

    public long getUID() {
	return uid;
    }

    public long getLong() {
	return uid;
    }

    public String toString() {
	return Long.toHexString(uid);
    }

} // UID
