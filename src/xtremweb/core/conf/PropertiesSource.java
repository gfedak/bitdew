package xtremweb.core.conf;

import java.util.Properties;

public interface PropertiesSource {
    
    public Properties getProperties() throws ConfigurationException ;

}
