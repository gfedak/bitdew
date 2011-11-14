package xtremweb.core.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



import javax.jdo.listener.DirtyLifecycleListener;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.role.examples.P2PClient;

import org.apache.commons.codec.binary.Hex;
/**
 * This class builds a HTTP response when a user attempts to download a song
 * 
 * @author josefrancisco
 * 
 */
public class DownloadSongServlet extends HttpServlet {

    /**
     * Client that encapsulate all BitDew logic
     */
    private P2PClient client;

    /**
     * Logging
     */
    protected Logger log = LoggerFactory.getLogger("DownloadSongServlet");

    /**
	 * 
	 */
    public DownloadSongServlet() {
	client = new P2PClient();
    }

    /**
     * Given a song name and his md5 signature, stablish a connection with the
     * machine having this song and download
     * 
     * @param request
     *            http request containing songname and md5 signature
     * @param response
     *            the song file
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	String directory = request.getParameter("directory");
	if(request.getParameter("md5") != null && request.getParameter("songname") != null)
	{
	    String md5 = request.getParameter("md5");
	    log.debug("md5 is " + md5);
	    String songname = request.getParameter("songname");
	    client.download(songname, md5);
	    client.republish(songname,md5);
	}
	String answer = "<table><tr><td>File name</td><td>MD5</td></tr>";
	 try {
	File dir = new File(directory);
	if(dir.exists())
	{ 
        	File[] file = dir.listFiles();
        	for(int i = 0; i < file.length ; i++ )
        	{   String item = file[i].getName();
        	    byte[] b = new byte [(int)file[i].length()];
        	    answer+="<tr>";
        	    if (!file[i].isDirectory()){
        		FileInputStream fis = new FileInputStream(file[i]) ;	    
        		fis.read(b);
        		MessageDigest md5dig;
        		md5dig = MessageDigest.getInstance("MD5");
        		byte[] bytes = md5dig.digest(b);
        		String hexw =  new String(Hex.encodeHex(bytes));
        		answer += "<td>"+item + "</td><td>" + hexw + "</td>";
        	    }
        	    answer+="</tr>";
        	}
        	answer+= "</textarea>";
        	response.getWriter().println(answer);
        	
	//Seed the file in the P2P network.
	}else{
	    response.getWriter().println("<p>That directory do not exist in your file system !!</p>");
	}
	response.setContentType("text/html");
	response.setStatus(HttpServletResponse.SC_OK);
	 } 
	catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }	
    }

    /**
     * Http POST method is not used for the moment
     */
    public void doPost() {
    }

}
