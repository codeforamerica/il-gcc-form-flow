cmd: java -jar /opt/il-gcc/app.jar
web: java -Dserver.port=$PORT $JAVA_OPTS -jar build/libs/*.jar
dashboard: JOBRUNR_DASHBOARD_ENABLED=true java -Dserver.port=8000 -jar /opt/il-gcc/app.jar
