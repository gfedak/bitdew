package xtremweb.core.conf;

public class PropertiesSourceFactory {
    
    public static PropertiesSource newInstance(String clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
	return (PropertiesSource)Class.forName(clazz).newInstance();
    }

}
