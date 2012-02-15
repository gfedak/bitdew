package xtremweb.serv.dt.jsaga;

import javax.naming.directory.SearchControls;

/**
 * Constants representing JNDI constants
 * @author josefrancisco
 *
 */
public class JndiLdapConstants {
    
    /**
     * Object scope
     */
    public static int OBJECT=SearchControls.OBJECT_SCOPE;
    
    /**
     * Inmediatly next level scope
     */
    public static int ONELEVEL=SearchControls.ONELEVEL_SCOPE;
    
    /**
     * Subtree scope
     */
    public static int SUBTREE=SearchControls.SUBTREE_SCOPE;

}
