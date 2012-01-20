package xtremweb.core.conf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.jpox.sco.Set;

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
    
    /**
     * Default properties file path
     */
    private static final String PROPERTIESFILE_DEFAULT = "conf/xtremweb.properties";
    
    /**
     * Properties file in jar
     */
    private static final String PROPERTIESJARFILE_DEFAULT = "/xtremweb.properties";
    
    /**
     * Properties structure
     */
    private static Properties properties;
    
    /**
     * Logger
     */
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
    
    /**
     * sets a property
     * @param key property key
     * @param value property value
     * @throws ConfigurationException
     */
    public static void setProperty(String key, String value) throws ConfigurationException {
	if (properties==null) throw new ConfigurationException("Properties not defined");
	properties.setProperty(key,value);
    }
    
    /**
     * get servlets class-path
     * @return an Array containing the servlets classpaths
     * @throws ConfigurationException if anything wrong occurs, or the classpath is empty
     */
    public static ArrayList getServlets() throws ConfigurationException {
	ArrayList array = new ArrayList();
	String[] prefix = properties.getProperty("xtremweb.core.http.servlets").split(properties.getProperty("xtremweb.core.http.splittingCharacter"));
	if (prefix == null || prefix.equals(""))
	    return new ArrayList();
	Properties props = new Properties();
	for(int i =0; i < prefix.length;i++)
	    array.add(prefix[i]);
	if (array.isEmpty())
	    throw new ConfigurationException("Please define correctly xtremweb.core.http.servlets variable");
	return array;
    }

} // ConfigurationProperties
