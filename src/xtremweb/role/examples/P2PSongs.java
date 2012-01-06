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
import xtremweb.serv.dc.DataUtil;

public class P2PSongs {
    /*! \example P2PSongs.java In this tutorial we will learn how to configure a P2P file
     * sharing system using BitDew, with this system,  you can share a folder on your machine on a P2P network, 
     * people can find and download files from your computer, and you can make remote requests, to
     * find and download files from the P2P network members. 
     * 
     * <h2> Components : </h2>
     * <ul>
     * <li>Distributed Hash Table</li>
     * <li>Bootstrap Node</li>
     * <li>Peer Nodes</li>
     * </ul>
     * <h3> The Distributed Hash Table : </h3>
     * For this experience, bitdew use the <a href="http://graal.ens-lyon.fr/~fchuffart/dokuwiki/doku.php?id=offtheshelfdlpt">DLPT</a> <a href="http://en.wikipedia.org/wiki/Distributed_hash_table">Distributed Hash Table</a> implementation. 
     * This component will be used as a distributed 'yellow pages' among the network members. It makes easy to find
     * a host sharing the file we are interested on. To successfully achieve this, our yellow pages contain two types of entries.
     * First, one word with the list of BitDew <em>Data</em> objects whose name has this word as a term. 
     * For example, the entry 
     * @code
     * [ "the" , ("THE cardigans-my favorite game.mp3","THE doors-riders on THE storm.mp3","david bowie-THE man who sold the world.mp3")]. 
     * @endcode
     * Corresponds to a list of bitdew Data objects that contains 'the' as part of the file name.
     * As you have seen in the previous exercises, bitdew Data objects contain the md5 file checksum too, this is the file signature, useful to uniquely identify the file on the network, and test
     * the file has not been corrupted after the transfer. MD5 checksums make part of the other type of entries, each of them are put together with a list of IP addresses. For example, let's suppose that the file 
     * "david bowie-the man who sold the world.mp3" has md5sum 9efe0c2a047d66685dfdc0ffffa92446, its entry on the yellow pages will be:
     * @code
     * [ 9efe0c2a047d66685dfdc0ffffa92446 ,  ( 234.34.56.71 , 27.23.78.99 , 134.240.123.25 )]. 
     * @endcode
     * This mean that a file with this md5 is actually hosted on 3 nodes, with the IP's listed above. 
     * 
     * With these entry types, we can find a song by first searching the file list matching our term, once the user choose a file from this list, we search according to its MD5 attribute , 
     * which IP's have this song. Finally, with all these data, a BitDew 'GET' can be stablished and the file is downloaded from the remote peer using the HTTP protocol. In the following image
     * you can see the event sequence, notice that the user application only talks to the API and the DHT library on the implementation layer is in charge of routing the messages according to its 
     * internal algorithms.
     * 
     * \image html events.png
     * 
     * <h2>Node types : </h2>
     * <h2>Bootstrap node :</h2>
     * According to DLPT implementation, one of the nodes must be the bootstrap node, that is, the node 
     * any peer who wants to join the network must contact first. After meeting this node, the peer can put and get information on the yellow pages, in a descentralized way,
     * by using the DLPT internal algorithms to forward the messages to other peers. For this purpose
     * A DLPT daemon will previously run on the bootstrap node, and it is encapsulated in the DC BitDew service.
     * 
     * <h2> Peer Nodes </h2>
     * Peer nodes store physically the files to be downloaded by other peer nodes, and they download files from other network members. For this purpose they run two instances of bitdew:
     * first instance has a DR to manage the HTTP repository to store your files, a DC to locally index their data once somebody wants to download a file, 
     * and a DT to manage  the file transfers. The second bitdew instance references also the previously created DR and DT, but DC references the bootstrap node DHT, this instance will be used
     * to perform researches on the P2P network. 
     * 
     * Next figure shows how the different node types work together : 
     * \image html bitdewp2pArch.png
     * 
     * <h1> Procedure </h1>
     * 
     * For this experience, we will need three computers connected through a TCP/IP network, we call them pc-1, pc-2 and pc-3, we will divide
     * the experience in three steps: song publication, song searching and song downloading.
     * <h2> Song publication </h2>
     * <ol> 
     *  <li> Choose one among your three machines and run there the bootstrap node, in our case we  will use pc-1. Download the latest version of bitdew-stand-alone-X.X.X.jar there, choose a folder (we will call it BITDEW_HOME), and
     *  move the jar there</li>
     *  
     *  <li> DLPT library is here, please download it and move it at the same level
     *  than the file bitdew-stand-alone-XXX.jar</li>
     *  
     *  <li> Download the file <a href="http://www.bitdew.net/properties_p2p.json">properties_p2p.json</a> </li>
     *  
     *  <li> Execute the following command
     *  
     *   @code 
     *   pc-1$ java -cp sbam_standalone.jar:bitdew-stand-alone-X.X.X.jar xtremweb.role.cmdline.CommandLineTool serv --file properties_p2p.json dc
     *   @endcode 
     *   
     *  This command starts the DLPT distributed hash table daemon, details on the properties_p2p.json will be explained in the next step.
     *  
     * <li> Take another host (pc-2 for instance), and download the last version of bitdew-stand-alone-X.X.X.jar, the DLPT jar file, and the file 
     * <a href="http://www.bitdew.net/properties_p2p.json">properties_p2p.json</a></li>
     * 
     * Let's look at three important JSON objects in this file:
     * <ol>
     * <li> <b>xtremweb.serv.dc :  {ddc: false} </b> </li>
     * In this object you can turn on/off the DHT, as it is a demanding process, it is turn off by default, to turn on just set the property ddc (distributed data catalog ) to true
     * <li> <b>xtremweb.core.http: {port: 8080,path:"/",documentRoot:".",uploadServlet:"/fileupload",p2pServlet: "/p2pquery",bootstrapNode: "address.to.my.host.com"}</b><br>
     * The bitdew-stand-alone jar comes with an embedded JETTY web server, JSON object <b>xtremweb.core.http</b>, contains
     * different properties needed to configure it, for this case you just need to configure path and bootstrapNode.   
     * </li>
     * 
     * <ul>
     * 
     * <li><b>path:</b> root for static resources relative to BITDEW_HOME, in this case, our root will be BITDEW_HOME  </li>
     * 
     * <li><b>bootstrapNode:</b> the node where the DHT is running, if you erase this property, the default will be localhost</li>
     * 
     * </ul>
     * 
     * <li> <b>xtremweb.serv.dr.http:{name: "http", port:"8080",path:"songs"}</b><br>
     * 
     * This JSON object describes to bitdew the http repository where files will be stored.
     * To make your files available, you can change "path" for the directory 
     * where songs are stored, in our case we have them on $BITDEW_HOME/songs (remember that <em>path</em> value is relative to the BITDEW_HOME variable)
     * </li>
     * </ol>
     * 
     * <li> Put properties-p2p.json file in the same level that bitdew-stand-alone-X.X.X.jar, and execute :
     * 
     * @code 
     * pc-2$ java -cp sbam_standalone.jar:bitdew-stand-alone-X.X.X.jar xtremweb.role.examples.CommandLineTool serv --file properties_p2p.json dc dr dt ds 
     * @endcode 
     * 
     * Then in a new command line console type :
     * @code
     * pc-2$ java -cp sbam_standalone.jar:bitdew-stand-alone-X.X.X.jar xtremweb.role.examples.P2PSongs properties-p2p.json <bootstrap_node> <directory_to_share>
     * @endcode
     * The following commands will: 
     * <ul> 
     * <li>Start locally the differnt dX's services</li> 
     * <li>Create two Bitdew API's (first one referencing the locally created dc and dt, second one referencing the distributed hash table on the \<bootstrap node\> </li>
     * <li>Register the songs contained in the local \<directory_to_share\> folder in the P2P network </li> 
     * </ul>
     * 
     * </ul> 
     * 
     * <b>(Commented and detailed code is shown below, if you want exact details of how this things are actually performed).</b> 
     * <li> You can repeat steps 2,3 in pc-3, and in as many computers as you desire, each computer will be a peer of our application </li>
     * <h2>Song searching </h2>
     * <li> Now, we can request for files on the P2P network: from any host in
     * your network open your webbrowser and type http://localhost:8080/findterm.html
     * You should see a window similar to the next one :
     * \image html findterm.png
     * In this window you need to fill three fields : 
     * <ul>
     * 	<li>Target directory : you write here in which directory you want to download the file </li>
     * <li> Enter term : you write here the file name you want to find</li>
     * <li> Enter owner ip : this is an optional field, once you launch a search, you will see in the last column the IP list of people having the file, 
     * if you have any preference you can choose the ip to download the file 
     * </ul>
     * <li> Write the keyword you want to look for and click on Send</li>
     * <h2>Song download </h2>
     * <li> If there are files whose name has your word as a term, a list similar to this one should appear, if not 
     * an empty table should appear : </li>
     * 
     * \image html results.png
     * 
     * <li> To download a file, choose the checkbox at the leftmost side and make click on "Download this song"</li>	
     * <li> A message of success should appear, then you can see at the bottom list the file you just have download, you can check
     * that the md5sum is equal to the original file.
     * <h2>Code source </h2>
     * Two java classes have been implemented to build this architecture, P2PClient source code :
     * <h3> P2PClient.java </h3>
     * \include P2PClient.java
     * <li>P2PSongs source-code :
     */

