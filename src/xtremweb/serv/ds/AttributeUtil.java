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
import xtremweb.api.activedata.ActiveDataException;

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
	return retour.substring(0,retour.length()-1) + " \t}";
    }


    public static Attribute parseAttribute(String  args) throws ActiveDataException {
	return parseAttribute(args.split(" "));
    }


    //form attr name = {replicat = truc, oob=bittorrent  }
    public static Attribute parseAttribute(String [] args) throws ActiveDataException {

	Attribute attr = new Attribute();

	String tmp = "";
	for (int i=1; i<(args.length); i++) 
	    tmp+=args[i];

	//set attribute name
	String name = tmp.substring(0,tmp.indexOf("={"));
	attr.setname(name);

	tmp=tmp.substring(tmp.indexOf("={")+2,tmp.length());
	if (tmp.endsWith("}")) 
	    tmp=tmp.substring(0,tmp.length()-1);

	try {
	    String[] pairs = tmp.split(",");
	    
	    for (int i=0; i<pairs.length; i++) {
		String[] attrValue = pairs[i].split("=");
		if (attrValue.length!=2)
		    throw new ActiveDataException("bad syntax, see help");

		if (attrValue[0].equals("replicat")) {
		    AttributeType.setAttributeTypeOn(attr, AttributeType.REPLICAT);
		    attr.setreplicat(Integer.parseInt(attrValue[1]));
		} else if (attrValue[0].equals("ft")) {
		    AttributeType.setAttributeTypeOn(attr, AttributeType.FT);
		    attr.setft((attrValue[1]).equals("true"));
		} else if (attrValue[0].equals("lftabs")) {
		    AttributeType.setAttributeTypeOn(attr, AttributeType.LFTABS);		    
		    long date = System.currentTimeMillis() + (Integer.parseInt(attrValue[1])*60000);
		    attr.setlftabs(date);
		} else if (attrValue[0].equals("lftrel")) {
		    AttributeType.setAttributeTypeOn(attr, AttributeType.LFTREL);
		    attr.setlftrel(attrValue[1]);
		} else if (attrValue[0].equals("affinity")) {
		    AttributeType.setAttributeTypeOn(attr, AttributeType.AFFINITY);
		    attr.setaffinity(attrValue[1]);
		} else if (attrValue[0].equals("oob")) {
		    AttributeType.setAttributeTypeOn(attr, AttributeType.OOB);
		    attr.setoob(attrValue[1]);
		}
	    }
	} catch (Exception e) {
	    log.debug("error reading attributes" + e);
	    throw new ActiveDataException("bad syntax cannot parse attribute");
	}
	return attr;
    } 

}
