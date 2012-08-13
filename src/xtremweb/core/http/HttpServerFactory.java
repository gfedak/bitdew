package xtremweb.core.http;

/**
 * This class creates a http server using the factory pattern
 * Created: Fri Aug 24 14:28:31 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class HttpServerFactory {
    
    /**
     * http server interface
     */
    public static HttpServer httpServer = null;;
    
    /**
     * Main method to retrieve a http server
     * @return
     * @throws Exception
     */
    public static HttpServer getHttpServer() throws Exception {
	if (httpServer == null) {
	    httpServer = new HttpServer();
	}
	return httpServer;
    } 

}
