package xtremweb.role.examples.akratos.pojo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="publicinfo")
@XmlType(propOrder={ "uid","privateuid", "publicname","tag","city","country","profession","publickey" })
public class User implements Serializable {
	
	private String uid;
	private String privateuid;
	private String publicname;
	private String tag;
	private String city;
	private String country;
	private String profession;
	private String publickey;
	
	public User(){
		
	}
	
	public User (String uid,String publicname,String tag,String city,String country,String profession,String publickey){
		/*this.uid = uid;
		this.publicname = publicname;
		this.tag = tag;
		this.city = city;
		this.country = country;
		this.profession = profession;
		this.publickey = publickey;*/
	}

	public String getPrivateuid(){
		return privateuid;	
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPublicname() {
		return publicname;
	}
	public void setPublicname(String publicname) {
		this.publicname = publicname;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProfession() {
		return profession;
	}

	public void setPrivateuid(String puid){
		this.privateuid = puid;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getPublickey() {
		return publickey;
	}
	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	
	public String toString(){
		return "uid "+uid+"profession "+profession+"city"+city+"country "+country+"tag "+tag+"uid "+uid+"publicname" ;
	}

}
