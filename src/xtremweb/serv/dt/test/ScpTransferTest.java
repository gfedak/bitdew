package xtremweb.serv.dt.test;


import java.io.File;
import java.util.Properties;
import java.util.Vector;

import org.junit.Test;

import junit.framework.TestCase;
import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.api.transman.TransferManager;
import xtremweb.api.transman.TransferManagerException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.conf.ConfigurationProperties;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.serv.ServiceLoader;
import xtremweb.role.cmdline.CommandLineTool;
import xtremweb.serv.dc.DataUtil;
import xtremweb.serv.dt.OOBTransfer;
import xtremweb.serv.dt.ftp.FtpTransfer;

public class ScpTransferTest extends TestCase {
	
    /**
     * Logger
     */
    protected static Logger log = LoggerFactory.getLogger(FtpTransfer.class);
	private CommandLineTool clt;
	public static void main(String[] args)
	{
		ScpTransferTest scpt = new ScpTransferTest();
		scpt.testBothMethods();
		
		System.out.println("termino");
	}
	
	@Test
	public void testBothMethods()
	{	try {
		
		Properties p = ConfigurationProperties.getProperties();
		if(p.getProperty("xtremweb.serv.dr.scp.name")!= null)
		{
			String path = System.getProperty("user.dir");
			log.debug("path is " + path);
			File f = new File(path + "/bitdew.mf");
			Vector comm;
			comm = ComWorld.getMultipleComms("localhost","rmi",4325,"dc","dr","dt","ds");
			BitDew bd = new BitDew(comm);
			TransferManager tm = new TransferManager(comm);
			tm.start();
			Data d = bd.createData(f);
			String uid = d.getuid();
			d.setoob("scp");
			log.debug("putting data ");
			OOBTransfer oob = bd.put(f, d);
			tm.registerTransfer(oob);
			tm.waitFor(d);
			log.debug("antes");
			File fi = new File(path + "/bitdew_copy.mf");

			Data dataget = bd.searchDataByUid(uid);
			OOBTransfer oobt = bd.get(dataget,fi);
			tm.registerTransfer(oobt);
			tm.waitFor(dataget);
			tm.stop();
			assertTrue(DataUtil.checksum(f).equals(DataUtil.checksum(fi)));	
			
		}
		else{
			log.warn("test is marked as successful, but it was not performed because there is not ssh connection in the xtremweb.properties");
		}
		} catch (ModuleLoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BitDewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransferManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (xtremweb.core.conf.ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void tearDown()
	{
		//System.exit(0);
	}

}
