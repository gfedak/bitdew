package xtremweb.serv.dt.http;

import xtremweb.core.log.*;
import xtremweb.core.uid.*;
import xtremweb.serv.dt.*;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.conf.*;
import xtremweb.core.http.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.commons.httpclient.params.*;

import java.io.*;
import java.util.Properties;

public class HttpTransfer extends BlockingOOBTransferImpl implements
	BlockingOOBTransfer, OOBTransfer {

    protected static HttpServer httpServer = null;
    protected HttpClient httpClient;
    protected GetMethod getMethod;
    protected PostMethod postMethod;
    protected static Logger log = LoggerFactory.getLogger(HttpTransfer.class);

    public static void init() {
	Properties mainprop;
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Http Protocol Information found : " + ce);
	    mainprop = new Properties();
	}

	try {
	    if (httpServer == null) {
		log.debug("starting HttpServer");
		httpServer = HttpServerFactory.getHttpServer();
	    }
	} catch (Exception e) {
	    log.warn("cannot start HttpServer : " + e);
	}
    }

    public HttpTransfer(Data d, Transfer t, Locator rl, Locator ll,
	    Protocol rp, Protocol lp) {
	super(d, t, rl, ll, rp, lp);
	transfer.setoob(this.getClass().toString());
    }

    public String toString() {
	return "http://" + remote_protocol.getserver() + ":"
		+ remote_protocol.getport() + "/" + remote_protocol.getpath()
		+ "/" + remote_locator.getref();
    }

    public void connect() throws OOBException {
	httpClient = new HttpClient();
	httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(
		5000);
	log.debug("connecting " + this.toString());
    }

    public void blockingSendSenderSide() throws OOBException {

	// FIXME TODO UGLY
	Properties mainprop;

	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Http Protocol Information found : " + ce);
	    mainprop = new Properties();
	}

	String uploadServlet = mainprop.getProperty(
		"xtremweb.serv.dt.http.uploadServlet", "/fileupload");
	// probleme de upload path et de download path et de uploadservlet
	String url = "http://" + remote_protocol.getserver() + ":"
		+ remote_protocol.getport() + "" + uploadServlet + "/";

	// L'URL qui va bien http://localhost:8080/fileupload/
	// http://localhost:8080//fileupload/ erreur File not found
	// http://localhost:8080//fileupload erreur Move Temporarly

	log.debug("Sending to " + url);
	postMethod = new PostMethod(url);
	try {
	    log.debug("localref is" + local_locator.getref());
	    File file = new File(local_locator.getref());
	    log.debug("sending " + file.getName() + " to " + url);

	    // Part[] parts = {new FilePart(file.getName(), file)};
	    Part[] parts = { new FilePart(remote_locator.getref(),
		    remote_locator.getref(), file) };

	    // prepare the file upload as a multipart POST request
	    postMethod.setRequestEntity(new MultipartRequestEntity(parts,
		    postMethod.getParams()));

	    // execute the transfer and get the result as a status
	    int status = httpClient.executeMethod(postMethod);

	    if (status == HttpStatus.SC_OK) {
		log.debug("Upload complete, response="
			+ postMethod.getResponseBodyAsString());
	    } else {
		log.debug("Upload failed, response="
			+ HttpStatus.getStatusText(status));
		throw new OOBException("Http errors when sending  " + "/"
			+ remote_locator.getref() + " "
			+ HttpStatus.getStatusText(status));
	    }
	} catch (Exception ex) {
	    log.debug("Error: " + ex);
	    throw new OOBException("Http errors when sending  " + "/"
		    + remote_locator.getref() + " " + ex);
	} finally {
	    postMethod.releaseConnection();
	}
    }

    public void blockingSendReceiverSide() throws OOBException {
    }

    public void blockingReceiveSenderSide() throws OOBException {
    }

    public void blockingReceiveReceiverSide() throws OOBException {
	// faut pas cherhcer
	// http://localhost:8080//data/25966a30-db9b-31db-9f4a-492d005f0c4a
	String url = "http://" + remote_protocol.getserver() + ":"
		+ remote_protocol.getport() + "/" + remote_protocol.getpath()
		+ "/" + remote_locator.getref();
	log.debug("getting " + url);
	getMethod = new GetMethod(url);

	// Provide custom retry handler is necessary
	getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		new DefaultHttpMethodRetryHandler(3, false));

	try {
	    // Execute the method.
	    int statusCode = httpClient.executeMethod(getMethod);

	    if (statusCode != HttpStatus.SC_OK) {
		log.debug("HttpClient getMethod failed: "
			+ getMethod.getStatusLine());
		throw new OOBException(
			"Http errors when setting retreive from " + url);
	    }

	    InputStream in = getMethod.getResponseBodyAsStream();
	    byte[] buff = new byte[1024];
	    int len;
	    FileOutputStream out = new FileOutputStream(new File(local_locator
		    .getref()));

	    while ((len = in.read(buff)) != -1) {
		// write byte to file
		out.write(buff, 0, len);
	    }

	} catch (HttpException e) {
	    log.debug("Fatal protocol violation: " + e);
	    throw new OOBException("Http errors when receiving receive " + "/"
		    + remote_locator.getref());
	} catch (IOException e) {
	    log.debug("Fatal transport error: " + e);
	    throw new OOBException("Http errors when receiving receive " + "/"
		    + remote_locator.getref());

	} finally {
	    log.debug("FIN du transfer");
	    getMethod.releaseConnection();
	}
    }

    public void disconnect() throws OOBException {

    }

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

	HttpTransfer http = new HttpTransfer(data, t, remote_locator,
		local_locator, remote_proto, local_proto);
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

	http = new HttpTransfer(data, t, remote_locator, local_locator,
		remote_proto, local_proto);

	try {
	    http.connect();
	    http.sendSenderSide();
	    http.disconnect();
	} catch (OOBException oobe) {
	    log.debug("" + oobe);
	}
	log.debug("upload completed");

    }

    public boolean poolTransfer() {
	return !isTransfering();
    }

} // HttpTransfer
