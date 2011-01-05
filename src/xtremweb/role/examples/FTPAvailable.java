package xtremweb.role.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamException;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.api.transman.TransferManager;
import xtremweb.api.transman.TransferManagerException;
import xtremweb.api.transman.TransferManagerFactory;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.CommRMITemplate;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.InterfaceRMIdc;
import xtremweb.core.iface.InterfaceRMIdr;
import xtremweb.core.iface.InterfaceRMIds;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dr.Protocol;

/**
 * This class makes available a remote FTP server folder in a bitdew supported
 * data grid.
 * 
 * @author jsaray
 * 
 */
public class FTPAvailable {


    private Vector<String> duids;
    /**
     * Apache FTPClient object
     */
    private FTPClient cl;

    /**
     * Host of the remote ftp server
     */
    private String host;

    /**
     * Password of the ftp account (if necessary)
     */
    private String passwd;

    /**
     * Login of the ftp account (if neccessary)
     */
    private String login;

    /**
     * Pathname of the folder we want to make available
     */
    private String pathname;

    /**
     * Port number of the ftp connection (default 21)
     */
    private int port;

    /**
     * if this flag is true there must be a .md5 file with md5 signatures list.
     */
    private boolean md5support = true;

    /**
     * BitDew API to make available the FTP folder
     */
    private BitDew bd;

    /**
     * BitDew server default port
     */
    private int commsport = 4325;

    /**
     * Data Catalog RMI interface
     */
    private InterfaceRMIdc dc;

    /**
     * Data repository RMI interface
     */
    private InterfaceRMIdr dr;

    /**
     * Data scheduler RMI interface
     */
    private InterfaceRMIds ds;

    /**
     * Log4J loggger
     */
    private Logger log = LoggerFactory.getLogger("FTPAvailable");

    private Vector comms;

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
    public FTPAvailable(String host, int port, String login, String passwd,
	    String pathname) {
	try {
	    duids = new Vector();
	    this.pathname = pathname;
	    this.host = host;
	    this.passwd = passwd;
	    this.login = login;
	    this.port = port;
	    dr = (InterfaceRMIdr) ComWorld.getComm("localhost", "rmi",
		    commsport, "dr");
	    comms = ComWorld.getMultipleComms("localhost", "rmi", commsport,
		    "dc", "dr", "dt", "ds");
	    bd = new BitDew(comms);
	    cl = new FTPClient();
	    //tf = new TransferManager(comms);
	    //tf.start();

	} catch (ModuleLoaderException e) {
	    log.debug("Exception in FTPAvailable constructor");
	    e.printStackTrace();
	}

    }

    

   

    /**
     * Program usage string
     */
    public void usage() {
	log.info("Usage : <host> <port>  <directory> [-l <login>][-ps <password>]");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	// int p = new Integer(args[1]).intValue();
	FTPAvailable ftpa = new FTPAvailable("ftp.lip6.fr", 21, "anonymous",
		null, "/pub/linux/distributions/slackware/slackware-current");
	// "/pub/linux/distributions/slackware/slackware-current");
	ftpa.connect();
	ftpa.changeDirectory("/pub/linux/distributions/slackware/slackware-current");
	ftpa.makeAvailable();

	ftpa.getFiles();
    }

