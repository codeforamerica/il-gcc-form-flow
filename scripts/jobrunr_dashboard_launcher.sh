#!/bin/sh
# script for starting jobrunr dashboard via Aptible, used in Procfile
export JOBRUNR_DASHBOARD_ENABLED=true
export JOBRUNR_SERVER_ENABLED=false
exec  java -jar /opt/il-gcc/app.jar
