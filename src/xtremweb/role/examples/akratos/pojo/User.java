package xtremweb.role.examples.akratos.pojo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * User 
 * @author jsaray
 *
 */
@XmlRootElement(name="publicinfo")
@XmlType(propOrder={ "uid","privateuid", "publicname","tag","city","country","profession","publickey" })
public class User implements Serializable {
	
	/**
	 * User uid
	 */
	private String uid;
	
	/**
	 * User private id that uniquely identifies him on the Desktop Grid, and that is used to 
	 */
	private String privateuid;
	
	/**
	 * User public name e.g Kevin Costner 
	 */
	private String publicname;
	
	/**
	 * Tag, like in twitter
	 */
	private String tag;
	
	/**
	 * Residence city
	 */
	private String city;
	
	/**
	 * Country
	 */
	private String country;
	
	/**
	 * Profession
	 */
	private String profession;
	
	/**
	 * Your public key in Base64
	 */
	private String publickey;
	
	/**
	 * User default constructor
	 */
	public User(){
		
	}
	
	/**
	 * gets your private uid
	 * @return
	 */
	public String getPrivateuid(){
		return privateuid;	
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
	 * Get your public name
	 * @return
	 */
	public String getPublicname() {
		return publicname;
	}
	
	/**
	 * Set your public name
	 * @param publicname your public name
	 */
	public void setPublicname(String publicname) {
		this.publicname = publicname;
	}
	
	/**
	 * Get tag
	 * @return
	 */
	public String getTag() {
		return tag;
	}
	
	/**
	*Set tag
	*/
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	/**
	 * Get city
	 * @return
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * Set city
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
	/**
	 * Get country
	 * @return
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Set country
	 * @param country
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * Get profession
	 * @return
	 */
	public String getProfession() {
		return profession;
	}
	
	/**
	 * Set private uid
	 * @param puid the private uid
	 */
	public void setPrivateuid(String puid){
		this.privateuid = puid;
	}
	
	/**
	 * Set profession
	 * @param profession the profession to set.
	 */
	public void setProfession(String profession) {
		this.profession = profession;
	}
	
	/**
	 * Get the public key
	 * @return
	 */
	public String getPublickey() {
		return publickey;
	}
	
	/**
	 * Set the public key
	 * @param publickey the new public key
	 */
	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	
	/**
	 * The user to string
	 */
	public String toString(){
		return "uid "+uid+"profession "+profession+"city"+city+"country "+country+"tag "+tag+"uid "+uid+"publicname" ;
	}

}
