package xtremweb.role.examples;

import jargs.gnu.CmdLineParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Vector;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamException;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.api.transman.TransferManagerException;
import xtremweb.api.transman.TransferManagerFactory;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.CommRMITemplate;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.InterfaceRMIdr;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.role.cmdline.CommandLineTool;

/**
 * This class makes available a remote FTP server folder in a bitdew powered
 * desktop grid.
 * 
 * @author jsaray
 * 
 */
public class FTPAvailable {

    /*! \example FTPAvailable.java
     * 
     * This example starts bitdew services (dr,dc,dt) and makes available files
     * on a remote ftp folder in bitdew. Then you can use "CommandLineTool get" option to
     * request new available files.
     * 
     * <ol> <li> Download and locate the file <a>xtremweb.properties</a>  in the same 
     * directory that the jar distribution.</li>
     * <li> Uncomment the commented lines concerning the ftp configuration, by default it 
     * will connects to ftp.lip6.fr, but you can change the configuration with your own ftp configuration data
     * (hostname,port,directory to make available,username and password). If you leave the passwd field unfilled, FTPAvailable
     * will try to perform an anonymous connection </li>
     * @code #xtremweb.serv.dr.protocols=dummy http ftp
     * #xtremweb.serv.dr.ftp.server=ftp.lip6.fr
     * #xtremweb.serv.dr.ftp.port=21 xtremweb.serv.dr.ftp.login=anonymous
     * #xtremweb.serv.dr.ftp.path=/pub/linux/distributions/slackware/slackware-current
     * #xtremweb.serv.dr.ftp.passwd=
     * @endcode <li> In the remote ftp folder must
     * exist the file CHECKSUMS.md5. FTPAvailable needs this file in order
     * to successfully complete a transfer. It should have the format <md5_checksum> './'<filename>, for example</li>
     * @code 928143c5d1a7825bc5f3c1d18fde1e31 ./ANNOUNCE.13_1
     * c08333e6569a6fd66fd59999bfa94eb5 ./BOOTING.TXT
     * 19494cd00c011661a882c23772eaa7f0 ./CHANGES_AND_HINTS.TXT
     * 18810669f13b87348459e611d31ab760 ./COPYING
     * @endcode
     * 
     * <li> Execute the following command </li>
     * @code java -cp bitdew-stand-alone.jar xtremweb.role.examples.FTPAvailable -f myfile.properties
     * @endcode
     * 
     * <li> This command will : <ol> <li>launch Bitdew services needed for this
     * experiment (data catalog,data transfer management,data repository) </li>
     * <li>connect to the configured FTP server ("xtremweb.serv.dr.ftp.server" property)</li>
     * <li>Build bitdew infrastructure (Data,Locators) needed to make files on 
     * the configured folder (property xtremweb.serv.dr.ftp.path) visible </li> </ol>
     * </li> <li>At the end of the command execution you should get something
     * like this :</li>
     * 
     * @code File ANNOUNCE.13_1 successfully available, uid=6390d3a0-1cbc-31e0-ab7e-c0c64755d3e6 
     * File BOOTING.TXT successfully available, uid=639edd60-1cbc-31e0-ab7e-c0c64755d3e6 
     * File CHANGES_AND_HINTS.TXT successfully available, uid=63a45ba0-1cbc-31e0-ab7e-c0c64755d3e6 
     * To retrieve any of these files in your system inside a file <file_name> please use xtremweb.role.cmdline.CommandLineTool get
     * <uid> <file_name>
     * 
     * @endcode <li> You can execute CommandLineTool get command in order to retrieve a
     * file from the ftp server, for example :</li>
     * 
     * @code java -cp xtremweb.role.cmdline.CommandLineTool get 639edd60-1cbc-31e0-ab7e-c0c64755d3e6 LOCAL_BOOTING.TXT
     * 
     * @endcode
     * 
     * <li> This command will download the file of uid
     * 639edd60-1cbc-31e0-ab7e-c0c64755d3e6 (remotely BOOTING.TXT) to the local
     * file LOCAL_BOOTING.TXT, you should get the following message:</li>
     * 
     * @code [ INFO] data has been successfully copied to LOCAL_BOOTING.TXT
     * 
     * @endcode Application code source : </ol>
     */
    /**
     * Apache FTPClient object
     */
    private FTPClient cl;

