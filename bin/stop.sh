#!/bin/bash
#Preparing working directory path
FILE_PATH=$(realpath $0)
WORK_DIR=${FILE_PATH%/*}
#Changed directory up on 1 level to project root directory
WORK_DIR=${WORK_DIR%/*}

cd "$WORK_DIR"

set -a; . bin/var.env; set +a;
APP_NAME=${INSTANCE_NAME}.jar
APP_LABEL="app.id=$APP_NAME"
PID=`ps -fe | grep $APP_LABEL | grep -v grep | tr -s " "|cut -d" " -f2`
if [ -z "$PID" ]
	then echo -e "\e[00;31m$APP_NAME is not running\e[00m"
	else       
	 #stop app 
	 echo -e "\e[00;32mStopping $APP_NAME\e[00m" 
	    kill $PID
fi
