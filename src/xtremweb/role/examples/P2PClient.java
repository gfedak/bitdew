package xtremweb.role.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.api.transman.TransferManager;
import xtremweb.api.transman.TransferManagerException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.InterfaceRMIdc;
import xtremweb.core.iface.InterfaceRMIdr;
import xtremweb.core.iface.InterfaceRMIds;
import xtremweb.core.iface.InterfaceRMIdt;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.role.examples.obj.SongBitdew;
import xtremweb.serv.dt.OOBTransfer;

/**
 * This class launchs a request over the P2P network of a specific term.
 * 
 * @author josefrancisco
 * 
 */
public class P2PClient {

    /**
     * BitDew API
     */
    private BitDew bitdew;

    /**
     * Transfer Manager API
     */
    private TransferManager tm;

    /**
     * Catalog service
     */
    private InterfaceRMIdc dc;

    /**
     * Transfer service
     */
    private InterfaceRMIdt dt;

    /**
     * Repository service
     */
    private InterfaceRMIdr dr;

    /**
     * Scheduler service
     */
    private InterfaceRMIds ds;

    /**
     * Machine local address constant
     */
    private String LOCAL_ADDRESS;

    private Logger log = LoggerFactory.getLogger("P2PClient");

    private String BOOTSTRAP;

    /**
     * Class constructor, initialize services and API
     * 
     * @param bootstrap
     */
    public P2PClient(String bootstrap) {

	try {
	    BOOTSTRAP = bootstrap;
	    LOCAL_ADDRESS = InetAddress.getLocalHost().getHostAddress();
	    // we build bitdew services dc dt and ds, dc will reference the DHT
	    dc = (InterfaceRMIdc) ComWorld.getComm(bootstrap, "rmi", 4325, "dc");
	    dt = (InterfaceRMIdt) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325,"dt");
	    ds = (InterfaceRMIds) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325,"ds");
	    // starting bitdew API
	    tm = new TransferManager(dt);
	    bitdew = new BitDew(dc, dr, dt, ds);
	    tm.start();
	} catch (ModuleLoaderException e) {
	    e.printStackTrace();
	} catch (UnknownHostException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Main method ,
     * 
     * @param args
     *            program arguments : args[0] bootstrapnode , args[1] term to
     *            search (if get)
     */
    public static void main(String[] args) {
	P2PClient p2p = new P2PClient(args[0]);
	p2p.get(args[1]);
    }

    /**
     * After a song list is shown in console client, this method receive the
     * song number the user has entered
     * 
     * @return the number the user has entered
     */
    public int readInput() {
	BufferedReader stdIn = new BufferedReader(new InputStreamReader(
		System.in));
	String number;
	try {
	    number = stdIn.readLine();
	    int nu = Integer.parseInt(number);
	    return nu;
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return -1;

    }

    /**
     * Search for a specific term on the P2P network
     * 
     * @param term
     *            the term to look for
     */
    public void get(String term) {
	try {
	    log.info("Search for songs artist or title : ");
	    BufferedReader stdIn = null;
	    List results;
	    results = bitdew.ddcSearch(term);
	    log.info("the value of results is " + results + " and the size is "
		    + results.size());
	    // iterate and print the results
	    for (int i = 1; i <= results.size(); i++) {
		SongBitdew sbd = (SongBitdew) results.get(i - 1);
		log.info(" Results for your query : " + i + ". "
			+ sbd.getFilename() + " md5 is " + sbd.getMd5());
	    }
	    log.info("Please write the song number you wish to download : ");
	    int nu = readInput();
	    String md5 = ((SongBitdew) results.get(nu - 1)).getMd5();

	    download(((SongBitdew) results.get(nu - 1)).getFilename(), md5);
	} catch (BitDewException e) {
	    e.printStackTrace();
	}

    }

    /**
     * Download a song from the P2P network
     * 
     * @param songname
     *            the song name
     * @param md5
     *            the song signature signature
     */
    public void download(String songname, String md5) {
	try {
	    // first retrieve the ip list of the machines having file names
	    //signatures md5
	    List ips = bitdew.ddcSearch(md5);
	   
	    // Once we have an ip of a machine having that md5sum, we are able to begin the download,
	    // but first we need to contact the machine catalog and repository
	    if (ips != null && ips.size() != 0) {
		dr = (InterfaceRMIdr) ComWorld.getComm((String) ips.get(0),
			"rmi", 4325, "dr");
		dc = (InterfaceRMIdc) ComWorld.getComm((String) ips.get(0),
			"rmi", 4325, "dc");
	    } else
		throw new BitDewException("There is not ip for that md5 ! ");
	    // Then we create a bitdew API
	    bitdew = new BitDew(dc, dr, dt, ds);
	    File file = new File(songname);
	    //getDataFromMd5 method help us to retrieve the correct data
	    Data d = bitdew.getDataFromMd5(md5);
	    d.setoob("http");
	    OOBTransfer oob;
	    //download begins
	    oob = bitdew.get(d, file);
	    tm.registerTransfer(oob);
	    tm.waitFor(d);
	    log.info("File : " + songname + " was successfully downloaded ");
	} catch (BitDewException e) {
	    e.printStackTrace();
	} catch (TransferManagerException e) {
	    e.printStackTrace();
	} catch (ModuleLoaderException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    /**
     * This method share the new downloaded file once a download has finished.
     * @param song file name
     * @param md5 the md5 cheksum
     */
    public void republish(String song,String md5) {
	try {
	    //We are going to publish the new downloaded song in the DHT, so we need to 
	    //build a reference to the distributed data catalog on the bootstrap node.
	    dc = (InterfaceRMIdc) ComWorld.getComm(BOOTSTRAP, "rmi", 4325, "dc");
	    dt = (InterfaceRMIdt) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325,"dt");
	    ds = (InterfaceRMIds) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325,"ds");
	    dr = (InterfaceRMIdr) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325,"dr");
	    bitdew = new BitDew(dc, dr, dt, ds);
	    String[] toks = song.split("[\\s\\._-]");
	    //We split the file name in every term composing it, and we index the song name
	    //according to these terms, then we index the md5 too, in order to find the IP address
	    for (int i = 0; i < toks.length; i++) {
		SongBitdew sb = new SongBitdew(song,md5);
		bitdew.ddcPublish(toks[i], sb);
		bitdew.ddcPublish(md5, LOCAL_ADDRESS);
	    }
	} catch (ModuleLoaderException e) {
	    e.printStackTrace();
	} catch (BitDewException e) {
	    e.printStackTrace();
	}
    }
}
