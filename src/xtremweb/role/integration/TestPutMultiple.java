package xtremweb.role.integration;

import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

/**
 * This class launches an instance of a PutProcess, is it used in putstress.rb script
 * to perform put stress tests from multiple machines to a single one.
 * @author jose
 *
 */
public class TestPutMultiple {
    /**
     * Logger class
     */
    private static Logger log = LoggerFactory.getLogger("TestGetMultiple");
    
    /**
     * Starts the process
     * @param sn stable node
     * @param getfname file name to put
     */
    public void go(String sn,String getfname)
    {	String STABLE_NODE = sn;	
	PutProcess pp = new PutProcess(STABLE_NODE,getfname);
	pp.execute();
	log.info("finish main");
    }
    
    /**
     * Main method
     * @param args an array containing the following parameters : 
     * args[0] node where bitdew services are running
     * args[1] data uid that you want to download
     * args[2] Application-defined process-id 
     */
    public static void main(String[] args){
	log.setLevel("debug");
	new TestPutMultiple().go(args[0],args[1]);
    }

}