    /**
     * BitDew API to make available the FTP folder
     */
    private BitDew bd;

    /**
     * Data repository RMI interface
     */
    
    private InterfaceRMIdr dr;
    private Protocol prot;
    /**
     * Log4J loggger
     */
    private Logger log = LoggerFactory.getLogger("FTPAvailable");
    /**
     * FTPAvailable default constructor
     */
    public FTPAvailable(boolean verbosity,String fileName) {
	String[] serverargs = { "serv", "dc", "dr", "dt","ds" };
	new CommandLineTool(serverargs);
	Vector comms;
	try {
	    if (verbosity)
		log.setLevel("debug");
	    comms = ComWorld.getMultipleComms("localhost", "rmi", 4325, "dc","dr", "dt","ds");
	    dr = (InterfaceRMIdr) comms.get(1);
	    prot = dr.getProtocolByName("ftp");
	    bd = new BitDew(comms);
	    cl = new FTPClient();
	} catch (ModuleLoaderException e) {
	    e.printStackTrace();
	} catch (RemoteException e) {
	    e.printStackTrace();
	}

    }
    
   

    /**
     * This method prints the syntax of FTPAvailable command
     */
    private static void printUsage() {
	System.err
		.println("Usage : FTPAvailable [-v][-f <fileName>] "
			+ "Options:\n"
			+ "-v verbosity\n"
			+ "-f name of a .properties file");
			
    }

    /**
     * Program main method
     * 
     * @param args
     */

    public static void main(String[] args) {
	System.out.println(System.getProperty("user.dir"));
	try {
	    CmdLineParser parser = new CmdLineParser();
	    CmdLineParser.Option verbose = parser.addBooleanOption('v',"verbose");
	    CmdLineParser.Option fileNameo = parser.addStringOption('f', "file");
	    parser.parse(args);
	    Boolean verb = (Boolean) parser.getOptionValue(verbose,Boolean.FALSE);
	    String fileName = (String) parser.getOptionValue(fileNameo,null);
	    if(fileName!=null)
	    {	
		String tot = System.getProperty("user.dir") + "/" + fileName;
		System.setProperty("PROPERTIES_FILE", tot);
	    }
	    String[] serverargs = { "serv", "dc", "dr", "dt" };
	    new CommandLineTool(serverargs);
	    FTPAvailable ftpa = new FTPAvailable(verb.booleanValue(),fileName);
	    ftpa.connect();
	    ftpa.login();
	    ftpa.changeDirectory();
	    ftpa.makeAvailable();
	    ftpa.disconnect();
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	    printUsage();
	    System.exit(2);
	}
    }

