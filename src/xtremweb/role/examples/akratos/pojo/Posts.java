package xtremweb.role.examples.akratos.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
@XmlRootElement(name="Posts")
public class Posts {
@XmlElement(name="post")
	private List<Post> posts;
	
	public void addPost(Post p){
		posts.add(p);
	}
}
