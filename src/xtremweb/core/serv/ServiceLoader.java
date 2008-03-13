package xtremweb.core.serv;

/**
 * ServiceLoader.java
 *
 *
 * Created: Thu Dec  8 13:13:21 2005
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */

import xtremweb.core.log.*;
import xtremweb.core.com.idl.*;
import java.util.List;
import java.util.Arrays;

public class ServiceLoader {

    protected CallbackTemplate _callback = null;
    protected Logger logger = Logger.getLogger("ServiceLoader");

    
    public ServiceLoader(String medium, int port, String... modules) {
	this(medium, port, Arrays.asList(modules));
    }


    public ServiceLoader(String medium, int port, List<String> modules) {
	//create a ModuleLoader
	new ModuleLoader();
	for (String module : modules) {

	    //load the callback associated to module
	    try {
		ModuleLoader.addCallback( module );
		_callback = (CallbackTemplate) ModuleLoader.getModule( module );
	    } catch (ModuleLoaderException e) {
		logger.fatal ("Client cannot load callback: " +  e);
	    }
	    
	    //Load a RMI Handler for this module
	    try {
		ModuleLoader.addHandler( module, medium, port);
	    } catch (ModuleLoaderException e) {
		logger.fatal ("Cannot install " + medium + " handler "+  module + " : " +  e);
	    }
	    logger.info("module " + module + " loaded");
	}
	
    } // ServiceLoader constructor

    public void addSubCallback(String medium, int port, String module, CallbackTemplate subcb) {
    	  //Load a RMI Handler for this module
    	   try {
  	    	 ModuleLoader.addSubCallback( module, subcb );
  			
//  			ModuleLoader.addSubCallback( module, subcb );
  		    } catch (ModuleLoaderException e) {
  			logger.fatal ("callback cannot bind "+  module + " : " +  e);
  		    }
  	    logger.info("module " + module + " loaded");
    	try {
		ModuleLoader.addHandler( module, medium, port);
		
//		ModuleLoader.addSubCallback( module, subcb );
	    } catch (ModuleLoaderException e) {
		logger.fatal ("Client cannot bind "+  module + " : " +  e);
	    }
	    
	 
       
    }
    
} // ServiceLoader
