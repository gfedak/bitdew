package xtremweb.serv.dt.jsaga;

/**
 * Interface to an LDAP api
 * @author josefrancisco
 *
 */
public interface LDAPInterface {
    
    /**
     * Connect to an LDAP server
     * @param host the ldap uri
     */
    public void connect(String host) throws LDAPEngineException;
    
    /**
     * 
     * @param service the EGEE service you want to query, list of available
     * services can be found on https://forge.ogf.org/sf/wiki/do/viewPage/projects.glue-wg/wiki/ServiceTypes
     * @return
     */
    public String searchByService(String service)throws LDAPEngineException;
    
    
    /**
     * Close the connection
     */
    public void close() throws LDAPEngineException;

}
