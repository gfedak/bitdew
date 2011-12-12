package xtremweb.core.log;

/**
 * Logger is an abstract class to specify the various methods to log messages
 *
 *
 *
 * @author <a href="mailto:Gilles.Fedak@inria.fr">Gilles Fedak</a>
 * @version 1.0
 */

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
