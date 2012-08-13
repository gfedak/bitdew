package xtremweb.serv.ds;

/**
 * Describe class CacheEntry here.
 *
 *
 * Created: Fri Aug 17 14:29:50 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.ds.Attribute;
import xtremweb.core.obj.ds.Host;
import xtremweb.core.util.SortedVector;
import java.util.*;

/**
 * This class represents an entry on the scheduler cache
 * @author jose
 *
 */
public class CacheEntry {
    
    /**
     * The data
     */
    private Data _data;
    
    /**
     * The attribute that the data has been scheduled with
     */
    private Attribute _attr;
    
    /**
     * Compare to owners lexicographically
     */
    private OwnerComparator comparator = new OwnerComparator();
    
    /**
     * The owners of this entry
     */
    private SortedVector owners;
       
    /**
     * Creates a new <code>CacheEntry</code> instance.
     *
     */
    public CacheEntry(Data data, Attribute attr) {
	_data = data;
	_attr = attr;
	owners = new SortedVector(comparator);
    }
    
    /**
     * Gets tha data uid
     * @return
     */
    public String getDataUid() {
	return _data.getuid();
    }
    
    /**
     * Get the data attribute
     * @return
     */
    public Attribute getAttribute() {
	return _attr;
    }
    
    /**
     * Get the data attribute uid
     * @return
     */
    public String getAttributeUid() {
	return _attr.getuid();
    }
    
    /**
     * 
     * @param attr
     */
    public void setAttribute( Attribute attr) {
	_attr = attr;
    }
    
    /**
     * Get the data
     * @return
     */
    public Data getData() {
	return _data;
    }
    
    /**
     * Get number of owners having a Data in the system
     * @return
     */
    public int getOwnersNumber() {
	return owners.size();
    }
    
    /**
     * Erase the owners
     */
    public void resetOwners() {
	owners.clear();
    }
    
    /**
     * Set new owner
     * @param host
     */
    public void setOwner(Host host) {
	owners.addElement(new Owner(host));
    }

    /**
     * Check which owners are not alive and supress from the owners cache
     */
    public void updateOwners() {
	Vector<Host> lateOwners = new Vector<Host>();
	for (Object o :  owners) {
	    Owner owner = (Owner) o;
	    if ( owner.isLate() ) 
		lateOwners.add(owner.getHost());
	}
	//remove owners which have a passed their timeout
	for (Host h : lateOwners) 
	    owners.removeElement(h);
    }

    /**
     * Update the owner because he is still alive
     * @param host
     */
    public void updateOwner(Host host) {
	int idx = owners.search(host);
	if (idx != -1) {
	    Owner owner = (Owner) owners.elementAt(idx);
	    owner.updateLastAlive();
	}
    }

    /**
     * Remove an owner from the owners cache
     * @param host
     */
    public void removeOwner(Host host) {
	owners.removeElement(host);
    }

    

}
