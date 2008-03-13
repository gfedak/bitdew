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

public class Owner {

    private Host host;
    private long lastAlive;
    private static long ALIVETIMEOUT = 10000;

    /**
     * Creates a new <code>Owner</code> instance.
     *
     */
    public Owner(Host h) {
	host = h;
	updateLastAlive();
    }

    public Host getHost() {
	return host;
    }

    public String getuid() {
	return host.getuid();
    }

    public void updateLastAlive() {
	lastAlive = System.currentTimeMillis();
    }

    public long getLastAlive() {
	return lastAlive;
    }

    public boolean isLate() {
	return ((System.currentTimeMillis() - lastAlive) > ALIVETIMEOUT);
    }

    public static void setAliveTimeout(long t) {
	ALIVETIMEOUT = t;
    }
}
