package xtremweb.core.log;

/**
 * Describe class Version here.
 *
 *
 * Created: Fri Mar 28 12:36:30 2008
 *
 * @author <a href="mailto:fedak@xtremciel.local">Gilles Fedak</a>
 * @version 1.0
 */
public class Version {

    public final static int major = 0;
    public final static int minor = 1;
    public final static int revision = 0;
    public final static String branch = "";

    public static String versionToString() {
	return "" + major + "." + minor + "." + revision + ((branch.equals(""))?"":("_"+branch));
    }

}
