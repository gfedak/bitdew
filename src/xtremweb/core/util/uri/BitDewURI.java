/*
** Handle the BitDew URI
** bitdew://gdx-1.orsay.grid5000.fr/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

** standard Java URI        [scheme:][//authority][path]
scheme:         bitdew
authority:      host    
path:              /uid

*/

package xtremweb.core.util.uri;

import java.net.*;

public class BitDewURI{

	private String _scheme;
	private String _host;
	private String _uid;
	private URI uri = null;
	
	private String _protocol;
	
	public String getScheme(){
		return _scheme;
	}
	
	public String getHost(){
		return _host;
	}
	
	public String getUid(){
		return _uid;
	}
	
	public String getProtocol(){
		return _protocol;
	}
	
	// From String u to BitDewURI object
	public BitDewURI(String u){
		int i = u.lastIndexOf(":");
		int j = u.lastIndexOf("/");
		_scheme = u.substring(0, i);
		_host = u.substring(i+3, j);
		_uid = u.substring(j+1, u.length());
	}
	
	public BitDewURI(String scheme, String host, String uid, String fragment) throws URISyntaxException{
	    if (scheme.toLowerCase().equals("bitdew"))
			_scheme = "bitdew";
		_host = host;
		_uid = uid;
		uri = new URI(_scheme, host, "/"+uid, fragment);
	}
	
	public BitDewURI(String scheme, String host, String uid) throws URISyntaxException{
	    if (scheme.toLowerCase().equals("bitdew"))
			_scheme = "bitdew";
		_host = host;
		_uid = uid;
		uri = new URI(_scheme, host, "/"+uid, null);
	}
	
	public BitDewURI(String host, String uid) throws URISyntaxException{
		_scheme = "bitdew";
		_host = host;
		_uid = uid;
		uri = new URI(_scheme, host, "/"+uid, null);
	}

	public void setProtocol(String s){
		_protocol = s;
	}
	
	public String toString(){
		return uri.toString();
	}
	
	public static void main(String[] args) throws URISyntaxException{
		System.out.println("Hello World");
		BitDewURI a = new BitDewURI("sagittaire-1.lyon.grid5000.fr", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
		System.out.println(a.toString());
		
		BitDewURI b = new BitDewURI("bitdew://sagittaire-1.lyon.grid5000.fr/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
		System.out.println(b.getScheme());
		System.out.println(b.getHost());
		System.out.println(b.getUid());
		b.setProtocol("ftp");
		System.out.println(b.getProtocol());
	}

}