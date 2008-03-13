package xtremweb.core.com.idl;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import xtremweb.core.log.*;
import java.util.*;
import java.lang.reflect.*;

public class ModuleLoader {

    protected static Hashtable callbacks =  new Hashtable();;
    public static    String proxyName=null;
    public static    int    proxyPort=-1;
    public static    int    rmiBackPort=4327;
    public static    int    rmiPort=4322;

    public static final String rootServiceClassPath = "xtremweb.serv";
    public static final String rootComClassPath = "xtremweb.core.com.com";

    protected static Logger logger =  LoggerFactory.getLogger(ModuleLoader.class);

    protected static Object createInstance( String className )  throws ModuleLoaderException {
	try {

	    //	    Class classClass = classLoader.loadClass( className, true);
	    ClassLoader cl = ModuleLoader.class.getClassLoader();
	    Class classClass = cl.loadClass( className);
	    Constructor classConstructor =
		classClass.getConstructor((java.lang.Class [])null);
	    //	    for ( int i=0; i<classConstructors.length; i++) {
	    //	logger.InfoBlue("constructor " + classConstructors[i].getName());		
	    return classClass.newInstance();
	} catch ( ClassNotFoundException cnfe ) {
	    logger.warn("ModuleLoader : cannot find class in classpath");
	} catch ( IllegalAccessException iae) {
	    logger.warn( "cannot create object from class " + className + " " + iae);
    	} catch ( NoSuchMethodException nsme) {
    	    logger.warn( "cannot find a contructor to create an object  " + className + " " + nsme);
//  	} catch ( InvocationTargetException ite) {
//  	    throw new ModuleLoaderException( "cannot call the constructor of the class  " + className, ite);
	} catch ( InstantiationException ie) {
	    logger.warn( "cannot instantiate the  class " + className + " " + ie);
	}	    
	throw new ModuleLoaderException( "cannot create instance for class " + className);
    }

    public static void addCallback( String module) throws ModuleLoaderException {
	// create an instance of the call back

	String callbackClassName = rootServiceClassPath + "." + module + ".Callback" + module;
	CallbackTemplate callb = (CallbackTemplate)  createInstance(callbackClassName);
	if ( callb!=null ) {	    	
	    callbacks.put( module, callb );
	    logger.info("ModuleLoader has registred callback: [" + module+ "]" );
	} else {
	    logger.info("ModuleLoader can't register callback: [" + module+ "]" );     
	} 
	

		//	throw new ModuleLoaderException( "cannot find a callback definition file for module " + module);
    }

    public static void addSubCallback(String module, CallbackTemplate subCallback) throws ModuleLoaderException {
    	callbacks.put( module, subCallback );
    	logger.info("ModuleLoader has registred SubCallback: " + subCallback +  " [" + module+ "]" );
    }
    
    public static CallbackTemplate getModule( String module ) throws  ModuleLoaderException {
	if ( ! callbacks.containsKey(module)) throw new ModuleLoaderException( "cannot find a callback  for module " + module);
	return (CallbackTemplate) callbacks.get( module );
    }

    public static void addHandler (String module, String media, int port) throws ModuleLoaderException  {
	//get the callback codes for this client
	if ( ! callbacks.containsKey(module)) throw new ModuleLoaderException( "cannot find a callback  for module " + module);
	// First findout handler
	
	//create a communication handler and register its callback
	if (media=="RMI") {
	    try {
		String handlerClassName =  "xtremweb.core.com.handler.HandlerRMI" + module;
 /*		HandlerRMITemplate handler= (HandlerRMITemplate)
	 	    createInstance("xtremweb."+ module+ ".HandlerRMI" +
		 		   module);
*/
 		HandlerRMITemplate handler= (HandlerRMITemplate)
	 	    createInstance( handlerClassName );
		
		//TODO essayer de demarrer un rmiregistry
		//		Registry registry = LocateRegistry.createRegistry(port);
		//registry.rebind(module, handler);
		Naming.rebind("//" + "localhost" + ":" + port + "/" + module, handler);

		handler.registerCallback((CallbackTemplate) callbacks.get( module ));
		logger.info("ModuleLoader has registred handler: [" + module+ "," + media+ "]");
		return;
	    } catch( ConnectException e) {
		logger.info("cannot connect to " + media + " server " + " when installing module " +  module + " on port " + port + " " + e);
		throw new ModuleLoaderException ();
	    }    catch( Exception e) {
		logger.info("cannot connect to " + media + " server " + " when installing module " +  module + " on port " + port + " " + e );
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
	    logger.info("ModuleLoader has registred handler: [" +
		       module+ "," + media+ "]");
	    return;
	}
	*/
	throw new ModuleLoaderException (" Cannot find a " + media + "handler for module " + module)  ;
    }


    //used to test
    public static void main ( String [] args) {

	new ModuleLoader();
	try {
	    addCallback( "client" );
	} catch (ModuleLoaderException e) {
	    logger.warn("ModuleLoader main(): " +  e);
	    System.exit( 1);
	}
	try {
	    addHandler( "client", "RMI", rmiPort );
	} catch (ModuleLoaderException e) {
	    logger.warn("ModuleLoader main(): " +  e);
	    System.exit( 1);
	}
	
    }
}
