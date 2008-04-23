package xtremweb.core.com.idl;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import xtremweb.core.log.*;
import java.util.*;
import java.lang.reflect.*;

public class ModuleLoader {

    private static Hashtable callbacks =  new Hashtable();;
    private static boolean handlersIntialised = false;
    public static final String rootServiceClassPath = "xtremweb.serv";
    public static final String rootComClassPath = "xtremweb.core.com.com";
    public static final int rmiBackPort = 4327;

    private static Logger log =  LoggerFactory.getLogger(ModuleLoader.class);

    private static Object createInstance( String className )  throws ModuleLoaderException {
	try {

	    //	    Class classClass = classLoader.loadClass( className, true);
	    ClassLoader cl = ModuleLoader.class.getClassLoader();
	    Class classClass = cl.loadClass( className);
	    Constructor classConstructor =
		classClass.getConstructor((java.lang.Class [])null);
	    //	    for ( int i=0; i<classConstructors.length; i++) {
	    //	log.InfoBlue("constructor " + classConstructors[i].getName());		
	    return classClass.newInstance();
	} catch ( ClassNotFoundException cnfe ) {
	    log.warn("ModuleLoader : cannot find class in classpath");
	} catch ( IllegalAccessException iae) {
	    log.warn( "cannot create object from class " + className + " " + iae);
    	} catch ( NoSuchMethodException nsme) {
    	    log.warn( "cannot find a contructor to create an object  " + className + " " + nsme);
//  	} catch ( InvocationTargetException ite) {
//  	    throw new ModuleLoaderException( "cannot call the constructor of the class  " + className, ite);
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
	

		//	throw new ModuleLoaderException( "cannot find a callback definition file for module " + module);
    }

    public static void addSubCallback(String module, CallbackTemplate subCallback) throws ModuleLoaderException {
    	callbacks.put( module, subCallback );
    	log.info("ModuleLoader has registred SubCallback: " + subCallback +  " [" + module+ "]" );
    }
    
    public static CallbackTemplate getModule( String module ) throws  ModuleLoaderException {
	if ( ! callbacks.containsKey(module)) throw new ModuleLoaderException( "cannot find a callback  for module " + module);
	return (CallbackTemplate) callbacks.get( module );
    }

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

		//		Registry registry = LocateRegistry.createRegistry(port);
		//registry.rebind(module, handler);
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
	/*	if ( (media=="TCP") || (media=="FTCP")) {
 	    TCPHandlerTemplate handler = (TCPHandlerTemplate)
	    createInstance("xtremweb."+ module+ ".TCPHandler" +
	    module);
	    
	    handler.registerCallback((CallbackTemplate) callbacks.get( module ));
	*/
	    /* if we run an handler on a host which is firewalled
	    * let's configure the handler for this
	    * note that this  should occured in a transparent way
	    */
	/*if ( media == "FTCP") {
	  if (proxyName==null || proxyPort==-1 ) throw new
		ModuleLoaderException (" Error when configuring "
		+ media + "handler for module " 
		+ module + ": if the host is defined as firewalled, then it should be given a proxy hostName and port" )  ;		
		handler.setFirewalled( true );
		handler.setProxy( proxyName, proxyPort);
		}

	    handler.registerModuleHandler( module, handler);
	    log.info("ModuleLoader has registred handler: [" +
		       module+ "," + media+ "]");
	    return;
	}
	*/
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
