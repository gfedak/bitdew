/*
BitDew::get(String, File)                   BitDewURI String
BitDew::get(BitDewURI, File)           BitDewURI Object

worker(client) program
make sure bitdew-stand-alone.jar (version 0.2.2) included in the classpath

Server/Master: generate a BitDewURI object, host+uid
from a String, such as 
bitdew://sagittaire-1.lyon.grid5000.fr/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

from BitDewURI string/object, parse BitDewURI get uid, then get Data object directly

*/
package xtremweb.role.examples;

import java.io.*;
import xtremweb.api.bitdew.*;
import xtremweb.api.transman.*;
import xtremweb.core.util.uri.*;
import xtremweb.core.log.*;
import xtremweb.core.com.idl.ComWorld;
import java.util.Vector;

public class BitDewURIExample{

    private BitDew bitdew;
    private TransferManager transferManager;

    Logger log = LoggerFactory.getLogger("BitDewURI get");

    public BitDewURIExample(BitDewURI uri) throws Exception{
	String host = uri.getHost();
	int port = 4325;
	Vector comms = ComWorld.getMultipleComms(host, "rmi", port, "dc", "dr", "dt", "ds");

	//now intialize the APIs
	bitdew = new BitDew(comms);
	//	transferManager = new TransferManager(comms);
	//	transferManager.start();
	transferManager = TransferManagerFactory.getTransferManager(comms);
	
    }
    public void getFromURI(BitDewURI uri, String fileName) throws Exception{	
	File file = new File(fileName);

	//retreive the data object
	//copy the remote data into the local file
	bitdew.get(uri, file);

	//wait for the data transfer to complete and stop the transfer manager
	transferManager.waitFor(uri);	
	transferManager.stop();
	log.info("data has been successfully copied to " + fileName);
    }

    public static void main(String args[]) throws Exception{
	
	//Create  BitDewURI object:  host+ uid    from server???
	///////////////////////
	System.out.println("Hello World");
	BitDewURI a = new BitDewURI("gdx-1.lyon.grid5000.fr", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
	System.out.println(a.toString());
	
	//Generate BitDewURI object:    string
	BitDewURI b = new BitDewURI("bitdew://sagittaire-1.lyon.grid5000.fr/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
	System.out.println(b.getScheme());
	System.out.println(b.getHost());
	System.out.println(b.getUid());
	//////////////////////////////////////////////

	BitDewURIExample exam = new BitDewURIExample(a);
	String fileName = "/home/btang/test.gz";
	exam.getFromURI(a, fileName);

    }
	
    
}