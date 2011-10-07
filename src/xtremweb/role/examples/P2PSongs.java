package xtremweb.role.examples;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.InterfaceRMIdc;
import xtremweb.core.iface.InterfaceRMIdr;
import xtremweb.core.iface.InterfaceRMIds;
import xtremweb.core.iface.InterfaceRMIdt;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.serv.ServiceLoader;
import xtremweb.role.examples.obj.SongBitdew;
import xtremweb.serv.dc.DataUtil;

public class P2PSongs {

    /*! \example P2PSongs.java This example shows how to configure a P2P file
     * sharing system using BitDew. For this experience you will need several
     * computers connected through internet. Next image shows how this will work: 
     * \image html archp2p.jpg 
     * Our P2P infrastructure consist on two types of
     * nodes : <ul> <li> Bootstrap node , the initial connection to our P2P
     * network, it uses a Distributed Hash Table implementation called 
     * <a href="http://graal.ens-lyon.fr/~fchuffart/dokuwiki/doku.php?id=offtheshelfdlpt">DLPT</a> to handle a
     * distributed song index. </li> <li> Peer node: each peer creates two
     * Bitdew API's that differs on the data catalog they use. One API uses a
     * local catalog to index the locally hosted songs the peer wants to
     * contribute. The second API references the Distributed Hash Table to query
     * and download any song in the P2P network. </li> </ul> <ol> <li> Choose
     * one computer as your bootstrap node and run :
     * 
     * @code java -jar bitdew-stand-alone-X.X.X.jar serv dc
     * 
     * @endcode </li> This command starts a distributed hash table daemon, we
     * use <a href=
     * "http://graal.ens-lyon.fr/~fchuffart/dokuwiki/doku.php?id=offtheshelfdlpt"
     * >DLPT</a> <li> In another host, download the file <a
     * href="http://www.bitdew.net/properties.json">properties.json</a> and
     * setup the property xtremweb.serv.dr.http:{name: "http", port:
     * "8080",path:"data/songs"}, changing "path" for the directory where you
     * have the songs to share. In our example, songs are in "data/songs"
     * folder. </li>
     * 
     * <li> Put properties.json file in the same level that
     * bitdew-stand-alone-X.X.X.jar, and execute :
     * 
     * @code java -jar serv dc dr dt ds 
     * java -cp bitdew-stand-alone-X.X.X.jar xtremweb.role.examples.P2PSongs properties.json <bootstrap_node>
     * <directory>
     * 
     * @endcode 
     * The following command will: <ul> <li>Start locally the differnt dX's
     * services</li> <li>Create two Bitdew API's (first one referencing the
     * locally created dc and dt, second one referencing the distributed hash
     * table on the \<bootstrap node\> <li>Register the songs contained in the
     * local \<directory\> folder in the P2P network </li> </ul> <li> You can
     * repeat the previous steps 2,3 in as many computers as you desire, each
     * computer will be a peer of our application </li>
     * 
     * <li> Now, we can request for files on the P2P network: from any host in
     * your network open your webbrowser and type http://<bootstrap-node>:8080/findterm.html
     * You should see a window similar to the next one (in order that this can work, port 8080 must be open in that machine) :
     * \image html findterm.png
     * 
     * <li> Write the keyword you want to look for and click on Send</li>
     * 
     * <li> A list similar to this one should appear : </li>
     * 
     * \image html results.png
     * 
     * <li> Download will begin as soon as you click on any link </li>	
     * </ol>
     */

    /**
     * Local Data catalog
     */
    private InterfaceRMIdc dc;

    /**
     * Data transfer service
     */
    private InterfaceRMIdt dt;

    /**
     * Data repository service
     */
    private InterfaceRMIdr dr;

    /**
     * Data scheduler service
     */
    private InterfaceRMIds ds;

    /**
     * Distributed data catalog service
     */
    private InterfaceRMIdc ddc;

    /**
     * BitDew api to interface with dc
     */
    private BitDew bitdew;

    /**
     * BitDew api to remotely interface with ddc
     */
    private BitDew bitdewddc;

    /**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger("P2PSongs");

    private String LOCAL_ADDRESS;

    /**
     * Class constructor, load different services and API instances
     * 
     * @param bootstrap
     *            the ip address of the node running initially the DHT
     */
    public P2PSongs(String bootstrap) {
	try {
	    // Common Java Method to get the host ip address
	    LOCAL_ADDRESS = InetAddress.getLocalHost().getHostAddress();
	    // Initialize services and two different bitdew APIs.
	    dc = (InterfaceRMIdc) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325,
		    "dc");
	    dt = (InterfaceRMIdt) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325,
		    "dt");
	    dr = (InterfaceRMIdr) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325,
		    "dr");
	    ds = (InterfaceRMIds) ComWorld.getComm(LOCAL_ADDRESS, "rmi", 4325,
		    "ds");
	    ddc = (InterfaceRMIdc) ComWorld.getComm(bootstrap, "rmi", 4325,
		    "dc");
	    bitdewddc = new BitDew(ddc, dr, dt, ds);
	    bitdew = new BitDew(dc, dr, dt, ds);
	} catch (ModuleLoaderException e) {
	    e.printStackTrace();
	} catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * This method publish all the songs in a local directory on the P2P network
     * using bitdew, each song title is tokenized and each token is registered
     * 
     * @param dirpath
     *            local path where we can find the files to share
     */
    public void put(String dirpath) {
	try {
	    log.info("puting file from folder " + dirpath);
	    File dir = new File(dirpath);
	    File[] files = dir.listFiles();
	    log.info("files size is " + files.length);
	    // iterate over directory content
	    for (int j = 0; j < files.length; j++) { // toks : tokens making
						     // part of files[j] name
		String[] toks = files[j].getName().split("[\\s\\._-]");
		File file = files[j];
		Data d = bitdew.createData(file);
		// iterate over file tokens,
		for (int i = 0; i < toks.length; i++) {
		    log.info("Parsing token " + toks[i]);
		    d.setoob("http");
		    Locator loc = bitdew.createRemoteLocator(d, "http");
		    bitdew.associateDataLocator(d, loc);
		    // auxiliar class representing a song, attributes: file, md5
		    SongBitdew sb;
		    String md5 = DataUtil.checksum(file);
		    sb = new SongBitdew(file.getName(), md5);
		    log.info("Putting song " + file.getName() + " with uid "
			    + d.getuid() + "md5 is " + md5);
		    // we publish the token associated with the object
		    // SongBitdew
		    bitdewddc.ddcPublish(toks[i], sb);
		    // we publish the song md5 and
		    bitdewddc.ddcPublish(md5, LOCAL_ADDRESS);
		}
		bitdew.put(file, d);
	    }
	} catch (BitDewException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Applications main method
     * 
     * @param args
     *            args[0] bootstrap node , args[1] songs directory
     */
    public static void main(String[] args) {
	log.debug("Searching for file in " + System.getProperty("user.dir")
		+ "/" + args[0]);
	File fp = new File(System.getProperty("user.dir") + "/" + args[0]);
	if (fp.exists()) {
	    System.setProperty("PROPERTIES_FILE",
		    System.getProperty("user.dir") + "/" + args[0]);
	    P2PSongs p2p = new P2PSongs(args[1]);
	    p2p.put(args[2]);
	}

    }

}
