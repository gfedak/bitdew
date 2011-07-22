package xtremweb.dao;

import java.util.Collection;

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

    /**
     * This method begins a transaction in case you are using a transactional
     * data model such a DBMS
     */
    public void beginTransaction();

    /**
     * This method perform a rollback on the transaction
     */
    public void transactionRollback();

    /**
     * This method close all resources allocated to the persistence manager
     */
    public void close();

    /**
     * This method commits a transaction
     */
    public void commitTransaction();

    /**
     * This method signals if a transaction is currently active
     * 
     * @return true if the transaction is active, false otherwise
     */
    public boolean transactionIsActive();

    /**
     * Gets the object represented by the parameter
     * 
     * @param arg0
     *            the object to retrieve
     * @return the object asked
     */
    public Object getObjectId(Object arg0);
}
