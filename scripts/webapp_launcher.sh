#!/bin/sh
# script for starting the webapp, used in Procfile
export JOBRUNR_SERVER_ENABLED=false
exec java -Dserver.port=$PORT $JAVA_OPTS -jar build/libs/*.jar
