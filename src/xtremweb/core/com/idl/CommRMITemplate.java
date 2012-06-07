package xtremweb.core.com.idl;
import java.rmi.*;
/**
 * CommRMITemplate.java
 * 
 * Template to create RMI RPC calls
 * Created: 
 *
 * @author fedak
 * @version 
 */

public class CommRMITemplate extends CommTemplate {
    
    protected Remote rmi; 

    

    public CommRMITemplate () {
	try {
	} catch ( Exception e ) {
	}
    }

    public void initComm ( String hostname,
			   int port,
			   String module
			) throws CommException {
	_hostname = hostname;
	_port = port;
	_module = module;
     try {

	 // Create a RMI handler
	 rmi =
	     Naming.lookup("//" + hostname + ":" + port + "/" + module);
	     
     } catch (RMISecurityException e) {
	 //Some jdk1.1.8 requires this
	 System.setSecurityManager(new RMISecurityManager());
	 try {
	     rmi =
		 Naming.lookup("//" + hostname + ":" + port +
			       "/" + module);
	 } catch (Exception e2) {
	     throw new CommException( "Cannot connect to XtremWeb Server\n"
				      + e2 );
	 }
     } catch( NoSuchObjectException nso ){
	 System.out.println("//" + hostname + ":" + port +
			    "/" + module);
	 try {
	 String [] list = Naming.list("//" + hostname + ":" + port +
				      "/" + module);
	 for ( int i=0; i<list.length; i++) 
	     System.out.println ( list[i] + "\n");
	 } catch(Exception e) {
	     System.out.println( "Cannot list rmi server" + e);
	 }
     } catch (Exception e) {
	 throw new CommException( "Cannot connect to XtremWeb Server\n"
				  + e );
     }
    }
    
    
}
