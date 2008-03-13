package xtremweb.core.com.idl;
/**
 * CommException.java
 * A simple Exception for all the communications
 *
 * Created: Wed Nov 22 12:11:17 2000
 *
 * @author Gilles Fedak
 * @version
 */

public class CommException extends Exception {
    
    public Exception lowlevelException;

    public CommException() {
	super();
    }
    
    public CommException(String s) {
	super(s);
    }

    public CommException( Exception e ) {
	super();
	lowlevelException = e;
    }

    public CommException( String s, Exception e ) {
	super(s);
	lowlevelException = e;
    }

} // CommException
