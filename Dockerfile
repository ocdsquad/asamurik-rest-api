FROM khipu/openjdk21-alpine

COPY target/asamurik-rest-api-0.0.1-SNAPSHOT.jar /projects/asamurik-rest-api.jar
CMD ["java","-jar","/projects/asamurik-rest-api.jar"]