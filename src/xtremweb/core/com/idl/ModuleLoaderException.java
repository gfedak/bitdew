package xtremweb.core.com.idl;

/**
 * ModuleLoaderException.java
 * A simple Exception for all the communications
 *
 * Created: late in an evening dream
 *
 * @author Gilles Fedak
 * @version
 */
import xtremweb.core.log.Logger;

public class ModuleLoaderException extends Exception  {

    Exception lowlevelException;  
    protected Logger logger = Logger.getLogger(this.getClass().getName());

    public ModuleLoaderException() {
       super();
    }

    public ModuleLoaderException(String s) {
        super(s);
    }

    public ModuleLoaderException( Exception e ) {
        super();
        lowlevelException = e;

    }
    
    public ModuleLoaderException( String s, Exception e ) {
        super(s);
        lowlevelException = e;
	logger.warn( "Module Loader: Low level Exception " +e); 
    }
}
