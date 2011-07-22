package xtremweb.dao;

import java.util.Collection;
import java.util.Iterator;

/**
 * This interface describe the basic operations tha bitdew expects from a data
 * access object
 * 
 * @author jsaray
 * 
 */
public interface InterfaceDao {

    /**
     * Get the object and erase it from the bd
     * 
     * @param obj
     * @return the object
     */
    public Object detachCopy(Object obj);

    /**
     * Get all objects of a given class
     * 
     * @param clazz
     *            the class
     * @return a collection of the objects of a given class
     */
    public Collection getAll(Class clazz);

    /**
     * Get an object giving a uid
     * 
     * @param clazz
     *            the class of the object
     * @param uid
     *            the uid of the object
     * @return the object whose id is uid
     */
    public Object getByUid(Class clazz, String uid);

    /**
     * Make an object persistent
     * 
     * @param obj
     *            the object to become persistent
     */
    public void makePersistent(Object obj, boolean autonomous);
    
    public void beginTransaction();
    
    public void transactionRollback();
    
    public void close();
    
    public void commitTransaction();
    
    public boolean transactionIsActive();
    
    public Object getObjectId(Object arg0);
}
