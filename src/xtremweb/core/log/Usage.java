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
    private static final int OPTION_TAB = 2;
    private static final int DESCRIPTION_TAB = 32;

    private String addTab(int tab, String msg){
	String result = "";
	for (int i=0; i<tab; i++) {
	    result += " ";
	}
	return result + msg;
    }

    public void title() {
	log.info("BitDew version " + Version.versionToString()); 
    }

    public void usage(String mesg) {
	log.info("Usage : " + mesg);
    }

    public void section(String msg) {
	log.info(msg);
    }

    public void ln(){
	log.info("");
    }

    public void option(String option, String description) {
	option("    ", option, description);
    }

    public void option(String shortOption, String longOption, String description) {
	String tmp = addTab(OPTION_TAB, ((shortOption.length() == 2)?(shortOption+", "):shortOption)+longOption);
        if (tmp.length()<(DESCRIPTION_TAB)) 
	    //same line
	     log.info(tmp + addTab(DESCRIPTION_TAB-tmp.length(),description) );
	else {
	    //2 lines
	    log.info(tmp);
	    log.info(addTab(DESCRIPTION_TAB,description));
	}
    }
    
} // Usage
