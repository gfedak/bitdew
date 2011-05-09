package xtremweb.core.conf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xtremweb.role.cmdline.CommandLineToolHelper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonProperties implements PropertiesSource {
    
    private JsonObject jobj;
    @Override
    public Properties getProperties() throws ConfigurationException {
	try {
	    BufferedReader isr = new BufferedReader(new InputStreamReader(new FileInputStream("properties.json")));
	    String tot="";
	    String s = isr.readLine();
	    s= s.trim();
	    Properties p = new Properties();
	    while (s != null){
		tot+=s;
		s = isr.readLine();
		if(s!=null)
		    s=s.trim();
	    }
	    
	    tot = CommandLineToolHelper.jsonize(tot);
	    jobj = new JsonParser().parse(tot).getAsJsonObject();	    
	    Set<Map.Entry<String,JsonElement>> set = jobj.entrySet();
	    Iterator iter = set.iterator();	    
	    while ( iter.hasNext() ){
		Map.Entry<String,JsonElement> mp = (Map.Entry<String,JsonElement>) iter.next();
		String key = mp.getKey();		
		JsonObject objint = jobj.get(key).getAsJsonObject();		
		Set<Map.Entry<String,JsonElement>> internset = objint.entrySet();
		Iterator intern = internset.iterator();
		while(intern.hasNext()){
		    Map.Entry<String,JsonElement> mpi = (Map.Entry<String,JsonElement>)  intern.next();
		    String keyin = mpi.getKey();
		    String keytostore = key+"."+keyin;
		    String finobj = mpi.getValue().getAsString();
		    p.put(keytostore, finobj);
		}
	    }
	    return p;
	} catch (FileNotFoundException e) {

	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	//throw new ConfigurationException("Configuration crash ");
	return null;
    }
    
    public Object getProperty(String key)
    {	Pattern p = Pattern.compile("[a-zA-Z]*\\.?");
	Matcher m = p.matcher(key);
	String s = m.group();
	String rest = key.substring(m.end(),key.length());
	JsonObject jobjs = jobj.get(s).getAsJsonObject();
	String finale = jobjs.get(rest).getAsString();
	return finale;
    }

}
