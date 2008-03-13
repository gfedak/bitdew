package xtremweb.core.exec;
import java.io.File;

/**
 * <code>JarExecutor</code> allows to launch the execution of Java
 * application, provided as jar files, in a separate JVM
 *
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

public class JarExecutor extends JavaByteCodeExecutor {


    /**
     * Creates a new <code>JarExecutor</code> instance.
     *
     * @param jarName a <code>String</code> value
     * @exception ExecutorLaunchException if an error occurs
     */
    public JarExecutor (String jarName) throws ExecutorLaunchException {
	super(null, null, null, jarName, null, true );
    } // JarExecutor constructor

    /**
     * Creates a new <code>JarExecutor</code> instance.
     *
     * @param execDir a <code>String</code> value
     * @param classPath a <code>String[]</code> value
     * @param propertiesOption a <code>String</code> value
     * @param jarName a <code>String</code> value
     * @param argv[] a <code>String</code> value
     * @exception ExecutorLaunchException if an error occurs
     */
    public JarExecutor( String execDir,
			String[] classPath, 
			String propertiesOption, 
			String jarName,   
			String argv[])  throws ExecutorLaunchException  {
	super( execDir,
	       (classPath==null ? null : Executor.join(classPath, ":"))
	       , propertiesOption
	       , jarName
	       , (argv == null ? null : Executor.join(argv, " "))
	       , true );
    } // JarExecutor constructor
    

    /**
     * Creates a new <code>JarExecutor</code> instance.
     *
     * @param execDir a <code>String</code> value
     * @param classPath a <code>String</code> value
     * @param propertiesOption a <code>String</code> value
     * @param jarName a <code>String</code> value
     * @param argv a <code>String</code> value
     * @exception ExecutorLaunchException if an error occurs
     */
    public JarExecutor(String execDir,
		       String classPath,
		       String propertiesOption,
		       String jarName
		       , String argv) throws ExecutorLaunchException  {
	super(execDir, classPath, propertiesOption, jarName, argv, true);
    } // JarExecutor constructor

    /**
     * Describe <code>main</code> method here.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String[] args) {
	String jar = null;
	if (args.length == 1)
	    jar = args[0];
	else 
	    System.exit(0);

	System.out.println(jar);

	try {
	    JarExecutor e = new JarExecutor( jar ) ;
	    e.startAndWait();	     
	} catch (ExecutorLaunchException ele) {
	    System.out.println("Error when launching " + jar + " " + ele);
	} // end of try-catch
    } // end of main()
    
} // JarExecutor