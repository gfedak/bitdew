package xtremweb.core.com.idl;

/**
 * ComWorld.java
 *
 *
 * Created: Thu Apr  6 15:25:16 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */
import xtremweb.core.log.*;
import xtremweb.core.conf.*;
import xtremweb.core.obj.ds.Host;
import xtremweb.core.db.*;
import java.lang.reflect.*;
import java.util.*;

public class ComWorld {
    
    public static Host host = null;
    
    public static Logger log = LoggerFactory.getLogger(ComWorld.class);

    private static final int RMI_DEFAULT_PORT = 4322;

    public ComWorld() {
	
    } // ComWorld constructor

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
		port = (Integer.valueOf(mainprop.getProperty("xtremweb.core.com.rmi.port", "" + RMI_DEFAULT_PORT))).intValue();
	    }
	}
	return port;
    }
  
    
    public static Host getHost() {
	if (host == null) {
	    host = new Host();
	    DBInterfaceFactory.getDBInterface().makePersistent(host);
	}
	return host;
    }

    public static Object getComm( String host, String layer, int port, String module) throws ModuleLoaderException {
	port = initPort(layer, port);
	String className ="";
	if (layer.toLowerCase().equals("local")) {
	    className = ModuleLoader.rootServiceClassPath + "." + module + ".Callback" + module;
	    return createInstance(className);
	} 
	if (layer.toLowerCase().equals("rmi")) {
	    className = ModuleLoader.rootComClassPath + ".CommRMI" + module;
	    try {
		Object comm = createInstance(className);
	    //	    try {
		Class c = comm.getClass();
		Class[] parameterTypes = new Class[] {String.class, int.class, String.class};
		Method method = c.getMethod("initComm",parameterTypes);
		Method[] methods = c.getMethods();	
		Object[] arguments = new Object[] {host, new Integer(port), module};
		
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

    public static Vector getMultipleComms (String host, String layer, int port,  String ... modules) throws ModuleLoaderException {
	Vector v = new Vector();
	for (String module : modules) {
	    v.add(getComm(host,layer, port, module));
	}
	return v;
    }


    protected static Object createInstance( String className )  throws ModuleLoaderException {
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

} // ComWorld
