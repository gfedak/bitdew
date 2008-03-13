package xtremweb.core.com.idl;

/**
 * ComContexte.java
 *
 *
 * Created: Tue Apr  4 08:52:54 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */
public class ComContexte {

    /* Defaults ports */
    public static final int DEFAULT_RMI_PORT = 4322;
    public static final int DEFAULT_TCP_PORT = 4321;

    /* default entry point to the network */
    public static final String DEFAULT_ATTACH_HOST = "localhost";

    public static final String ATTACH_HOST_PROPERTY = "xtremweb.com.attachHost";

    protected String attachHost;

    public ComContexte() {
	attachHost = System.getProperty(ATTACH_HOST_PROPERTY, DEFAULT_ATTACH_HOST);
    } // ComContexte constructor
    
} // ComContexte
