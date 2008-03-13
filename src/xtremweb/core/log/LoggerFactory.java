package xtremweb.core.log;

import java.util.Hashtable;

/**
 * LoggerFactory.java
 *
 *
 * Created: Sun Apr  2 11:24:56 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */
public class LoggerFactory {

    /**
     * The name of the system property identifying our {@link Log}
     * implementation class.
     */
    public static final String LOG_PROPERTY =
        "xtremweb.core.log.Logger";

   /**
     * The  instances that have
     * already been created, keyed by logger name.
     */
    protected static Hashtable instances = new Hashtable();

   /**
     * Name of the class implementing the Log interface.
     */
    private static String logClassName;
    private static boolean debug=false;


    static {
	logClassName = getLogClassName();
	if (debug) System.out.println("Logger configured with : " + logClassName);	
    } // LoggerFactoryImpl constructor
 

    /**
     * Return the fully qualified Java classname of the {@link Log}
     * implementation we will be using.
     */
    protected static String getLogClassName() {
	ClassLoader cl =  LoggerFactory.class.getClassLoader();

        // Return the previously identified class name (if any)
        if (logClassName != null) {
            return logClassName;
        }

	try {
	    logClassName = System.getProperty(LOG_PROPERTY);
	    if ( logClassName != null)
		return logClassName;
	} catch (SecurityException e) {
	    ;
	}

	//check for log4j
	try {
            cl.loadClass("org.apache.log4j.Logger");
            cl.loadClass("xtremweb.core.log.Log4JLogger");
	    logClassName = "xtremweb.core.log.Log4JLogger";
            return logClassName;
        } catch (Throwable t) {
            ;
        }

	//check for jdk logger
	try {
            cl.loadClass("java.util.logging.Logger");
            cl.loadClass("xtremweb.core.log.JDKLogger");
            Class throwable = cl.loadClass("java.lang.Throwable");
            if (throwable.getDeclaredMethod("getStackTrace", (java.lang.Class[]) null) != null) {
		logClassName="xtremweb.core.log.JdkLogger";
		return logClassName;
	    }
        } catch (Throwable t) {
            ;
        }

        if (logClassName == null) {
            logClassName = "xtremweb.core.log.DefaultLogger";
        }
	
        return (logClassName);
    }


    // --------------------------------------------------------- Public Methods
	
    /**
     * Convenience method to return a named logger, without the application
     * having to care about factories.
     *
     * @param clazz Class from which a log name will be derived
     *
     * @exception LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    public static Logger getLogger(Class clazz) {
        return (getLogger(clazz.getName()));

    }

    /**
     * <p>Construct (if necessary) and return a <code>Logger</code> instance,
     * using the factory's current set of configuration attributes.</p>
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *  returned (the meaning of this name is only known to the underlying
     *  logging implementation that is being wrapped)
     *
     */
    public static Logger getLogger(String name) {

        Logger instance = (Logger) instances.get(name);
	
        if (instance == null) {
	    try {
		ClassLoader cl = LoggerFactory.class.getClassLoader();
		instance = (Logger) cl.loadClass(logClassName).newInstance();
		instance.init(name);
		instances.put(name, instance);

	    } catch (Exception e) {
		//		throw new LoggerException("cannot instantiate " + logClassName + " : " + e );
		;
	    }
        }
        return (instance);
    }

} // LoggerFactory
