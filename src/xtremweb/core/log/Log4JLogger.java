package xtremweb.core.log;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.io.*;
import java.util.Properties;
/**
 * Describe class Log4JLogger here.
 *
 *
 * Created: Tue Jul 17 13:07:00 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class Log4JLogger extends xtremweb.core.log.Logger {

    org.apache.log4j.Logger logger;

    /**
     * Creates a new <code>Log4JLogger</code> instance.
     *
     */
    public Log4JLogger() {
	logger=  org.apache.log4j.Logger.getLogger("");
    }

    public Log4JLogger(String module) {
	logger=  org.apache.log4j.Logger.getLogger(module);
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

    public static xtremweb.core.log.Logger getLogger( String module) {
	try {
	    setProperties("conf/log4j.properties");
	} catch(LoggerException le) {
	    System.out.println(le);
	}
	return new Log4JLogger(module);
    }
    
}
