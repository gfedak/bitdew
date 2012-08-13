package xtremweb.api.activedata;

import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.ds.Attribute;

/**
 * This interface describe the methods that should be called in a callback.
 * Currently only two methods are supported : onDataScheduled, on DataDeleted
 * Created: Thu Dec 27 21:46:25 2007
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public interface ActiveDataCallback {
    
    /**
     * Triggered when a data is scheduled from the data scheduler to the client cache
     * @param data the data that was scheduled
     * @param attr the attribute that was scheduled
     */
    public  void onDataScheduled(Data data, Attribute attr);
    
    /**
     * Triggered when a data is erased from the client data cache
     * @param data the data tha was deleted
     * @param attr the attribute
     */
    public  void onDataDeleted(Data data, Attribute attr);

}
