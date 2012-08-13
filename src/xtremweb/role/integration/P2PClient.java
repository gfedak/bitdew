package xtremweb.role.integration;

import java.io.BufferedReader;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.api.transman.TransferManager;
import xtremweb.api.transman.TransferManagerException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.Interfacedc;
import xtremweb.core.iface.Interfacedr;
import xtremweb.core.iface.Interfaceds;
import xtremweb.core.iface.Interfacedt;
import xtremweb.core.log.*;
import xtremweb.core.obj.dc.Data;
import xtremweb.serv.dt.OOBTransfer;

//
//This class request over the P2P network a specific term for later download
//
//@author josefrancisco
//
//
public class P2PClient {
    
    private Logger log;
    
    //
    // Bitdew API
    private BitDew bitdew;
    
    //
    // Data Catalog service
    private Interfacedc dc;
    
    //
    // Data Transfer service
    private Interfacedt dt;
    
    //
    // Data repository service
    private Interfacedr dr;
    
    //
    // Data scheduler service
    private Interfaceds ds;
    
    //
    // Transfer manager API
    private TransferManager tm;
    
    //
    //Bootstrap node hostname
    private String BOOTSTRAP;
    
    //
    //Localhost hostname
    private String LOCAL_ADDRESS;
    
    //
    // Class constructor, initialize services and API 
    //
    public P2PClient(String bootstrap) {
	BOOTSTRAP = bootstrap;
	try {
	    log = LoggerFactory.getLogger("P2PClient");
	    log.setLevel("INFO");
	    log.info("logging sysgtem activated");
	    Log4JLogger.setProperties("conf/log4jcmdlinetool.properties");
	    LOCAL_ADDRESS = InetAddress.getLocalHost().getHostAddress();
	    dc = (Interfacedc) ComWorld.getComm(BOOTSTRAP, "rmi", 4325, "dc");
	    dt = (Interfacedt) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325, "dt");
	    tm = new TransferManager(dt);
	    bitdew = new BitDew(dc, dr, ds,true);
	} catch (ModuleLoaderException e) {
	    e.printStackTrace();
	} catch (UnknownHostException e) {
	    e.printStackTrace();
	} catch (LoggerException e) {
	    e.printStackTrace();
	}
    }
    //
    // Search for a list of songs that match a specified term.
    // @param term the term to search for 
    public void get(String term) {
	try {
	    BufferedReader stdIn = null;
	    List results;
	    //ddcSearch method will return the list of file names having "term" as one of their words
	    results = bitdew.ddcSearch(term);
	    
	    System.out.println("The value of results is " + results + " and the size is " + results.size());
	    for (int i = 1; i <= results.size(); i++) {
		Data sbd = (Data) results.get(i - 1);
		System.out.println("Result " + sbd.getname() + " " + sbd.getchecksum() + " " + getIps(sbd.getchecksum()));
	    }
	    System.out.println("END");
	    bitdew.ddcStop();
	} catch (BitDewException e) {
	    e.printStackTrace();
	}
    }
    
    //
    // This method includes you as the owner of a file once you have downloaded it
    // @param song file name
    // @param md5 the md5 cheksum
    //
    public void republish(String song, String md5) throws ModuleLoaderException, BitDewException {
	try {
	    // We are going to publish the new downloaded song in the DHT, so we
	    // need to
	    // build a reference to the distributed data catalog on the
	    // bootstrap node.
	    dc = (Interfacedc) ComWorld.getComm(BOOTSTRAP, "rmi", 4325, "dc");
	    dt = (Interfacedt) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325, "dt");
	    ds = (Interfaceds) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325, "ds");
	    // build a new bitdew instance from these services.
	    bitdew = new BitDew(dc, dr, ds,true);
	    String[] toks = song.split("[\\s\\._-]");
	    // We split the file name to extract each word composing it, and we
	    // index the song name
	    // with each one of this words, then we index the MD5 too together
	    // with the IP address
	    for (int i = 0; i < toks.length; i++) {
		Data sb = new Data();
		sb.setname(song);
		sb.setchecksum(md5);
		bitdew.ddcPublish(md5, LOCAL_ADDRESS);
	    }
	} catch (ModuleLoaderException e) {
	    e.printStackTrace();
	    throw new ModuleLoaderException(e.getMessage());
	} catch (BitDewException e) {
	    e.printStackTrace();
	    throw new BitDewException(e.getMessage());
	}
    }
    
    //
    // Download a song from the P2P network, it builds the neccessary build infrastructure to achieve this.
    // @param songname the song name
    // @param md5 the song signature 
    //
    public void download(String songname, String md5, String ip) {
	try {
	    // first retrieve the ip list of machines having signatures md5
	    List ips = bitdew.ddcSearch(md5);
	    System.out.println("The ips for that md5 are " + ips.size() + " name to download " + songname);
	    // Once we have an ip of a machine having that md5sum, we are able
	    // to begin the download,
	    // but first we need to contact that machine's catalog and
	    // repository
	   // bitdew.ddcStop();
	    System.out.println("Creating services ");
	    if (ips != null && ips.size() != 0 && ip.equals("none")) {
		dr = (Interfacedr) ComWorld.getComm((String) ips.get(0), "rmi", 4325, "dr");
		dc = (Interfacedc) ComWorld.getComm((String) ips.get(0), "rmi", 4325, "dc");
	    } else if (!ip.equals("none")) {
		dr = (Interfacedr) ComWorld.getComm((String) ip, "rmi", 4325, "dr");
		dc = (Interfacedc) ComWorld.getComm((String) ip, "rmi", 4325, "dc");
	    } else
		throw new BitDewException("There is not ip for that md5 ! ");
	    System.out.println("Services created");
	    // Then we create a new bitdew API from these newly created services
	    bitdew = new BitDew(dc, dr, ds);
	    tm.start();
	    File file = new File(songname);
	    // the getDataFromMd5 method help us to retrieve a bitdew data
	    // having a given MD5sum
	    Data d = bitdew.getDataFromMd5(md5);
	    // we will use http protocol
	    d.setoob("http");
	    OOBTransfer oob;
	    // download begins
	    oob = bitdew.get(d, file);
	    tm.registerTransfer(oob);
	    tm.waitFor(d);
	    tm.stop();
	    republish(songname, md5);
	    System.out.println("DONE");
	} catch (BitDewException e) {
	    e.printStackTrace();

	} catch (TransferManagerException e) {
	    e.printStackTrace();

	} catch (ModuleLoaderException e) {
	    e.printStackTrace();

	}
    }
    
    // Given a md5 file checksum, retrieve a comma-separated list of IPs storing that file
    // @param md5 the md5 checksum to search for
    private String getIps(String md5) throws BitDewException {
	String responsexml = "";
	List ips;
	ips = bitdew.ddcSearch(md5);
	HashSet s = new HashSet();
	s.addAll(ips);
	for (Iterator iterator = s.iterator(); iterator.hasNext();) {
	    String object = (String) iterator.next();
	    responsexml += object + ",";
	}
	return responsexml.substring(0, responsexml.length() - 1);
    }
    
    //Main method 
    // Usage : java -cp xtremweb.role.examples.P2PClient search term_to_search
    //         java -cp xtremweb.role.examples.P2PClient download song_name song_md5 [ip_source]
    public static void main(String[] args) {
	P2PClient p2p = new P2PClient(args[1]);
	if (args[0].equals("search")) {
	    p2p.get(args[2]);
	} else if (args[0].equals("download"))
	    p2p.download(args[2], args[3], args[4]);
	else{
	    System.out.println("There was an error on your syntax ");
	    System.out.println("Usage 1) java -cp sbam_standalone.jar:bitdew-stand-alone.jar xtremweb.role.examples.P2PClient search term_to_search");
            System.out.println("      2) java -cp sbam_standalone.jar:bitdew-stand-alone.jar xtremweb.role.examples.P2PClient download song_name song_md5 [ip_source]");
	}
    }
}