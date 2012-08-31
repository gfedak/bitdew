package xtremweb.serv.dt.jsaga;

/**
 * This class represents an LDAP exception
 * @author jsaray
 *
 */
public class LDAPEngineException extends Exception{
    
	/**
	 * LDAP exception constructor, forwards to super constructor
	 * @param message
	 */
    public LDAPEngineException(String message){
	super(message);
    }
}
