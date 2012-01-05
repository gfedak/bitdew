package xtremweb.core.com.idl;

import xtremweb.core.log.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.rmi.RemoteException;
import java.util.Timer;

/**
 * HandlerRMI.java Handles RMI communications from servers
 * 
 * Created: Sun Jul 9 17:46:53 2000
 * 
 * @author Gilles Fedak
 * @version
 */

public class HandlerRMITemplate extends UnicastRemoteObject {

    /**
     * Logger
     */
    protected static Logger log = LoggerFactory.getLogger("HandlerRMI");

    /**
     * Callback template
     */
    protected CallbackTemplate callback;

    /**
     * Module name (dc,dt,dr,ds)
     */
    protected String moduleName;

    /**
     * Class constructor
     */
    public HandlerRMITemplate() throws RemoteException {
	super(ComWorld.getRmiServerPort());
    }

    /**
     * Set the associated callback
     */
    public void registerCallback(CallbackTemplate cb) {
	callback = cb;
    }
}
