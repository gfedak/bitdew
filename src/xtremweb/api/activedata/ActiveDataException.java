package xtremweb.api.activedata;

/**
 * Describe class ActiveDataException here.
 *
 *
 * Created: Sat Jan  5 14:59:31 2008
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class ActiveDataException  extends Exception{

    /**
     * Creates a new <code>ActiveDataException</code> instance.
     *
     */
    public ActiveDataException() {

    }

   /**
     * Creates a new <code>ActiveDataException</code> instance.
     *
     */
    public ActiveDataException(String msg) {
	super(msg);
    }

}
