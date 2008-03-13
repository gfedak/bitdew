package xtremweb.core.log;

/**
 * Usage.java
 *
 *
 * Created: Fri Apr 14 10:46:03 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */
public class Usage {

    public static Logger log = LoggerFactory.getLogger("");

    public Usage() {
	
    } // Usage constructor

    public void usage(String mesg) {
	log.info("Usage : " + mesg);
    }

    public void option(String option, String description) {
	log.info("\t" + option + "\t\t" + description);
    }
    
} // Usage
