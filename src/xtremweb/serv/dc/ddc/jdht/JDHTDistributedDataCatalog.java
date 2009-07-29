package xtremweb.serv.dc.ddc.jdht;

import xtremweb.serv.dc.*;
import xtremweb.serv.dc.ddc.*;
import org.kth.dks.JDHT;


/**
 * Describe class JDHTDistributedDataCatalog here.
 *
 *
 * Created: Thu Aug 31 15:23:05 2006
 *
 * @author <a href="mailto:fedak@xtremciel.local">Gilles Fedak</a>
 * @version 1.0
 */
public class JDHTDistributedDataCatalog extends DistributedDataCatalogImpl implements DistributedDataCatalog {

    protected JDHT jDHT = null;
    protected String url = null;
    protected int DEFAULT_PORT = 5500;
 
   /**
     * Creates a new <code>JDHTDistributedDataCatalog</code> instance.
     *
     */
    public JDHTDistributedDataCatalog() {

    }

    public void start()throws DDCException {
	try {
	    jDHT = new JDHT();
	    url = ((JDHT)jDHT).getReference();
	} catch (Exception e) {
	    logger.warn("cannot start a JDHT " + e);
	    throw new DDCException();
	}
    }

    public String entryPoint() throws DDCException {
	if (url == null) {
	    logger.warn("cannot start a JDHT ");
	    throw new DDCException();	    
	} else
	    return url;

    }

    public void join(String url) throws DDCException {
	try {
	    jDHT = new JDHT(DEFAULT_PORT, url);
	    url = ((JDHT)jDHT).getReference();
	} catch (Exception e) {
	    logger.warn("cannot start a JDHT " + e);
	    throw new DDCException();
	}
    }
    public void publish(String key, String value) throws DDCException {
	try {
	    jDHT.put(key,value);
	} catch (Exception e) {
	    logger.warn("cannot start a JDHT " + e);
	    throw new DDCException();
	}
    }

    public String search(String key) throws DDCException  {
	String value = null;
	try {
	    jDHT.get(key);
	} catch (Exception e) {
	    logger.warn("cannot start a JDHT " + e);
	    throw new DDCException();
	}
	return value;
    }



}
