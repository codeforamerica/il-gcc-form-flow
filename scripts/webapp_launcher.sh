#!/bin/sh
# script for starting the webapp, used in Procfile
exec java -Dserver.port=$PORT $JAVA_OPTS -jar build/libs/*.jar
