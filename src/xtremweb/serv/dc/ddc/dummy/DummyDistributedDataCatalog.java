package xtremweb.serv.dc.ddc.dummy;
import java.util.List;
import xtremweb.serv.dc.*;
import xtremweb.serv.dc.ddc.*;

/**
 * Describe class DummyDistributedDataCatalog here.
 *
 *
 * Created: Tue Jul 17 13:36:14 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class DummyDistributedDataCatalog extends DistributedDataCatalogImpl implements DistributedDataCatalog {

    /**
     * Creates a new <code>DummyDistributedDataCatalog</code> instance.
     *
     */
    public DummyDistributedDataCatalog() {

    }


    public void start() throws DDCException {
    }

    public String entryPoint() throws DDCException {
	return null;
    }

    public void join(String url) throws DDCException {
    }

    public void publish(String key, String value) throws DDCException {
    }

    public List search(String key) throws DDCException {
	return null;
    }


}
