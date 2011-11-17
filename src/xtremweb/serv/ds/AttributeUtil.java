package xtremweb.serv.ds;

import xtremweb.core.obj.ds.Attribute;
/**
 * Describe class AttributeUtil here.
 *
 *
 * Created: Fri Aug 24 14:19:26 2007
 *
 * @author <a href="mailto:fedak@lri7-234.lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

import xtremweb.core.log.*;
import xtremweb.role.cmdline.CommandLineToolHelper;
import xtremweb.api.activedata.ActiveDataException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class AttributeUtil {

    public static Logger log = LoggerFactory.getLogger(AttributeUtil.class);

    public static String toString(Attribute attr) {
	String retour =  "attr " + attr.getname() + " [" + attr.getuid()+ "] = {";
	if ((attr.getmask() & AttributeType.REPLICAT) == AttributeType.REPLICAT) 
	    retour += " replicat=" + attr.getreplicat();
	if ((attr.getmask() & AttributeType.FT) == AttributeType.FT) 
	    retour += " ft=" + attr.getft();
	if ((attr.getmask() & AttributeType.LFTABS) == AttributeType.LFTABS) 
	    retour += " lftabs=" + attr.getlftabs();
	if ((attr.getmask() & AttributeType.LFTREL) == AttributeType.LFTREL) 
	    retour += " lftrel=" + attr.getlftrel();
	if ((attr.getmask() & AttributeType.AFFINITY) == AttributeType.AFFINITY) 
	    retour += " affinity=" + attr.getaffinity();
	if ((attr.getmask() & AttributeType.OOB) == AttributeType.OOB) 
	    retour += " oob=" + attr.getoob();
	if ((attr.getmask() & AttributeType.DISTRIB) == AttributeType.DISTRIB) 
	    retour += " distrib=" + attr.getdistrib();
	return retour + " }";
    }


    public static String toMultilineString(Attribute attr) {
	String retour =  "attr " + attr.getname() + " =  { \n";
	boolean first = false;

	if ((attr.getmask() & AttributeType.REPLICAT) == AttributeType.REPLICAT) { 
	    retour += "\t replicat=" + attr.getreplicat() + "\n";
	}
	if ((attr.getmask() & AttributeType.LFTABS) == AttributeType.LFTABS) {
	    retour += "\t lftabs=" + attr.getlftabs()  + "\n";
	}
	if ((attr.getmask() & AttributeType.LFTREL) == AttributeType.LFTREL) {
	    retour += "\t lftrel=" + attr.getlftrel() + "\n";
	}
	if ((attr.getmask() & AttributeType.AFFINITY) == AttributeType.AFFINITY) {
	    retour += "\t affinity=" + attr.getaffinity() + "\n";
	}
	if ((attr.getmask() & AttributeType.FT) == AttributeType.FT) {
	    retour += "\t fault tolerant\n";
	}
	if ((attr.getmask() & AttributeType.OOB) == AttributeType.OOB) {
	    retour += "\t oob=" + attr.getoob() + "\n";
	}
	if ((attr.getmask() & AttributeType.DISTRIB) == AttributeType.DISTRIB) {
	    retour += "\t distrib=" + attr.getdistrib() + "\n";
	}
	return retour.substring(0,retour.length()-1) + " \t}";
    }


    public static Attribute parseAttribute(String  args) throws ActiveDataException,JsonSyntaxException {

	JsonObject jsono = new JsonParser().parse(args).getAsJsonObject();
	Attribute attr = new Attribute();

	if(jsono.get("name")!=null){
	    attr.setname(jsono.get("name").getAsString());

	}
	if(jsono.get("replicat")!=null){
	    AttributeType.setAttributeTypeOn(attr, AttributeType.REPLICAT);
	    attr.setreplicat(jsono.get("replicat").getAsInt());
	}
	if(jsono.get("affinity")!=null){
	    AttributeType.setAttributeTypeOn(attr, AttributeType.AFFINITY);
	    attr.setaffinity(jsono.get("affinity").getAsString());
	}
	if(jsono.get("lftabs")!=null){
	    AttributeType.setAttributeTypeOn(attr, AttributeType.LFTABS);
	    attr.setlftabs(jsono.get("lftabs").getAsLong());
	}
	if(jsono.get("oob")!=null){
	    AttributeType.setAttributeTypeOn(attr, AttributeType.OOB);
	    attr.setoob(jsono.get("oob").getAsString());
	}
	if(jsono.get("ft")!=null){
	    AttributeType.setAttributeTypeOn(attr, AttributeType.FT);
	    attr.setft(jsono.get("ft").getAsBoolean());
	}
	if(jsono.get("distrib")!=null){
	    AttributeType.setAttributeTypeOn(attr, AttributeType.DISTRIB);
	    attr.setdistrib(jsono.get("distrib").getAsInt());	
	}
	return attr;
    }
}
