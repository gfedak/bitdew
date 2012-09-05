package xtremweb.role.examples.akratos.pojo;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Contact class represent a triplet privateuid, uid, publickey, privateuid , it is parsed from a XML file using JAXB
 * uid contact uid
 * privateuid the private uid, this uid is used as the destination field for personal/private info
 * publickey the contact public key
 * @author jsaray
 *
 */
@XmlRootElement(name="Contact")
@XmlType(propOrder={ "uid","privateuid", "publickey" })
public class Contact {
	
	/**
	 * Default constructor
	 */
	public Contact(){
		
	}
	
	/**
	 * Multiple parameter constructor
	 * @param uid2
	 * @param puid
	 * @param pu_key
	 */
	public Contact(String uid2,String puid, String pu_key) {
		privateuid = puid;
		this.uid = uid2;
		this.publickey = pu_key;
	}
	
	/**
	 * Get public key
	 * @return
	 */
	public String getPublickey() {
		return publickey;
	}
	
	/**
	 * Set public key
	 * @param publickey
	 */
	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	
	/**
	 * Get uid
	 * @return
	 */
	public String getUid() {
		return uid;
	}
	
	/**
	 * Set uid
	 * @param uid
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	/**
	 * Get private uid
	 * @return
	 */
	public String getPrivateuid(){
		return privateuid;
	}
	
	/**
	 * Set private uid
	 * @param puid
	 */
	public void setPrivateuid(String puid){
		this.privateuid = puid;
	}
	
	/**
	 * Private uid
	 */
	private String privateuid;
	
	/**
	 * Uid
	 */
	private String uid ;
	
	/**
	 * Public key
	 */
	private String publickey;
	

}
