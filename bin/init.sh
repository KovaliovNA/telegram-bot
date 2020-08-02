#!/bin/bash
LOG_PATH="/logs"
#removing escaping windows character
sed -i -e 's/\r$//' ./bin/*
sed -i -e 's/\r$//' ./config/*

#set env variables
set -a; . bin/var.env; set +a;

#adding security rights
chmod 777 ./bin/*
chmod 777 ./config/*.groovy

chmod 777 *.jar

./bin/stop.sh

sleep 3

./bin/start.sh