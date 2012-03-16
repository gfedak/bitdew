package xtremweb.role.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.conf.ConfigurationProperties;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

import org.apache.commons.codec.binary.Hex;

/**
 * This class builds a HTTP response when a user attempts to download a song
 * 
 * @author josefrancisco
 * 
 */
public class DownloadSongServlet extends HttpServlet {


    private String BOOTSTRAP_NODE;
    /**
     * Logging
     */
    protected Logger log = LoggerFactory.getLogger("DownloadSongServlet");

    /**
     * Class constructor
     */
    public DownloadSongServlet() {
	Properties props;
	try {
	    props = ConfigurationProperties.getProperties();
	
	    String bootstrapnode = (String) props
		    .getProperty("xtremweb.core.http.bootstrapNode");
	    BOOTSTRAP_NODE = (bootstrapnode != null) ? bootstrapnode
		    : InetAddress.getLocalHost().getHostAddress();
	} catch (ConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * Given a song name and his md5 signature, stablish a connection with the
     * host who has this song and download it
     * 
     * @param request
     *            http request containing songname and md5 signature
     * @param response
     *            the song file
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	String directory = request.getParameter("directory");
	String ip = request.getParameter("ip");
	try {
	    if (request.getParameter("md5") != null
		    && request.getParameter("songname") != null) {
		String md5 = request.getParameter("md5");
		log.debug("md5 is " + md5 +  " ip is " + ip);
		String songname = request.getParameter("songname");
		// P2PClient is executed in a different java process because some problems were appearing 
		// when attempting to connect to the DLPT from a servlet contained in a web browser, on a node different 
		// than the bootstrap
		String[] cmdtolaunch = new String[]{"java","-cp","sbam_standalone.jar:bitdew-stand-alone-0.2.8.jar","xtremweb.role.integration.P2PClient","download",BOOTSTRAP_NODE,songname,md5,ip};
		log.debug("Cmd to launch is " + cmdtolaunch); 
		Process p = Runtime.getRuntime().exec(cmdtolaunch);
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader (new InputStreamReader(in));
		String line = reader.readLine();
		while(!line.equals("DONE"))
		{
		    log.debug("DownloadServlet subprocess " + line);
		    line = reader.readLine();
		}
	    }
	    String answer = "<table><tr><td>File name</td><td>MD5</td></tr>";
	    File dir = new File(directory);
	    if (dir.exists()) {
		File[] file = dir.listFiles();
		for (int i = 0; i < file.length; i++) {
		    String item = file[i].getName();
		    byte[] b = new byte[(int) file[i].length()];
		    answer += "<tr>";
		    if (!file[i].isDirectory()) {
			FileInputStream fis = new FileInputStream(file[i]);
			fis.read(b);
			MessageDigest md5dig;
			md5dig = MessageDigest.getInstance("MD5");
			byte[] bytes = md5dig.digest(b);
			String hexw = new String(Hex.encodeHex(bytes));
			answer += "<td>" + item + "</td><td>" + hexw + "</td>";
		    }
		    answer += "</tr>";
		}
		answer += "</table>";
		response.getWriter().println(answer);

		// Seed the file in the P2P network.
	    } else {
		response.getWriter()
			.println(
				"<p>That directory do not exist in your file system !!</p>");
	    }
	    response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_OK);
	}  catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	    response.getWriter().println("<p>"+e.getMessage()+"</p>");
	    response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

    }

    /**
     * Http POST method is not used for the moment
     */
    public void doPost() {
    }

}
