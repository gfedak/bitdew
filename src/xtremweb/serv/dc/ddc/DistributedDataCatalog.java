package xtremweb.serv.dc.ddc;
import java.io.Serializable;
import java.util.List;

/**
 * This class interfaces bitdew with a DHT to be able to use
 * a distributed data catalog (dc) service
 *
 * Created: Thu Aug 31 13:55:39 2006
 *
 * @author <a href="mailto:fedak@xtremciel.local">Gilles Fedak</a>
 * @version 1.0
 */
public interface DistributedDataCatalog {
    
    /**
     * Start the distributed data catalog
     * @throws DDCException
     */
    public void start() throws DDCException;
    
    /**
     * Give the entry point (DHT bootstrap node) for this catalog
     * @return a url signaling the location of the bootstrap node.
     * @throws DDCException
     */
    public String entryPoint() throws DDCException;
    
    /**
     * Join to the distributed data catalog
     * @param url the bootstrap node url
     * @throws DDCException
     */
    public void join(String url) throws DDCException;
    
    /**
     * Publish an entry on the catalog
     * @param key
     * @param value
     * @throws DDCException
     */
    public void publish(String key, Serializable value) throws DDCException;
    
    /**
     * Search for an object in the distributed data catalog.
     * @param key the term to search
     * @return a List of objects matching the key
     * @throws DDCException
     */
    public List search(String key) throws DDCException;
    
    /**
     * Stop the distributed data catalog and release the assigned resources
     * @throws DDCException
     */
    public void stop() throws DDCException;
}
