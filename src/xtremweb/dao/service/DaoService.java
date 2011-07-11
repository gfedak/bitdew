package xtremweb.dao.service;

import java.util.Collection;
import java.util.Iterator;
import javax.jdo.Query;
import xtremweb.core.obj.dn.Service;
import xtremweb.dao.DaoJDOImpl;

/**
 * This class interface with service table thorugh jdo
 * 
 * @author jsaray
 * 
 */
public class DaoService extends DaoJDOImpl {

    /**
     * get the service having a specific name
     * 
     * @param name
     *            the name
     */
    public Service getServiceByName(String name) {
	String str;
	Service s = null;
	Query query = pm.newQuery(xtremweb.core.obj.dn.Service.class,
		"service == '" + name + "'");
	Collection result = ((Collection) query.execute());
	Iterator iter = result.iterator();
	while (iter.hasNext()) {
	    s = (Service) iter.next();
	    str = s.getbundle();
	}
	return s;
    }

}
