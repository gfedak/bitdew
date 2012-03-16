package xtremweb.role.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

public class ServletHelper {

    public static String getDirectoryFiles(String directory)
	    throws IOException, NoSuchAlgorithmException {
	String answer = "<table><tr><td>File name</td><td>MD5</td></tr>";
	File dir = new File(directory);
	if (dir.exists()) {
	    File[] file = dir.listFiles();
	    for (int i = 0; i < file.length; i++) {
		String item = file[i].getName();
		byte[] b = new byte[(int) file[i].length()];
		answer += "<tr>";
		if (!file[i].isDirectory()) {
		    FileInputStream fis = new FileInputStream(file[i]);
		    fis.read(b);
		    MessageDigest md5dig;
		    md5dig = MessageDigest.getInstance("MD5");
		    byte[] bytes = md5dig.digest(b);
		    String hexw = new String(Hex.encodeHex(bytes));
		    answer += "<td>" + item + "</td><td>" + hexw + "</td>";
		}
		answer += "</tr>";
	    }
	    answer += "</table>";
	} else {
	    answer = ("<p>That directory do not exist in your file system !!</p>");
	}
	return answer;
    }

}
