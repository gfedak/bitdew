package xtremweb.role.examples.akratos.pojo;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Your personal info
 * @author jsaray
 *
 */
@XmlRootElement(name="personalinfo")
@XmlType(propOrder={ "uid", "publicname" , "posts" })
public class PersonalInfo {
	
	/**
	 * Personal uid
	 */
	private String uid;
	
	/**
	 * Your public name
	 */
	private String publicname ;
	
	/**
	 * Your private post list
	 */
	private Posts posts ;
	
	/**
	 * Get your uid
	 * @return
	 */
	public String getUid() {
		return uid;
	}
	
	/**
	 * Set your uid
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	/**
	 * Get your publicname
	 * @return
	 */
	public String getPublicname() {
		return publicname;
	}
	
	/**
	 * Set your public name
	 * @param publicname
	 */
	public void setPublicname(String publicname) {
		this.publicname = publicname;
	}
	
	/**
	 * Get your posts list
	 * @return the post list
	 */
	public Posts getPosts() {
		return posts;
	}
	
	/**
	 * Set your posts list
	 * @param posts your posts list
	 */
	public void setPosts(Posts posts) {
		this.posts = posts;
	}
	

}
