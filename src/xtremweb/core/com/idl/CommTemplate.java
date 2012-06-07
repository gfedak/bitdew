package xtremweb.core.com.idl;

/**
 * This class represents the client-side object (STUB) that will represent a server side server.
 * The developer can extend this class to get different kinds of distributed protocols (RMI, SOAP, XMLRPC etc).
 * @author jsaray
 *
 */
public abstract class CommTemplate {
	
	/**
	 * Hostname where services are running
	 */
	protected String _hostname;
	
	/**
	 * Port to contact
	 */
	protected int _port;
	
	/**
	 * Requested module
	 */
	protected String _module;
	
	/**
	 * Self-exp
	 * @return
	 */
	public String getHostName() {
		return _hostname;
	}
	
	/**
	 * Self-exp
	 * @return
	 */
	public int getPort() {
		return _port;
	}
	
	/**
	 * Self-exp
	 * @return
	 */
	public String getModule() {
		return _module;
	}
	
	/**
	 * This class will be integrated in a Factory Pattern, for this, customized initialization is performed
	 * through an auxiliar method.
	 * @return
	 */
	public abstract void initComm ( String hostname, int port,String module) throws CommException;
	

}
