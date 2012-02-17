package xtremweb.core.conf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.role.cmdline.CommandLineToolHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This class implements a properties source using Json language
 * @author josefrancisco
 */
public class JsonProperties implements PropertiesSource {
    
    /**
     * first place to search the json file
     */
    private final String PATH = "properties.json";
    
    /**
     * Place to search the json file inside the JAR
     */
    private final String JAR_PATH = "/properties.json";
    
    /**
     * json object
     */
    private JsonObject jobj;
    
    /**
     * Log
     */
    private Logger log = LoggerFactory.getLogger(JsonProperties.class);
    
    /**
     * Convert the json structure to a Properties one, for example if we have 
     * {key1: {key2: "value1"},key3: {key4: "value2"}}, it will be converted as if the properties file were :
     * 
     * key1.key2 = "value1"
     * key3.key4 = "value2"
     */
    public Properties getProperties() throws ConfigurationException {

	String tot = "";
	Properties p = new Properties();
	BufferedReader br = null;
	try {
	    if (System.getProperty("PROPERTIES_FILE") == null) {
		br = new BufferedReader(new InputStreamReader(
			new FileInputStream(PATH)));
		String s = br.readLine();
		s = s.trim();
		while (s != null) {
		    tot += s;
		    s = br.readLine();
		    if (s != null)
			s = s.trim();
		}
	    } else {
		String pfile = System.getProperty("PROPERTIES_FILE");
		br = new BufferedReader(new InputStreamReader(
			new FileInputStream(pfile)));
		String s = br.readLine();
		s = s.trim();
		while (s != null) {
		    tot += s;
		    s = br.readLine();
		    if (s != null)
			s = s.trim();
		}
	    }
	} catch (FileNotFoundException e) {
	    try {
		InputStream is = getClass().getResourceAsStream(JAR_PATH);
		br = new BufferedReader(new InputStreamReader(is));
		String s = br.readLine();
		s = s.trim();
		while (s != null) {
		    tot += s;
		    s = br.readLine();
		    if (s != null)
			s = s.trim();
		}

	    } catch (Exception ext) {
		ext.printStackTrace();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	tot = CommandLineToolHelper.jsonize(tot);
	log.debug("before jsonize is " + tot);
	//get the json object
	jobj = new JsonParser().parse(tot).getAsJsonObject();
	Set<Map.Entry<String, JsonElement>> set = jobj.entrySet();
	Iterator iter = set.iterator();
	//iteration over the json file watched as a set of <String,JsonElement>
	while (iter.hasNext()) {
	    Map.Entry<String, JsonElement> mp = (Map.Entry<String, JsonElement>) iter
		    .next();
	    //extract key
	    String key = mp.getKey();
	    //extract object
	    JsonObject objint = jobj.get(key).getAsJsonObject();
	    //internal entries of the object, the maximum depth are two levels
	    Set<Map.Entry<String, JsonElement>> internset = objint.entrySet();
	    Iterator intern = internset.iterator();
	    while (intern.hasNext()) {
		Map.Entry<String, JsonElement> mpi = (Map.Entry<String, JsonElement>) intern
			.next();
		String keyin = mpi.getKey();
		String keytostore = key + "." + keyin;
		String finobj = mpi.getValue().getAsString();
		p.put(keytostore, finobj);
	    }
	}
	return p;
    }
    
    /**
     * Give a property from the json file
     * @param key a string of the form key1.key2.key3....finalvalue
     * @return the JsonObject that correspond to this key, that is :
     * {key1.key2...key(n-1): finalvalue}
     */
    public Object getProperty(String key) {
	Pattern p = Pattern.compile("[a-zA-Z]*\\.?");
	Matcher m = p.matcher(key);
	String s = m.group();
	String rest = key.substring(m.end(), key.length());
	JsonObject jobjs = jobj.get(s).getAsJsonObject();
	String finale = jobjs.get(rest).getAsString();
	return finale;
    }

}
