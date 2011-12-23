package xtremweb.serv.dt.bittorrent;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import xtremweb.serv.dt.bittorrent.exception.HttpToolsException;

/**
 * Helper class to handle .torrent file transfers through http
 * @author josefrancisco
 *
 */
public class HttpTools {
    
    /**
     * Get a filename through http
     * @param filename the filename
     * @param url the url where the file is
     * @throws HttpToolsException
     */
    public static void getHttpFile(String filename, String url)
	    throws HttpToolsException {
	HttpClient httpclient = new DefaultHttpClient();
	HttpGet httpget = new HttpGet(url);
	HttpResponse response;
	try {
	    response = httpclient.execute(httpget);
	    HttpEntity entity = response.getEntity();
	    if (entity != null) {
		FileOutputStream os = new FileOutputStream(filename);
		entity.writeTo(os);
	    } else {
		throw new HttpToolsException(
			"There was an error on the http response, Http response code : "
				+ response.getStatusLine().getStatusCode()
				+ " http reason : "
				+ response.getStatusLine().getReasonPhrase());
	    }
	} catch (IOException e) {
	    throw new HttpToolsException(
		    "There was an error executing the GetHttp query to get the .torrent file : "
			    + e.getMessage());
	}

    }

    public static void postRequest(String url) throws HttpToolsException {

    }

}
