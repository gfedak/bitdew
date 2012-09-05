package xtremweb.serv.ds;

/**
 * <code>CacheEntryComparator</code> implements a Comparator in order
 * to add entries to the data cache
 *
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

import java.util.*;

/**
 * A comparator between two cache entries
 * @author jsaray
 *
 */
public class CacheEntryComparator implements Comparator {
	
	/**
	 * Compare to cache entries
	 */
    public int compare(Object p1, Object p2) {
	String s1;
	String s2;
	if (p1 instanceof String)
	    s1 = (String) p1;
	else
	     s1=((CacheEntry) p1).getDataUid();
	if (p2 instanceof String) 
	    s2 = (String) p2;
	else
	     s2=((CacheEntry) p2).getDataUid(); 
	return  s1.compareTo(s2);
    }
}
