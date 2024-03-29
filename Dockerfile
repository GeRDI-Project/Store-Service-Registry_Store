FROM openjdk:8
COPY ./target/store-service-registry-*-jar-with-dependencies.jar /usr/src/store-service-registry/app.jar
WORKDIR /usr/src/store-service-registry
ENTRYPOINT ["java", "-jar" , "app.jar"]
