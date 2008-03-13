package xtremweb.serv.ds;
import xtremweb.core.obj.ds.Host;

import java.util.*;

/**
 * Describe class OwnerComparator here.
 *
 *
 * Created: Sun Oct 21 18:08:01 2007
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class OwnerComparator implements Comparator {

    public int compare(Object p1, Object p2) {
	String s1;
	String s2;
	if (p1 instanceof String)
	    s1 = (String) p1;
	else if  (p1 instanceof Host)
	    s1=((Host) p1).getuid();
	else
	    s1=((Owner) p1).getHost().getuid();
	if (p2 instanceof String) 
	    s2 = (String) p2;
	else if  (p2 instanceof Host)
	    s2=((Host) p2).getuid();
	else
	    s2=((Owner) p2).getHost().getuid(); 
	return  s1.compareTo(s2);
    }
}
