package xtremweb.core.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

public class PropertiesFile implements PropertiesSource {
    
    private static final String PROPERTIESFILE_DEFAULT = "conf/xtremweb.properties";
    private static final String PROPERTIESJARFILE_DEFAULT = "/xtremweb.properties";
    private static String propertiesFile = null;
    private static Properties properties;
    private static Logger log = LoggerFactory.getLogger(PropertiesSource.class);
    
    public Properties getProperties() throws ConfigurationException
    {
	properties = new Properties();
	propertiesFile = System.getProperty("PROPERTIES_FILE");
	log.debug("properties file " + propertiesFile);
	if (propertiesFile == null)
	    propertiesFile = PROPERTIESFILE_DEFAULT;

	InputStream data = null;

	try {
	    //load the data from a file of it exists
	    if ((new File(propertiesFile)).exists()) {
		data = new FileInputStream(propertiesFile);
		properties.load(data);
		log.info("set properties from file " + propertiesFile);
		return properties;
	    }
	} catch (Exception e) {
	    log.info("cannot load properties from file " + propertiesFile + " : " + e);
	}

	try {
	    //load the properties from within the jar file
	    propertiesFile = PROPERTIESJARFILE_DEFAULT;
	    data = getClass().getResourceAsStream(propertiesFile);
	    properties.load(data);
	    log.info("set properties from resource file, bundled with jar " + getClass().getResource(propertiesFile));
	} catch (Exception e) {
	    log.info("cannot load properties from resource file, bundled with jar " + propertiesFile + " : " + e);
	    throw new ConfigurationException();
	}
	return null;

    }

}
