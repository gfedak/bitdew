package xtremweb.role.bench;

/**
 * Describe class BenchMaster here.
 *
 *
 * Created: Fri Oct  6 22:29:35 2006
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */


import jargs.gnu.CmdLineParser;
import xtremweb.core.log.*;
import xtremweb.core.serv.*;
import xtremweb.core.conf.*;
import xtremweb.serv.bench.*;
import xtremweb.core.com.idl.*;

public class BenchMaster {

    /**
     * Creates a new <code>BenchMaster</code> instance.
     *
     */
    public BenchMaster() {

    }

    public static void main(String[] args) throws Exception{
	Logger log = Logger.getLogger(" ");
	CmdLineParser parser = new CmdLineParser();
	CmdLineParser.Option sizeOption = parser.addIntegerOption("size");
	CmdLineParser.Option roundsOption = parser.addIntegerOption("rounds");
	CmdLineParser.Option workersOption = parser.addIntegerOption("workers");
        try {
            parser.parse(args);
        }
        catch ( CmdLineParser.OptionException e ) {
            log.debug(e.getMessage());
	    //            usage();
        }

	int size = ((Integer) parser.getOptionValue(sizeOption,new Integer(0))).intValue();
	int workers = ((Integer) parser.getOptionValue(workersOption,new Integer(1))).intValue();
	int rounds = ((Integer) parser.getOptionValue(roundsOption,new Integer(1))).intValue();
	String[] modules = {"dc","dr","dt","ds","bench"};
	ServiceLoader sl = new ServiceLoader("RMI", 4322, modules);
	Callbackbench cbbench = (Callbackbench) ModuleLoader.getModule( "bench" );

	cbbench.configure(workers, size, rounds, "ddcbench");
    }

}
