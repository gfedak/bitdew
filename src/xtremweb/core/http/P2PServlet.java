package xtremweb.core.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

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
import xtremweb.role.examples.obj.SongBitdew;

/**
 * This class builds a HTTP response with the list of songs matching a specific term 
 * entered by the user
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
	 * Constructor
	 */
	public P2PServlet()
	{
		try {
			
			String LOCAL_ADDRESS = InetAddress.getLocalHost().getHostAddress();
			ddc = (InterfaceRMIdc)ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325, "dc");
			dr = (InterfaceRMIdr)ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325, "dr");
			dt = (InterfaceRMIdt)ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325, "dt");
			ds = (InterfaceRMIds)ComWorld.getComm(LOCAL_ADDRESS, "RMI", 4325, "ds");
			bd = new BitDew(ddc,dr,dt,ds);
		} catch (ModuleLoaderException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a list of songs matching a term given by the user
	 * @request HTTP request with a user-specified term
	 * @response The response the servlet will produce
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{	String HOST_NAME = InetAddress.getLocalHost().getHostName();
		String param = request.getParameter("term");
		log.debug("parameter term : " + param);
		response.setContentType("xml");		
		String responsexml = "<html><head></head><body><center><h3>Results found for : " + param + "</h3><table border=\"1\"><tr><td>Song Name</td><td>MD5</td></tr>";
		List l;
		try {
			l = bd.ddcSearch(param);
			//for each result write it on a html table
			for(int i = 0; i < l.size(); i++)
			{
				responsexml+="<tr>" +
						        "<td><a href=http://localhost:8080/download?songname="+((SongBitdew)l.get(i)).getFilename()+"&md5="+((SongBitdew)l.get(i)).getMd5()+">"+((SongBitdew)l.get(i)).getFilename()+"</td>"+
						        "<td><a href=http://localhost:8080/getips?md5="+((SongBitdew)l.get(i)).getMd5()+">"+((SongBitdew)l.get(i)).getMd5()+"</td>"+
						     "</tr>";
			}
			responsexml+="</table></center></body></html>";
			response.getWriter().println(responsexml);
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);	
			} catch (BitDewException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Http POST method is not used for the moment
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	}

}
