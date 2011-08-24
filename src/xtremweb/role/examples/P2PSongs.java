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
import xtremweb.role.examples.obj.SongBitdew;
import xtremweb.serv.dc.DataUtil;

public class P2PSongs {
	
	/*! \example P2PSongs.java
	 * 
	 * This example shows how to configure a P2P file sharing system using BitDew.
	 * For this experience you will need several computers connected through internet
	 * <ol>
	 * 
	 * <li> 
	 * Choose one computer as your bootstrap node and run here the daemon of a distributed hash table :
	 * @code
	 * java -jar bitdew-stand-alone-X.X.X.jar serv dc
	 * @endcode
	 * </li>
	 * 
	 * <li>
	 * In another host, download the file <a>properties.json</a> and setup the property xtremweb.serv.dr.http:{name: "http", port: "8080",path:"data/songs"},
	 * putting in path, the directory from you want the songs be downloaded
	 * </li>
	 * 
	 * <li> 
	 * Initialize a data repository and a data catalog in your local machine using :
	 * @code
	 * java -cp bitdew-stand-alone-X.X.X.jar serv dc dt
	 * @endcode
	 * </li>
	 * 
	 * <li>
	 * Execute the class P2PSongs using:
	 * @code
	 * java -cp bitdew-stand-alone-X.X.X.jar xtremweb.role.examples.P2PSongs <bootstrap_node> <directory>
	 * @endcode
	 * Where <bootstrap_node> is the hostname of the node choosen in the first step and <directory> is the directory you
	 * want to share
	 * </li>
	 * 
	 * <li>
	 * You can repeat the previous steps 2,3,4 in as many computers as you desire, each computer will be a peer of our application
	 * </li>
	 * 
	 * <li>
	 * Now we can request for files on the P2P network: choose any host that you own and type
	 * @code
	 * java -cp bitdew-stand-alone-X.X.X.jar xtremweb.role.examples.P2PClient <bootstrap_node> <term>
	 * @endcode
	 * 
	 * Where <bootstrap_node> is the node you choose on your first step, and <term> is the term that you want to look for
	 * (ex. maddona)
	 * </li>
	 * 
	 * <li>
	 * A numbered list of possible matches will appear :
	 * <ol>
	 * <li>Madonna_like_a_virgin.mp3</li>
	 * <li>Madonna_die_another_day.mp3</li>
	 * <li>Madonna_american_life.mp3</li>
	 * </ol>
	 * </li>
	 * 
	 * <li>
	 * We use for the moment a console User-Interface (this will be soon improved by a graphical web-ui), so just type
	 * the number of the file you want to download and downloading will inmediately begin.
	 * </li>
	 * </ol>
	 */
	
	
	/**
	 * Local Data catalog
	 */
	private InterfaceRMIdc dc ;
	
	/**
	 * Data transfer service
	 */
	private InterfaceRMIdt dt ;
	
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
	private Logger log = LoggerFactory.getLogger("P2PSongs");
	
	/**
	 * P2P loader
	 * @param bootstrap the ip address of the node running initially the DHT
	 */
	public P2PSongs(String bootstrap)
	{	
		try {
			dc = (InterfaceRMIdc) ComWorld.getComm(InetAddress.getLocalHost().getHostAddress(), "rmi", 4325, "dc");
			dt = (InterfaceRMIdt) ComWorld.getComm(InetAddress.getLocalHost().getHostAddress(), "rmi", 4325, "dt");
			dr = (InterfaceRMIdr) ComWorld.getComm(InetAddress.getLocalHost().getHostAddress(), "rmi", 4325, "dr");
			ds = (InterfaceRMIds) ComWorld.getComm(InetAddress.getLocalHost().getHostAddress(), "rmi", 4325, "ds");			
			ddc = (InterfaceRMIdc) ComWorld.getComm(bootstrap, "rmi", 4325, "dc");
			bitdewddc = new BitDew( ddc,dr,dt,ds );
			bitdew = new BitDew( dc,dr,dt,ds );						
		} catch (ModuleLoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * This method publish all the songs in a directory on the P2P network using bitdew
	 * @param dirpath local path where we can find the files to share
	 */
	public void put(String dirpath)
	{try {
		System.out.println("puting file from folder " + dirpath);
		File dir = new File(dirpath);
		File[] files = dir.listFiles();
		System.out.println("files size is " + files.length);
		for(int j = 0 ; j < files.length ; j++)
		{	//toks : tokens making part of files[j] name
			String[] toks = files[j].getName().split("[\\s\\._-]");
			File file = files[j];
			Data d = bitdew.createData(file);
			for( int i =0 ; i < toks.length; i++ )
			{	log.info("Parsing token " + toks[i]);
				d.setoob("http");
				Locator loc = bitdew.createRemoteLocator(d,"http");
				bitdew.associateDataLocator(d,loc);
				SongBitdew sb;
				String md5 = DataUtil.checksum(file);
				sb = new SongBitdew(file.getName(),md5);
				log.info("Putting song " + file.getName() + " with uid " + d.getuid() + "md5 is " + md5);
				bitdewddc.ddcPublish(toks[i],sb);
				bitdewddc.ddcPublish(md5,InetAddress.getLocalHost().getHostAddress());
				
			}
			bitdew.put(file,d);
		}
	}catch (BitDewException e) {
		e.printStackTrace();
	}catch (UnknownHostException e) {
		e.printStackTrace();
	}
	}
	
	
	
	/**
	 * Applications main method
	 * @param args 	 args[0] bootstrap node , args[1] songs directory
	 */
	public static void main(String[] args)
	{
		P2PSongs p2p = new P2PSongs(args[0]);
		p2p.put(args[1]);
	}

}
