FROM openjdk:17

MAINTAINER "Thinkenterprise"

EXPOSE 8080

ADD ./target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
