package xtremweb.serv.dt.bittorrent;

/**
 * Describe class AzureusConnector here.
 *
 *
 * Created: Fri Mar 30 16:35:41 2007
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import xtremweb.core.log.*;


public class AzureusConnector {

    //		private final String[] args;
    public static final String ACCESS_STRING = "Azureus Start Server Access";
    protected static  Logger log = LoggerFactory.getLogger(BittorrentConnector.class);

    protected static AzureusCore az = null;

    /**
     * Creates a new <code>AzureusConnector</code> instance.
     *
     */
    public AzureusConnector() throws BittorrentException {
	if (az == null) {
	    az = new AzureusCore();
	    az.startAzureusCore();
	}
    }

    /**
     * Attempt to send args via socket connection.
     * @return true if successful, false if connection attempt failed
     */
    public  boolean sendMsg(String [] args) throws BittorrentException {
    	Socket sck = null;
    	PrintWriter pw = null;
    	try {
	    String msg = "StartSocket: passing startup args to already-running Azureus java process listening on [127.0.0.1: 6880]";
	    
	    log.debug(msg);
	    //    		System.out.println( msg );       	
	    sck = new Socket("127.0.0.1", 6880);
         
	    // NOTE - this formatting is also used by AzureusCoreSingleInstanceClient and other org.gudy.azureus2.ui.common.Main.StartSocket
	    
	    pw = new PrintWriter(new OutputStreamWriter(sck.getOutputStream(),"UTF8"));
         
	    StringBuffer buffer = new StringBuffer(ACCESS_STRING + ";args;");
	    
	    for(int i = 0 ; i < args.length ; i++) {
		String arg = args[i].replaceAll("&","&&").replaceAll(";","&;");
		buffer.append(arg);
		buffer.append(';');
	    }
	    
	    pw.println(buffer.toString());
	    pw.flush();
	    
	    return true;
    	}
    	catch(Exception e) {
	    e.printStackTrace();
	    log.debug("Error Azureus Connector " + e);
	    return false;  //there was a problem connecting to the socket
    	}
    	finally {
	    try {
		if (pw != null)  pw.close();
	    }
	    catch (Exception e) {}
	    
	    try {
		if (sck != null) 	sck.close();
    		}
	    catch (Exception e) {}
    	}
    }
    

    /**
     * Attempt to send args via socket connection.
     * @return true if successful, false if connection attempt failed
     */
    public static boolean sendMsg(String arg) throws BittorrentException {
    	Socket sck = null;
    	PrintWriter pw = null;
    	try {
	    String msg = "StartSocket: passing startup args to already-running Azureus java process listening on [127.0.0.1: 57006]";
	    
	    log.debug(msg);
	    //    		System.out.println( msg );       	
	    sck = new Socket("127.0.0.1", 57006);
         
	    // NOTE - this formatting is also used by AzureusCoreSingleInstanceClient and other org.gudy.azureus2.ui.common.Main.StartSocket
	    
	    pw = new PrintWriter(new OutputStreamWriter(sck.getOutputStream(),"UTF8"));         
	    pw.println(arg);
	    pw.flush();
	    
	    return true;
    	}
    	catch(Exception e) {
	    e.printStackTrace();
	    log.debug("Error Azureus Connector " + e);
	    return false;  //there was a problem connecting to the socket
    	}
    	finally {
	    try {
		if (pw != null)  pw.close();
	    }
	    catch (Exception e) {}
	    
	    try {
		if (sck != null) 	sck.close();
    		}
	    catch (Exception e) {}
    	}
    }
    

    
    public static void main(String args[]) throws Exception {
	new AzureusConnector().sendMsg(args);
    }
}
