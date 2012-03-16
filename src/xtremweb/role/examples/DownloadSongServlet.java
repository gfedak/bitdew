package xtremweb.role.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.conf.ConfigurationProperties;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

/**
 * This class builds a HTTP response when a user attempts to download a song
 * 
 * @author josefrancisco
 * 
 */
public class DownloadSongServlet extends HttpServlet {

    /**
     * Independent node that starts DHT
     */
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
	    String bootstrapnode = (String) props.getProperty("xtremweb.core.http.bootstrapNode");
	    BOOTSTRAP_NODE = (bootstrapnode != null) ? bootstrapnode : InetAddress.getLocalHost().getHostAddress();
	} catch (ConfigurationException e) {
	    e.printStackTrace();
	} catch (UnknownHostException e) {
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String directory = request.getParameter("directory");
	String ip = request.getParameter("ip");
	try {
	    if (request.getParameter("md5") != null && request.getParameter("songname") != null) {
		String md5 = request.getParameter("md5");
		log.debug("md5 is " + md5 + " ip is " + ip);
		String songname = request.getParameter("songname");
		// P2PClient is executed in a different java process
		String[] cmdtolaunch = new String[] { "java", "-cp", "sbam_standalone.jar:bitdew-stand-alone-0.2.8.jar", "xtremweb.role.examples.P2PClient",
			"download", BOOTSTRAP_NODE, songname, md5, ip };
		Process p = Runtime.getRuntime().exec(cmdtolaunch);
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		while (!line.equals("DONE")||line.contains("ERROR")) {
		    log.debug("DownloadServlet subprocess " + line);
		    line = reader.readLine();
		}if(line.contains("ERROR")){
		    throw new Exception(line);
		}
	    }
	    String answer = ServletHelper.getDirectoryFiles(directory);
	    response.getWriter().println(answer);
	    response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_OK);
	} catch (Exception e) {
	    e.printStackTrace();
	    response.getWriter().println("<p>" + e.getMessage() + "</p>");
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
