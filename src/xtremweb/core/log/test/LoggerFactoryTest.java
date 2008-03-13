package xtremweb.core.log.test;
import xtremweb.core.log.*;

import org.junit.Before; 
import org.junit.Ignore; 
import org.junit.Test; 
import junit.framework.*;
import static org.junit.Assert.*;

public class LoggerFactoryTest {

    @Test public void getLoggerClass() {
	Logger log = null;
	try {
	    log  =  LoggerFactory.getLogger(LoggerFactoryTest.class);
	    log.info("ok");
	} catch(Exception e) {
	    System.out.println("Error" + e);
	}
    }

    @Test public  void getLogger() {
	Logger log = null;
	try {
	    log  =  LoggerFactory.getLogger("LoggerFactoryTest");
	    log.info("ok");
	} catch(Exception e) {
	    System.out.println("Error" + e);
	}
    }

} // Test
