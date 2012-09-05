package xtremweb.role.examples.akratos.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contact list
 * @author jsaray
 *
 */
@XmlRootElement(name="contacts")
public class Contacts {
	
	/**
	 * Field initialization
	 */
	public Contacts(){
		contacts = new ArrayList<Contact>();
	}
	
	/**
	 * List attribute
	 */
	private List<Contact> contacts;
	
	/**
	 * Get all contacts
	 * @return contact list
	 */
	public List<Contact> getContacts() {
		return contacts;
	}
	
	/**
	 * Set the contacts
	 * @param contacts
	 */
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	
	/**
	 * Add a new contact
	 * @param c the contact to add
	 */
	public void addContact(Contact c){		
		contacts.add(c);
	}
	
	/**
	 * add a new contact
	 * @param uid
	 * @param puid
	 * @param public_key
	 */
	public void addContact(String uid, String puid , String public_key){
		Contact c = new Contact(uid,puid,public_key);	
		contacts.add(c);
	}
	
	/**
	 * Get collection size
	 * @return
	 */
	public int getSize() {
		
		return contacts.size();
	}
	
	/**
	 * Get a contact on a given index
	 * @param i the index
	 * @return the contact on that index
	 */
	public Contact getContact(int i) {
		return contacts.get(i);
	}
	
	/**
	 * Empty the list
	 */
	public void empty() {
		contacts.removeAll(contacts);
		
	}
	

}
