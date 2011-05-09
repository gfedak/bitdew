package xtremweb.core.conf.test;

import java.util.Properties;

import org.junit.Test;

import junit.framework.TestCase;

import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.conf.ConfigurationProperties;
import xtremweb.core.conf.JsonProperties;

public class JsonPropertiesTest extends TestCase {
    
    private JsonProperties json;
    private Properties confprop;
    public void setUp(){
	json= new JsonProperties();
	try {
	    confprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    @Test public void testJson(){
	try {
	    
	    Properties propjson = json.getProperties();
	    String value1 = propjson.getProperty("xtremweb.serv.dr.ftp.server");	    
	    String value2 = confprop.getProperty("xtremweb.serv.dr.ftp.server");
	    
	    assertEquals(value1,value2);
	    
	    value1 = propjson.getProperty("xtremweb.serv.dr.ftp.path");
	    value2 = confprop.getProperty("xtremweb.serv.dr.ftp.path");
	    
	    assertEquals(value1,value2);
	    
	    value1 = propjson.getProperty("xtremweb.serv.dr.protocols");
	    value2 = confprop.getProperty("xtremweb.serv.dr.protocols");
	    
	    assertEquals(value1,value2);
	    
	    value1 = propjson.getProperty("xtremweb.serv.dt.bittorrent.azureusjar");
	    value2 = confprop.getProperty("xtremweb.serv.dt.bittorrent.azureusjar");
	    
	    assertEquals(value1,value2);
	    
	    value1 = propjson.getProperty("xtremweb.core.handler.perf");
	    value2 = confprop.getProperty("xtremweb.core.handler.perf"); 
	    
	    assertEquals(value1,value2);
	    
	    
	} catch (ConfigurationException e) {
	    fail();
	    e.printStackTrace();
	}
    }
    
    public void tearDown(){
    }

}