    //
    //Local Data catalog
    //
    private InterfaceRMIdc dc;

    //
    // Data transfer service
    //
    private InterfaceRMIdt dt;

    //
    // Data repository service
    //
    private InterfaceRMIdr dr;

    //
    // Data scheduler service
    //
    private InterfaceRMIds ds;

    //
    // Distributed data catalog service
    //
    private InterfaceRMIdc ddc;

    //
    // BitDew api to interface with dc
    //
    private BitDew bitdew;

    //
    // BitDew api to remotely interface with ddc
    //
    private BitDew bitdewddc;

    //
    // Logger
    //
    private static Logger log = LoggerFactory.getLogger("P2PSongs");
    
    //
    // Machine IP address where this code runs
    //
    private String LOCAL_ADDRESS;

    //
    // Class constructor, load different services and API instances
    // @param bootstrap
    //           the ip address of the node running initially the DHT
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
	    // Once we succesfully get RMI references, we build two different APIs, first one referencing the bootstrap and
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
		    Data songdata = new Data();
		    String md5 = DataUtil.checksum(file);
		    songdata.setname(file.getName());
		    songdata.setchecksum(md5);
		   
		    log.info("Putting song " + file.getName() + " with uid " + d.getuid() + "md5 is " + md5);
		 //
		 // we publish on the DHT  the song term associated with the object SongBitdew, so we will be able to find
		 // the song when any of its terms is searched.  
		 //
		    bitdewddc.ddcPublish(toks[i], songdata);
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
	File fp = new File(System.getProperty("user.dir") + File.separator + args[0]);
	if (fp.exists()) {
	    System.setProperty("PROPERTIES_FILE",
		    System.getProperty("user.dir") + File.separator + args[0]);
	    P2PSongs p2p = new P2PSongs(args[1]);
	    p2p.put(args[2]);
	}

    }

}
