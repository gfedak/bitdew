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
import org.jfree.util.Log;

import sun.net.ftp.FtpClient;
import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
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

public class FTPAvailable {
    
    private FTPClient cl;
    
    private String host;
    private String passwd;
    private String login;
    private String pathname;
    private int port;
    private boolean md5support;
    
    private BitDew bd;
    private int commsport = 4325;
    private InterfaceRMIdc dc;
    private InterfaceRMIdr dr;
    private InterfaceRMIds ds;
    
    private Logger log = LoggerFactory.getLogger("FTPAvailable");
    
    public FTPAvailable(String host,int port,String login,String passwd) {
	try {
	    this.host = host;
	    this.passwd = passwd;
	    this.login = login;
	    this.port = port;
	    Vector comms = ComWorld.getMultipleComms("localhost", "rmi", commsport, "dr", "dt", "ds");
	    bd = new BitDew(comms);
	    cl = new FTPClient();
	} catch (ModuleLoaderException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
	int p = new Integer(args[1]).intValue();
	FTPAvailable ftpa = new FTPAvailable(args[0],p,args[2],args[3]);
	ftpa.connect();
	ftpa.makeAvailable();
	ftpa.disconnect();
    }
    
    public void disconnect()
    {
	try {
	    cl.disconnect();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    public Locator prepareLocalLocator(Data data)
    {	File f = new File("local");
	Protocol prot;
	Locator local_locator=null;
	try {
	    prot = dr.getProtocolByName("ftp");
	    local_locator = new Locator();
	    //local_locator.setref(f.getAbsolutePath());
	    local_locator.setdatauid(data.getuid());
	    local_locator.setprotocoluid(prot.getuid());
	} catch (RemoteException e) {
	    
	    e.printStackTrace();
	}
	//local_locator.setdrname();
	return local_locator;
    }
    
    public Locator prepareRemoteLocator(Data data)
    {
	File f = new File("local");
	Protocol prot;
	Locator remote_locator=null;
	try {
	    prot = dr.getProtocolByName("ftp");
	    remote_locator = new Locator();
	    prot.setpath(f.getPath());
	    remote_locator.setdatauid(data.getuid());
	    remote_locator.setprotocoluid(prot.getuid());
	    remote_locator.setdrname(((CommRMITemplate) dr).getHostName());
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
	return remote_locator;
    }
    
    public void makeAvailable()
    {	
    	Properties md5 = null;
	try {
	    FTPFile[] files = cl.listFiles();
	    for(int i =0 ; i < files.length;i++)
	    {	String name = files[i].getName();
		if(md5support)
		    md5 = getSignatures(pathname);
	    	long size = files[i].getSize();
		Data data = bd.createData(name);
		data.setoob("FTP");
		Locator local_locator = prepareLocalLocator(data);
		Locator remote_locator = prepareRemoteLocator(data);
		bd.putLocator(local_locator);
		bd.putLocator(remote_locator);
		log.info("File " + name + " successfully available, uid="+ data.getuid());
	    }
	    log.info("To retrive any of these files please use get <datauid> <file_name>");
	    
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (BitDewException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
    }
    
    private CharSequence fromFile(String filename) throws IOException 
    { 
	FileInputStream fis = new FileInputStream(filename); 
	FileChannel fc = fis.getChannel(); // Create a read-only CharBuffer on the file 
	ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int)fc.size()); 
	CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf); 
	return cbuf; 
    }
    
    private Properties getSignatures(String pathname) {
	Properties p= new Properties();
	FileOutputStream fos;
	try {
	    fos = new FileOutputStream(new File("sigs.md5"));
	    fos.close();
	    CharSequence cs = fromFile("sigs.md5");
	    boolean result=cl.retrieveFile("SIGNATURES.md5",fos);
	    //original \w*\s{2}\.\/[A-Z]{1}.*
	    Pattern ptt= Pattern.compile("\\w*\\s{2}\\.\\/[A-Z]{1}.*");
	    Matcher matcher =ptt.matcher(cs);
	    while(matcher.find())
	    {
		String[] tokens = matcher.group().split(" ");
		p.put(tokens[0], tokens[1]);
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
    
    public void connect()
    {	
	try {
	    cl.connect(host, port);
	    log.debug("connect server answer " +cl.getReplyString());
	    log.info("connected to " + host);
	    cl.changeWorkingDirectory(pathname);
	    log.debug("change dir answer " + cl.getReplyString());
	    log.info("directory changed to " + host);
	} catch (SocketException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
