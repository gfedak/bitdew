package xtremweb.api.activedata;

import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.ds.Attribute;

/**
 * Describe class ActiveDataCallback here.
 *
 *
 * Created: Thu Dec 27 21:46:25 2007
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public interface ActiveDataCallback {

    public  void onDataScheduled(Data data, Attribute attr);

    public  void onDataDeleted(Data data, Attribute attr);

}
