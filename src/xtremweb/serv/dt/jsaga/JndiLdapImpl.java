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
     * Search for an element in the LDAP tree
     */
    public String search(String base, int scope, String query, String[] attrs_param)throws LDAPEngineException {
	SearchControls ctrls = new SearchControls();
	ctrls.setReturningAttributes(attrs_param);
	ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	NamingEnumeration<SearchResult> answers;
	Attribute gluesuid=null;		
	try {
	    answers = context.search("mds-vo-name=local,o=grid",query, ctrls);
	    for(;answers.hasMore();)
	    {
	        SearchResult sr = answers.next();
	        Attributes attrs = sr.getAttributes();
	        gluesuid = attrs.get("GlueServiceEndpoint");
	        break;
	    }
	} catch (NamingException e) {
	    e.printStackTrace();
	    throw new LDAPEngineException("There was a problem executing LDAP command " + e.getMessage());
	}
	

	return gluesuid.toString();
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
     * @param an
     *            url of the form ldap://host:port
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

    /**
     * 
     */
    public String getGFTPEndpoint() throws LDAPEngineException {
	SearchControls ctrls = new SearchControls();
	ctrls.setReturningAttributes(new String[]{"GlueServiceEndpoint"});
	ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	NamingEnumeration<SearchResult> answers;
	Attribute gluesuid=null;		
	try {
	    answers = context.search("mds-vo-name=local,o=grid","(&(objectclass=GlueService)(GlueServiceAccessControlBaseRule=vo:vo.rhone-alpes.idgrilles.fr)(GlueServiceType=org.glite.RTEPublisher))", ctrls);
	    for(;answers.hasMore();)
	    {
	        SearchResult sr = answers.next();
	        Attributes attrs = sr.getAttributes();
	        gluesuid = attrs.get("GlueServiceEndpoint");
	        break;
	    }
	} catch (NamingException e) {
	    e.printStackTrace();
	    throw new LDAPEngineException("There was a problem executing LDAP command " + e.getMessage());
	}
	

	return gluesuid.toString();
    }
}