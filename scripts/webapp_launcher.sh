#!/bin/sh
# script for starting the webapp, used in Procfile
export JOBRUNR_SERVER_ENABLED=false
exec java -Dserver.port=$PORT -Dlogging.config=classpath:logback-spring.xml $JAVA_OPTS -jar app.jar
