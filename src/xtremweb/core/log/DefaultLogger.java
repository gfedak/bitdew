package xtremweb.core.log;

/**
 * Logger.java
 *
 *
 * Created: Thu Mar 10 11:11:52 2005
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class DefaultLogger extends xtremweb.core.log.Logger{

    //    private String _module="";

    public DefaultLogger() {
    } // Logger constructor

    public DefaultLogger(String module) {
	_module = module;
    } // Logger constructor

    public void init(String module ) {
	_module = module;
    }

    public void info(String msg) {
	System.out.println(_module + " : " + msg);
    }

    public void warn(String msg) {
	System.out.println(_module + " : " + msg);
    }

    public void debug(String msg) {
	System.out.println(_module + " : " + msg);
    }

    public void fatal(String msg) {
	System.out.println(_module + " : " + msg);
    }

    public void setLevel(String level) {
    }
 
    public static Logger getLogger( String module) {
	return new DefaultLogger(module);
    }
    
} // Logger
