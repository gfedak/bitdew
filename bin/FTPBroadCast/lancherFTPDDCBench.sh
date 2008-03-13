#!/bin/sh


#MACHINES="gdx"

COMMAND2="cd ~/bitdewbt; sh killDDCBench.sh"
WORKERS=$2
MASTER=$3
LOOPS=$4
SIZE=$5

WORKERSLIST=$6

case "$1" in
 "help")
    echo " sh lancherGdXDDCBench.sh start/stop/help --worker --master --loops --size  --workerslist"
   ;;
 "start")
   MACHINES=`cat $6`
   for i in $MACHINES
   do
    
       echo "Start worker on $i"
	   COMMAND1="cd ~/bitdewbt;sh lancherFTP.sh ${MASTER} ${LOOPS} $i ${SIZE} ${WORKERS} &>/dev/null"
	   echo `ssh -x hehaiwu@$i ${COMMAND1} &>/dev/null`
	   echo "coucou"
	  
   done
   ;;
  "stop")
   MACHINES=`cat $2`
    for i in $MACHINES 
     do
	    echo "Stop worker on $i"
	    echo `ssh  hehaiwu@$i ${COMMAND2} &>/dev/null`
	    
	   
     done
    ;;
    
    esac   

