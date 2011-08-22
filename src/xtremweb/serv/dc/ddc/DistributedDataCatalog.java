package xtremweb.serv.dc.ddc;
import java.io.Serializable;
import java.util.List;
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
    public void publish(String key, Serializable value) throws DDCException;
    public List search(String key) throws DDCException;

}
