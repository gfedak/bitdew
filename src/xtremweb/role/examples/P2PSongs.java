package xtremweb.role.examples;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
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
import xtremweb.core.obj.dc.Locator;
import xtremweb.role.examples.obj.SongBitdew;
import xtremweb.serv.dc.DataUtil;
import xtremweb.serv.dt.OOBTransfer;

public class P2PSongs {
    /*! \example P2PSongs.java In this tutorial we will learn how to configure a P2P file
     * sharing system using BitDew. For this experience you will need several
     * computers connected through a TCP/IP network. Next image shows how this can be achieved using Bitdew data services: 
     * \image html archp2p.jpg 
     * Our P2P infrastructure consist on two types of
     * nodes : <ul> <li> Bootstrap node , the initial connection to our P2P
     * network, it runs a bitdew dc with a Distributed Hash Table implementation to handle a
     * distributed song index. </li> <li> Peer node: each peer creates two
     * Bitdew API's that differs on the data catalog they use. One of them uses a
     * local catalog to index the locally hosted songs the peer wants to
     * contribute. The second one references the Distributed Hash Table to query
     * and download any song in the P2P network. </li> </ul> 
     * <ol> 
     *  <li> Download the last version of bitdew-stand-alone jar, choose a folder (we will call it BITDEW_HOME), and
     *  put the jar there </li>
     *  <li> Choose one computer as your bootstrap node and run :
     *   @code java -jar bitdew-stand-alone-X.X.X.jar serv dc
     *   @endcode 
     *  </li> This command starts a distributed hash table daemon, we
     * use <a href=
     * "http://graal.ens-lyon.fr/~fchuffart/dokuwiki/doku.php?id=offtheshelfdlpt"
     * >DLPT</a> 
     * <li> In another host, download the file <a
     * href="http://www.bitdew.net/properties_p2p.json">properties_p2p.json</a> . Let's look at
     * two important JSON objects in this file:
     * <ol>
     * <li> <b>xtremweb.core.http: {port: 8080,path:"/",documentRoot:".",uploadServlet:"/fileupload",p2pServlet: "/p2pquery",bootstrapNode: "address.to.my.host.com", sshTunneling:"yes"}</b><br>
     * The bitdew-stand-alone jar comes with an embedded JETTY web server. JSON object <b>xtremweb.core.http</b>, contains
     * different properties needed to configure this web server, you just need to configure path,bootstrapNode and sshTunneling.   
     * <ul>
     * <li>path: root for static resources relative to BITDEW_HOME, in this case, our root will be BITDEW_HOME  </li>
     * <li>bootstrapNode: the node where the DHT is running, if you erase this property, the default will be localhost</li>
     * <li>sshTunneling: if you are performing the experience in a restricted network, setting "yes" to this attribute will
     * imply that you will signal the bootstrap node later, when building the ssh tunnel</li>
     * </ul>
     * <li> <b>xtremweb.serv.dr.http:{name: "http", port:"8080",path:"songs"}</b><br>
     * This JSON object describes to bitdew the http repository where files will be stored.
     * To make your files available, you can change "path" for the directory 
     * where songs are stored, in our case we have them on $BITDEW_HOME/songs (remember that <em>path</em> value is relative to the BITDEW_HOME variable)</li>
     * </ol>
     * <li> Put properties-p2p.json file in the same level that
     * bitdew-stand-alone-X.X.X.jar, and execute :
     * 
     * @code 
     * java -jar bitdew-stand-alone-X.X.X.jar serv dc dr dt ds 
     * @endcode 
     * 
     * Then in a new command line console type :
     * @code
     * java -cp bitdew-stand-alone-X.X.X.jar xtremweb.role.examples.P2PSongs properties-p2p.json <bootstrap_node> <directory>
     * @endcode
     * The following commands will: <ul> <li>Start locally the differnt dX's
     * services</li> <li>Create two Bitdew API's (first one referencing the
     * locally created dc and dt, second one referencing the distributed hash
     * table on the \<bootstrap node\> <li>Register the songs contained in the
     * local \<directory\> folder in the P2P network </li> </ul></ul> 
     * <b>(Commented and detailed code is shown below, if you want exact details of how this things are actually performed).</b> 
     * <li> You can
     * repeat step 3 in as many computers as you desire, each
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
     * 
     * Download and republish methods on the P2PClient.java class are in charge of this. First download method
     * build the bitdew infrastructure to be able to download this file.
     * @code
     * public void download(String songname, String md5) {
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
     * @endcode
     * Republish method register the new downloaded song in the DHT, as now this peer can share this new file.
     * @code
     * public void republish(String song,String md5) {
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
     * @endcode
     * <li>P2PSongs source-code :
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
	    //
	    //We take the hostname as we are going to need it to configure our bitdew services.
	    //
	    LOCAL_ADDRESS = InetAddress.getLocalHost().getHostAddress();
	    //
	    //Initialize services, dc will reference the local data catalog and ddc will reference the
	    //distributed hash table on the bootstrap node
	    //
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
	    //
	    // Once we succesfully get RMI references, we build two different APIS, first one referencing the bootstrap and
	    // second one referencing the local data catalog
	    //
	    bitdewddc = new BitDew(ddc, dr, dt, ds);
	    bitdew = new BitDew(dc, dr, dt, ds);
	} catch (ModuleLoaderException e) {
	    e.printStackTrace();
	} catch (UnknownHostException e) {
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
	    File dir = new File(dirpath);
	    File[] files = dir.listFiles();
	    // iterate over directory content
	    for (int j = 0; j < files.length; j++) { 
		//
		// We extract each song term, these terms will work as indexes on the Distributed
		// hash table
		//
		String[] toks = files[j].getName().split("[\\s\\._-]"); 
		File file = files[j];
		//
		// We create a Bitdew data from each file
		//
		Data d = bitdew.createData(file);
		//
		// For each token we build a BitDew locator, we associate this locator
		// with our data, and we build a SongBitdew object
		//
		for (int i = 0; i < toks.length; i++) {
		    d.setoob("http");
		    Locator loc = bitdew.createRemoteLocator(d, "http");
		    bitdew.associateDataLocator(d, loc);
		 //
		 // SongBitdew is a serializable object representing the song file, it has a md5 checksum and a file name. 
		 // We need it as the DLPT implementation receives a serializable object as second parameter.
		 //
		    SongBitdew sb;
		    String md5 = DataUtil.checksum(file);
		    sb = new SongBitdew(file.getName(), md5);
		    log.info("Putting song " + file.getName() + " with uid " + d.getuid() + "md5 is " + md5);
		 //
		 // we publish on the DHT  the song term associated with the object SongBitdew, so we will be able to find
		 // the song when any of its terms is searched.  
		 //
		    bitdewddc.ddcPublish(toks[i], sb);
		 //
		 // As performed in the previous step, if we request a given term, we will have a BitdewSong object <md5,file name>,
		 // so we need to map the md5 with our local IP address, so anyone can stablish a connection with us to
		 // download our song 
		 //
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
	//First argument is file name
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
