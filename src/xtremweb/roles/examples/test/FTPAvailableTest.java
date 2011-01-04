package xtremweb.roles.examples.test;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.*;

import xtremweb.api.bitdew.BitDewException;
import xtremweb.api.transman.TransferManagerException;
import xtremweb.core.obj.dc.Data;
import xtremweb.role.examples.FTPAvailable;

public class FTPAvailableTest extends TestCase {

    private FTPAvailable ftp;

    private final String testhost = "ftp.lip6.fr";
    private final String testlogin = "anonymous";
    private final String testpath = "/pub/linux/distributions/slackware/slackware-current";
    private final int testport = 21;

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

    // TODO look for allowing username and password, currently just anonymous
    // connection
    public void setUp() {
	ftp = new FTPAvailable(testhost, 21, testlogin, null, testpath);
	ftp.connect();
    }

    /*
     * @Test public void testChangeDirectory() { ftp.changeDirectory(testpath);
     * int response = ftp.getReplyCode()/100; assertEquals(response,2);
     * assertEquals(ftp.getCurrentDirectory(),testpath); }
     * 
     * 
     * 
     * @Test public void testConnect() { ftp.connect();
     * System.out.println("code " + ftp.getReplyCode()); int response =
     * ftp.getReplyCode()/100; assertEquals(response,2);//first digit in ftp
     * connection equals 2 means command succesfully issued
     * //assertEquals(ftp.getCurrentDirectory(),testpath); }
     */

    
      @Test public void testGetSignatures() { ftp.changeDirectory(testpath);
      Properties p = ftp.getSignatures(testpath); assertNotNull(p);
      assertNotNull(p.get("./COPYRIGHT.TXT"));
      assertNotNull(p.get("./README_LVM.TXT"));
      assertNotNull(p.get("./README_CRYPT.TXT"));
      assertNotNull(p.get("./SPEAK_INSTALL.TXT"));
      
      assertEquals(p.get("./COPYRIGHT.TXT"),"4a79c816389ed23235c36244f4210c81");
      assertEquals(p.get("./README_LVM.TXT"),"1e5ebcf8675508efc07cc6b869dbe6e6");
      assertEquals(p.get("./README_CRYPT.TXT"),"f7a6a57330634d5eb77eae3f3d5c32bc");
      assertEquals(p.get("./SPEAK_INSTALL.TXT"),"058f7b10e84d0d51d7725e923ed11cc9");

      }
     

    @Test
   /* public void testMakeAvailable() {
	ftp.changeDirectory(testpath);
	// ftp.getSignatures(testpath);
	ftp.makeAvailable();
	 try {
	// retreive the data object
	for (int i = 0; i < ftp.getDuids().size(); i++) {
	    String s = ftp.getData(i);
	    File file = new File("result"+i+".txt");
	    Data data;
	   data = ftp.getBitDewApi().searchDataByUid(s);
	   ftp.getBitDewApi().get(data, file);
	    ftp.getTf().waitFor(data);
	    ftp.getTf().stop();
	   // log.info("data has been successfully copied to " + fileName);
	}
	} catch (BitDewException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (TransferManagerException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}*/

	/*
	 * Data data = ftp.getBitDewApi().searchDataByUid(dataUid);
	 * ftp.getBitDewApi().get(data, file); transferManager.waitFor(data);
	 * transferManager.stop(); assertNotNull
	 */
   // }

    public void tearDown() {
	ftp.disconnect();
    }

}
