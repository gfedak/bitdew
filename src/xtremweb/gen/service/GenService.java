package xtremweb.gen.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;

public class GenService {

    private static Logger logger;

    public static void main(String[] args) {
	try {
	    logger = Logger.getLogger(GenService.class);
	    if (args.length == 0) {
		usage();
		return;
	    }
	    String service = args[0];
	    System.setProperty("servname", service);
	    System.out.println("user dir " + System.getProperty("user.dir"));
	    File dir = new File(System.getProperty("user.dir")
		    + "/src/xtremweb/serv/" + service + "/");
	    if (!dir.exists())
		dir.mkdir();
	    else {
		logger.error("You already define that service ");
		return;
	    }
	    System.out.println("current " + dir.getAbsolutePath());
	    File nucleus = new File(dir.getAbsolutePath() + "/Callback"
		    + service + ".java");

	    BufferedWriter bw;

	    bw = new BufferedWriter(new FileWriter(nucleus));
	    bw.write("package xtremweb.serv." + service + ";\n");
	    bw.write("import xtremweb.core.com.idl.CallbackTemplate;\n");
	    bw.write("import xtremweb.core.iface.InterfaceRMI"+service+";\n");
	    bw.write("import java.rmi.RemoteException;\n");
	    bw.write("\n");
	    bw.write("\n");
	    bw.write("public class Callback" + service
		    + " extends CallbackTemplate implements InterfaceRMI"
		    + service + " {\n");
	    bw.write("}\n");
	    bw.flush();
	    bw.close();
	    logger.info("class xtremweb.serv." + service + ".Callback"
		    + service + " succesfully Generated");
	    File idl = new File(dir.getAbsolutePath() + "/" + service + ".idl");
	    bw = new BufferedWriter(new FileWriter(idl));
	    bw.write("<Module name=\"" + service + "\">\n");
	 
	    bw.write("</Module>");
	    bw.flush();
	    bw.close();
	    logger.info("File " + service
		    + ".idl succesfully generated in xtremweb.serv." + service);
	    File jdo = new File(dir.getAbsolutePath() + "/package.jdo");
	    bw = new BufferedWriter(new FileWriter(jdo));
	    bw.write("<?xml version=\"1.0\"?>\n");
	    bw.write("<jdo>\n");
	    bw.write("<package name=\"xtremweb.core.obj." + service + "\">\n");
	    bw.write("</package>\n");
	    bw.write("</jdo>\n");
	    bw.flush();
	    bw.close();
	    logger
		    .info("File package.jdo succesfully generated in xtremweb.serv."
			    + service);
	} catch (IOException e) {
	    System.out.println("The following error has occurred : ");
	    e.printStackTrace();
	}

    }
    
    
    

    public static void usage() {
	System.out
		.println("Usage : xtremweb.gen.service.GenService <servicename>");
    }

}