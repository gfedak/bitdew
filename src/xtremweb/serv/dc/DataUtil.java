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
import xtremweb.core.obj.dc.Data;
import xtremweb.core.uid.*;

import java.security.*;
import java.io.*;
import java.nio.*;
import java.math.BigInteger;

/**
 *  <code>DataUtil</code> is a set ogf utilitary methods.
 *
 * @author <a href="mailto:fedak@fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class DataUtil {

    private static String digits = "0123456789ABCDEF";

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
	return "" + data.getuid() + ":" + data.getname() + ":" + data.getchecksum();
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
	    return hash.toString( 16 );
	    
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
     *  <code>fileToData</code> converts a File to a Sata
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
