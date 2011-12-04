package xtremweb.serv.dt.bittorrent.test;

import java.io.File;

import org.junit.Test;

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
	String FILE_PATH = System.getProperty("user.dir") + File.separator
		+ "bitdew.mf";
	String TARGET_PATH = System.getProperty("user.dir") + File.separator
		+ "bitdew.mf.torrent";
	BittorrentTools.makeTorrent(FILE_PATH, TARGET_PATH);

	assertTrue(new File(TARGET_PATH).exists());

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
