package xtremweb.serv.dt.http;
import java.net.URLEncoder;
import xtremweb.core.log.*;
import xtremweb.serv.dt.*;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.conf.*;
import xtremweb.serv.dt.bittorrent.HttpTools;

import java.io.*;
import java.util.Properties;

import org.apache.http.HttpException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HttpContext;
import org.apache.http.entity.*;
import org.apache.http.HttpResponse;




/**
 * This class performs a one-threaded http transfer on the transfer manager
 * 
 * @author jose
 * 
 */
public class HttpTransfer extends BlockingOOBTransferImpl implements BlockingOOBTransfer, OOBTransfer {

    /**
     * Request timeout
     */
    private final int HTTP_TIMEOUT = 20000;

    /**
     * Apache API http client
     */
    protected DefaultHttpClient httpClient;

    /**
     * Apache API getMethod
     */
    protected HttpGet getMethod;

    /**
     * Apache API postMethod
     */
    protected HttpPost postMethod;
    
    /**
     * Retry 
     */
    protected int retrytimes = 5;
    /**
     * Logger
     */
    protected static Logger log = LoggerFactory.getLogger(HttpTransfer.class);

    /**
     * Flag to indicate that is already connected
     */
    private boolean b = false;

    /**
     * Default constructor, needed on Factory Pattern
     */
    public HttpTransfer() {

    }

    /**
     * Parameter constructor
     * 
     * @param d
     *            data
     * @param t
     *            transfer
     * @param rl
     *            remote locator
     * @param ll
     *            local locator
     * @param rp
     *            remote protocol
     * @param lp
     *            local protocol
     */
    public HttpTransfer(Data d, Transfer t, Locator rl, Locator ll, Protocol rp, Protocol lp) {
	super(d, t, rl, ll, rp, lp);
	transfer.setoob(this.getClass().toString());
    }

    /**
     * Get a string representation for this transfer
     */
    public String toString() {
	return "http://" + remote_protocol.getserver() + ":" + remote_protocol.getport() + "/" + remote_protocol.getpath() + "/" + remote_locator.getref();
    }

    /**
     * Initialize apache http client
     */
    public void connect() throws OOBException {
	try{
	httpClient = new DefaultHttpClient();
	Properties mainprop = ConfigurationProperties.getProperties();
	String retrytimesstr = mainprop.getProperty("xtremweb.serv.dr.http.retry");
	if (retrytimesstr != null && !retrytimesstr.equals(""))
	{
	    retrytimes = Integer.parseInt(retrytimesstr);
	}
	HttpRequestRetryHandler myretry = new HttpRequestRetryHandler(){
	    public boolean retryRequest(IOException exception, int executionCount, HttpContext arg2) {
		log.debug("Retrying request " + executionCount);
		try{
		    Thread.sleep(10000);//THIS MUST BE ERASED AS SOON AS THE TIMEOUT PROPERTY THAT CORRESPONDS IS FOUND
		}catch(Exception e){
		    e.printStackTrace();
		}
		if (executionCount >= retrytimes) 
	            return false;	       
	        return true;
	    }
	};
	log.debug("set a retry handler ");

	httpClient.setHttpRequestRetryHandler(myretry);
	log.debug("connecting " + this.toString());
	b = true;
	}catch(Exception e){
	    e.printStackTrace();
	    throw new OOBException(e.getMessage());
	}
    }

