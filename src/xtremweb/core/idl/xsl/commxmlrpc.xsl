<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.1">

<xsl:output method="text" />
<xsl:include href="lib.xsl"/>

<xsl:template match="/">    

<xsl:variable name="moduleName"
  select="/Module/@name" />
<xsl:message>Processing CommRMI for module <xsl:value-of select="$moduleName" /></xsl:message>

<xsl:variable name="filename"
  select="concat('src/xtremweb/core/com/xmlrpc/CommXmlRpc',$moduleName,'.java')" />
<xsl:message>Creating <xsl:value-of select="$filename" /></xsl:message>
<xsl:document href="{$filename}" method="text">
package xtremweb.core.com.xmlrpc;

/**
 * CommRMI<xsl:value-of select="Module/@name" />.java
 *
 *  Creates RMI Communication to an RMI server
 *
 * This class returns a proxy to the previously deployed CallbackXX POJO
 * @author jsaray
 * @version "1.0"
 */

import xtremweb.core.com.idl.*;
import xtremweb.core.iface.Interface<xsl:value-of select="$moduleName"/>;
<xsl:if test="count(Module/Object) != 0">
import xtremweb.core.obj.<xsl:value-of select="Module/@name" />.*;
</xsl:if>
import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;

import java.net.URL;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.util.ClientFactory;

<xsl:value-of select="Module/@import" />

public class CommXmlRpc<xsl:value-of select="$moduleName"/> extends CommXmlRpcTemplate implements Interface<xsl:value-of select="$moduleName"/> {
  
  private Interface<xsl:value-of select="$moduleName"/><xsl:text> </xsl:text><xsl:value-of select="$moduleName"/>;
  
  private Logger log = LoggerFactory.getLogger("CommXmlRpc<xsl:value-of select="$moduleName"/>");
  
  public void initComm ( String hostname, int port, String module) throws CommException{
      try {
      _hostname = hostname ;
      _port = port ;
      _module = module ; 
	log.info("Recovering XML-RPC Interface ... " + _module);
	String className = ModuleLoader.rootIfaceClassPath;
	XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	config.setServerURL(new URL(("http://" + _hostname + ":" + _port + "/xmlrpc_" + _module)));
	config.setEnabledForExceptions(true);
	config.setEnabledForExtensions(true);
	XmlRpcClient client = new XmlRpcClient();
	client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
	client.setConfig(config);
	Class iface = Class.forName(ModuleLoader.rootIfaceClassPath + _module);
	ClientFactory factory = new ClientFactory(client);
	<xsl:value-of select="$moduleName"/><xsl:text> </xsl:text> = (Interface<xsl:value-of select="$moduleName"/>)factory.newInstance(iface);
      }catch (MalformedURLException mue) {
	log.info("bad URL syntax : " + "http://" + _hostname + ":" + _port+ "/xmlrpc");
	mue.printStackTrace();
      } catch (ClassNotFoundException cnfe) {
	log.info("cannot find class : "+ ModuleLoader.rootIfaceClassPath + ".InterfaceRMI"+ _module);
	cnfe.printStackTrace();
      }
      
  }
  
  <xsl:for-each select="/Module/Method">
    <xsl:call-template name="java-method">
      <xsl:with-param name="moduleName" select="$moduleName" />
      <xsl:with-param name="methodName" select="." />
      <xsl:with-param name="params"
        select="./Param" />
      <xsl:with-param name="return"
        select="./Return" />
      <xsl:with-param name="exceptions" select="./Throws"/>
    </xsl:call-template>
  </xsl:for-each>
}
</xsl:document>
</xsl:template>


<!-- The main template for a Java Method -->
<xsl:template name="java-method">
<xsl:param name="moduleName" />
<xsl:param name="methodName" />
<xsl:param name="params" />
<xsl:param name="return" />
<xsl:param name="exceptions"/>
<!-- Builds the attributes --> 
     public <xsl:value-of select="concat($return/@type , ' ', $methodName/@name)"/>( <xsl:call-template name="params-to-list"><xsl:with-param name="params" select="$params"/></xsl:call-template> ) throws XmlRpcException <xsl:call-template name="exception-list-comma-after"><xsl:with-param name="exceptions" select="$exceptions"/></xsl:call-template>  {
         <xsl:if test="$return/@type!='void'"><xsl:value-of select="concat($return/@type , ' x;')"/></xsl:if>
         try {
              <xsl:if test="$return/@type!='void'">x = </xsl:if><xsl:value-of select="concat($moduleName,'.',$methodName/@name)"/>( <xsl:call-template name="params-to-untyped-list"><xsl:with-param name="params" select="$params"/></xsl:call-template> );
         } catch (Exception e) {
		 throw new XmlRpcException(" " +e);
	}
         return<xsl:if test="$return/@type!='void'"> x</xsl:if>;
     }
</xsl:template>



</xsl:stylesheet>
