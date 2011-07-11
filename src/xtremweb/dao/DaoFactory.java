package xtremweb.dao;

public class DaoFactory {
    
    public static InterfaceDao getInstance(String clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException 
    {
	return (InterfaceDao) Class.forName(clazz).newInstance();
    }

}
