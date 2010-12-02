package xtremweb.core.com.idl;

import xtremweb.core.log.*;
import xtremweb.core.conf.*;
import xtremweb.core.obj.ds.Host;
import xtremweb.core.db.*;
import java.lang.reflect.*;
import java.util.*;

/**
 *  <code>ComWorld</code> allows to configure the distributed system.
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class ComWorld {
    
    private static Host host = null;
    
    private static Logger log = LoggerFactory.getLogger("ComWorld");

    private static final int RMI_DEFAULT_CLIENT_PORT = 4325;
    private static final int RMI_DEFAULT_SERVER_PORT = 4327;
    private static final int RMI_DEFAULT_REGISTRY_PORT = 4327;

    private static int rmiClientPort;
    private static int rmiServerPort;
    private static int rmiRegistryPort;


    static {
	try {
	    Properties prop = ConfigurationProperties.getProperties();
	    rmiClientPort =  (Integer.valueOf(prop.getProperty("xtremweb.core.com.rmi.client.port", "" + RMI_DEFAULT_CLIENT_PORT))).intValue();
	    rmiServerPort =  (Integer.valueOf(prop.getProperty("xtremweb.core.com.rmi.server.port", "" + RMI_DEFAULT_SERVER_PORT))).intValue();
	    rmiRegistryPort =  (Integer.valueOf(prop.getProperty("xtremweb.core.com.rmi.registry.port", "" + RMI_DEFAULT_REGISTRY_PORT))).intValue();
	} catch(Exception e) {
	    log.debug("cannot configure rmi ports");
	}
    }

    /**
     * Creates a new <code>ComWorld</code> instance.
     *
     */
    public ComWorld() {
	
    } // ComWorld constructor

    /**
     *  <code>initPort</code>. If port is equal to zero, will try to
     *  read it from the configuration file or will set it with
     *  default value.
     *
     * @param media a <code>String</code> value
     * @param port an <code>int</code> value
     * @return an <code>int</code> value
     */
    public static int initPort(String media, int port) {
	Properties mainprop;
	try {
	    mainprop = ConfigurationProperties.getProperties();
	} catch (ConfigurationException ce) {
	    log.warn("No Embedded HTTP Protocol Information found : " + ce); 
	    mainprop = new Properties();
	}

	if (port==0) {
	    if (media.toLowerCase().equals("rmi")) {
		return rmiClientPort;
	    }
	}
	return port;
    }
  
    
    /**
     *  <code>getHost</code> return the Host.
     *
     * @return a <code>Host</code> value
     */
    public static Host getHost() {
	if (host == null) {
	    host = new Host();
	    DBInterfaceFactory.getDBInterface().makePersistent(host);
	}
	return host;
    }

    /**
     *  <code>getComm</code> creates an interface to the service
     *  according to the host, the port and the communication media.
     *
     * @param host a <code>String</code> value
     * @param media a <code>String</code> value
     * @param port an <code>int</code> value
     * @param service a <code>String</code> value
     * @return an <code>Object</code> value
     * @exception ModuleLoaderException if an error occurs
     */
    public static Object getComm( String host, String media, int port, String service) throws ModuleLoaderException {
	port = initPort(media, port);
	String className ="";
	if (media.toLowerCase().equals("local")) {
	    className = ModuleLoader.rootServiceClassPath + "." + service + ".Callback" + service;
	    return createInstance(className);
	} 
	if (media.toLowerCase().equals("rmi")) {
	    className = ModuleLoader.rootComClassPath + ".CommRMI" + service;
	    try {
		Object comm = createInstance(className);
	    //	    try {
		Class c = comm.getClass();
		Class[] parameterTypes = new Class[] {String.class, int.class, String.class};
		Method method = c.getMethod("initComm",parameterTypes);
		Method[] methods = c.getMethods();	
		Object[] arguments = new Object[] {host, new Integer(port), service};
		
		method.invoke(comm, arguments);
		return comm;

	    } catch (NoSuchMethodException nsme ) {
		log.warn("cannot find method " + nsme );
		// } catch (Exception e) {
		//log.debug("cannot invoke method " + e );
	    } catch (InvocationTargetException ite) {
		log.debug("wrong invokation " + ite);
	    }  catch (IllegalArgumentException iae) {
		log.debug("illegal argument " + iae);
	    } catch (IllegalAccessException ilae) {
		log.debug("illegal access  " + ilae);
	    }
	} 
	throw new ModuleLoaderException ("Not able to instantiate  " + className);
    }

    /**
     *  <code>getMultipleComms</code>  creates a list of interfaces to
     *  the services according to the host, the port and the
     *  communication media. 
     *
     * @param host a <code>String</code> value
     * @param media a <code>String</code> value
     * @param port an <code>int</code> value
     * @return a <code>Vector</code> value
     * @exception ModuleLoaderException if an error occurs
     */
    public static Vector getMultipleComms (String host, String media, int port,  String ... services) throws ModuleLoaderException {
	Vector v = new Vector();
	for (String service : services) {
	    v.add(getComm(host,media, port, service));
	}
	return v;
    }

    private static Object createInstance( String className )  throws ModuleLoaderException {
	try {
	    Class classClass = (ComWorld.class.getClassLoader()).loadClass( className);
		
	    return classClass.newInstance();
	} catch ( ClassNotFoundException cnfe ) {
	    log.debug("ModuleLoader : cannot find class in classpath" + cnfe);
	} catch ( IllegalAccessException iae) {
	    log.debug( "cannot create object from class " + className + " " + iae);
	} catch ( InstantiationException ie) {
	    log.debug( "cannot instantiate the  class " + className  + " " + ie);
	}
	throw new ModuleLoaderException( "cannot instantiate the  class " + className);
    }

    public static int getRmiClientPort() {
	return ComWorld.rmiClientPort;
    }

    public static int getRmiServerPort() {
	return ComWorld.rmiServerPort;
    }

    public static int getRmiRegistryPort() {
	return ComWorld.rmiRegistryPort;
    }


} // ComWorld
