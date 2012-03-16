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
import xtremweb.core.iface.InterfaceRMIdc;
import xtremweb.core.iface.InterfaceRMIdr;
import xtremweb.core.iface.InterfaceRMIds;
import xtremweb.core.iface.InterfaceRMIdt;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;

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

    private String BOOTSTRAP_NODE;

    /**
     * Constructor
     */
    public P2PServlet() {

    }

    /**
     * Returns a list of songs matching a term given by the user
     * 
     * @request HTTP request with a user-specified term
     * @response The response the servlet will produce
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String param = request.getParameter("term");
	log.debug("parameter term : " + param);
	log.info("This servlet P2PServlet has been called");
	response.setContentType("xml");
	String responsexml = "<table border=\"1\"><tr><td>Download</td><td>Song Name</td><td>MD5</td><td>Owner(s)</td></tr>";
	List l;
	try {

	    Properties props = ConfigurationProperties.getProperties();
	    String bootstrapnode = (String) props.getProperty("xtremweb.core.http.bootstrapNode");
	    log.debug("bootstrap node value is " + BOOTSTRAP_NODE);
	    BOOTSTRAP_NODE = (bootstrapnode != null) ? bootstrapnode : InetAddress.getLocalHost().getHostAddress();
	    String LOCAL_ADDRESS = InetAddress.getLocalHost().getHostAddress();
	    ddc = (InterfaceRMIdc) ComWorld.getComm(BOOTSTRAP_NODE, "RMI", 4325, "dc");
	    dr = (InterfaceRMIdr) ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325, "dr");
	    dt = (InterfaceRMIdt) ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325, "dt");
	    ds = (InterfaceRMIds) ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325, "ds");
	    bd = new BitDew(ddc, dr, dt, ds, true);
	    log.debug("enter in p2pservlet get " + bd);
	    l = bd.ddcSearch(param);

	    log.debug(" size of l is " + l.size());
	    // for each result write it on a html table
	    for (int i = 0; i < l.size(); i++) {
		String md5 = ((Data) l.get(i)).getchecksum();
		responsexml += "<tr>" + "<td><input type=\"checkbox\" name=\"checkbox" + i + "\"/></td>" + "<td>" + ((Data) l.get(i)).getname() + "</td>"
			+ "<td>" + md5 + "</td>" + "<td>" + getIps(md5) + "</td>" + "</tr>";
	    }
	    responsexml += "</table>";
	    response.getWriter().println(responsexml);
	    response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_OK);
	} catch (BitDewException e) {
	    e.printStackTrace();
	    response.getWriter().println(
		    "<table border=\"1\">" + "<tr><td>Download</td><td>Song Name</td><td>MD5</td><td>Owner(s)</td></tr>"
			    + "<tr><td>There was a problem executing your request " + e.getMessage() + "</td>" + "<td></td><td></td><td></td></tr>"
			    + "</table>");
	    response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	} catch (ModuleLoaderException e) {
	    log.warn("All bitdew services could not be loaded, if you want to use BitDew API make sure you launch them before " + e.getMessage());
	    e.printStackTrace();
	} catch (ConfigurationException e) {
	    log.info("there was an exception !! " + e.getMessage());
	    e.printStackTrace();
	}
    }
    
    /**
     * Get the list of ips the host a file with checksum equals to parametrized md5.
     * @param md5
     * @return comma-separated ip list
     * @throws BitDewException
     */
    private String getIps(String md5) throws BitDewException {
	String responsexml = "";
	List ips;
	ips = bd.ddcSearch(md5);
	HashSet s = new HashSet();
	s.addAll(ips);
	for (Iterator iterator = s.iterator(); iterator.hasNext();) {
	    String object = (String) iterator.next();
	    responsexml += object + ", ";
	}
	return responsexml;
    }

    /**
     * Http POST method is not used for the moment
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}
