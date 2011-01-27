package xtremweb.serv.dc;

/**
 * DataUtil.java
 *
 *
 * Created: Tue Mar  7 17:28:06 2006
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
import xtremweb.api.bitdew.BitDew;
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.uid.*;

import java.security.*;
import java.io.*;
import java.nio.*;
import java.math.BigInteger;

/**
 *  <code>DataUtil</code> is a set ogf utilitary methods.
 *
 * @author <a href="mailto:Gilles.Fedak@inria.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class DataUtil {

    private static String digits = "0123456789ABCDEF";
    private static Logger log = LoggerFactory.getLogger(DataUtil.class);
    /**
     * Creates a new <code>DataUtil</code> instance.
     *
     */
    public DataUtil() {
	
    } // DataUtil constructor
    
    /**
     *  <code>toString</code>.
     *
     * @param data a <code>Data</code> value
     * @return a <code>String</code> value
     */
    public static String toString(Data data) {
	return "data " + data.getname() + " [" + data.getuid() + "] = { " + "md5=" + data.getchecksum() + " size=" + data.getsize() + " }";
    }

    /**
     *  <code>checksum</code> conputes the MD5 digest of a file.
     *
     * @param f a <code>File</code> value
     * @return a <code>String</code> value
     */
    public static String checksum(File f) {


	long fileSize = f.length();
	byte buffer[] = new byte[1024];
	try {
	    FileInputStream fis = new FileInputStream (f);
	    MessageDigest messageDigest = MessageDigest.getInstance("MD5");

	    int read;
	    while ((read = fis.read(buffer))!=-1 ) {
		messageDigest.update(buffer,0,read);
	    }
	    fis.close();

	    BigInteger hash = new BigInteger( 1, messageDigest.digest() );
	    String nopad = hash.toString(16);
	    log.debug("before padding " + nopad);
	    if(nopad.length()!=32)//some leftside digits are 0 and the biginteger conversion is skipping them
	    {	while(nopad.length()!=32)//padd some 0's
	    	{    nopad = "0" + nopad;
	    	}
	    }
	    String pad = nopad;
	    log.debug(" no wrong number " + pad);
	    return pad;
	    
	} catch (FileNotFoundException fnfe) {
	    System.out.println("No such file" + fnfe);
	}catch (NoSuchAlgorithmException nsae) {
	    System.out.println("Wrong cheksum algo" + nsae);
	}catch (IOException ioe) {
	    System.out.println("Error reading file" + ioe);
	}
	return "CHKSUM";
    }

    public static String toHex(byte[] data , int length) {
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<length; i++) {
            int v = data[i] & 0xff;
            buf.append(digits.charAt(v>>4));
            buf.append(digits.charAt(v & 0xf));
        }
        return buf.toString();
    }

    public static String toHex(byte[] data) {
        return toHex(data, data.length);
    }
    
    /**
     *  <code>fileToData</code> converts a File to a Data
     *
     * @param file a <code>File</code> value
     * @return a <code>Data</code> value
     */
    public static Data fileToData(File file) {
	Data data = new Data();
	data.setname(file.getName());
	data.setsize(file.length());
	data.setchecksum(checksum(file));
	data.settype(0);
	return data;
    }

    
    /**
     * <code>main</code> tests the class (the checksum)
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String [] args) {
	System.out.println("checksum " + args[0]+ " " + checksum(new File(args[0])));

    }
} // DataUtil
