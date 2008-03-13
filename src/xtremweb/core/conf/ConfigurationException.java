package xtremweb.core.conf;

/**
 * ConfigurationException.java
 *
 *
 * Created: Tue Mar  7 11:48:05 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */
import java.lang.Exception;

public class ConfigurationException extends Exception {

    public ConfigurationException() {
	
    } // ConfigurationException constructor

    public ConfigurationException(String message) {
	super(message);
    } // ConfigurationException constructor

    
} // ConfigurationException
