#!/bin/sh
#java -classpath log4j.jar:.  MLDonkey
#javac -classpath log4j.jar:.  *.java
ANT_HOME=.
IBIS_HOME=/opt/ibis-1.3
export ANT_HOME IBIS_HOME
[ -n "$JAVA_HOME" ] || eval `java -classpath tools JavaHome`
exec ./ant $*
