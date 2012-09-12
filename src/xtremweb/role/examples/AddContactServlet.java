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

/**
 * This class builds a HTTP response with the list of songs matching a specific
 * term entered by the user
 * 
 * @author josefrancisco
 * 
 */
public class AddContactServlet extends HttpServlet {

    /**
     * Logger
     */
    protected Logger log = LoggerFactory.getLogger("FindFriendServlet");

    private String BOOTSTRAP_NODE;

    /**
     * Constructor
     */
    public AddContactServlet() {
    }

    /**
     * Returns a list of songs matching a term given by the user
     * 
     * @request HTTP request with a user-specified term
     * @response The response the servlet will produce
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
try{	
		String uid = request.getParameter("uid");	
		String public_key = request.getParameter("public_key");
		String private_uid = request.getParameter("private_uid");
		System.out.println("the pub ois " + public_key);
		Contacts conts = (Contacts)AkratosUtil.unmarshall(Contacts.class,"contacts.xml");
		conts.addContact(new Contact (uid,private_uid,public_key) );
		AkratosUtil.marshall(Contacts.class,conts,"contacts.xml");
	}catch(Exception e){
		e.printStackTrace();
	}
    }
    
    
    /**
     * Http POST method is not used for the moment
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
    }

}
