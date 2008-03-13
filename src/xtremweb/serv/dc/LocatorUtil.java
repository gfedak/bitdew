package xtremweb.serv.dc;

import java.io.File;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dc.Data;
/**
 *  LocatorUtil implements utilitary methods around the Locator Class
 *
 *
 * Created: Wed Feb 28 17:15:47 2007
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

public class LocatorUtil {

    /**
     * Creates a new <code>LocatorUtil</code> instance.
     *
     */
    public LocatorUtil() {

    }

    /**
     * <code>localLocator</code> returns a Locator constructed from
     * the definition of a File and a Data. The File is assumed to be
     * local. 
     *
     * @param data a <code>Data</code> value
     * @param file a <code>File</code> value
     * @return a <code>Locator</code> value
     */
    public static Locator localLocator(Data data, File file) {
	Locator local_locator = new Locator();
	local_locator.setdatauid(data.getuid());
	local_locator.setdrname("local");
	//local_locator.setprotocoluid(local_proto.getuid());
	local_locator.setref(file.getAbsolutePath());
	return local_locator;
    }

}
