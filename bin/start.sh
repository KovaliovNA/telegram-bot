#!/usr/bin/env bash
#!/bin/bash
#Preparing working directory path
FILE_PATH=$(realpath $0)
WORK_DIR=${FILE_PATH%/*}
#Changed directory up on 1 level to project root directory
WORK_DIR=${WORK_DIR%/*}

cd "$WORK_DIR"

#set env variables
set -a; . bin/var.env; set +a;

#Setting variables
JAVA_HOME=$JAVA_HOME
CONFIG_PATH="$WORK_DIR/config"
APP_NAME=${INSTANCE_NAME}.jar
APP_LABEL="app.id=$APP_NAME"
JAVA_OPTS="$JAVA_OPTS -Xms128m -Xmx256m"
JAVA_OPTS="$JAVA_OPTS -Dlabel.$APP_LABEL"

if [ ! -d "./logs" ];
then
  mkdir -p ./logs
  echo -e "\e[00;32mLogs folder was created\e[00m"
else
    echo -e "\e[00;32mLogs folder already exists\e[00m"
fi

sleep 2
rm ./logs/console.out

#Collect existing process by name
PID=`ps -fe | grep $APP_LABEL | grep -v grep | tr -s " "|cut -d" " -f2`

if [ -n "$PID" ]
	then echo -e "\e[00;31m$APP_NAME is already running (pid: $PID)\e[00m"
    else
	    #start app
        echo -e "\e[00;32mStarting $APP_NAME\e[00m"
	    CMD="${JAVA_HOME}/bin/java -jar $JAVA_OPTS $APP_NAME"
	    $CMD >> "$WORK_DIR/logs/console.out" 2>&1 &
	    echo -e "\e[00;32m$APP_NAME is running with pid: $!\e[00m"
    fi

sleep 15

LOG_STATE=`grep -oE 'Started .+ in .+ seconds \(JVM running for .+\)' ./logs/console.out`

if [[ -n "$LOG_STATE" ]]
    then
        echo -e "\e[00;32mServer started successfully\e[00m"
    else
       echo -e "\e[00;31mStartup Failed. Check logs/console.out\e[00m"
    fi