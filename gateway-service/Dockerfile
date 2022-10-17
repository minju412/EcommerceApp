FROM openjdk:17-ea-11-jdk-slim

VOLUME /tmp

COPY target/gateway-service-0.0.1-SNAPSHOT.jar GatewayService.jar

ENTRYPOINT ["java","-jar","GatewayService.jar"]