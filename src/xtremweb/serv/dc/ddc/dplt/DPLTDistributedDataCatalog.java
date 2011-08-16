package xtremweb.serv.dc.ddc.dplt;

import fr.inria.graal.sbam.core.api.IPeer;
import fr.inria.graal.sbam.core.impl.FactoryPeer;
import fr.inria.graal.sbam.core.services.PfConfigService;
import fr.inria.graal.sbam.dlpt.api.IClient;
import fr.inria.graal.sbam.dlpt.impl.DlptClient;
import xtremweb.serv.dc.ddc.DDCException;
import xtremweb.serv.dc.ddc.DistributedDataCatalog;
import xtremweb.serv.dc.ddc.DistributedDataCatalogImpl;
import java.util.List;


public class DPLTDistributedDataCatalog extends DistributedDataCatalogImpl implements DistributedDataCatalog {

	private IClient client;
	private PfConfigService pconfig;

	public void start() throws DDCException {
		
		scalexp.BootstrapExp.observation = false;
		scalexp.BootstrapExp.bootstrapExp(new String[]{"DEBUG"});
		pconfig = PfConfigService.getInstance();

	}


	public String entryPoint() throws DDCException {
		
		//TODO Convert the object pconfig to String
		
		
		return pconfig.toString();
	}


	public void join(String url) throws DDCException {
		
		PfConfigService.getInstance().loadConfig(pconfig);
		IPeer locPeer = FactoryPeer.createLocalPeer(new FactoryPeer.PEERGROUP[]{FactoryPeer.PEERGROUP.DLPTCLIENTHOSTER});
		client = new DlptClient(locPeer);
	}
	
	/**
	 * Value must be serializable !!!!!
	 * XSTREAM serialize objects to XML
	 */
	public void publish(String key, String value) throws DDCException {
		client.putResourcesSeq(value, key);
	}


	public List search(String key) throws DDCException {
		//TODO change originally string by a List or Collection
		return client.getResources(key);
	}

}
