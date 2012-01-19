package xtremweb.serv.dt.bittorrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import xtremweb.serv.dt.bittorrent.exception.HttpToolsException;

/**
 * Helper class to handle .torrent file transfers through http
 * 
 * @author josefrancisco
 * 
 */
public class HttpTools {

    /**
     * Get a filename through http
     * 
     * @param filename
     *            the filename
     * @param url
     *            the url where the file is
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

    public static void postFileHttp(String fileName, String url)
	    throws HttpToolsException {
	try {
	    HttpClient httpclient = new DefaultHttpClient();

	    HttpPost httppost = new HttpPost(url);

	    FileBody bin = new FileBody(new File(fileName));
	    StringBody comment = new StringBody("Filename: " + fileName);

	    MultipartEntity reqEntity = new MultipartEntity();
	    reqEntity.addPart("bin", bin);
	    reqEntity.addPart("comment", comment);
	    httppost.setEntity(reqEntity);

	    HttpResponse response = httpclient.execute(httppost);
	    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		throw new HttpToolsException(
			"There was an error on the http response, Http response code : "
				+ response.getStatusLine().getStatusCode()
				+ " http reason "
				+ response.getStatusLine().getReasonPhrase());
	} catch (ClientProtocolException e) {
	    throw new HttpToolsException(
		    "There was an error executing the file POST request "
			    + e.getMessage());
	} catch (IOException e) {
	    throw new HttpToolsException(
		    "There was an error executing the file POST request "
			    + e.getMessage());
	}
    }
    
    public static String httpPost(String url) throws HttpToolsException
    {	HttpClient client = new DefaultHttpClient();
	HttpPost httppost = new HttpPost(url);
	HttpResponse response;
	try {
	    response = client.execute(httppost);
	    HttpEntity entity = response.getEntity();
	    return EntityUtils.toString(entity);
	} catch (ClientProtocolException e) {
	    throw new HttpToolsException("Client protocol exception " + e.getMessage());
	} catch (IOException e) {
	    throw new HttpToolsException("IO exception " + e.getMessage());
	}
	
    }

}
