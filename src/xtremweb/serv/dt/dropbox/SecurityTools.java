package xtremweb.serv.dt.dropbox;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class SecurityTools {
    
    private static String computeSignature(String baseString, String keyString) throws GeneralSecurityException, UnsupportedEncodingException {

	SecretKey secretKey = null;

	byte[] keyBytes = keyString.getBytes();
	secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

	Mac mac = Mac.getInstance("HmacSHA1");

	mac.init(secretKey);

	byte[] text = baseString.getBytes();

	return new String(Base64.encodeBase64(mac.doFinal(text))).trim();
	}


}
