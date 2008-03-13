package xtremweb.core.exec;

/**
 * <code>Executor</code>
 * Launch and execute process
 *
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.lang.Runtime;
import xtremweb.core.log.*;

public class Executor {

    /**
     * This is the command line
     * This includes the process command line and its options
     */
    protected String commandLine=null;
    /**
     * This is the process execution directory
     */
    protected String executionDirectory=null;

    /** This is the process standard input */
    protected InputStream stdin=null;
    /** This is the process standard output */
    protected OutputStream stdout=null;
    /** This is the process standard error */
    protected OutputStream stderr=null;

    /** This is the managed process */
    private Process process=null;

    /** This is the execution Runtime*/
    private Runtime machine=Runtime.getRuntime();

    /** This stores the process running flag */
    private boolean isRunning=false;

    private static  final int BLOCK_SIZE = 65500; // bytes

    /** This is the timer period */
    private static  final int SLEEP_UNIT = 50;   // msec

    /**
     * This is a timer; it helps to periodically check process status 
     * and to flush I/O
     */
    private Timer timer = new Timer();


    /** This is the debug logger */
    protected static  Logger log = LoggerFactory.getLogger(Executor.class);

    /**
     * This constructor sets I/O to null
     */
    public Executor() {
    }

    /**
     * This constructor sets execution directory and I/O to null
     * @param cmd is the command to execute
     * @see executionDirectory
     * @see stdin
     * @see stdout
     * @see stderr
     */
    public Executor(String cmd) {
	this( cmd, null);

    } // Executor constructor
    

    /**
     * This constructor sets I/O to the standard input and output
     * @param cmd is the command to execute
     * @param dir is the execution directory 
     * @see executionDirectory
     * @see stdin
     * @see stdout
     * @see stderr
     */
    public Executor(String cmd, String dir) {
	this(cmd, dir, System.in, (OutputStream) System.out, (OutputStream) System.err );
    }
    

    /**
     * This constructor sets I/O to null
     * @param cmd is the command to execute
     * @param dir is the execution directory 
     * @param in is the process input
     * @param out is the process output
     * @param err is the process output error
     * @see executionDirectory
     * @see stdin
     * @see stdout
     * @see stderr
     */
    public Executor(String cmd, String dir, InputStream in,
		    OutputStream out, OutputStream err) {  
	commandLine = cmd;
	executionDirectory = dir;
	stdin = in;
	stdout = out;
	stderr = err;
    }

    /**
     * This retreives an attribute (as the name says)
     * @return the expected attribute
     */
    public String getCmdLine() {
	return commandLine;
    }

    /**
     * This retreives an attribute (as the name says)
     * @return the expected attribute
     */
    public String getExecDir() {
	return executionDirectory;
    }


    /**
     * This sets an attribute (as the name says)
     * @param v is the new attribute value
     */
    public void setCmdLine(String v) {
	commandLine = v;
    }


    /**
     * This sets an attribute (as the name says)
     * @param v is the new attribute value
     */
    public void setExecDir(String v) {
	executionDirectory = v;
    }


    /**
     * <code>setStdin</code> set stdin
     *
     * @param in an <code>InputStream</code> value
     */
    public void setStdin(InputStream in) {
	stdin=in;
    }

    /**
     *  <code>getStdin</code>get stdin
     *
     * @return an <code>InputStream</code> value
     */
    public InputStream getStdin() {
	return stdin;
    }


    /**
     *  <code>setStdout</code> set stdout
     *
     * @param out an <code>OutputStream</code> value
     */
    public void setStdout(OutputStream out) {
	stdout=out;
    }

    /**
     *  <code>getStdout</code>  get stdout
     *
     * @return an <code>OutputStream</code> value
     */
    public OutputStream getStdout() {
	return stdout;
    }


    /**
     *  <code>setStderr</code> set stderr
     *
     * @param err an <code>OutputStream</code> value
     */
    public void setStderr(OutputStream err) {
	stderr=err;
    }

    /**
     *  <code>getStderr</code>get stderr
     *
     * @return an <code>OutputStream</code> value
     */
    public OutputStream getStderr() {
	return stderr;
    }

    /**
     * This stops the process, if any
     * @exception  ExecutorLaunchException is thrown on error (no running process)
     */
    public void stop () throws ExecutorLaunchException {

	log.debug("stop process");

	try {
	    process.destroy ();
	    isRunning = false;	    

	}
	catch (Throwable e) {
	    log.debug("Can't stop process : " + e);
	    throw new ExecutorLaunchException();
	}
    }


    /**
     * This starts the process using Runtime.exec ()
     * It instanciates a timer to check process state and periodically flush the I/O
     * @exception ExecutorLaunchException if an error occurs
     * @see timer
     * @see flushPipe ()
     */
    public void start() throws ExecutorLaunchException {

	log.debug("start process : " + commandLine + 
		  (executionDirectory!=null?(" in directory : " + executionDirectory):""));

	try {
	    if ( executionDirectory == null) {
		process = machine.exec(commandLine);
	    }
	    else {
		process = machine.exec(commandLine, null, new File(executionDirectory));
	    }

	    isRunning = true;	    
	}
	catch (IOException e) {
	    log.debug("Can't start process : " + commandLine + " " +e);
	    throw new ExecutorLaunchException();
	}

	timer.scheduleAtFixedRate( new TimerTask() {
		public void run() {
		    flushPipe();
		}
	    },  0,      // run now
	    SLEEP_UNIT);
    }


    /**
     * This tells whether the process is running or not
     * @return a <code>boolean</code> value
     */
    public boolean isRunning() {
	return isRunning;
    }


    /**
     * This checks for process terminason and flushes the I/O
     * @see pipe (InputStream, OutputStream, boolean)
     */
    public void flushPipe() {
	int returnCode;
	try {		  
	    returnCode = process.exitValue();
	    log.debug("process still running");
	    isRunning = false;
	    timer.cancel ();
	}
	catch (IllegalThreadStateException ie) {
	}
	
	try {
	    pipe (stdin, process.getOutputStream(), false);
	    pipe (process.getInputStream(), stdout, false);
	    pipe (process.getErrorStream(), stderr, false);
	}
	catch(Exception e) {
	}
    }


    /**
     * This waits until the process exits
     * @return the process return code
     */
    public int waitFor() {
	int returnCode = 0;
	
	for (isRunning = true; isRunning;) {
	    try {
		Thread.sleep(SLEEP_UNIT);
	    } catch (InterruptedException e) {
		
	    } // end of try-catch
	      
	    try {		  
		returnCode = process.exitValue();
		isRunning = false;
	    } catch (IllegalThreadStateException ie) {}
	}
	//	  System.out.println("done" + returnCode);
	//flush
	flushPipe();
	timer.cancel();
	return(returnCode);	  
    }


    /**
     * This start the process and wait until the process exits
     * @return the process exit code
     * @exception ExecutorLaunchException if an error occurs
     * @see start ()
     * @see waitFor ()
     */
    public int startAndWait() throws ExecutorLaunchException {
	start();
	return waitFor();
    }

    public void writeMessage(String message) {
	
	PrintWriter pw = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));
	pw.println(message);
	pw.flush();	
    }


    /**
     * This pipes from one stream to other
     * @param in is the input stream to read from; if null this method returns immediatly
     * @param out is output stream to write to; if null nothing is written
     * @param isBlocking tells whether I/O are blocking or not
     */
    private void pipe(InputStream in, OutputStream out, boolean isBlocking) throws IOException {
	int nread;
	int navailable;
	int total = 0;

	if (in == null)
	    return;

	synchronized (in) {

	    byte[] buf = new byte[BLOCK_SIZE];

	    while((navailable = isBlocking ? Integer.MAX_VALUE : in.available()) > 0 &&
		  (nread = in.read(buf, 0, Math.min(buf.length, navailable))) >= 0) {

		if (out != null)
		    out.write(buf, 0, nread);
		total += nread;
	    }

	}

	if (out != null)
	    out.flush();
    }

    /**
     * Describe <code>join</code> method here.
     *
     * @param tab[] a <code>String</code> value
     * @param sep a <code>String</code> value
     * @return a <code>String</code> value
     */
    protected static String join (String tab[], String sep) {
	String result = "";
	if ( tab==null) {
	    return null;
	} // end of if ()	
	if ( tab.length ==0 ) {
	    return "";
	} // end of if ()	
	for ( int i=0; i< tab.length - 1; i++) {
	    result += (tab[i] + sep);
	} // end of for ()
	return result + tab[tab.length-1];
    }

    /**
     * This is the standard main () method
     * This is only for test purposes
     * @param args a <code>String[]</code> value
     */
    public static void main(String[] args) {
	String execString;
	if (args.length == 0)
	    execString = "/usr/bin/telnet localhost 8649";
	else 
	    execString = Executor.join(args, " ");

	System.out.println(execString);

	Executor e = new Executor( execString ) ;

	try {
	    e.startAndWait();	     
	} catch (ExecutorLaunchException ele) {
	    System.out.println("Error when launching " + execString + " " + ele);
	} // end of try-catch
	

    } // end of main()

    

} // Executor
