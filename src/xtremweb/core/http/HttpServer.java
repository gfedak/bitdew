package xtremweb.core.http;

import xtremweb.core.log.*;
import xtremweb.core.conf.*;
import java.util.*;
import org.mortbay.jetty.*;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.*;
import org.mortbay.jetty.handler.*;
import xtremweb.role.examples.*;

/**
 * <code>HttpServer</code> is an embedded HTTP Server. It allows to
 * download file and to upload file with a dedicated Servlet
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

public class HttpServer {

    /**
     * <code>DEFAULT_PORT</code> on which the Http server listen to
     *
     */
    public static int DEFAULT_PORT = 8080;

    /**
     * <code>DEFAULT_DOCUMENT_ROOT</code> is the path to where data are stored
     *
     */

    public static String DEFAULT_DOCUMENT_ROOT = ".";

    /**
     * <code>DEFAULT_DOCUMENT_PATH</code>  is the path as seen by the
     * Http client
     *
     */
    public static String DEFAULT_DOCUMENT_PATH = "/";

    /**
     *  <code>DEFAULT_UPLOAD_SERVLET</code> acces to the Uploader servlet
     *
     */
    public static String DEFAULT_UPLOAD_SERVLET = "/fileupload";
    
    /**
     * p2p servlet path
     */
    public static String DEFAULT_P2P_SERVLET = "/p2pquery";
    
    /**
     * Log
     */
    protected static Logger log = LoggerFactory.getLogger(HttpServer.class);
    
    /**
     * The port where the HttpServer will run
     */
    private static int _port; //port to contact the server
    
    /**
     * local path where files are stored 
     */
    private static String _documentRoot;
    
    /**
     * Path as seen by remote when getting data
     */
    private static String _documentPath;
    
    /**
     * remote reference used to store data
     */
    private static String _uploadServlet;
    
    /**
     * Jetty Context
     */
    private static Context uiContext;
    
    /**
     * Jetty Server
     */
    private static Server server;

    /**
     * Creates a new <code>HttpServer</code> instance.
     *
     * @exception Exception if an error occurs
     */
    public HttpServer() throws Exception {
	Properties mainprop;
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Embedded HTTP Protocol Information found : " + ce); 
	    mainprop = new Properties();
	}
	//FIXME move the configuration of this to xtremweb.serv.dt.http and move the initialisation
	//of the upload applet to a function so that it can be called from 
	_port = (Integer.valueOf(mainprop.getProperty("xtremweb.core.http.port", "" + DEFAULT_PORT))).intValue();
	_documentPath = mainprop.getProperty("xtremweb.core.http.path", DEFAULT_DOCUMENT_PATH);
	_documentRoot = mainprop.getProperty("xtremweb.core.http.documentRoot", DEFAULT_DOCUMENT_ROOT);
	_uploadServlet = mainprop.getProperty("xtremweb.core.http.uploadServlet", DEFAULT_UPLOAD_SERVLET);
	init();
    }


    /**
     * Creates a new <code>HttpServer</code> instance.
     *
     * @param port an <code>int</code> value
     * @param documentRoot a <code>String</code> value
     * @param documentPath a <code>String</code> value
     * @param uploadServlet a <code>String</code> value
     * @exception Exception if an error occurs
     */
    public HttpServer(int port, String documentRoot, String documentPath, String uploadServlet) throws Exception {
	//set up the correct variable
	_port = port;
	_documentRoot = documentRoot;
	_documentPath = documentPath;
	_uploadServlet = uploadServlet;
	
	init();
    }

    /**
     *  <code>init</code> performs the following action :
     * <ul>
     * <li>Starts a Jetty web server on the specifyed port</li>
     * <li>Create a context for file download</li>
     * <li>Create a context for file upload</li>
     * <li>Create a Servlet for file upload and associate this to file
     * upload context</li>
     * </ul>
     *
     * @exception Exception if an error occurs
     */
    public void init() throws Exception {
	//We first start the Server configured with a special port
    	
	server = new Server();
	//
	ResourceHandler resource = new ResourceHandler();
	
	String bundle = getClass().getResource("/xtremweb/core/http/html").toExternalForm();
	resource.setResourceBase(bundle);

	Connector connector=new SocketConnector();
	connector.setPort(_port);
	server.setConnectors(new Connector[]{connector});
        
	//we defines 2 contexts : one for the servlet and one for serving files
	log.debug("port:"+_port);
	log.debug("documentRoot:"+_documentRoot);
	log.debug("documentPath:"+_documentPath);
	log.debug("uploadServlet:"+_uploadServlet);
	log.debug("Searching for resources in " + bundle);
	
	//the servlet is accessed with the /fileupload reference
	Context fileuploadContext = new Context(Context.SESSIONS);
	fileuploadContext.setContextPath(_uploadServlet);
	fileuploadContext.addServlet(new ServletHolder(new UploadServlet(_documentRoot)), "/*");
	
	Context p2pContext = new Context(Context.SESSIONS);
	p2pContext.setContextPath("/p2pquery");
	p2pContext.addServlet(new ServletHolder(new P2PServlet()),"/*");
	
	Context downloadcontext = new Context(Context.SESSIONS);
	downloadcontext.setContextPath("/download");
	downloadcontext.addServlet(new ServletHolder(new DownloadSongServlet()),"/*");
	
	//regular documents are served with the regular resource handler
	ResourceHandler resource_handler=new ResourceHandler();
	resource_handler.setResourceBase(_documentRoot);

	ContextHandler filedownloadContext = new ContextHandler();
	filedownloadContext.setContextPath(_documentPath);
	filedownloadContext.setHandler(resource_handler);

	ContextHandlerCollection contexts = new ContextHandlerCollection();
	contexts.setHandlers(new Handler[]{fileuploadContext,filedownloadContext,p2pContext,downloadcontext});
        
	uiContext = new Context(contexts, "/ui", Context.SESSIONS);
        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{resource,contexts,new DefaultHandler()});

	//we associate the contexts with the server and start the server
	server.setHandler(handlers);
	server.start();
	log.info("Http Server started serving files on " + _documentRoot + " with http://localhost:" + _port + "/" + _documentPath +" and uploading files with the servlet http://localhost:" + _port + "/" + _uploadServlet );
	//	server.join();
    }


    /**
     *  <code>getPort</code> return ports
     *
     * @return an <code>int</code> value
     */
    public int getPort() {
	return _port;
    }

    /**
     *  <code>getDocumentRoot</code> return Document Root Path
     *
     * @return a <code>String</code> value
     */
    public String getDocumentRoot() {
	return _documentRoot;
    }

    /**
     * <code>getDocumentPath</code> return remote document path
     *
     * @return a <code>String</code> value
     */
    public String getDocumentPath() {
	return _documentPath;
    }

    /**
     * <code>getUploadServlet</code> return upload servlet path
     *
     * @return a <code>String</code> value
     */
    public String getUploadServlet() {
	return _uploadServlet;
    }

    /**
     * Add a servlet
     * @param servletClassName
     * @param servletMapping
     */
    public void addUiServlet(String servletClassName, String servletMapping) {
	uiContext.addServlet(servletClassName, servletMapping);
    }
    
    /**
     * Start the server
     * @throws Exception
     */
    public void start() throws Exception {
	server.start();
    }
}
