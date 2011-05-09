package xtremweb.role.cmdline.test;

import java.io.File;
import java.util.Vector;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.role.cmdline.CommandLineTool;

public class CommandLineToolTest {
    
    private static BitDew  bd;
    private Vector comms;
    
    public CommandLineToolTest()
    {	
	try {
	    comms = ComWorld.getMultipleComms("localhost", "rmi", 4325 , "dr","dt","ds","dc");
	} catch (ModuleLoaderException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	bd = new BitDew(comms);
    }
    
    
    public static void main(String[] args)
    {	try {
	Vector comms = ComWorld.getMultipleComms("localhost", "rmi", 4325 , "dr","dt","ds","dc");
    	BitDew bd = new BitDew(comms);
	String[] cmdargsone = {"add","{repository:{login:\"jsaray\",name:\"ftp\",server:\"perso.ens-lyon.fr\",port: 21,passwd:\"yes\",path:\"/testing\"}}"};
	CommandLineTool cmdlinetool = new CommandLineTool(cmdargsone);

	//String[] cmdargstwo = {"add","{repository:{login:\"anonymous\",path: \"/pub/linux/distributions/slackware/slackware-current\",port:21, name:\"ftp\",server:\"ftp.lip6.fr\",passwd:\"no\"}}"};
	//cmdlinetool.add(cmdargstwo);
	String[] cmdargsthree = {"add","{repository:{name: \"scp\",login:\"jsaray\",server:\"access.lyon.grid5000.fr\",prkeypath:\"/home/jsaray/.ssh/id_dsa\",knownhosts:\"/home/jsaray/.ssh/known_hosts/\",passphrase:\"yes\",port: 22,path:\"/home/jsaray\"}}"};
	cmdlinetool.add(cmdargsthree);
	
	Protocol p = null;
	
	p = bd.getProtocolByName("ftp");
	System.out.println(" Protocol name " + p.getname() + " protocol host " + p.getserver() +" login "+p.getlogin() + " port "+ p.getport() +" path " +p.getpath() + " using passwd " +p.getpassword());
	} catch (BitDewException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ModuleLoaderException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	
	
    }

}
