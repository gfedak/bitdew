package xtremweb.serv.dt.jsaga;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.conf.ConfigurationProperties;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

/**
 * JNDI implementation
 * 
 * @author josefrancisco
 * 
 */
public class JndiLdapImpl implements LDAPInterface {
    
    /**
     * JNDI Ldap interface
     */
    private InitialDirContext  context;
    
    /**
     * Logger
     */
    private Logger log =  LoggerFactory.getLogger(JndiLdapImpl.class);
    
    /**
     * JndiLdap default constructor
     * @throws ConfigurationException
     */
    public JndiLdapImpl() 
    {
	
    }
    
    
    /**
     * Search for an element in the LDAP tree
     */
    public String searchByService(String service)throws LDAPEngineException {
	SearchControls ctrls = new SearchControls();
	String query ="(& (objectclass=GlueService) (GlueServiceAccessControlBaseRule=vo:vo.rhone-alpes.idgrilles.fr) (GlueServiceType="+service+"))";
	ctrls.setReturningAttributes(new String[]{"GlueServiceEndPoint"});
	ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	NamingEnumeration<SearchResult> answers;
	Attribute gluesuid=null;	
	String finalret;
	try {
	    answers = context.search("mds-vo-name=local,o=grid",query, ctrls);
	    for(;answers.hasMore();)
	    {
	        SearchResult sr = answers.next();
	        Attributes attrs = sr.getAttributes();
	        gluesuid = attrs.get("GlueServiceEndpoint");
	        break;
	    }
	    finalret = (String)gluesuid.get();
	    log.debug("Final ret is "+finalret);
	} catch (NamingException e) {
	    e.printStackTrace();
	    throw new LDAPEngineException("There was a problem executing LDAP command " + e.getMessage());
	}
	

	return finalret;
    }

    /**
     * Give back the allocated resources
     */
    public void close() throws LDAPEngineException{
	try {
	    context.close();
	} catch (NamingException e) {
	    e.printStackTrace();
	   throw new LDAPEngineException("There was a problem closing resources " + e.getMessage());
	}
    }

    /**
     * Connect to a LDAP server
     * 
     * @param host an
     *            uri of the form ldap://host:port
     */
    public void connect(String host) throws LDAPEngineException{
	try {
	    Properties props = new Properties();
	    props.put(Context.INITIAL_CONTEXT_FACTORY,
		    "com.sun.jndi.ldap.LdapCtxFactory");
	    props.put(Context.PROVIDER_URL,
		    host);
	    props.put(Context.REFERRAL, "ignore");
	    context = new InitialDirContext(props);
	} catch (NamingException e) {
	    e.printStackTrace();
	    throw new LDAPEngineException("There was a problem connecting to LDAP server "+host);
	}
    }
}