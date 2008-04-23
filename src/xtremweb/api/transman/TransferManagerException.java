package xtremweb.api.transman;

import java.lang.Exception;

/**
 * Describe class <code>TransferManagerException</code> here.
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class TransferManagerException extends Exception {

    /**
     * Creates a new <code>TransferManagerException</code> instance.
     *
     */
    public TransferManagerException() {
    }

    /**
     * Creates a new <code>TransferManagerException</code> instance.
     *
     * @param msg a <code>String</code> value
     */
    public TransferManagerException(String msg) {
	super(msg);
    }

} 