    /**
     * This method creates the locators needed so bitdew can recognize the
     * data in a remote ftp folder on the data grid.
     */
    public Vector makeAvailable() {
	Properties md5 = null;
	Vector v = new Vector();
	try {
	    cl.enterLocalPassiveMode();
	    FTPFile[] files = cl.listFiles();
	    log.info("calculating md5 signatures from CHECKSUMS.md5");
	    md5 = getSignatures();
	    for (int i = 0; i < files.length; i++) {
		String name = files[i].getName();
		//if the file is a checksum file, we do not want to make it available:
		if (name.equals("CHECKSUMS.md5")
			|| name.equals("CHECKSUMS.md5.asc")
			|| files[i].isDirectory())
		    continue;
		Data data = bd.createData(name,"FTP",files[i].getSize(),md5.getProperty(name));
		Locator remote_locator = bd.createRemoteLocator(data);
		bd.associateDataLocator(data, remote_locator);
		log.info("File " + name + " , uid="+ data.getuid());
		v.add(data.getuid());
	    }
	    log.info("To retrieve any of these files in your system on a file <file_name> please use CmdLineTool get <uid> <file_name>");
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (BitDewException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    log.debug("weird exception");
	    e.printStackTrace();
	}
	return v;
    }

    /**
     * Disconnects this class instance from the FTP server
     */
    public void disconnect() {
	try {
	    cl.disconnect();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Assuming there is a SIGNATURES.md5 file on pathname, this method parses
     * that file and builds a properties structure to provide an easy access to
     * the md5 file signatures.
     * 
     * @param pathname
     * @return a hash structure <name of file,md5 signature>
     * @throws FileNotFoundException
     *             if a CHECKSUMS.MD5 file could not be found
     */
    public Properties getSignatures() throws Exception {
	Properties p = new Properties();
	boolean exist = false;
	FileOutputStream fos;
	try {
	    log.debug("entered into getSignatures, number of files "
		    + cl.listFiles().length);
	    fos = new FileOutputStream(new File("sigs.md5"));
	    boolean result; 
	    cl.enterLocalPassiveMode();
	    result = cl.retrieveFile("CHECKSUMS.md5", fos);
	    fos.close();
	    if (!result)
		throw new FileNotFoundException(
			"There is no CHECKSUMS.md5 file on that directory");
	    BufferedReader br = new BufferedReader(new FileReader("sigs.md5"));
	    String str = br.readLine();
	    while (str != null) {
		log.debug("enter in while get signatures");
		log.debug("tam del split " + str.split("/").length);
		if (str.split("/").length == 2) {
		    exist = true;
		    log.debug(str + " is a file");
		    String[] tokens = str.split("  ./");
		    if (tokens.length != 2)
			throw new Exception(
				"The md5 file is not propertly parsed I'm waiting <md5> ./<filename>");
		    log.debug("Tokens : t" + tokens[1] + " p" + tokens[0]);
		    p.put(tokens[1], tokens[0]);
		}
		str = br.readLine();
	    }
	    if (!exist)
		throw new Exception(
			"The md5 file is not propertly parsed I'm waiting <md5> ./<filename>");
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return p;
    }

    /**
     * Changes directory to the one signaled by pathname
     * 
     * @param pathname
     *            the directory we want the FTP server to change
     */
    public String changeDirectory() throws Exception {
	cl.pwd();
	System.out.println(" el path actual es " + cl.getReplyString());
	System.out.println(" el path a cambiar es " + prot.getpath());
	    if (!cl.changeWorkingDirectory(prot.getpath()))
		throw new Exception("Unknown directory " + prot.getpath());
	    log.info("changed directory to " + cl.pwd());
	    log.info("server answer " + cl.getReplyString());
	return cl.printWorkingDirectory();
    }

    /**
     * This method try to logs in the FTP server signaled by the user
     * @param user the user name
     * @param passwd the password
     * @throws Exception if a problem is found
     */
    public void login() throws Exception {
	String user=prot.getlogin();
	String passwd=prot.getpassword();
	System.out.println("login " + user + " passwd " + passwd);
	try {
	    if (user.equals("anonymous")) {
		if (!cl.login(user, ""))
		    throw new Exception(
			    "Cannot possible to connect as anonymous");
	    }
	    else {
		if (!cl.login(user, passwd))
		    throw new Exception("Cannot be connected");
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    /**
     * This method connnects an instance of this class to the FTP server
     * @host the host we want to connect
     */
    public void connect() throws Exception {
	String host=null;
	try {
	    host = prot.getserver();
	    cl.connect(prot.getserver());
	    log.info("connect server answer " + cl.getReplyString());
	    log.info("connected to " + host);
	} catch (UnknownHostException e) {
	    throw new Exception("Unknown host " + host);
	} catch (SocketException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}