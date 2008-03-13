package xtremweb.core.exec;
import java.io.File;

/**
 * <code>JavaByteCodeExecutor</code> is a class which permits to
 * launch Java appplication in a separate JVM
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */



public class JavaByteCodeExecutor extends Executor{

    protected String jarNameOrClassName;
    protected String args;
    protected boolean isJar;
    protected String jvm;
    protected String classPath;
    protected String propertiesOption;
    
    /**
     * Creates a new <code>JavaByteCodeExecutor</code> instance.
     *
     * @param cP a <code>String</code> value
     * @param pO a <code>String</code> value
     * @param jNoC a <code>String</code> value
     * @param argv a <code>String</code> value
     * @param iJ a <code>boolean</code> value
     * @exception ExecutorLaunchException if an error occurs
     */
    protected JavaByteCodeExecutor( String eD,
				    String cP, 
				    String pO, 
				    String jNoC, 
				    String argv, 
				    boolean iJ)  throws ExecutorLaunchException {
	executionDirectory = eD;
	jarNameOrClassName = jNoC;
	classPath = cP;
	propertiesOption = pO;
	args = argv;
	isJar = iJ;
	setJVM();
	setCmdLine();
    } // JavaByteCodeExecutor constructor

    private void setJVM() throws ExecutorLaunchException {
	try {
	    jvm = System.getProperty("java.home") + File.separator +
		"bin" + File.separator + "java";
	} catch ( Exception e) {
	    log.debug("Can't acces to property java.home");
	    throw new ExecutorLaunchException();
	} // end of try-catch	
    }

    private void setCmdLine() {
	//set the vm
	commandLine = jvm;

	//set the classpath
	if ( classPath != null) 
	    commandLine += " -classpath " + classPath + " "; 

	//set the properties
	if ( propertiesOption != null) 
	    commandLine += " " + propertiesOption + " "; 


	// set hte jar switch jar
	if ( isJar ) 
	    commandLine += " -jar " ;

	//set the class or jar name
	commandLine += jarNameOrClassName;

	//set the args 
	if ( args != null ) 
	    commandLine += " " + args + " ";
    }

} // JavaByteCodeExecutor
