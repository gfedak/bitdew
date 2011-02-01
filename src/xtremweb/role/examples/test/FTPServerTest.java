package xtremweb.role.examples.test;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.net.io.CopyStreamException;
import junit.framework.TestCase;

import org.junit.*;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.api.transman.TransferManager;
import xtremweb.api.transman.TransferManagerException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.obj.dc.Data;
import xtremweb.role.cmdline.CommandLineTool;
import xtremweb.role.examples.FTPServer;
import xtremweb.role.examples.PutGet;

public class FTPServerTest extends TestCase {

    private FTPServer ftp;

    private final String testhost = "ftp.lip6.fr";
    private final String testlogin = "anonymous";
    private final String testpath = "/pub/linux/distributions/slackware/slackware-current";

    // TODO look for allowing username and password, currently just anonymous
    // connection
    public void setUp() {
	try {
	    ftp = new FTPServer();
	} catch (Exception e) {
	    fail();
	}

    }

    @Test
    public void testBrowse() {
	try {
	    ftp.browseFtpServer();
	} catch (Exception e) {
	    fail();
	    e.printStackTrace();
	}
    }

    @Test
    public void testMakeAvailable() {
	Properties p;

	try {
	    ftp.browseFtpServer();
	    Vector v = ftp.makeAvailable();
	    assertNotNull(v);

	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void tearDown() {
	try {
	    ftp.disconnect();
	} catch (Exception e) {
	    fail();
	}
    }

}
