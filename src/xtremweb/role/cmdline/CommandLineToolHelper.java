package xtremweb.role.cmdline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;



/**
 * This class provides the jsonize method to simplify the user 
 * object introduction
 * @author jsaray
 *
 */
public class CommandLineToolHelper {
    
    /**
     * this method add the "" characters to all th json properties ex.
     * {name: "jesica",surname: "adams" } is transformed to {"name": "jesica","surname": "adams" }
     * @param str
     * @return the modified string
     */
    public static String jsonize(String str) {
	str = str.replace('\'', '"');
	StringBuffer total = new StringBuffer(str);
	// pattern ','|'{'<word>':'
	Pattern betweenBraces = Pattern.compile("[,{].*?:");
	Matcher m = betweenBraces.matcher(total);
	// each find search for a string of the form [,{]<word>:
	m.find();
	//insert in the sides of the word token quotation marks
	total.insert(m.start() + getFirstAlpha(m.group()), "\"");
	total.insert(m.end() - getLastAlpha(m.group()) + 1, "\"");
	int end = m.end();
	while (m.find(end)) {
	    total.insert(m.start() + getFirstAlpha(m.group()), "\"");
	    total.insert(m.end() - getLastAlpha(m.group()) + 1, "\"");
	    end = m.end();
	    //because total variable has changed we need to recalculate the match
	    m = betweenBraces.matcher(total);
	}
	return new String(total);
    }
    
    /**
     * Get the index of the first alphabetic character
     * @param s
     * @return the position where the first alphanumeric character is
     */
    public static int getFirstAlpha(String s) {
	int a = 0;
	for (int i = 0; i < s.length(); i++) {
	    if (s.charAt(i) == ',' || s.charAt(i) == '{' || s.charAt(i) == ' ') {
	    } else {
		a = i;
		return a;
	    }
	}
	return -1;
    }
    
    public static void notNull(String field, Object arg) throws NullPointerException
    {
	if(arg == null)
	    throw new NullPointerException( field + " cannot be null ");
    }
    
    /**
     * Get the index of the last alphabetic character
     * @param s
     * @return the position of the last alphanumeric character
     */
    public static int getLastAlpha(String s) {
	int k = 0;
	for (int i = s.length() - 1; i >= 0; i--) {
	    k++;
	    if (s.charAt(i) == ':' || s.charAt(i) == ' ') {
	    } else {

		return k;
	    }
	}
	return -1;
    }
    
    public static String nullOrObject(JsonElement obj)
    {	return obj==null?null:obj.getAsString();
    
    }
    

    public static void main(String[] args) {
	String teststring = "{database:{driver:\"org.hsqldb.jdbcDriver\",url:\"jdbc:hsqldb:mem:test\",user:\"sa\",password:\"\",connectionPooling: \"DBCP\"},port: 4325,protocols:[{ protocol : \"ftp\",server:\"ftp.lip6.fr\", port: 21, login: \"anonymous\",passwd: \"anonymous\", path: \"/pub/linux/distributions/slackware/slackware-current\"},{ protocol : \"ftp\",server: \"192.168.55.53\",port: 21, login: \"ftp\", passwd: \"fedak@lri.fr\", path: \"/pub/incoming\"},{ protocol : \"http\", port: 8080, server: \"localhost\",path:\"data\"},{ protocol : \"bittorrent\", path: \"torrents\", port: 6969},{ protocol : \"dummy\"}],transfers : [{ transfer : \"bittorrent\", makeTorrentExec: \"/Users/fedak/shared/projects/bitdew/python/btmakemetafile.py\", azureusjar: \"/Users/fedak/shared/projects/bitdew/lib/azureus.jar\",trackerExec: \"/Users/fedak/shared/projects/bitdew/python/bttrack.py\",trackerOption: \"--dfile dstate\"}]}";
	String res = CommandLineToolHelper.jsonize(teststring);
	System.out.println("resolve " + res);
    }
}
