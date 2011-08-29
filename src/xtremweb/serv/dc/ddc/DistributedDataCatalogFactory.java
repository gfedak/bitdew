package xtremweb.serv.dc.ddc;

import xtremweb.core.log.*;

/**
 * Describe class DistributedDataCatalogImpl here.
 *
 *
 * Created: Thu Aug 31 13:59:43 2006
 *
 * @author <a href="mailto:fedak@xtremciel.local">Gilles Fedak</a>
 * @version 1.0
 */
public class DistributedDataCatalogFactory  {

    protected static Logger logger = LoggerFactory.getLogger("DDC Factory");

   //public static final String DEFAULT_DDC_DHT = "xtremweb.serv.dc.ddc.dummy.DummyDistributedDataCatalog";
    
   //TODO put in properties file the constant DEFAULT_DDC_DHT
   public static final String DEFAULT_DDC_DHT = "xtremweb.serv.dc.ddc.dlpt.DLPTDistributedDataCatalog";

    protected static DistributedDataCatalog ddc = null;

    public static DistributedDataCatalog getDistributedDataCatalog() {
	return getDistributedDataCatalog(DEFAULT_DDC_DHT);
    }

    public static DistributedDataCatalog getDistributedDataCatalog(String className) {
	if (ddc != null) return ddc;
	//ClassLoader.getSystemClassLoader();
	ClassLoader classLoader = DistributedDataCatalogFactory.class.getClassLoader();

	try {
	    ddc = (DistributedDataCatalog) (classLoader.loadClass(className)).newInstance();
	} catch (Exception e) {
	    logger.warn("Cannot instantiate : " + className + " trying default DDC Factory\n" + e);
	    try {
		ddc = (DistributedDataCatalog)classLoader.loadClass(DEFAULT_DDC_DHT).newInstance();
	    } catch (Exception ee) {
		logger.warn("Cannot instantiate : " + className + "\n" + ee);
	    }
	}
	return ddc;
    }

}
