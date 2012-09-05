package xtremweb.role.examples.akratos.util;

import xtremweb.core.obj.dc.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.jdom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xtremweb.role.examples.akratos.Akratos;

/**
 * A set of utilities to support Akratos.java
 * @author jsaray
 *
 */
public class AkratosUtil {
	
	/**
	 * Unmarshall a XMl file by using JAXB api
	 * @param clazz the class to map the XML file
	 * @param FILE_NAME the XML file name
	 * @return a new instance of the unmarshalled object
	 * @throws JAXBException if anything wrong happens manipulating the file with JAXB
	 * @throws FileNotFoundException if FILE_NAME do not exist
	 */
    public static Object unmarshall(Class clazz, String FILE_NAME) throws JAXBException, FileNotFoundException {
	JAXBContext context = JAXBContext.newInstance(clazz);
	;
	Unmarshaller um = context.createUnmarshaller();
	if (FILE_NAME == null || FILE_NAME.equals(""))
	    return um.unmarshal(new FileReader(Akratos.PUBLICINFO_XML));
	else
	    return um.unmarshal(new FileReader(FILE_NAME));
    }
    
    /**
     * This method reduces the input data list to only unique values according to the checksum
     * @param datas the data list with maybe multiple datas with the same checksum
     * @return a Data list with unique md5 files
     */
    public static List<Data> getUniques(List<Data> datas) {
	Hashtable<String,Data> ht = new Hashtable<String,Data>();
	List l = new ArrayList<Data>();
	for (int i = 0; i < datas.size(); i++) {
	    Data d = datas.get(i);
	    String key = d.getchecksum();
	    if (ht.get(key) == null) {
		ht.put(key, d);
		l.add(d);
	    }
	}
	return l;
    }
    
    /**
     * Transform a data list into a hashtable with key the data uid
     * @param datas the data list
     * @return a hashtable representation of the data list
     */
    public static Hashtable getDataHashTable(List<Data> datas) {
	Hashtable ht = new Hashtable();
	for (int i = 0; i < datas.size(); i++) {
	    Data d = datas.get(i);
	    ht.put(d.getuid(), d);
	}
	return ht;
    }
    
    /**
     * Transform a object into its XML representation using JAXB
     * @param clazz the object class
     * @param obj the object to serialize
     * @param filename the XML filename
     * @throws JAXBException if anything goes wrong parsing with JAXB.
     */
    public static void marshall(Class clazz, Object obj, String filename) throws JAXBException {
	JAXBContext context = JAXBContext.newInstance(clazz);
	Marshaller m = context.createMarshaller();
	m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	m.marshal(obj, System.out);
	Writer w = null;
	try {
	    if (filename == null || filename.equals(""))
		w = new FileWriter(Akratos.PUBLICINFO_XML);
	    else
		w = new FileWriter(filename);
	    m.marshal(obj, w);
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		w.close();
	    } catch (Exception e) {
	    }
	}
    }

    /**
     * 
     * 
     * This method encrypts a given file using AES, then it encrypts the AES key
     * with RSA
     * 
     * Warning: a conflict (JAR HELL) should happen because of the apache codecs
     * @param contact_uid
     *            this will be the encrypted file name to distinguish the
     *            message recipient
     * @param public_key
     *            contact public key
     * @param file
     *            info to cipher, your personal information
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    public static void cipherAndWriteFile(String contact_uid, String public_key, InputStream file)
	    throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
	    IllegalBlockSizeException, BadPaddingException, IOException {
	// This outputstream writes the AES key encrypted with RSA
	FileOutputStream aeskey_ciphered_rsa = new FileOutputStream(
		new File(contact_uid + Akratos.AES_CIPHERED_POSTFIX));
	// This outputstream writes the file encrypted with AES
	FileOutputStream plaintext_ciphered_aes = new FileOutputStream(new File(contact_uid));

	String pukey = public_key;

	// Generate a 128 bit AES key and convert to byte array
	KeyGenerator kg = KeyGenerator.getInstance("AES");
	SecretKey skey = kg.generateKey();
	byte[] raw = skey.getEncoded();
	kg.init(128);

	// encode plaintext with AES key
	SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	Cipher cipheraes = Cipher.getInstance("AES");
	cipheraes.init(Cipher.ENCRYPT_MODE, skeySpec);
	byte[] bencode_cipherplaintext_aes = cipheraes.doFinal(IOUtils.toByteArray(file));

	// Decode base64 contact public key, and use it to encrypt AES key.
	byte[] keyb = Base64.decodeBase64(pukey);
	PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyb));
	Cipher cipherrsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	cipherrsa.init(Cipher.ENCRYPT_MODE, publicKey);
	byte[] cipherData = cipherrsa.doFinal(raw);

	// Write both encrypted files
	IOUtils.write(cipherData, aeskey_ciphered_rsa);
	IOUtils.write(bencode_cipherplaintext_aes, plaintext_ciphered_aes);

    }

    /**
     * Using an encrypted AES key and an encrypted file, decrypt AES key using
     * private key and decrypt file using AES key.
     * 
     * @param file_to_decode
     *            the file you want to finally decrypts.
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String uncipher(String file_to_decode) throws IOException, NoSuchAlgorithmException,
	    NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException,
	    BadPaddingException {
	// convert to byte array the ciphered AES key
	byte[] aeskey, plaintext;
	InputStream is_reader_aes_key = new FileInputStream(new File(file_to_decode + Akratos.AES_CIPHERED_POSTFIX));
	byte[] s = IOUtils.toByteArray(is_reader_aes_key);
	// read the private key , and transform into byte array
	BufferedReader buff_reader_private_key = new BufferedReader(new FileReader(new File(Akratos.PRIVATE_KEY)));
	String pkey_instring = buff_reader_private_key.readLine();
	byte[] benc_privateKey = Base64.decodeBase64(pkey_instring);

	// decrypt AES key using your RSA private key.
	byte[] benc_file_to_decode = IOUtils.toByteArray(new FileInputStream(file_to_decode));
	PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(benc_privateKey);
	KeyFactory kf = KeyFactory.getInstance("RSA");
	PrivateKey privKey = kf.generatePrivate(keySpec);
	Cipher cipherrsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	cipherrsa.init(Cipher.DECRYPT_MODE, privKey);
	aeskey = cipherrsa.doFinal(s);

	// Decrypt file using the decrypted AES key
	Cipher cipheraes = Cipher.getInstance("AES");
	Key aeskey_ = new SecretKeySpec(aeskey, "AES");
	cipheraes.init(Cipher.DECRYPT_MODE, aeskey_);
	plaintext = cipheraes.doFinal(benc_file_to_decode);
	return new String(plaintext);
    }

    public static String getFriendUid(String plaintext) throws ParserConfigurationException, SAXException, IOException {
	DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	InputSource is = new InputSource();
	is.setCharacterStream(new StringReader(plaintext));
	Document doc = db.parse(is);
	NodeList nodes = (NodeList) doc.getElementsByTagName("uid");
	return nodes.item(0).getTextContent();
    }
}
