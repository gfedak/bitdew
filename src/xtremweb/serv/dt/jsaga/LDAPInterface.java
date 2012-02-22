package xtremweb.serv.dt.jsaga;

import java.util.Collection;

/**
 * Interface to an LDAP api
 * @author josefrancisco
 *
 */
public interface LDAPInterface {
    
    /**
     * Connect to an LDAP uri
     * @param host
     * @param port
     */
    public void connect(String host) throws LDAPEngineException;
    
    /**
     * 
     * @param base the base in the LDAP tree
     * @param scope the scope of the search
     * @param query the query to search
     * @return
     */
    public String searchByService(String service)throws LDAPEngineException;
    
    /**
     * Returns a gridftp endpoint (url) suitable to make a transfer
     * @return
     * @throws LDAPEngineException
     */
    public String getGFTPEndpoint()throws LDAPEngineException;
    
    /**
     * Close the connection
     */
    public void close() throws LDAPEngineException;

}
