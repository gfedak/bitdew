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
  select="concat('src/xtremweb/core/http/Callback',$moduleName,'XmlRpcServlet','.java')" />
<xsl:message>Creating <xsl:value-of select="$filename" /></xsl:message>
<xsl:document href="{$filename}" method="text">


/**
 * CommRMI<xsl:value-of select="Module/@name" />.java
 *
 *  Creates the exotic ApacheRPC RequestProcessorFactoryFactory to control that only one object is created
 *
 * This class deploys a CallbackXX in the XMLRPC Servlet.
 * @author jsaray
 * @version "1.0"
 */
package xtremweb.core.http;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.apache.xmlrpc.XmlRpcException;
import xtremweb.core.iface.Interface<xsl:value-of select="$moduleName"/>;
import xtremweb.serv.<xsl:value-of select="$moduleName"/>.Callback<xsl:value-of select="$moduleName"/>;
import xtremweb.core.com.xmlrpc.Callback<xsl:value-of select="$moduleName"/>ProcessorFactoryFactory;
public class Callback<xsl:value-of select="$moduleName"/>XmlRpcServlet extends XmlRpcServlet {
	
	public Callback<xsl:value-of select="$moduleName"/>XmlRpcServlet(){
		super();
	}
	
	protected XmlRpcHandlerMapping newXmlRpcHandlerMapping(){
            try{
		PropertyHandlerMapping phm = new PropertyHandlerMapping();
		Interface<xsl:value-of select="$moduleName"/><xsl:text>  </xsl:text><xsl:value-of select="$moduleName"/> = new Callback<xsl:value-of select="$moduleName"/>();
		phm.setRequestProcessorFactoryFactory(new Callback<xsl:value-of select="$moduleName"/>ProcessorFactoryFactory(<xsl:value-of select="$moduleName"/>));
		phm.setVoidMethodEnabled(true);
                phm.addHandler(Interface<xsl:value-of select="$moduleName"/>.class.getName(), Interface<xsl:value-of select="$moduleName"/>.class);
		return phm;
            }catch ( XmlRpcException e ){
                e.printStackTrace();
            }
            return null;
	}

}
</xsl:document>
</xsl:template>
</xsl:stylesheet>