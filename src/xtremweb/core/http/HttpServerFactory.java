package xtremweb.core.http;

/**
 * Describe class HttpServerFactory here.
 *
 *
 * Created: Fri Aug 24 14:28:31 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class HttpServerFactory {

    public static HttpServer httpServer = null;;

    public static HttpServer getHttpServer() throws Exception {
	if (httpServer == null) {
	    httpServer = new HttpServer();
	}
	return httpServer;
    } 

}
