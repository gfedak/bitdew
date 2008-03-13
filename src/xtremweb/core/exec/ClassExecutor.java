package xtremweb.core.exec;

/**
 * <code>ClassExecutor</code> allows to launch the execution of a Java 
 * Class in a separate VM
 *
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class ClassExecutor extends JavaByteCodeExecutor {
    
    /**
     * Creates a new <code>ClassExecutor</code> instance.
     *
     * @param execDir a <code>String</code> value
     * @param classPath a <code>String[]</code> value
     * @param propertiesOption a <code>String</code> value
     * @param jarName a <code>String</code> value
     * @param argv[] a <code>String</code> value
     * @exception ExecutorLaunchException if an error occurs
     */
    public ClassExecutor( String execDir,
			  String[] classPath, 
			  String propertiesOption, 
			  String jarName,   
			  String argv[] ) throws ExecutorLaunchException  {
	super( execDir
	       , (classPath==null ? null : Executor.join(classPath, ":"))
	       , propertiesOption
	       , jarName
	       , (argv == null ? null : Executor.join(argv, " "))
	       , true );
    }
    
    /**
     * Creates a new <code>ClassExecutor</code> instance.
     *
     * @param execDir a <code>String</code> value
     * @param classPath a <code>String</code> value
     * @param propertiesOption a <code>String</code> value
     * @param jarName a <code>String</code> value
     * @param argv a <code>String</code> value
     * @exception ExecutorLaunchException if an error occurs
     */
    public ClassExecutor( String execDir,
			  String classPath,  
			  String propertiesOption, 
			  String jarName, 
			  String argv) throws ExecutorLaunchException  {
	super(execDir, classPath, propertiesOption, jarName, argv, true);
    } // JarExecutor constructor
    
    public static void main(String[] args) {
	
    } // end of main()

}