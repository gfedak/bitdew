package xtremweb.serv.ds.test;
import  xtremweb.serv.ds.*;

import xtremweb.core.log.*;
import xtremweb.core.db.*;
import xtremweb.core.obj.ds.Attribute;


import org.junit.Before; 
import org.junit.Ignore; 
import org.junit.Test; 
import junit.framework.*;
import static org.junit.Assert.*;

import java.util.*;

/**
 * Describe class AttributeTypeTest here.
 *
 *
 * Created: Fri Nov  2 14:27:10 2007
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class AttributeTypeTest {

    Logger log = LoggerFactory.getLogger("Attribute Type Test");
    DBInterface dbi = DBInterfaceFactory.getDBInterface();


   @Test public void setAttributeTypeTest() {
       Attribute attr = new Attribute();

	dbi.makePersistent(attr);

	AttributeType.setAttributeTypeOn(attr, AttributeType.REPLICAT);

	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOff(attr, AttributeType.REPLICAT);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOn(attr, AttributeType.FT);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOff(attr, AttributeType.FT);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOn(attr, AttributeType.AFFINITY);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOff(attr, AttributeType.AFFINITY);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOn(attr, AttributeType.LFTABS);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOff(attr, AttributeType.LFTABS);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOn(attr, AttributeType.LFTREL);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOff(attr, AttributeType.LFTREL);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOn(attr, AttributeType.OOB);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOff(attr, AttributeType.OOB);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOn(attr, AttributeType.REPLICAT);
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOn(attr, AttributeType.FT);
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOn(attr, AttributeType.AFFINITY);
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOn(attr, AttributeType.LFTABS);
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOn(attr, AttributeType.LFTREL);
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOn(attr, AttributeType.OOB);
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));
	
	AttributeType.setAttributeTypeOff(attr, AttributeType.REPLICAT);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOff(attr, AttributeType.FT);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOff(attr, AttributeType.AFFINITY);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOff(attr, AttributeType.LFTABS);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOff(attr, AttributeType.LFTREL);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertTrue(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

	AttributeType.setAttributeTypeOff(attr, AttributeType.OOB);
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.REPLICAT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.FT));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.AFFINITY));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTABS));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.LFTREL));
	assertFalse(AttributeType.isAttributeTypeSet(attr, AttributeType.OOB));

    }

    
    
}
