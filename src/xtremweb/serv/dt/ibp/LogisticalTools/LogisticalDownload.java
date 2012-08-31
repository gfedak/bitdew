/* $Id$ */

package xtremweb.serv.dt.ibp.LogisticalTools;

import edu.utk.cs.loci.exnode.*;

/**
 * Logistical download
 * @author jsaray
 *
 */
public class LogisticalDownload {
	
	/**
	 * Version
	 */
    final static String VERSION = "0.01a"; 
    
    /**
     * Connections
     */
    final static int DFLT_CONNECTIONS  = 1;
    
    /**
     * Transfer size
     */
    int transferSize =	 -1; // useless
    
    /**
     * Exnode
     */
    public Exnode exnode = null;
    
    /**
     * Logistical download
     */
    public LogisticalDownload() {}

    /**
     * Download
     * @param xndfile
     * @param outputfilename
     * @param connections
     */
    public void download(String xndfile, String outputfilename, int connections) 
    {
        final String output = outputfilename;
        final int conn = connections;
        
        try {               
                exnode = Exnode.fromXML(xndfile);
                final long length = exnode.getLength();
                exnode.progress = new edu.utk.cs.loci.exnode.Progress(length);
                Thread t = new Thread( new Runnable() {
                    public void run() {
                        try {
                            exnode.read(output, 0, length, transferSize, conn);
                        } catch(Exception e) {
                            System.out.println("Unable to complete read operation: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
                while (exnode.getProgress()==null) {
                   try {
                        this.wait(1000);
                        System.out.println("Progress object not yet initailized... Please wait...");
                    } catch(Exception ignored) {
                    }
                }
            } catch(NullPointerException e) {
                e.printStackTrace();
                System.err.println("Please specify an input");
            } catch(Exception e) {
                System.out.println("Unable to complete read operation: " + e.getMessage());
                e.printStackTrace();
            }
    }

    /**
     * Usage
     */
    public static void usage() 
    {
	
	System.out.println("LogisticalDownload v." + VERSION + " (LoCI 2004)");
	System.out.println("usage: java LogisticalDownload [OPTION] XND-file");
	
	String textoptions = "" + 
	    "  -f\tSpecify the output filename by removing .xnd to\n" +
	    "    \tthe exnode filename.\n" +
	    "  -t\tSpecify the maximum number of threads to use to\n" +
	    "    \tperform Download.\n";
	System.out.println(textoptions);

    }
    
    /**
     * Main
     * @param args
     */
    public static void main(String[] args) {

	boolean sameoutput = false;
	int connections  = DFLT_CONNECTIONS;

        String outputfilename = "";
	String inputfilename = "";

	if(args.length == 0) {
	    usage();
	    System.exit(0);
	} 

	for(int i = 0; i < args.length; i++) {
	    String arg = args[i];
	    if((char)arg.charAt(0) == '-') {
		switch((char)arg.charAt(1)) {
		case 'f':
		    sameoutput=true;
		    break;
		case 'o':
		    outputfilename = args[i+1];
		    i++;
		    break;
		case 't':
		    connections = Integer.parseInt(args[i+1]);
		    i++;
		    break;
		case 'T':
		    i++;
		    break;
		}
	    } else {
		inputfilename=args[i];
	    }
	}
	
	if(sameoutput || outputfilename.equals("")) {
	    // delete extension (.xnd)
	    outputfilename = inputfilename.substring(0, inputfilename.length()-4); 
	}
	
	LogisticalDownload ld = new LogisticalDownload();	
	ld.download(inputfilename, outputfilename, connections);

    }
}
