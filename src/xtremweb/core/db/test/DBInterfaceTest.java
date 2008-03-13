package xtremweb.core.db.test;

import java.util.Properties;

import  xtremweb.core.db.*;
import xtremweb.core.log.*;
import xtremweb.core.conf.*;


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
public class DBInterfaceTest {

    Logger log =  LoggerFactory.getLogger(DBInterfaceTest.class);
    DBInterface dbi =  DBInterfaceFactory.getDBInterface();

    @Before public void getDBInterface() {
	DBInterface dbi = DBInterfaceFactory.getDBInterface();
	assert dbi instanceof DBInterface;

    }

    @Test public void makePersitent() {
	xtremweb.core.obj.dc.Data data = new xtremweb.core.obj.dc.Data();
	dbi.makePersistent(data);
	assert (data.getuid()!= null);
    }
    
}
