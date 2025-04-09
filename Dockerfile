FROM eclipse-temurin:21-jdk-alpine

# Install LibreOffice (which includes soffice)
RUN apk add --no-cache libreoffice libreoffice-common

RUN mkdir /opt/il-gcc /opt/pdf-fonts
COPY . /opt/il-gcc
COPY src/main/resources/pdf-fonts/* /opt/pdf-fonts/
WORKDIR /opt/il-gcc

RUN ./gradlew clean assemble && \
    cp build/libs/*.jar app.jar

# Copy and prepare the launcher script
COPY scripts/webapp_launcher.sh /opt/il-gcc/webapp_launcher.sh
RUN chmod +x /opt/il-gcc/webapp_launcher.sh

# Expose ports
EXPOSE 8080
EXPOSE 8000

# Run the launcher script on container start
ENTRYPOINT ["/opt/il-gcc/webapp_launcher.sh"]