    public void getFiles() {
	log.debug("enter into getFiles");
	try {
	    // retreive the data object

	    for (int i = 0; i < 1; i++) {
		log.debug("enter into for of getFiles");
		String s = getData(i);
		File file = new File("result" + i + ".txt");
		Data data;
		data = getBitDewApi().searchDataByUid(s);
		log.debug("Data captured just as it is uid" + data.getuid() + " md5: " + data.getchecksum() + " size:" + data.getsize());
		getBitDewApi().get(data, file);
		TransferManagerFactory.getTransferManager().waitFor(data);
		TransferManagerFactory.getTransferManager().stop();
		// log.info("data has been successfully copied to " + fileName);
	    }
	} catch (BitDewException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (TransferManagerException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} 
    }

    /**
     * Disconnects this class instance from the FTP server
     */
    public void disconnect() {
	try {
	    cl.disconnect();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public Locator prepareLocalLocator(Data data) {
	Protocol prot;
	Locator local_locator = null;
	try {
	    prot = dr.getProtocolByName("ftp");
	    local_locator = new Locator();
	    // local_locator.setref(f.getAbsolutePath());
	    duids.add(data.getuid());
	    local_locator.setdatauid(data.getuid());
	    local_locator.setprotocoluid(prot.getuid());
	} catch (RemoteException e) {

	    e.printStackTrace();
	}
	// local_locator.setdrname();
	return local_locator;
    }

    public String getData(int pos) {
	return duids.get(pos);
    }

    public Vector getDuids() {
	return duids;
    }

    public BitDew getBitDewApi() {
	return bd;
    }

    public Locator prepareRemoteLocator(Data data) {
	File f = new File("local");
	Protocol prot;
	Locator remote_locator = null;
	try {
	    prot = dr.getProtocolByName("ftp");
	    remote_locator = new Locator();
	    duids.add(data.getuid());
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
     * This method creates the locators necessaries so bitdew can recognize the
     * data in a remote ftp folder on the data grid.
     */
    public void makeAvailable() {
	Properties md5 = null;
	try {
	    cl.enterLocalPassiveMode();
	    FTPFile[] files = cl.listFiles();
	    log.debug(getReplyString());
	    log.debug("Number of files " + files.length);
	    if (md5support)
		    md5 = getSignatures(pathname);
	    for (int i = 0; i < 5; i++) {
		String name = files[i].getName();
		
		if(name.equals("CHECKSUMS.md5")||name.equals("CHECKSUMS.md5.asc"))
		    continue;
		
		log.debug("Name of file " + name);		
		long size = files[i].getSize();
		Data data = bd.createData(name);
		data.setoob("FTP");
		data.setsize(files[i].getSize());
		data.setchecksum(md5.getProperty(name));
		Locator remote_locator = prepareRemoteLocator(data);
		// bd.putLocator(local_locator);
		log.debug("data to put md5:" + data.getchecksum() + " uid: "+data.getuid()+" size: " + data.getsize() + " name " + data.getname());
		bd.putData(data);
		bd.put(data, remote_locator);
		
		log.info("File " + name + " successfully available, uid="
			+ data.getuid());

	    }
	    log.info("To retrive any of these files please use get <datauid> <file_name>");

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (BitDewException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    log.debug("weird exception");
	    e.printStackTrace();
	}

    }

    private CharSequence fromFile(String filename) throws IOException {
	FileInputStream fis = new FileInputStream(filename);
	FileChannel fc = fis.getChannel(); // Create a read-only CharBuffer on
					   // the file
	ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0,
		(int) fc.size());
	CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf);
	return cbuf;
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
    public Properties getSignatures(String pathname) {
	Properties p = new Properties();
	FileOutputStream fos;

	try {
	    /*
	     * log.debug("entered into getSignatures, number of files " + cl.
	     * listFiles(). length); /*for(String x : cl.listNames())
	     * log.debug("Name : " + x);
	     */
	    fos = new FileOutputStream(new File("sigs.md5"));
	    // fos.close();

	    boolean result = retrieveFile("CHECKSUMS.md5", fos);

	    if (!result)
		throw new FileNotFoundException();
	    // original \w*\s{2}\.\/[A-Z]{1}.*
	    Pattern ptt = Pattern.compile("\\w*\\s{2}\\.\\/[A-Z]{1}.*");
	    Matcher matcher = ptt.matcher(fromFile("sigs.md5"));
	    while (matcher.find()) {
		log.debug("entered to while, expression recognized");
		String[] tokens = matcher.group().split("  ./");
		log.debug("Tokens : t" + tokens[1] + " p" + tokens[0]);
		// if(tokens[1].)
		p.put(tokens[1], tokens[0]);
	    }
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return p;
    }

    public boolean retrieveFile(String s, FileOutputStream fos) {
	boolean b = false;
	try {
	    cl.enterLocalPassiveMode();
	    b = cl.retrieveFile(s, fos);
	    log.debug("retrieve file " + cl.getReplyString());
	} catch (CopyStreamException ex) {
	    log.debug("CopyStreamException caused by " + ex.getIOException());
	} catch (IOException e) {
	    // TODO Auto-generated catch block
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
    public void changeDirectory(String pathname) {
	try {
	    if (getLogin().equals("anonymous"))
		cl.login("anonymous", "");
	    else
		cl.login(login, passwd);
	    cl.changeWorkingDirectory(pathname);
	    log.debug("changed directory to " + cl.pwd());
	    log.debug("change dir answer " + cl.getReplyString());
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public String getLogin() {
	return login;
    }

    /**
     * This method connnects an instance of this class to the FTP server
     * specified during object construction
     */
    public void connect() {
	try {
	    cl.connect(host);
	    log.debug("connect server answer " + cl.getReplyString());
	    log.info("connected to " + host);
	} catch (SocketException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * Gets the FTP server answer to a before issued commmand
     * 
     * @return a string with server's answer
     */
    public String getReplyString() {
	return cl.getReplyString();
    }

    /**
     * Gets the FTP reply code to a before issued command.
     * 
     * @return an integer representing ftp reply code
     */
    public int getReplyCode() {
	return cl.getReplyCode();
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
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

}
