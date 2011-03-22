package xtremweb.serv.dt.ssh;

/**
 * Represents an SSHTransfer exception
 * 
 * @author jsaray
 * 
 */
public class SSHTransferException extends Exception {

    /**
     * Unique constructor.
     * 
     * @param message
     */
    public SSHTransferException(String message) {
	super(message);
    }
}
