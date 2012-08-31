package xtremweb.serv.dt.bittorrent.exception;

/**
 * Handles the exception thrown by httptools class
 * @author jsaray
 *
 */
public class HttpToolsException extends Exception{
    
	/**
	 * Class constructor, only delegates to superclass constructor
	 * @param msg
	 */
    public HttpToolsException(String msg)
    {
	super(msg);
    }
}
