package xtremweb.serv.dc.ddc.dlpt;

import com.google.gson.Gson;
import fr.inria.graal.sbam.core.api.IPeer;
import fr.inria.graal.sbam.core.impl.FactoryPeer;
import fr.inria.graal.sbam.core.services.PfConfigService;
import fr.inria.graal.sbam.dlpt.impl.DlptClient;
import xtremweb.serv.dc.ddc.DDCException;
import xtremweb.serv.dc.ddc.DistributedDataCatalog;
import xtremweb.serv.dc.ddc.DistributedDataCatalogImpl;

import java.io.Serializable;
import java.util.List;

/**
 * This class implements a DHT using the DLPT prefix tree
 * 
 * @author josefrancisco
 * 
 */
public class DLPTDistributedDataCatalog extends DistributedDataCatalogImpl implements DistributedDataCatalog {

    /**
     * DLPT Client
     */
    private DlptClient client;

    /**
     * This object contains server initialization
     */
    private PfConfigService pconfig;

    /**
     * Start the DLPT locally
     */
    public void start() throws DDCException {

	scalexp.BootstrapExp.observation = false;
	scalexp.BootstrapExp.bootstrapExp(new String[] {});
	pconfig = PfConfigService.getInstance();

    }

    /**
     * Get the entry point to this client
     */
    public String entryPoint() throws DDCException {

	// TODO Convert the object pconfig to String
	Gson gson = new Gson();
	String json = gson.toJson(pconfig);

	return json;
    }

    /**
     * Join to this DLPT
     */
    public void join(String url) throws DDCException {
	Gson gson = new Gson();
	PfConfigService newconfig = gson.fromJson(url, PfConfigService.class);
	PfConfigService.getInstance().loadConfig(newconfig);
	IPeer locPeer = FactoryPeer.createLocalPeer(new FactoryPeer.PEERGROUP[] { FactoryPeer.PEERGROUP.DLPTCLIENTHOSTER });
	client = new DlptClient(locPeer);
    }

    /**
     * Value must be serializable !!!!! XSTREAM serialize objects to XML
     */
    public void publish(String key, Serializable value) throws DDCException {
	client.putResourcesSeq(value, key);
    }

    /**
     * Search for a specified key on the DLPT
     * 
     * @return list of objects matching the key
     */
    public List search(String key) throws DDCException {
	return client.getResources(key);
    }
    
    /**
     * Release the assigned resources
     */
    public void stop() throws DDCException {
	client.stop();
    }

}
