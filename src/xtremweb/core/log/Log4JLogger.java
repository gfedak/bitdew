package xtremweb.core.log;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import java.io.*;
import java.util.Properties;
/**
 * Describe class Log4JLogger here.
 *
 *
 * Created: Tue Jul 17 13:07:00 2007
 *
 * @author <a href="mailto:Gilles.Fedak@inria.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class Log4JLogger extends xtremweb.core.log.Logger {

    org.apache.log4j.Logger logger;
    private static boolean debug = false;
    private final static String  DEFAULT_LOG4J_PROPERTIES = "conf/log4j.properties";
    private static String log4jProperties = DEFAULT_LOG4J_PROPERTIES;

    /**
     * Creates a new <code>Log4JLogger</code> instance.
     *
     */
    public Log4JLogger() {
	try {
	    setProperties(log4jProperties);
	} catch (LoggerException le) {
	    System.out.println("cannot init properly : " + le );
	} 
	logger=  org.apache.log4j.Logger.getLogger("");
    }

    public Log4JLogger(String module) {
	this();
	_module = module;
    } // Logger constructor


    public void init(String module ) {
	_module = module;
    }


    public void info(String msg) {
	logger.info(msg);
    }

    public void warn(String msg) {
	logger.warn(msg);
    }

    public void debug(String msg) {
	logger.debug(msg);
    }

    public void fatal(String msg) {
	logger.fatal(msg);
	System.exit(2);
    }

    
    public static void setProperties(String propertiesFileName)  throws LoggerException {
	if (debug) {
	    System.out.println("Log4j properties file : " + propertiesFileName);
	}
	log4jProperties = propertiesFileName;
	File propFile = new File(propertiesFileName);
	if (propFile.exists())
	    PropertyConfigurator.configure(propertiesFileName);
	else {
	    //we trim the conf/ from the propertiesFileName as the conf  
	    propertiesFileName = propertiesFileName.substring("conf/".length(), propertiesFileName.length());
	    InputStream in = Log4JLogger.class.getClassLoader().getResourceAsStream(propertiesFileName);
	    try {
		Properties props = new Properties();
		props.load(in);
		PropertyConfigurator.configure(props);
	    } catch (Exception ioe) {
		throw new LoggerException(propertiesFileName + " cannot be found niether in the current path, neither in the jar archive");
	    }
	}
    }

    //FIXME : make this generic to all logger
    public void setLevel(String level) {
	if ("debug".equalsIgnoreCase(level)) {
	    logger.setLevel(Level.DEBUG);
	} else if ("info".equalsIgnoreCase(level)) {
	    logger.setLevel(Level.INFO);
	} else if ("error".equalsIgnoreCase(level)) {
	    logger.setLevel(Level.ERROR);
	} else if ("fatal".equalsIgnoreCase(level)) {
	    logger.setLevel(Level.FATAL);
	} else if ("warn".equalsIgnoreCase(level)) {
	    logger.setLevel(Level.WARN);
	}
    }

}
