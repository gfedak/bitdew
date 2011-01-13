package xtremweb.role.examples;

//TODO there is a problem when FTPAvailable and PUtGet are launched in the same thread; the
//transfer doesnt finish and havs the type senderreceiver
//TODO mirar las repercusiones en el repository
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

    /*
     * ! \example FTPAvailable.java This example starts bitdew services
     * (dr,dc,dt) and makes available files on a remote ftp folder in bitdew.
     * Then you can use "PutGet get" option to request new available files.
     * 
     * <ol> <li> Modify the file xtremweb.properties located at conf directory
     * adding data concerning the ftp server:
     * 
     * @code xtremweb.serv.dr.protocols=dummy http <B>ftp</B>
     * xtremweb.serv.dr.ftp.name=ftp xtremweb.serv.dr.ftp.server=ftp.lip6.fr
     * xtremweb.serv.dr.ftp.port=21 xtremweb.serv.dr.ftp.login=anonymous
     * xtremweb
     * .serv.dr.ftp.path=/pub/linux/distributions/slackware/slackware-current
     * xtremweb.serv.dr.ftp.passwd=none
     * 
     * @endcode <li> Make sure that in the folder you want to make available
     * exist the file CHECKSUMS.md5. The software needs this file in order to
     * successfully complete a transfer. The file must have this syntax :
     * 
     * @code 928143c5d1a7825bc5f3c1d18fde1e31 ./ANNOUNCE.13_1
     * c08333e6569a6fd66fd59999bfa94eb5 ./BOOTING.TXT
     * 19494cd00c011661a882c23772eaa7f0 ./CHANGES_AND_HINTS.TXT
     * 18810669f13b87348459e611d31ab760 ./COPYING
     * 
     * @endcode
     * 
     * <li> Execute the following command </li>
     * 
     * @code java -cp bitdew-stand-alone.jar xtremweb.role.examples.FTPAvailable
     * -s -h ftp.lip6.fr -d /pub/linux/distributions/slackware/slackware-current
     * 
     * @endcode
     * 
     * <li> This command will : <ol> <li>launch Bitdew services needed for this
     * experience (dc,dt,dr) <li>connect to FTP server ftp.lip6.fr <li>make
     * available files under the
     * /pub/linux/distributions/slackware/slackware-current directory </ol> <li>
     * At the end of the command execution you should get something like this :
     * 
     * @code File ANNOUNCE.13_1 successfully available,
     * uid=6390d3a0-1cbc-31e0-ab7e-c0c64755d3e6 File BOOTING.TXT successfully
     * available, uid=639edd60-1cbc-31e0-ab7e-c0c64755d3e6 File
     * CHANGES_AND_HINTS.TXT successfully available,
     * uid=63a45ba0-1cbc-31e0-ab7e-c0c64755d3e6 To retrieve any of these files
     * in your system inside a file <file_name> please use PutGet get
     * <file_name> <uid>
     * 
     * @endcode <li> You can execute PutGet get command in order to retrieve a
     * file from the ftp server, for example :
     * 
     * @code java -cp xtremweb.role.examples.PutGet get LOCAL_BOOTING.TXT
     * 639edd60-1cbc-31e0-ab7e-c0c64755d3e6
     * 
     * @endcode <li> This command will download the file of uid
     * 639edd60-1cbc-31e0-ab7e-c0c64755d3e6 (remotely BOOTING.TXT) to the local
     * file LOCAL_BOOTING.TXT, you should get the following message:
     * 
     * @code [ INFO] data has been successfully copied to LOCAL_BOOTING.TXT
     * 
     * @endcode
     * 
     * <li> Source code of the ftp available example
     * 
     * </ol>
     */
    // Testing purposes
    
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

    /**
     * Log4J loggger
     */
    private Logger log = LoggerFactory.getLogger("FTPAvailable");
    
    public FTPAvailable(){
	
	
    String[] serverargs = { "serv", "dc", "dr", "dt" };
    new CommandLineTool(serverargs);
    Vector comms;
    try {
	comms = ComWorld.getMultipleComms("localhost", "rmi", 4325,
	    "dc", "dr", "dt");
	 dr = (InterfaceRMIdr) comms.get(1);
	    bd = new BitDew(comms);
	    cl = new FTPClient();
    } catch (ModuleLoaderException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
    }
   
	
    }
    /**
     * FTPAvailable constructor
     * 
     * @param host
     *            the host address we want to connect
     * @param port
     *            the port used to ftp
     * @param login
     *            your login, (or anonymous if FTP server is not using
     *            authentication)
     * @param passwd
     *            your password
     * @param pathname
     *            folders pathname
     */
    public FTPAvailable(boolean b,String host, int port, String login, String passwd,
	    String pathname) {
	try {
	    if(b)
		log.setLevel("debug");
	    String[] serverargs = { "serv", "dc", "dr", "dt" };
	    new CommandLineTool(serverargs);
	    Vector comms = ComWorld.getMultipleComms("localhost", "rmi", 4325,
		    "dc", "dr", "dt");
	    dr = (InterfaceRMIdr) comms.get(1);
	    bd = new BitDew(comms);
	    cl = new FTPClient();
	    connect(host);
	    login(login,passwd);
	    changeDirectory(pathname);
	    makeAvailable(pathname);
	} catch (ModuleLoaderException e) {
	    log.debug("Exception in FTPAvailable constructor");
	    e.printStackTrace();
	} catch (Exception e) {
	    System.err
	    .println("There was a problem on program execution : Reason "
		    + e.getMessage());
	    e.printStackTrace();
	}
    }
    
    /**
     * This method prits the way of using FTPAvailable command
     */
    private static void printUsage() {
	System.err
		.println("Usage : FTPAvailable [-v][-h <host>] [-p <port>] [-d <dir> -u <login> -k <password>]\n"
			+ "Options:\n"
			+ "-s signature aware, if this option is signaled, a SIGNATURES.md5 file must exists on the directory signaled by -d option\n"
			+ "-h ftp host (default to ftp.lip6.fr)\n"
			+ "-p connection port (default to 21)\n"
			+ "-d directory to make available (default to /pub/linux/distributions/slackware/slackware-current)\n"
			+ "-l ftp session username (if empty the program tries to connect as anonymous user\n"
			+ "-k ftp session password");
    }
    /**
     * Program main method
     * 
     * @param args
     */

    public static void main(String[] args) {

	try {
	    CmdLineParser parser = new CmdLineParser();
	    CmdLineParser.Option verbose = parser.addBooleanOption('v',
		    "verbose");

	    CmdLineParser.Option host = parser.addStringOption('h', "host");
	    CmdLineParser.Option port = parser.addIntegerOption('p', "port");
	    CmdLineParser.Option dir = parser.addStringOption('d', "dir");
	    CmdLineParser.Option login = parser
		    .addStringOption('u', "username");
	    CmdLineParser.Option passwd = parser.addStringOption('k',
		    "password");
	    parser.parse(args);
	    Boolean verb = (Boolean) parser.getOptionValue(verbose,
		    Boolean.FALSE);

	    String hostValue = (String) parser.getOptionValue(host,
		    "ftp.lip6.fr");
	    Integer portValue = (Integer) parser.getOptionValue(port,
		    new Integer(21));
	    String dirValue = (String) parser.getOptionValue(dir,
		    "/pub/linux/distributions/slackware/slackware-current");
	    String loginValue = (String) parser.getOptionValue(login,
		    "anonymous");
	    String pswValue = (String) parser.getOptionValue(passwd, null);

	    FTPAvailable ftpa = new FTPAvailable(verb.booleanValue(),hostValue, portValue,
		    loginValue, pswValue, dirValue);

	    
	} catch (CmdLineParser.OptionException e) {
	    System.err.println(e.getMessage());
	    printUsage();
	    System.exit(2);
	}
    }

    /**
     * This method creates the locators necessaries so bitdew can recognize the
     * data in a remote ftp folder on the data grid.
     */
    public Vector makeAvailable(String pathname) {
	Properties md5 = null;
	Vector v = new Vector();
	try {
	    log.info("Making available folder ... ");
	    log.debug("connection type " + cl.getDataConnectionMode());

	    cl.enterLocalPassiveMode();
	    FTPFile[] files = cl.listFiles();
	    log.debug(cl.getReplyString());
	    log.debug("Number of files " + files.length);

	    log.info("calculating md5 signatures from CHECKSUMS.md5");
	    md5 = getSignatures();

	    for (int i = 0; i < files.length; i++) {
		String name = files[i].getName();
		if (name.equals("CHECKSUMS.md5")
			|| name.equals("CHECKSUMS.md5.asc")
			|| files[i].isDirectory())
		    continue;

		log.debug("Name of file " + name);
		Data data = bd.createData(name);
		data.setoob("FTP");
		data.setsize(files[i].getSize());
		data.setchecksum(md5.getProperty(name));
		Locator remote_locator = prepareRemoteLocator(pathname,data);
		log.debug("data to put md5:" + data.getchecksum() + " uid: "
			+ data.getuid() + " size: " + data.getsize() + " name "
			+ data.getname());
		bd.putData(data);
		bd.put(data, remote_locator);

		log.info("File " + name + " successfully available, uid="
			+ data.getuid());
		v.add(data.getuid());
	    }
	    log.info("To retrieve any of these files in your system on a file <file_name> please use PutGet get <file_name> <uid>");

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
     * Gets bitdew api in order to start and cancel transfers
     * @return
     */
    public BitDew getBitDewApi() {
	return bd;
    }
    
    /**
     * Given a data, this method prepares the ftp locator
     * @param data
     * @return
     */
    public Locator prepareRemoteLocator(String pathname,Data data) {
	File f = new File("local");
	Protocol prot;
	Locator remote_locator = null;
	try {
	    prot = dr.getProtocolByName("ftp");
	    remote_locator = new Locator();
	    prot.setpath(f.getPath());
	    remote_locator.setdatauid(data.getuid());
	    remote_locator.setprotocoluid(prot.getuid());
	    remote_locator.setdrname(((CommRMITemplate) dr).getHostName());
	    remote_locator.setref(pathname + "/" + data.getname());
	    remote_locator.setpublish(true);
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
	return remote_locator;
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
	    boolean result = retrieveFile("CHECKSUMS.md5", fos);
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
     * This method is used to download SIGNATURES.md5 file
     * @param s
     * @param fos
     * @return
     */
    public boolean retrieveFile(String s, FileOutputStream fos) {
	boolean b = false;
	try {
	    cl.enterLocalPassiveMode();
	    b = cl.retrieveFile(s, fos);
	    log.debug("retrieve file " + cl.getReplyString());
	} catch (CopyStreamException ex) {
	    log.debug("CopyStreamException caused by " + ex.getIOException());
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return b;
    }

    /**
     * Changes directory to the one signaled by pathname
     * 
     * @param pathname
     *            the directory we want the FTP server to change
     */
    public void changeDirectory(String pathname) throws Exception {
	try {

	    if (!cl.changeWorkingDirectory(pathname))
		throw new Exception("Unknown directory " + pathname);
	    log.info("changed directory to " + cl.pwd());
	    log.info("server answer " + cl.getReplyString());
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }
    
    /**
     * login method
     * @throws Exception
     */
    public void login(String user,String passwd) throws Exception {
	try {
	    if (user.equals("anonymous")) {
		if (!cl.login("anonymous", ""))
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
     * specified during object construction
     */
    public void connect(String host) throws Exception {
	try {
	    cl.connect(host);
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
    /**
     * Returns the current directory from which we are retrieving files.
     * 
     * @return
     */
    public String getCurrentDirectory() {
	try {
	    log.debug("current working directory " + cl.printWorkingDirectory());
	    return cl.printWorkingDirectory();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }
    /*
     * public static void main(String[] args) { FTPAvailable ftp = new
     * FTPAvailable("perso.ens-lyon.fr", 21, "jsaray", "mejvac07", "/testing");
     * ftp.setDebugMode(); try { ftp.connect(); ftp.login();
     * ftp.changeDirectory(); ftp.makeAvailable(); PutGet pg = null;
     * 
     * pg = new PutGet("localhost", 4325);
     * 
     * 
     * 
     * for (int i = 0; i < 1; i++) { String s = ftp.getData(i);
     * System.out.println("Trying  " + s); pg.get("newfile" + i + ".txt", s);
     * 
     * } System.out.println("termino"); } catch (BitDewException e) {
     * 
     * e.printStackTrace();
     * 
     * } catch (TransferManagerException e) {
     * 
     * e.printStackTrace();
     * 
     * } catch (Exception e) {
     * 
     * e.printStackTrace();
     * 
     * } }
     * 
     * /** Program usage string
     */
}