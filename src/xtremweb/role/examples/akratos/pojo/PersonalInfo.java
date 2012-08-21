package xtremweb.role.examples.akratos.pojo;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="personalinfo")
@XmlType(propOrder={ "uid", "publicname" , "posts" })
public class PersonalInfo {
	
	private String uid;
	
	private String publicname ;
	
	private Posts posts ;

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

	public Posts getPosts() {
		return posts;
	}

	public void setPosts(Posts posts) {
		this.posts = posts;
	}
	

}
