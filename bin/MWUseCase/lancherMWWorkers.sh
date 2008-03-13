#!/bin/sh


COMMAND2="cd ~/bitdewbt; sh killWorker.sh"
WORKERS=$4
MASTER=$2
FILE=$3
ORDER=$5
WORKERSLIST=$6
OOB=$7
DIR=$8

case "$1" in
 "help")
    echo " sh lancherMWWorkers.sh start/stop/help --master --file --workers --order --workerslist --oob --dir"
   ;;
 "start")
   MACHINES=`cat $6`
   for i in $MACHINES
   do
    
       echo "Start worker on $i"
	   COMMAND1="cd ~/bitdewbt;sh lancherWorker.sh ${MASTER} ${FILE} ${WORKERS} $i ${ORDER} ${OOB} ${DIR}&>/dev/null"
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

