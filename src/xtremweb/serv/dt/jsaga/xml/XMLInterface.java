package xtremweb.serv.dt.jsaga.xml;

/**
 * An interface to talk with XML parsing engines
 * @author josefrancisco
 *
 */
public interface XMLInterface {
    
	/**
	 * get the element value according to ist name
	 * @param name
	 * @return
	 */
    public String getElement(String name);

}
