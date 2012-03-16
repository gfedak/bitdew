package xtremweb.role.examples;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This http servlet answers a list of files present on a directory on the local machine
 * @author jose
 *
 */
public class GetFilesServlet extends HttpServlet {
    
    /**
     * Get a file (not directories) list of files contained in a directory.
     * Request parameters : directory: absolute path to directory.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException {
	try {
	    String directory = request.getParameter("directory");
	    String answer = ServletHelper.getDirectoryFiles(directory);
	    response.getWriter().println(answer);
	    response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_OK);
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	    response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	} catch (IOException e) {
	    e.printStackTrace();
	    response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
    }
    
    /**
     * No Post for now
     */
    public void doPost() {

    }

}
