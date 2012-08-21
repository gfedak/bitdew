package xtremweb.role.examples.akratos.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="contacts")
public class Contacts {
	
	public Contacts(){
		contacts = new ArrayList<Contact>();
	}
	
	private List<Contact> contacts;

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public void addContact(Contact c){		
		contacts.add(c);
	}
	
	public void addContact(String uid, String puid , String public_key){
		Contact c = new Contact(uid,puid,public_key);	
		contacts.add(c);
	}

	public int getSize() {
		
		return contacts.size();
	}

	public Contact getContact(int i) {
		return contacts.get(i);
	}

	public void empty() {
		contacts.removeAll(contacts);
		
	}
	

}
