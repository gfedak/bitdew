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

//FIXME make this class abstract or an interface

public abstract class Logger {

    protected String _module="";

    public Logger() {
    } // Logger constructor

    public Logger(String module) {
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
	System.exit(2);
    }

    public abstract void setLevel(String level);

    //FIXME the whole class should be abstract
    public static Logger getLogger( String module) {
	return new DefaultLogger(module);
    }
    
} // Logger
