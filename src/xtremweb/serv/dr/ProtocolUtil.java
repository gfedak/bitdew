package xtremweb.serv.dr;

import java.net.URI;
import java.net.URISyntaxException;

import xtremweb.core.obj.dr.Protocol;

/**
 * Miscellaneus methods to handle protocols
 * @author josefrancisco
 *
 */
public class ProtocolUtil {
    
    /**
     * Output a protocol on the screen
     * @param proto the protocol to output
     * @return a string representing the protocol
     */
    public static String toString(Protocol proto) {
	String tmp =  "proto " + proto.getname() + " [" + proto.getuid() + "] = { ";
	if (proto.getname() .equals ("dummy"))
	    return tmp + " }";
	else
	    tmp += proto.getname()+ ":/";

	if (proto.getlogin() != null) { 
	    tmp += proto.getlogin() ;
	    if (proto.getpassword() != null) tmp+= "[" + proto.getpassword() + "]";
	    tmp += "@";
	}
	if (proto.getserver() != null) tmp+= proto.getserver();
	if (proto.getport() != -1) tmp+= ":" + proto.getport();
	if (proto.getpath() != null) tmp+= "/"+proto.getpath();
	return tmp + " }";
    }
    
    /**
     * Convert a string uri into the Bitdew protocol object
     * @param uri a java compliant URI
     * @return the bitdew protocol that it represents
     * @throws URISyntaxException if the URI syntax is wrong
     */
    public static Protocol getProtocol(String uri) throws URISyntaxException{
	Protocol p=new Protocol();
	URI myuri = new URI(uri);
	p.setname(myuri.getScheme());
	p.setserver(myuri.getHost());
	if (myuri.getScheme().equals("gsiftp"))
	    p.setpath("/tmp");
	else
	p.setpath(myuri.getPath());
	p.setport(myuri.getPort());
	return p;
    }

}
