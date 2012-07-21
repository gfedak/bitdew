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

    private static final int OPTION_TAB = 2;
    private static final int DESCRIPTION_TAB = 32;
    private static final int DESCRIPTION_LEN = 64;

    private static final String ln = System.getProperty("line.separator"); 

    private String addTab(int tab, String msg){
	String result = "";
	for (int i=0; i<tab; i++) {
	    result += " ";
	}
	return result + msg;
    }

    /**
     * <code>title</code> displays title.
     *
     */
    public void title() {
	System.out.println("BitDew version " + Version.versionToString()); 
    }

    /**
     * <code>usage</code> displays short usage.
     *
     * @param mesg a <code>String</code> value
     */
    public void usage(String mesg) {
	System.out.println("Usage : " + mesg);
    }

    /**
     *  <code>section</code> displays heading section.
     *
     * @param msg a <code>String</code> value
     */
    public void section(String msg) {
	System.out.println(msg);
    }

    /**
     * <code>ln</code> new line.
     *
     */
    public void ln(){
	System.out.println("");
    }

    /**
     *  <code>option</code> displays long option and description
     *
     * @param option a <code>String</code> value
     * @param description a <code>String</code> value
     */
    public void option(String option, String description) {
	option("    ", option, description);
    }

    /**
     * <code>option</code> displays short option (4 char max), long
     * option and description.
     *
     * @param shortOption a <code>String</code> value
     * @param longOption a <code>String</code> value
     * @param description a <code>String</code> value
     */
    public void option(String shortOption, String longOption, String description) {
	String tmpOpt = addTab(OPTION_TAB, ((shortOption.length() == 2)?(shortOption+", "):shortOption)+longOption);
	//format description
	String tmpDescr = "";
	while (description.length()>DESCRIPTION_LEN) {
	    tmpDescr = tmpDescr + addTab(DESCRIPTION_TAB, description.substring(0,DESCRIPTION_LEN-1)) + ln;
	    description = description.substring(DESCRIPTION_LEN-1, description.length());
	}
	tmpDescr = tmpDescr + addTab(DESCRIPTION_TAB, description);

	//append description to option
        if (tmpOpt.length()<(DESCRIPTION_TAB)) 
	    //same line
            System.out.println(tmpOpt + addTab(DESCRIPTION_TAB-tmpOpt.length(),tmpDescr.substring(DESCRIPTION_TAB, tmpDescr.length())) );
	else {
	    //2 lines
	    System.out.println(tmpOpt);
	    System.out.println(tmpDescr);
	}
    }
    
} // Usage
