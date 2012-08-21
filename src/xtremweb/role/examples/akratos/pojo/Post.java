package xtremweb.role.examples.akratos.pojo;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="Post")
@XmlType(propOrder={ "timestamp", "description" })
public class Post {
	
	private String timestamp;
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String descp) {
		this.description = descp;
	}

}
