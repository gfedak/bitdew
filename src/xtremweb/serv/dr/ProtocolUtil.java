package xtremweb.serv.dr;

import xtremweb.core.obj.dr.Protocol;

public class ProtocolUtil {

    public static String toString(Protocol proto) {
	String tmp =  "proto " + proto.getname() + " [" + proto.getuid() + "] = { ";
	if (proto.getname() .equals ("dummy"))
	    return tmp + " }";
	else
	    tmp += proto.getname()+ ":/";

	if (proto.getlogin() != null) { 
	    tmp += proto.getlogin() ;
	    if (proto.getpassword() != null) tmp+= "[" + proto.getpassword() + "]";
	    tmp += "@";
	}
	if (proto.getserver() != null) tmp+= proto.getserver();
	if (proto.getport() != -1) tmp+= ":" + proto.getport();
	if (proto.getpath() != null) tmp+= "/"+proto.getpath();
	return tmp + " }";
    }

}
