package xtremweb.role.examples;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.conf.ConfigurationProperties;
import xtremweb.core.iface.Interfacedc;
import xtremweb.core.iface.Interfacedr;
import xtremweb.core.iface.Interfaceds;
import xtremweb.core.iface.Interfacedt;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.role.examples.akratos.util.AkratosUtil;
import xtremweb.role.examples.akratos.pojo.*;
import xtremweb.role.examples.akratos.Akratos;

/**
 * This class builds a HTTP response with the list of songs matching a specific
 * term entered by the user
 * 
 * @author josefrancisco
 * 
 */
public class FindFriendServlet extends HttpServlet {

    

    /**
     * Bitdew API
     */
    private Akratos akratos;

    /**
     * Logger
     */
    protected Logger log = LoggerFactory.getLogger("FindFriendServlet");

    private String BOOTSTRAP_NODE;

    /**
     * Constructor
     */
    public FindFriendServlet() {
	akratos = new Akratos( ) ;
    }

    /**
     * Returns a list of songs matching a term given by the user
     * 
     * @request HTTP request with a user-specified term
     * @response The response the servlet will produce
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	String tagtosearch = request.getParameter("tag");
	String nametosearch = request.getParameter("name");
	
	response.setContentType("xml");
	String responsexml = "<table border=\"1\"><tr><td>Name</td><td>Uid</td><td>Tag</td><td>City</td><td>Job</td><td>Public Key</td></tr>";
	List l;
	try {

	    Properties props = ConfigurationProperties.getProperties();
	    String bootstrapnode = (String) props.getProperty("xtremweb.core.http.bootstrapNode");
	    log.debug("bootstrap node value is " + bootstrapnode);
	    BOOTSTRAP_NODE = (bootstrapnode != null) ? bootstrapnode : InetAddress.getLocalHost().getHostAddress();
	    String LOCAL_ADDRESS = InetAddress.getLocalHost().getHostAddress();
	   
	    l = akratos.findFriend(tagtosearch,nametosearch);

	    log.debug(" size of l is " + l.size());
	    // for each result write it on a html table
	    for (int i = 0; i < l.size(); i++) {
		User user = ((User) l.get(i));
		String name = user.getPublicname();
		String uid = user.getUid();
		String tag = user.getTag();
		String city = user.getCity();
		String job = user.getProfession();
		String public_key = user.getPublickey();
		String privateuid = user.getPrivateuid();
		responsexml += "<td><input type=\"checkbox\" name=\"checkbox" + i + "\"/></td><td>" + name + "</td>" + "<td width=\"300\">" + uid + "</td><td>" + tag + "</td>" + "<td>" + city + "</td>" + "<td>" + job + "</td><td>" + privateuid + "</td><td width=\"3000\">" + public_key + "</td></tr>";
	    }
	    responsexml += "</table>";
	    response.getWriter().println(responsexml);
	    response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_OK);
	} catch (ConfigurationException e) {
	    log.info("there was an exception !! " + e.getMessage());
	    e.printStackTrace();
	}
    }
    
    
   

}
