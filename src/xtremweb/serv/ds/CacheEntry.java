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

public class CacheEntry {

    private Data _data;
    private Attribute _attr;
    private OwnerComparator comparator = new OwnerComparator();
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

    public String getDataUid() {
	return _data.getuid();
    }

    public Attribute getAttribute() {
	return _attr;
    }

    public String getAttributeUid() {
	return _attr.getuid();
    }

    public void setAttribute( Attribute attr) {
	_attr = attr;
    }

    public Data getData() {
	return _data;
    }

    public int getOwnersNumber() {
	return owners.size();
    }

    public void resetOwners() {
	owners.clear();
    }

    public void setOwner(Host host) {
	owners.addElement(new Owner(host));
    }

    //FIXME needs to make Sorted Vector generics
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

    //FIXME needs to make Sorted Vector generics
    public void updateOwner(Host host) {
	int idx = owners.search(host);
	if (idx != -1) {
	    Owner owner = (Owner) owners.elementAt(idx);
	    owner.updateLastAlive();
	}
    }


    public void removeOwner(Host host) {
	owners.removeElement(host);
    }

    

}
