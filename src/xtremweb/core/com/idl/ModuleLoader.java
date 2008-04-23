package xtremweb.core.com.idl;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import xtremweb.core.log.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * <code>ModuleLoader</code> loads module (aka service).
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class ModuleLoader {

    private static Hashtable callbacks =  new Hashtable();;
    private static boolean handlersIntialised = false;
    public static final String rootServiceClassPath = "xtremweb.serv";
    public static final String rootComClassPath = "xtremweb.core.com.com";
    public static final int rmiBackPort = 4327;

    private static Logger log =  LoggerFactory.getLogger(ModuleLoader.class);

    private static Object createInstance( String className )  throws ModuleLoaderException {
	try {

	    ClassLoader cl = ModuleLoader.class.getClassLoader();
	    Class classClass = cl.loadClass( className);
	    Constructor classConstructor =
		classClass.getConstructor((java.lang.Class [])null);

	    return classClass.newInstance();
	} catch ( ClassNotFoundException cnfe ) {
	    log.warn("ModuleLoader : cannot find class in classpath");
	} catch ( IllegalAccessException iae) {
	    log.warn( "cannot create object from class " + className + " " + iae);
    	} catch ( NoSuchMethodException nsme) {
    	    log.warn( "cannot find a contructor to create an object  " + className + " " + nsme);

	} catch ( InstantiationException ie) {
	    log.warn( "cannot instantiate the  class " + className + " " + ie);
	}	    
	throw new ModuleLoaderException( "cannot create instance for class " + className);
    }

    private static void initHandlers(String media, int port) {
	if (media.toLowerCase().equals("rmi")) {
	    //initialise port
	    try {
		LocateRegistry.createRegistry(port);
		log.debug("RMI registry ready.");
		handlersIntialised = true;
	    } catch (Exception e) {
		log.fatal("Exception starting RMI registry:" + e);
	    }
	}
    }

    /**
     *  <code>addCallback</code>  adds callback, that is code which
     *  will called whenever a request is made to the handler.
     *
     * @param module a <code>String</code> value
     * @exception ModuleLoaderException if an error occurs
     */
    public static void addCallback( String module) throws ModuleLoaderException {
	// create an instance of the call back
	String callbackClassName = rootServiceClassPath + "." + module + ".Callback" + module;
	CallbackTemplate callb = (CallbackTemplate)  createInstance(callbackClassName);
	if ( callb!=null ) {	    	
	    callbacks.put( module, callb );
	    log.info("ModuleLoader has registred callback: [" + module+ "]" );
	} else {
	    log.info("ModuleLoader can't register callback: [" + module+ "]" );     
	} 
    }

    /**
     *  <code>addSubCallback</code> overides a module which a
     *  specified class 
     *
     * @param module a <code>String</code> value
     * @param subCallback a <code>CallbackTemplate</code> value
     * @exception ModuleLoaderException if an error occurs
     */
    public static void addSubCallback(String module, CallbackTemplate subCallback) throws ModuleLoaderException {
    	callbacks.put( module, subCallback );
    	log.info("ModuleLoader has registred SubCallback: " + subCallback +  " [" + module+ "]" );
    }
    
    /**
     *  <code>getModule</code> return the callback associated with the
     *  module 
     *
     * @param module a <code>String</code> value
     * @return a <code>CallbackTemplate</code> value
     * @exception ModuleLoaderException if an error occurs
     */
    public static CallbackTemplate getModule( String module ) throws  ModuleLoaderException {
	if ( ! callbacks.containsKey(module)) throw new ModuleLoaderException( "cannot find a callback  for module " + module);
	return (CallbackTemplate) callbacks.get( module );
    }

    /**
     *  <code>addHandler</code> add a handler to received request on
     *  port and media
     *
     * @param module a <code>String</code> value
     * @param media a <code>String</code> value
     * @param port an <code>int</code> value
     * @exception ModuleLoaderException if an error occurs
     */
    public static void addHandler (String module, String media, int port) throws ModuleLoaderException  {
	//get the callback codes for this client
	if ( ! callbacks.containsKey(module)) throw new ModuleLoaderException( "cannot find a callback  for module " + module);

	port = ComWorld.initPort(media,port);

	// First findout handler	
	//create a communication handler and register its callback
	if (media.toLowerCase().equals("rmi")) {
	    try {
		String handlerClassName =  "xtremweb.core.com.handler.HandlerRMI" + module;

 		HandlerRMITemplate handler= (HandlerRMITemplate)
	 	    createInstance( handlerClassName );
		//starts rmiregistry
		if (!handlersIntialised) {
		    initHandlers(media,port);
		}

		Naming.rebind("//" + "localhost" + ":" + port + "/" + module, handler);
		handler.registerCallback((CallbackTemplate) callbacks.get( module ));
		log.info("ModuleLoader has registred handler: [" + module+ "," + media+ "]");
		return;
	    } catch( ConnectException e) {
		log.info("cannot connect to " + media + " server " + " when installing module " +  module + " on port " + port + " " + e);
		throw new ModuleLoaderException ();
	    }    catch( Exception e) {
		log.info("cannot connect to " + media + " server " + " when installing module " +  module + " on port " + port + " " + e );
		throw new ModuleLoaderException (); 
	    }
	}
	throw new ModuleLoaderException (" Cannot find a " + media + "handler for module " + module)  ;
    }

    //TODO move this to a test unit
    public static void main ( String [] args) {
	int    rmiPort=4322;
	new ModuleLoader();
	try {
	    addCallback( "client" );
	} catch (ModuleLoaderException e) {
	    log.warn("ModuleLoader main(): " +  e);
	    System.exit( 1);
	}
	try {
	    addHandler( "client", "RMI", rmiPort );
	} catch (ModuleLoaderException e) {
	    log.warn("ModuleLoader main(): " +  e);
	    System.exit( 1);
	}
	
    }
}
