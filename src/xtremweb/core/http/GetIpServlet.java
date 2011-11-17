package xtremweb.core.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.InterfaceRMIdc;
import xtremweb.core.iface.InterfaceRMIdr;
import xtremweb.core.iface.InterfaceRMIds;
import xtremweb.core.iface.InterfaceRMIdt;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

public class GetIpServlet extends HttpServlet {

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
     * Class constructor
     */
    public GetIpServlet() {
	try {

	    String LOCAL_ADDRESS = InetAddress.getLocalHost().getHostAddress();
	    ddc = (InterfaceRMIdc) ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325,
		    "dc");
	    dr = (InterfaceRMIdr) ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325,
		    "dr");
	    dt = (InterfaceRMIdt) ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325,
		    "dt");
	    ds = (InterfaceRMIds) ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325,
		    "ds");
	    bd = new BitDew(ddc, dr, dt, ds);
	} catch (ModuleLoaderException e) {
	    log.warn("All bitdew services could not be loaded, if you want to use BitDew API make sure you launch them before " + e.getMessage());
	} catch (UnknownHostException e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Retrieves a http response with the IP's having a given md5 signature
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	String md5 = request.getParameter("md5");
	List ips;
	try {
	    ips = bd.ddcSearch(md5);
	    HashSet s= new HashSet();
	    s.addAll(ips);
	    String responsexml = "<html><head></head><body><center><h3>Results found for : "
		    + md5 + "</h3><table border=\"1\"><tr><td>Ip</td></tr>";
	   
	    for (Iterator iterator = s.iterator(); iterator.hasNext();) {
		String object = (String) iterator.next();
		responsexml += "<tr><td>" +object + "</td></tr>";
	    }
	 
	    responsexml += "</table></center></body></html>";
	    response.getWriter().println(responsexml);
	    response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_OK);
	} catch (BitDewException e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * For the moment this method do nothing
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
    }

}
