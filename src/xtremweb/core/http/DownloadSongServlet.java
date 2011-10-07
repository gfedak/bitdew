package xtremweb.core.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.role.examples.P2PClient;

/**
 * This class builds a HTTP response when a user attempts to download a song
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
	public DownloadSongServlet(){
		try {
			String LOCAL_ADDRESS = InetAddress.getLocalHost().getHostAddress();
			client = new P2PClient(LOCAL_ADDRESS);		
		}  catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Given a song name and his md5 signature, stablish a connection with the machine having this song and download
	 * @param request http request containing songname and md5 signature
	 * @param response the song file
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String md5 = request.getParameter("md5");		
		log.debug("md5 is " + md5);
		ServletOutputStream op=response.getOutputStream();
		String songname = request.getParameter("songname");
		client.download(songname, md5);
		File file = new File(songname);
		response.setContentType( "application/octet-stream" );
        response.setContentLength( (int)file.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + songname+ "\"" );
        FileInputStream in = new FileInputStream(songname);
        int length;
        while ((in != null) && ((length = in.read()) != -1))
           op.write(length);
        in.close();
        op.flush();
        op.close();		
	}
	
	/**
	 * Http POST method is not used for the moment
	 */
	public void doPost(){
	}

}
