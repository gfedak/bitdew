package xtremweb.dao;

import xtremweb.dao.data.*;

public class DaoFactory {

    public static InterfaceDao getInstance(String clazz)
    {
	
	    
	    try {
		return (InterfaceDao) Class.forName(clazz).newInstance();
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
	return null;
	    
	
	
    }

}
