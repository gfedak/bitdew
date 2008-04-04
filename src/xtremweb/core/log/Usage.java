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
    private int optionTab = 8;
    private int sectionTab = 4;

    private void displayWithTab(int tab, int size, String msg){
	String result = "";
	char[] tmp = new char[tab];
	for (int i=0; i<tab; i++) {
	    
	}

    }

    public void title() {
	log.info("BitDew version " + Version.versionToString()); 
    }

    public void usage(String mesg) {
	log.info("Usage : " + mesg);
    }

    public void option(String option, String description) {
	log.info("\t" + option + "\t\t" + description);
    }
    
} // Usage
