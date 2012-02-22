package xtremweb.serv.dt.jsaga;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.jfree.util.Log;

import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.conf.ConfigurationProperties;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.serv.dt.http.HttpTransfer;

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
    
    private Logger log =  LoggerFactory.getLogger(JndiLdapImpl.class);
    
    
    private Properties confprops ; 
    
    public JndiLdapImpl() throws ConfigurationException
    {
	confprops = ConfigurationProperties.getProperties();
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
	String fin=null;
	SearchControls ctrls = new SearchControls();
	String servtype  = confprops.getProperty("xtremweb.serv.dr.jsaga.transfertypes");
	Log.debug("transfer type " + servtype); 
	ctrls.setReturningAttributes(new String[]{"GlueServiceEndpoint"});
	ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	NamingEnumeration<SearchResult> answers;
	Attribute gluesuid=null;		
	try {
	    String querycommand = "(&(objectclass=GlueService)(GlueServiceAccessControlBaseRule=vo:vo.rhone-alpes.idgrilles.fr)(GlueServiceType="+servtype+"))";
	    log.debug("command is " + querycommand);
	    answers = context.search("mds-vo-name=local,o=grid",querycommand, ctrls);
	    for(;answers.hasMore();)
	    {	log.debug("there is one answer");
	        SearchResult sr = answers.next();
	        Attributes attrs = sr.getAttributes();
	        gluesuid = attrs.get("GlueServiceEndpoint");
	        log.debug("the answer is " + gluesuid);
	        fin = (String)gluesuid.get();
	        break;
	    }
	} catch (NamingException e) {
	    e.printStackTrace();
	    throw new LDAPEngineException("There was a problem executing LDAP command " + e.getMessage());
	}
	if (gluesuid==null)
            throw new LDAPEngineException("There is no GSIFTP resource");
	//String reto = modifAdHocTemporal(fin);
	return "gsiftp://prabi-ce3.ibcp.fr:2811/tmp";
    }


    private String modifAdHocTemporal(String string) {
	try {
	    String[] toks = string.split("/{1}");
	    URL url = new URL(string);
	    String host = url.getHost();
	    String protocol= url.getProtocol();
	    int port = url.getPort();
	    String aret = protocol + "://" + host + ":" + port + "/tmp";
	    System.out.println("returning "+ aret);
	    return aret;
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	}
	return null;
    }
    
     
}