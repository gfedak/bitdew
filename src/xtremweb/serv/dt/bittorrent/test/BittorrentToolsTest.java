package xtremweb.serv.dt.bittorrent.test;

import java.io.File;

import org.junit.Test;

import xtremweb.serv.dt.bittorrent.BittorrentTools;
import junit.framework.TestCase;

public class BittorrentToolsTest extends TestCase{
    
    public void setUp()
    {
	
    }
    
    public void tearDown()
    {
	
    }
    
    @Test
    public static void testDownloadTorrent()
    {
	String FILE_PATH = "/Users/josefrancisco/Downloads/lola.avi";
	String TARGET_PATH = "/Users/josefrancisco/torrents/lola.avi.torrent";
	BittorrentTools.startBittorrentTracker();
	  
	BittorrentTools.makeTorrent(FILE_PATH,TARGET_PATH);
	
	assertTrue(new File(TARGET_PATH).exists());

    }
    
    public static void main(String[] args)
    {	System.setProperty("PROPERTIES_FILE","trunk-1/conf/properties.json");
	testDownloadTorrent();
    }
    

 
   

}
