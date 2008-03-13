package xtremweb.serv.dt.bittorrent;

/**
 * BittorrentConnector.java
 *
 *
 * Created: Wed May 31 13:39:54 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */

import xtremweb.serv.dt.*;
import xtremweb.core.log.*;
import java.io.*;
import java.net.*;

public class BittorrentConnector {

    protected Socket socket; 
    protected BufferedReader inb;
    protected PrintStream outb;

    private String hostname = "localhost";
    private int port = 57006;

    protected static  Logger log = LoggerFactory.getLogger(BittorrentConnector.class);

    public BittorrentConnector() throws OOBException {
	
	try {
	    socket = new Socket(hostname, port);
	    inb = new BufferedReader(new InputStreamReader( socket.getInputStream() ));
	    outb = new PrintStream( socket.getOutputStream() );     
	} catch (UnknownHostException e) {
	    throw new OOBException("can't connect to the azureus client : unknown host");
	} catch (IOException ioe) {
	    throw new OOBException("can't connect to the azureus client");
	} // end of try-catch
	
	try {
	    String s;
	    int lc =0;
	    int BLOCK_SIZE = 65500;
	    char[] buf = new char[BLOCK_SIZE];
	    while(inb.ready()){
		int nread = inb.read(buf, 0, buf.length);
//		s = inb.readLine();

		//	s = new String(buf);
		System.out.println(lc++ + " "  +new String (buf));
		//		System.out.println(lc++ + " " + s);
	    }
	    
	    /*	    int BLOCK_SIZE = 65500;
	    byte[] buf = new byte[BLOCK_SIZE];
	    int nread;
	    int navailable;
	    InputStream in = socket.getInputStream();
	    while( (navailable = in.available()) > 0 &&
		(nread = in.read(buf, 0, Math.min(buf.length, navailable))) >= 0) {
	    //		if (out != null)
		    System.out.write(buf, 0, nread);
		    //		total += nread;
	    }
	    */
	    log.debug("YOYOYO");
	} catch (Exception e) {
	    
	} // end of try-catch
    } // BittorrentConnector constructor

    protected String response()  {
	String res="";
	try {
	    String s;
	    while(( s = inb.readLine())!=null) {
		System.out.println(s);
		res+=s;
	    }
	} catch (Exception e) {
	    System.out.println("cant get a responese");
	    //	    throw new MLDonkeyException(); 
	} // end of try-catch
	return res;
    }

    protected void logout() {
	sendmsg("logout");
    }

    protected void setIncomingDirectory( String dir) {
	sendmsg("set \"Completed Files Directory\" " + dir + " string" );
    }

   protected void sendmsg(String msg) {       
	try {
	    outb.println(msg);
	    outb.flush();
	    System.out.println("DEBUG : " +msg);
	    Thread.sleep(1000);
	    log.debug(response());	     
	} catch ( Exception e) {
	    System.out.println("Telnet Connection broken to mlnet");
	    //	    throw new MLDonkeyException(); 
	} // end of try-catch
	
    }

    public static void main(String [] args) {
	try {
	    BittorrentConnector btc = new BittorrentConnector();
	    Thread.sleep(1000);
	    System.out.println("DEBUG GILLE");
	    btc.sendmsg("show torrents");
	    btc.response();
	    btc.sendmsg("show torrents");
	    //	    btc.response();
	    btc.sendmsg("show torrents");
	    btc.sendmsg("show torrents");
	} catch (Exception e) {};
    }

} // BittorrentConnector
