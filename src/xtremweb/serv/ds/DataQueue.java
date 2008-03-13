package xtremweb.serv.ds;
import xtremweb.core.util.SortedVector;

/**
 * Describe class DataQueue here.
 *
 *
 * Created: Mon Oct 22 15:34:40 2007
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class DataQueue extends SortedVector {

    /**
     * Creates a new <code>DataQueue</code> instance.
     *
     */
    public DataQueue() {
	super(new CacheEntryComparator());
    }
}