    /**
     * Send a file to a http repository
     */
    public void blockingSendSenderSide() throws OOBException {
	// L'URL qui va bien http://localhost:8080/fileupload/
	// http://localhost:8080//fileupload/ erreur File not found
	// http://localhost:8080//fileupload erreur Move Temporarly
	Properties mainprop;
	HttpResponse response = null;
	try {
	    mainprop = ConfigurationProperties.getProperties();
	    String uploadServlet = mainprop.getProperty("xtremweb.serv.dt.http.uploadServlet", "/fileupload");
	    // probleme de upload path et de download path et de uploadservlet
	    String url = "http://" + remote_protocol.getserver() + ":" + remote_protocol.getport() + uploadServlet + "/";
	    log.debug("Sending to " + url);
	    postMethod = new HttpPost(url);

	    log.debug("localref is" + local_locator.getref());
	    File file = new File(local_locator.getref());
	    //file.renameTo(dest)
	    log.debug("sending " + file.getName() + " to " + url);

	    MultipartEntity entity = new MultipartEntity();

	    FileBody filebody = new FileBody(file);
	    StringBody stringbody = new StringBody(remote_locator.getref());
	    entity.addPart("bin", filebody);
	    entity.addPart("remote_locator",stringbody);
	    // prepare the file upload as a multipart POST request
	    postMethod.setEntity(entity);

	    // execute the transfer and get the result as a status
	    response = httpClient.execute(postMethod);
	    int status = response.getStatusLine().getStatusCode();
	    if (status == HttpStatus.SC_OK) {
		log.debug("Upload complete, response=" + response.getStatusLine().getReasonPhrase());
		
	    } else {
	    DataInputStream dis = new DataInputStream(response.getEntity().getContent());
	    String reason = dis.readLine();
		log.debug("Upload failed, response=" + reason);
		throw new OOBException("Http errors when sending  " + "/" + remote_locator.getref() + " " + reason);
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	    log.debug("Error: " + ex);
	    throw new OOBException("Http errors when sending  " + "/" + remote_locator.getref() + " " + ex);
	} finally {
		try{
			if(response != null && response.getEntity() != null)
				response.getEntity().consumeContent();
		}catch(IOException e){
			log.fatal("There was a problem releasing the resources");
			throw new OOBException("IOException " + e.getMessage());
		}
	}
    }
    
    /**
     * Nothing to do here for this case
     */
    public void blockingSendReceiverSide() throws OOBException {
    }
    
    /**
     * Nothing to do here for this case
     */
    public void blockingReceiveSenderSide() throws OOBException {
    }
    
    /**
     * Receive a file using http
     */
    public void blockingReceiveReceiverSide() throws OOBException {
	if (!b) {
	    httpClient = new DefaultHttpClient();
	    httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,HTTP_TIMEOUT);
	    log.debug("connecting " + this.toString());
	    b = true;
	}
	HttpResponse response = null;
	try {
	    String url = "http://" + remote_protocol.getserver() + ":" + remote_protocol.getport() + "/" + HttpTools.httpEncode(URLEncoder.encode(remote_protocol.getpath(),"UTF-8")) + "/" + HttpTools.httpEncode(URLEncoder.encode(remote_locator.getref(),"UTF-8"));
	  
        log.debug("getting " + url);
	    //url = URLEncoder.encode(url, "ISO-8859-1");
	    log.debug("The encoded url is " + url);
	    getMethod = new HttpGet(url);

	    // Execute the method.
	    response = httpClient.execute(getMethod);
	    int statusCode = response.getStatusLine().getStatusCode();
	    if (statusCode != HttpStatus.SC_OK) {
		log.debug("HttpClient getMethod failed: " + response.getStatusLine().getReasonPhrase());
		throw new OOBException("Http errors when setting retreive from " + url + "HttpClient getMethod failed: " + response.getStatusLine().getReasonPhrase());
	    }
	   
	    InputStream in = response.getEntity().getContent();
	    byte[] buff = new byte[1024];
	    int len;
	    FileOutputStream out = new FileOutputStream(new File(local_locator.getref()));

	    while ((len = in.read(buff)) != -1) {
		// write byte to file
		out.write(buff, 0, len);
	    }

	} catch (IOException e) {
	    log.fatal("Fatal transport error: " + e);
	    throw new OOBException("Http errors when receiving receive " + "/" + remote_locator.getref());

	} finally {
	    log.debug("FIN du transfer");
	    try{
	    	if(response != null && response.getEntity() != null){
	    		response.getEntity().consumeContent();
	    	}
	    }catch(IOException e){
	    	log.fatal("There was a problem releasing the resources");
	    	throw new OOBException("IO exception : " + e.getMessage());
	    }
	}
    }
    /**
     * Disconnect and give back resources
     */
    public void disconnect() throws OOBException {

    }
    
    /**
     * Main method, executes a test
     * @param args
     */
    public static void main(String[] args) {

	Data data = new Data();

	// Preparer le local
	Protocol local_proto = new Protocol();
	local_proto.setname("local");

	Locator local_locator = new Locator();
	local_locator.setdatauid(data.getuid());
	local_locator.setdrname("localhost");
	local_locator.setprotocoluid(local_proto.getuid());
	local_locator.setref("copy-test");

	// Preparer le proto pour l'acces remote
	Protocol remote_proto = new Protocol();
	remote_proto.setname("http");
	remote_proto.setpath("data");
	remote_proto.setport(8080);

	Locator remote_locator = new Locator();
	remote_locator.setdatauid(data.getuid());
	remote_locator.setdrname("localhost");
	remote_locator.setprotocoluid(remote_proto.getuid());
	remote_locator.setref("test-http");

	// prepare
	Transfer t = new Transfer();

	t.setlocatorremote(remote_locator.getuid());
	t.setlocatorlocal(local_locator.getuid());
	// Data data = DataUtil.fileToData(file);

	HttpTransfer http = new HttpTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);
	// log.debug(htto.toString());
	try {
	    http.connect();
	    http.receiveReceiverSide();
	    http.disconnect();
	} catch (OOBException oobe) {
	    log.debug(" " + oobe);
	}

	remote_locator.setref("copy_test-http");
	remote_proto.setpath("fileupload");

	http = new HttpTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);

	try {
	    http.connect();
	    http.sendSenderSide();
	    http.disconnect();
	} catch (OOBException oobe) {
	    log.debug("" + oobe);
	}
	log.debug("upload completed");

    }
    
    /**
     * For this case return its istransfering status
     */
    public boolean poolTransfer() {
	return !isTransfering();
    }

} // HttpTransfer
