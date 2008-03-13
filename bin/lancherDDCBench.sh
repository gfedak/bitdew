#!/bin/sh


#MACHINES="gdx"

COMMAND2="cd ~/bitdewbt; sh killDDCBench.sh"
WORKERS=$2
MASTER=$3
LOOPS=$4
SIZE=$5
ORDER=$6

WORKERSLIST=$7

case "$1" in
 "help")
    echo " sh lancherGdXDDCBench.sh start/stop/help --worker --master --loops --size --order --workerslist"
   ;;
 "start")
   MACHINES=`cat $7`
   for i in $MACHINES
   do
    
       echo "Start worker on $i"
	   COMMAND1="cd ~/bitdewbt;sh lancherBT.sh ${MASTER} ${LOOPS} $i ${SIZE} ${WORKERS} ${ORDER}&>/dev/null"
	   echo `ssh -x hhe@$i ${COMMAND1} &>/dev/null`
	   echo "coucou"
	  
   done
   ;;
  "stop")
   MACHINES=`cat $2`
    for i in $MACHINES 
     do
	    echo "Stop worker on $i"
	    echo `ssh  hhe@$i ${COMMAND2} &>/dev/null`
	    
	   
     done
    ;;
    
    esac   

