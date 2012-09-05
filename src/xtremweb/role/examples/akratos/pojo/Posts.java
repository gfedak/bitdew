package xtremweb.role.examples.akratos.pojo;

import java.util.List;


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;


/**
 * Posts list
 * @author jsaray
 *
 */
@XmlRootElement(name="Posts")
public class Posts {
	/**
	 * The post list
	 */
@XmlElement(name="post")
	private List<Post> posts;
	
	/**
	 * Add a new post
	 * @param p
	 */
	public void addPost(Post p){
		posts.add(p);
	}
}
