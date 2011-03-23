#!/bin/bash
export BD_HOME=$1

if [ -e $BD_HOME/temp-jar ] 
then
java -cp build:conf:temp-jar xtremweb.role.cmdline.CommandLineTool serv dt dn dr ds dc
else
echo 'you must run deflate-jars ant task before launching this'
fi
