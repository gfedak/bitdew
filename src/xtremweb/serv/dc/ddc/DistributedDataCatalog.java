package xtremweb.serv.dc.ddc;

/**
 * Describe interface DistributedDataCatalog here.
 *
 *
 * Created: Thu Aug 31 13:55:39 2006
 *
 * @author <a href="mailto:fedak@xtremciel.local">Gilles Fedak</a>
 * @version 1.0
 */
public interface DistributedDataCatalog {

    public void start() throws DDCException;
    public String entryPoint() throws DDCException;
    public void join(String url) throws DDCException;
    public void publish(String key, String value) throws DDCException;
    public String search(String key) throws DDCException;

}
