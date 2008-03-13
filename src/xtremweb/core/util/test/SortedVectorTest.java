package xtremweb.core.util.test;
import  xtremweb.core.util.SortedVector;
import xtremweb.core.log.*;

import org.junit.Before; 
import org.junit.Ignore; 
import org.junit.Test; 
import junit.framework.*;
import static org.junit.Assert.*;

import java.util.*;

/**
 * Describe class SortedVectorTest here.
 *
 *
 * Created: Mon Oct 22 11:00:28 2007
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */

public class SortedVectorTest {

    Logger log = LoggerFactory.getLogger("Data Scheduler Test");

    SortedVector vs = new SortedVector( new ElementsOrder() );

    class TestElement {
	private String id;
	private String value;
	
	public TestElement(String i, String v) {
	    id = i;
	    value = v;
	}
	
	public String getId() {
	    return id;
	}
	
	public String getValue() {
	    return value;
	}
    }
   
    //This a comparator for the test
    class ElementsOrder implements Comparator {
	
	public int compare(Object p1, Object p2) {
	    String s1;
	    String s2;
	    if (p1 instanceof String)
		s1 = (String) p1;
	    else
		s1=((TestElement) p1).getId();
	    if (p2 instanceof String) 
		s2 = (String) p2;
	    else
		s2=((TestElement) p2).getId();
	    return  s1.compareTo(s2);
	}
    }
    
    @Before public void init() {
	vs.addElement(new TestElement("1", "un"));
	vs.addElement(new TestElement("3", "trois"));
	vs.addElement(new TestElement("2", "deux"));
    }

    @Test public void testAddDifferent() {
	String retour = "";
	int i=0;
	for (Object obj : vs) {
	    TestElement te = (TestElement) obj; 
	    retour += "[" + te.getId() + ":" + te.getValue() + "] ";
	    assertEquals(te.getId(), "" + ++i);
	}
	log.debug( retour );
    }

    @Test public void testAddSimilar() {
	vs.addElement(new TestElement("3", "trois"));
	String retour = "";
	int i=0;
	for (Object obj : vs) {
	    TestElement te = (TestElement) obj; 
	    retour += "[" + te.getId() + ":" + te.getValue() + "] ";
	    assertEquals(te.getId(), "" + ++i);
	}
	log.debug( retour );
    }

    @Test public void testRemove() {
	vs.removeElement(new TestElement("3", "trois"));
	vs.removeElement("2");
	String retour = "";
	int i=0;
	for (Object obj : vs) {
	    TestElement te = (TestElement) obj; 
	    retour += "[" + te.getId() + ":" + te.getValue() + "] ";
	    assertEquals(te.getId(), "" + ++i);
	}
	log.debug( retour );
    }


}
