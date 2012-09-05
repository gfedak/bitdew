package xtremweb.role.examples.akratos.pojo;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
/**
 * A post, a piece of information to share
 * @XmlRootElement(name="Post")
 * @author jsaray
 *
 */
@XmlType(propOrder={ "timestamp", "description" })
public class Post {
	
	/**
	 * Post Timestamp
	 */
	private String timestamp;
	
	/**
	 * get the post timestamp
	 * @return a string representing the post timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Set the post timestamp
	 * @param timestamp
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Post description
	 */
	private String description;
	
	/**
	 * Get post description
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set post description
	 * @param descp
	 */
	public void setDescription(String descp) {
		this.description = descp;
	}

}
