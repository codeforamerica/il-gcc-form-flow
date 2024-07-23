#!/bin/bash
# script for starting jobrunr dashboard via Aptible, used in Procfile
export JOBRUNR_DASHBOARD_ENABLED=true
exec  java -jar /opt/il-gcc/app.jar
