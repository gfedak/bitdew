package xtremweb.dao;

/**
 * This class represents a dao factory, it enhance new Dao development in case
 * it changes on time
 * 
 * @author josefrancisco
 * 
 */
public class DaoFactory {

    /**
     * Get an instance of the class introduced by parameter
     * 
     * @param clazz
     * @return the class introduced by parameter
     */
    public static InterfaceDao getInstance(String clazz) {

	try {
	    return (InterfaceDao) Class.forName(clazz).newInstance();
	} catch (InstantiationException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
	return null;

    }

}
