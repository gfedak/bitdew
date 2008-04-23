package xtremweb.api.bitdew;

import java.lang.Exception;

/**
 * <code>BitDewException</code>.
 *
 * @author <a href="mailto:fedak@dick">Gilles Fedak</a>
 * @version 1.0
 */
public class BitDewException extends Exception {

    /**
     * Creates a new <code>BitDewException</code> instance.
     *
     */
    public BitDewException() {
	
    } // BitDewException constructor

    /**
     * Creates a new <code>BitDewException</code> instance.
     *
     * @param msg a <code>String</code> value
     */
    public BitDewException(String msg) {
	super(msg);
    } // BitDewException constructor

    
} // BitDewException
