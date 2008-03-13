package xtremweb.core.db.test;
import  xtremweb.core.db.*;
import xtremweb.core.log.*;

import org.junit.Before; 
import org.junit.Ignore; 
import org.junit.Test; 
import junit.framework.*;
import static org.junit.Assert.*;

/**
 * Describe class DBInterfaceFactoryTest here.
 *
 *
 * Created: Mon Oct  8 11:52:21 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class DBInterfaceFactoryTest {

    Logger log =  LoggerFactory.getLogger(DBInterfaceFactoryTest.class);

    @Test public void getDBInterface() {

	DBInterface dbi = DBInterfaceFactory.getDBInterface();
	assert dbi instanceof DBInterface;
    }

    
}
