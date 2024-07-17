FROM eclipse-temurin:17-jdk-jammy

RUN mkdir /opt/il-gcc /opt/pdf-fonts
COPY . /opt/il-gcc
COPY src/main/resources/pdf-fonts/* /opt/pdf-fonts/
WORKDIR /opt/il-gcc

RUN ./gradlew assemble && \
    cp /opt/il-gcc/build/libs/app.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/il-gcc/app.jar", "--debug"]
