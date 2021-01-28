# syntax=docker/dockerfile:experimental

## Build inside docker with a maven container
FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# adjust for your own needs
RUN --mount=type=cache,target=/root/.m2 mvn -f pom.xml clean package
#RUN --mount=type=bind,source=./.m2,target=/root/.m2,target=$HOME/.m2 mvn -f pom.xml clean package
#RUN mvn -f pom.xml clean package

## Copy isolated build from intermediate container(useful on server)
FROM payara/micro:5.2020.7-jdk11 AS prod
## Fixes clock in payara log
ENV TZ Europe/Oslo
COPY --from=build /app/target/***REMOVED***-1.0.war $DEPLOY_DIR/***REMOVED***-1.0.war
# create image folder if missing and set ownership
RUN mkdir /opt/payara/images
RUN chown payara:payara /opt/payara/images
USER payara
# deploy the application to root / (ref docker hub)
CMD ["--deploymentDir", "/opt/payara/deployments", "--contextroot", "ROOT"]


## Local build from IDE; only copy (no docker build) from exising .war file built by IDE
FROM payara/micro AS run-only
ENV TZ Europe/Oslo
COPY ./target/***REMOVED***-1.0.war $DEPLOY_DIR/***REMOVED***-1.0.war
