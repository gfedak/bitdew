package xtremweb.serv.dc.ddc.dlpt.test;

import org.junit.Test;

import junit.framework.TestCase;
import xtremweb.serv.dc.ddc.DDCException;
import xtremweb.serv.dc.ddc.dlpt.DLPTDistributedDataCatalog;


public class DLPTTest extends TestCase {
	
	public void setUp()
	{
		
	}
	
	@Test
	public void testJson()
	{
		DLPTDistributedDataCatalog dplt = new DLPTDistributedDataCatalog();
		String json;
		try {
			dplt.start();
			json = dplt.entryPoint();
			assertNotNull(json);
			System.out.println("Json is " +json);
		} catch (DDCException e) {
			fail();
			e.printStackTrace();
		}
		
	}
	
	public void tearDown()
	{
		
	}
}
