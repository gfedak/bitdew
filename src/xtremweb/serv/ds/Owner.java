package xtremweb.serv.ds;

/**
 * Describe class Owner here.
 *
 *
 * Created: Sun Oct 21 17:50:31 2007
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
import xtremweb.core.obj.ds.Host;

/**
 * Represents a client having a data.
 * @author jose
 *
 */
public class Owner {
    
    /**
     * The owner host
     */
    private Host host;
    
    /**
     * When was the last time this owner was active ?
     */
    private long lastAlive;
    
    /**
     * Maximum amount of time that we will wait for lastAlive attribute be changed
     */
    private static long ALIVETIMEOUT = 10000;

    /**
     * Creates a new <code>Owner</code> instance.
     *
     */
    public Owner(Host h) {
	host = h;
	updateLastAlive();
    }
    
    /**
     * Get the host
     * @return
     */
    public Host getHost() {
	return host;
    }
    
    /**
     * Get host uid
     * @return
     */
    public String getuid() {
	return host.getuid();
    }
    
    /**
     * The host is still alive and we will update the lastAlive value
     */
    public void updateLastAlive() {
	lastAlive = System.currentTimeMillis();
    }
    
    /**
     * Get last alive
     * @return
     */
    public long getLastAlive() {
	return lastAlive;
    }
    
    /**
     * It will be late when the current time is more than lastAlive + ALIVETIMEOUT
     * @return
     */
    public boolean isLate() {
	return ((System.currentTimeMillis() - lastAlive) > ALIVETIMEOUT);
    }
    
    /**
     * change timeout default value
     * @param t
     */
    public static void setAliveTimeout(long t) {
	ALIVETIMEOUT = t;
    }
}
