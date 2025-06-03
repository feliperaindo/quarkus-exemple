FROM registry.access.redhat.com/ubi9/openjdk-21:1.22-1.1747241889 AS build

USER root

WORKDIR /home/default/build

COPY pom.xml .

RUN mvn clean install

COPY . .

RUN mvn package

USER 1001

FROM ibm-semeru-runtimes:open-21-jre-jammy AS runtime

USER root

WORKDIR /home/app

COPY --from=build /home/default/build/target/quarkus-app/ ./

USER 1001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
