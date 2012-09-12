package xtremweb.role.integration;

import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

/**
 * This class must be executed in a different java process each time it is called.
 * Its purpose is to put some stress in a remote Transfer Manager invoking the bitdew get command concurrently.
 * In order to understand more about this class, please read the stresstest.rb script in scripts folder.
 * @author josefrancisco
 *
 */
public class TestGetMultiple {
    
    /**
     * File name that a retrieved file will have on localhost, is the 'getfile' constant
     */
    private static String GETFNAME="getfile";
    /**
     * Logger class
     */
    private static Logger log = LoggerFactory.getLogger("TestGetMultiple");
    
    /**
     * Starts a bitdew get routine
     * @param sn stable node
     * @param uid the bitdew uid to get
     * @param tam the process number
     * @param getfname the file name
     */
    public void go(String sn,String uid,int tam,String getfname)
    {	String STABLE_NODE = sn;	
	GetProcess tg = new GetProcess(STABLE_NODE,uid,tam,getfname);
	tg.execute();
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
	new TestGetMultiple().go(args[0],args[1],Integer.parseInt(args[2]),GETFNAME);
    }

}
