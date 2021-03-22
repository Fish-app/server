#FROM maven:3.6.3-jdk-11-slim AS build
#
#ARG VERSION=1.0
#ARG REVISION=SNAPSHOT
#
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#RUN --mount=type=bind,source=./.build_m2,target=/root/.m2 mvn -f pom.xml clean package
#
#
#FROM open-liberty:full-java11-openj9 AS prod
#LABEL   name="AUTH" \
#        version="$VERSION-$REVISION"
#COPY --chown=1001:0 src/main/liberty/config /config/
#COPY --chown=1001:0 config/server.env config/
#COPY --from=build --chown=1001:0 /app/target/*.war /config/apps
#EXPOSE 9085 9448


FROM open-liberty:21.0.0.3-full-java11-openj9
ARG VERSION=1.0
ARG REVISION=SNAPSHOT
LABEL   name="AUTH" \
        traefik.http.routers.userr.rule=Path:/user \
        version="$VERSION-$REVISION"

COPY --chown=1001:0 src/main/liberty/config /config/
COPY --chown=1001:0 ../config/server.env /config/
COPY --chown=1001:0 ./target/resource_jars/ /opt/ol/wlp/usr/shared/resources/
COPY --chown=1001:0 ./target/user-1.0.war /config/apps

EXPOSE 9085 9448