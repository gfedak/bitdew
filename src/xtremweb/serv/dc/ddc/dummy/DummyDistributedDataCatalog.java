package xtremweb.serv.dc.ddc.dummy;
import java.io.Serializable;
import java.util.List;
import xtremweb.serv.dc.ddc.*;

/**
 * This class implements the DistributedDataCatalog interface in a dummy way.
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

    public void publish(String key, Serializable value) throws DDCException {
    }

    public List search(String key) throws DDCException {
	return null;
    }


    @Override
    public void stop() throws DDCException {
	// TODO Auto-generated method stub
	
    }


}
