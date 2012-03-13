package xtremweb.role.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import xtremweb.core.iface.InterfaceRMIdc;
import xtremweb.core.iface.InterfaceRMIdr;
import xtremweb.core.iface.InterfaceRMIds;
import xtremweb.core.iface.InterfaceRMIdt;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

/**
 * This class builds a HTTP response with the list of songs matching a specific
 * term entered by the user
 * 
 * @author josefrancisco
 * 
 */
public class P2PServlet extends HttpServlet {

    /**
     * Data catalog
     */
    private InterfaceRMIdc ddc;

    /**
     * Data repository
     */
    private InterfaceRMIdr dr;

    /**
     * Data scheduler
     */
    private InterfaceRMIds ds;

    /**
     * Data Transfer
     */
    private InterfaceRMIdt dt;

    /**
     * Bitdew API
     */
    private BitDew bd;

    /**
     * Logger
     */
    protected Logger log = LoggerFactory.getLogger("P2PServlet");
    
    /**
     * The node that starts DHT
     */
    private String BOOTSTRAP_NODE;

    /**
     * Constructor
     */
    public P2PServlet() {
	try {
	    Properties props = ConfigurationProperties.getProperties();
	    String bootstrapnode = (String) props
		    .getProperty("xtremweb.core.http.bootstrapNode");
	    BOOTSTRAP_NODE = (bootstrapnode != null) ? bootstrapnode
		    : InetAddress.getLocalHost().getHostAddress();
	    String LOCAL_ADDRESS = InetAddress.getLocalHost().getHostAddress();
	    ddc = (InterfaceRMIdc) ComWorld.getComm(BOOTSTRAP_NODE, "RMI",
		    4325, "dc");
	   
	    dt = (InterfaceRMIdt) ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325,
		    "dt");
	    ds = (InterfaceRMIds) ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325,
		    "ds");
	    bd = new BitDew(ddc, dr, dt, ds);

	} catch (ModuleLoaderException e) {
	    log.warn("All bitdew services could not be loaded, if you want to use BitDew API make sure you launch them before "
		    + e.getMessage());
	} catch (UnknownHostException e) {
	    log.info("There was an exception !! " + e.getMessage());
	} catch (ConfigurationException e) {
	    log.info("there was an exception !! " + e.getMessage());
	}
    }

    /**
     * Returns a list of songs matching a term given by the user
     * 
     * @request HTTP request with a user-specified term
     * @response The response the servlet will produce
     */
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	String param = request.getParameter("term");
	log.debug("parameter term : " + param);
	log.debug("bootstrap node value is " + BOOTSTRAP_NODE);
	response.setContentType("xml");
	String responsexml = "<table border=\"1\"><tr><td>Download</td><td>Song Name</td><td>MD5</td><td>Owner(s)</td></tr>";
	List l;
	Process p = Runtime.getRuntime().exec("java -cp sbam_standalone.jar:bitdew-stand-alone-0.2.8.jar xtremweb.role.integration.P2PClient search "+ BOOTSTRAP_NODE +" "+ param);
	int i = 0;
	InputStream in = p.getInputStream();
	String line;
	BufferedReader reader = new BufferedReader (new InputStreamReader(in));
	log.debug("enter in while");
	line = reader.readLine();
	log.debug("pass, line is " + line);
	while (!line.equals("END")) {
	    log.debug("value of line " + line);
	    if(line.contains("Result")){
		System.out.println ("Stdout: " + line);
		String name = line.split(" ")[1];
		String md5 = line.split(" ")[2];
		String ips = line.split(" ")[3];
		responsexml += "<tr>"+ "<td><input type=\"checkbox\" name=\"checkbox" + i+ "\"/></td>" + "<td>" + name
			+ "</td>" + "<td>" + md5 + "</td>" + "<td>"
				+ ips + "</td>" + "</tr>";
	    
	    i++;
	    }
	    line= reader.readLine();
	}
	responsexml += "</table>";
	response.getWriter().println(responsexml);
	response.setContentType("text/html");
	response.setStatus(HttpServletResponse.SC_OK);
    }
    /**
     * Http POST method is not used for the moment
     */
    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {

    }

}
