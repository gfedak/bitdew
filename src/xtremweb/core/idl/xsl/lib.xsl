<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.1">

<!-- The template to transform a list of parameters to a list of type name separated by , -->
<xsl:template name="params-to-list">
<xsl:param name="params" />

<!-- Builds the attributes -->
<xsl:variable name="list">
<xsl:for-each select="$params"><xsl:value-of select="concat(@type, ' ', @name, ', ')"/></xsl:for-each>  
</xsl:variable>
<xsl:value-of select="substring($list,1,string-length($list)-2)"/>
</xsl:template>


<!-- The template to transform a list of parameters to a list of untyped name separated by , -->
<xsl:template name="params-to-untyped-list">
<xsl:param name="params" />

<!-- Builds the attributes -->
<xsl:variable name="list">
<xsl:for-each select="$params"><xsl:value-of select="concat( @name, ', ')"/></xsl:for-each>  
</xsl:variable>
<xsl:value-of select="substring($list,1,string-length($list)-2)"/>
</xsl:template>




</xsl:stylesheet>
