FROM openjdk:8
COPY ./target/store-service-registry-*-jar-with-dependencies.jar /usr/src/store-prototype/app.jar
WORKDIR /usr/src/store-prototype
ENTRYPOINT ["java", "-jar" , "app.jar"]
