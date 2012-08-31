package xtremweb.serv.dt.bittorrent;

import java.lang.Exception;


/**
 * Encapsulated Bittorrent exception
 * <code>BittorrentException</code> defines Exception
 *
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class BittorrentException extends Exception {

    /**
     * Serial version
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>BittorrentException</code> instance.
     *
     */
    public BittorrentException(String message ) {
	super(message);
    }



}
