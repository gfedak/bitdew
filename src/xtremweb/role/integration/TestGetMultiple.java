package xtremweb.role.integration;

import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

public class TestGetMultiple {
    
    private static Logger log = LoggerFactory.getLogger("TestGetMultiple");
    
    public void go(String sn,String uid,int tam)
    {	String STABLE_NODE = sn;
	//for ( int i =0; i < tam;i++)
	//{
	    ThreadGet tg = new ThreadGet(STABLE_NODE,uid,tam);
	    tg.execute();
	//}
	log.info("finish main");
    }
    
    public static void main(String[] args){
	log.setLevel("debug");
	new TestGetMultiple().go(args[0],args[1],Integer.parseInt(args[2]));
    }

}
