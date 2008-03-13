package xtremweb.core.conf;

import java.util.Properties;
import java.io.*;
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
	properties = new Properties();
	propertiesFile = System.getProperty("PROPERTIES_FILE");
	log.debug("propertie file" + propertiesFile);
	if (propertiesFile == null)
	    propertiesFile = PROPERTIESFILE_DEFAULT;
	try {
	    properties.load(new FileInputStream(propertiesFile));
	} catch (Exception e) {
	    throw new ConfigurationException("cannot find " + propertiesFile);
	}
    } // ConfigurationProperties constructor
    
    /**
     *  <code>reload</code> properties
     *
     * @param file a <code>String</code> value
     * @exception ConfigurationException if an error occurs
     */
    public static void reload(String file) throws ConfigurationException {
	propertiesFile = file;
	try {
	    properties.load(new FileInputStream(propertiesFile));
	} catch (Exception e) {
	    throw new ConfigurationException("cannot find " + propertiesFile);
	}
    } // ConfigurationProperties constr

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

} // ConfigurationProperties
