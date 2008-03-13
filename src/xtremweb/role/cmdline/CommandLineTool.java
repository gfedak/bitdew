package xtremweb.role.cmdline;
/**
 * CommandLineTool.java
 *
 *
 * Created: Fri Apr 14 10:35:22 2006
 *
 * @author <a href="mailto:">Gilles Fedak</a>
 * @version 1.0
 */
import xtremweb.api.bitdew.*;
import xtremweb.api.activedata.*;
import xtremweb.api.transman.*;
import xtremweb.serv.dc.*;
import xtremweb.serv.ds.*;
import xtremweb.core.iface.*;
import xtremweb.core.log.*;
import xtremweb.core.com.com.*;
import xtremweb.core.com.idl.*;
import xtremweb.core.serv.*;
import xtremweb.role.ui.*;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.ds.Attribute;
import xtremweb.serv.ds.AttributeType;
import xtremweb.core.log.*;
import xtremweb.core.http.*;
import java.io.File;
import jargs.gnu.CmdLineParser;
import java.util.*;


public class CommandLineTool {

    private BitDew bitdew;
    private ActiveData activeData;
    private TransferManager transferManager = null;
    private enum HelpFormat {SHORT, LONG};

    private String host;
    private String attruid;
    private String fileName;
    private String dirName;
    private int port;
    private boolean server = false;
    private static Logger log = LoggerFactory.getLogger("CommandLineTool");

    public CommandLineTool(String[] args) {

	if (log instanceof Log4JLogger) {
	    try {
	    Log4JLogger.setProperties("conf/log4jcmdlinetool.properties");
	    } catch (LoggerException le) {
		log.debug(le.toString());
	    }
	}

	String[] otherArgs = parse(args);

	//if there's no other argument display helps
	if (otherArgs.length==0) 
	    usage(HelpFormat.SHORT); 

	try {
	    //start services
	    if (otherArgs[0].equals("serv")) {
		Vector services = processServices(otherArgs);
		ServiceLoader sl = new ServiceLoader("RMI", port, services);
		UIFactory.createUIFactory();
		server = true;
		return;
	    } else {
		Vector comms = ComWorld.getMultipleComms(host, "rmi", port, "dc", "dr", "dt", "ds");
		
	    }

	    //create attr
	    if (otherArgs[0].equals("attr")) {
		Attribute attr = AttributeUtil.parseAttribute(otherArgs);
		attr = processAttr(attr);
	    }
	    
	    //create data
	    if (otherArgs[0].equals("data")) {
		log.debug("data " + otherArgs[1] + " " + otherArgs[2]);
		//		cmdline.processData(otherArgs[1], otherArgs[2]);
	    }
	} catch(ModuleLoaderException e) {
	    log.warn("Cannot find service " +e);
	} catch (BitDewException bde) {
	    log.warn(" cmdline error  : " + bde);
	    System.exit(0);
	} catch (Exception e) {
	    log.warn(" cmdline error   : " + e);
	    System.exit(0);
	}	
    } // CommandLineTool constructor
    
    public Vector processServices(String[] serv) {
	Vector v = new Vector();
	for (String s: serv) {
	    if (s.equals("dc") ||
		s.equals("ds") ||
		s.equals("dr") ||
		s.equals("dt"))
		v.add(s);
	}
	return v;
    }

    private void processData() throws BitDewException{
	Data data = bitdew.createData();
    }

    private Attribute processAttr(Attribute attr)  throws BitDewException, ActiveDataException {
	Attribute _attr = activeData.registerAttribute(attr);
	log.debug(AttributeUtil.toString(_attr));
	return _attr;
    }

    private void processData(String fileName, Attribute attr) throws BitDewException, ActiveDataException {
	File file = new File(fileName);
	Data data = bitdew.createData(file);
	activeData.schedule(data, attr);
    }

    private String[] parse(String[] args) {
	
	//if there's no argument display helps
	if (args.length==0) 
	    usage(HelpFormat.SHORT); 

	CmdLineParser parser = new CmdLineParser();

	CmdLineParser.Option helpOption = parser.addBooleanOption('h',"help");
	CmdLineParser.Option dirOption = parser.addStringOption('d',"dir");
	CmdLineParser.Option hostOption = parser.addStringOption("host");
	CmdLineParser.Option fileOption = parser.addStringOption("file");
	CmdLineParser.Option attrOption = parser.addStringOption("attr");

	try {
            parser.parse(args);
        }
        catch ( CmdLineParser.OptionException e ) {
            log.debug(e.getMessage());
            usage(HelpFormat.SHORT);
        }

	host = (String) parser.getOptionValue(hostOption,"localhost");
	attruid = (String) parser.getOptionValue(attrOption);
	fileName = (String) parser.getOptionValue(fileOption);
	dirName = (String) parser.getOptionValue(dirOption, ".");	
	boolean help = ((Boolean)parser.getOptionValue(helpOption, Boolean.FALSE)).booleanValue();

 	//if the help option is set display long help
	if (help) 
	    usage(HelpFormat.LONG); 

	return parser.getRemainingArgs();
    }

    public void usage(HelpFormat format) {
	Usage usage = new Usage();
	switch (format) {
	case LONG:
	    usage.usage("java -jar bitdew-stand-alone.jar [Options] Commands [Command Options]");
	    usage.option("","" );
	    usage.option("Options","" );
	    usage.option("-h, --help","display this helps" );
	    usage.option("-d, --dir","working directory" );
	    usage.option("--host","service hostname" );
	    usage.option("--port","service port" );
	    usage.option("","" );
	    usage.option("Commands","" );
	    usage.option("serv [dc|dr|dt|ds]","start the list of services separated by a space");
	    usage.option("attr ","create attribute");
	    usage.option("data","create data");
	    usage.option("","" );
	    usage.option("--file filename","file name to be created" );
	    usage.option("--attr attruid","attribute uid associated to the data" );
	    usage.option("setattr","set attribute to data");
	    break;
	case SHORT:
	    usage.usage("try java -jar bitdew-stand-alone.jar [-h, --help] for more information");
	    break;
	}
	System.exit(2);
    }

    public static void main(String[] args){
	CommandLineTool cmd = new CommandLineTool(args);
    }
} // CommandLineTool
