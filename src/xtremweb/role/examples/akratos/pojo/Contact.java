package xtremweb.role.examples.akratos.pojo;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="Contact")
@XmlType(propOrder={ "uid","privateuid", "publickey" })
public class Contact {
	public Contact(){
		
	}
	public Contact(String uid2,String puid, String pu_key) {
		privateuid = puid;
		this.uid = uid2;
		this.publickey = pu_key;
	}
	public String getPublickey() {
		return publickey;
	}
	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPrivateuid(){
		return privateuid;
	}

	public void setPrivateuid(String puid){
		this.privateuid = puid;
	}
	private String privateuid;
	private String uid ;
	private String publickey;
	

}
