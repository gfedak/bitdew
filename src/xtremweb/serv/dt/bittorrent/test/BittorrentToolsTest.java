package xtremweb.serv.dt.bittorrent.test;

import java.io.File;

import org.jfree.util.Log;
import org.junit.Test;

import xtremweb.serv.dt.OOBException;
import xtremweb.serv.dt.bittorrent.BittorrentException;
import xtremweb.serv.dt.bittorrent.BittorrentTools;
import junit.framework.TestCase;

/**
 * Unit test for bittorrent tools
 * @author jsaray
 *
 */
public class BittorrentToolsTest extends TestCase {
    
    /**
     * Junit setup
     */
    public void setUp() {

    }
    
    /**
     * Junit teardown
     */
    public void tearDown() {

    }
    
    /**
     * Perform a torrent make and check that it was correctly done
     */
    @Test
    public static void testDownloadTorrent() {
	try {
	String FILE_PATH = System.getProperty("user.dir") + File.separator
		+ "bitdew.mf";
	String TARGET_PATH = System.getProperty("user.dir") + File.separator
		+ "bitdew.mf.torrent";
	
	    BittorrentTools.init();
	
	BittorrentTools.makeTorrent(FILE_PATH, TARGET_PATH);

	assertTrue(new File(TARGET_PATH).exists());
	} catch (OOBException e) {
	    fail();
	    e.printStackTrace();
	} catch (BittorrentException e) {
	    Log.warn(" There was a problem when making the torrent file, maybe you dont have installed the binery file ");
	   
	}
    }
    
    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
	System.setProperty("PROPERTIES_FILE", "trunk-1/conf/properties.json");
	testDownloadTorrent();
    }

}
