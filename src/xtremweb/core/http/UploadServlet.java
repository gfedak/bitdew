package xtremweb.core.http;


import xtremweb.core.log.*;
import xtremweb.core.conf.*;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
  
import org.mortbay.jetty.*;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.*;
import org.mortbay.jetty.handler.*;
import org.mortbay.util.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.disk.*;

import java.util.List;
import java.util.Iterator;
import java.io.File;

/**
 * <code>UploadServlet</code> is a servlet which allows to upload
 * data to the server.
 * It relies on the Jakarta FileUpload package
 *
 */
public class UploadServlet extends HttpServlet {
    
    private String _documentRoot; 
    
    /**
     * Creates a new <code>UploadServlet</code> instance.
     *
     * @param documentRoot a <code>String</code> value
     */
    public UploadServlet(String documentRoot) {
	//	_documentRoot = documentRoot;
    }
 
    
    /**
     * Creates a new <code>UploadServlet</code> instance.
     *
     * @param documentRoot a <code>String</code> value
     */
    public UploadServlet() {
	_documentRoot = ".";
    }
 
   
    /**
     *  <code>doGet</code> http Get method
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	System.out.println("Content Type ="+request.getContentType());
	response.setContentType("text/html");
	response.setStatus(HttpServletResponse.SC_OK);
	response.getWriter().println("<h1>File Upload Page</h1><FORM name=\"filesForm\" action=\"fileupload\"  method=\"post\" enctype=\"multipart/form-data\">  File:<input type=\"file\" name=\"file\"/><br/> <input type=\"submit\" name=\"Submit\" value=\"Upload Files\"/></FORM>");
    }
    
    /**
     *  <code>doPost</code> http post method
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	try {
	    System.out.println("Content Type ="+request.getContentType());
	    DiskFileItemFactory factory = new DiskFileItemFactory();
	    // Configure the factory here, if desired.
	    ServletFileUpload upload = new ServletFileUpload(factory);
	    // Configure the uploader here, if desired.
	    List fileItems = upload.parseRequest(request);
	    
	    Iterator itr = fileItems.iterator();
	    
	    while(itr.hasNext()) {
		FileItem fi = (FileItem)itr.next();
		
		if(!fi.isFormField()) {
		    System.out.println("\nFIELD NAME: "+fi.getFieldName());
		    System.out.println("\nNAME: "+fi.getName());
		    System.out.println("SIZE: "+fi.getSize());
		    File fNew= new File(_documentRoot, fi.getName());
		    //	File fNew= new File(fi.getName());
		    System.out.println(fNew.getAbsolutePath());
		    fi.write(fNew);
		}
		else {
		    System.out.println("Field ="+fi.getFieldName());
		}
	    }
	} catch (Exception e) {
	    System.out.println("Error" + e);
	}
	
	response.setContentType("text/html");
	response.setStatus(HttpServletResponse.SC_OK);
	response.getWriter().println("<h1>File Uploaded</h1>");
    }
    
}
