package xtremweb.core.conf;

import java.util.Properties;
import xtremweb.core.log.*;
/**
 * <code>ConfigurationProperties</code> permits to configure the
 * middleware
 *
 * The default configuration file is conf/xtremweb.properties
 * It can be overloaded using the commmandline switch 
 *  -DPROPERTIES_FILE="new_properties_file"
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

public class ConfigurationProperties {
    private static final String PROPERTIESFILE_DEFAULT = "conf/xtremweb.properties";
    private static final String PROPERTIESJARFILE_DEFAULT = "/xtremweb.properties";
    private static String propertiesFile = null;
    private static Properties properties;
    private static Logger log = LoggerFactory.getLogger(ConfigurationProperties.class);

    static {
	try {
	    new ConfigurationProperties();
	} catch (ConfigurationException ce){
	    log.debug("Cannot set up default properties : " + ce);
	}
    }

    /**
     * Creates a new <code>ConfigurationProperties</code> instance.
     *
     * @exception ConfigurationException if an error occurs
     */
    public ConfigurationProperties() throws ConfigurationException {
	try {
	    properties = PropertiesSourceFactory.newInstance("xtremweb.core.conf.JsonProperties").getProperties();
	} catch (InstantiationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    } // ConfigurationProperties constructor
    
    /**
     * <code>getProperties</code> gets the properties
     *
     * @return a <code>Properties</code> value
     * @exception ConfigurationException if an error occurs
     */
    public static Properties getProperties() throws ConfigurationException {
	if (properties==null) throw new ConfigurationException("Properties not defined");
	return properties;
    }
    public static void setProperty(String key, String value) throws ConfigurationException {
	if (properties==null) throw new ConfigurationException("Properties not defined");
	properties.setProperty(key,value);
    }

} // ConfigurationProperties
