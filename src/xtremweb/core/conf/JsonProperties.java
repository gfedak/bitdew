package xtremweb.core.conf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
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

public class JsonProperties implements PropertiesSource {
    private final String PATH = "properties.json";
    private final String JAR_PATH = "/properties.json";
    private JsonObject jobj;
    private Logger log = LoggerFactory.getLogger(JsonProperties.class);

    public String convertStreamToString(InputStream is) throws IOException {
	/*
	 * To convert the InputStream to String we use the Reader.read(char[]
	 * buffer) method. We iterate until the Reader return -1 which means
	 * there's no more data to read. We use the StringWriter class to
	 * produce the string.
	 */
	if (is != null) {
	    Writer writer = new StringWriter();

	    char[] buffer = new char[4];
	    try {
		Reader reader = new BufferedReader(new InputStreamReader(is,
			"UTF-8"));
		int n;
		while ((n = reader.read(buffer)) != -1) {
		    writer.write(buffer, 0, n);
		}
	    } finally {
		is.close();
	    }
	    return writer.toString();
	} else {
	    return "";
	}
    }

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
	jobj = new JsonParser().parse(tot).getAsJsonObject();
	Set<Map.Entry<String, JsonElement>> set = jobj.entrySet();
	Iterator iter = set.iterator();
	while (iter.hasNext()) {
	    Map.Entry<String, JsonElement> mp = (Map.Entry<String, JsonElement>) iter
		    .next();
	    String key = mp.getKey();
	    JsonObject objint = jobj.get(key).getAsJsonObject();
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
