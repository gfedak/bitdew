package xtremweb.core.util;

import java.util.*;
import java.lang.*;

/**
 * <code>SortedVector</code> is a class which keeps the element of the
 * vector sorted. It provides a log2 binary search. The good thing is
 * that it can store any element  and can be paramertrized with a
 * dedicated comparator. 
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class SortedVector extends Vector {

    private Comparator order;

    /**
     * Creates a new <code>SortedVector</code> instance with a
     * comparator as a parameter. 
     *
     * @param op a <code>Comparator</code> value
     */
    public SortedVector(Comparator op) {
	order = op;
    }

    /**
     *  <code>addElement</code>adds an element and ensure that the
     *  vector is correctly sorted
     *
     * @param objp an <code>Object</code> value
     */
    public void addElement(Object objp) {
	if ( objp == null)
	    throw new IllegalArgumentException("null arg");
	int low = 0;
	int high = size() - 1;
	int mid = low;
	while (low <= high) {
	    mid = (low + high) / 2;
	    int c = order.compare(objp, elementAt(mid));
	    if (c < 0)
		high = mid - 1;
	    else if (c > 0)
		low = mid + 1;
	    else  return;	    
	}
	insertElementAt(objp,low);
    }

    /**
     *  <code>search</code> return the index of the element
     *  accordingly to the comparator
     *
     * @param objp an <code>Object</code> value
     * @return an <code>int</code> value
     */
    public int search(Object objp) {
	if ( objp == null)
	    throw new IllegalArgumentException("null arg");

	int low = 0;
	int high = size() - 1;
	while (low <= high) {
	    int mid = (low + high) / 2;
	    int c = order.compare(objp, elementAt(mid));
	    if (c < 0)
		high = mid - 1;
	    else if (c > 0)
		low = mid + 1;
	    else
		return mid;
	}
	return -1;
    }
    
    /**
     * <code>removeElement</code> removes the Element from the
     * SortedVector. 
     * A boolean is returned : true if the Object was present in the
     * SortedVector, false elsewise.
     *
     * @param obj an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public boolean removeElement(Object obj) {
	if ( obj == null)
	    throw new IllegalArgumentException("null arg");
	int idx = search(obj);
	if (idx == -1) return false;
	removeElementAt(idx);
	return true;
    }

}
