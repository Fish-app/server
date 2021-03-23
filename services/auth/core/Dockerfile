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


LABEL name="AUTH"
LABEL version="$VERSION-$REVISION"

LABEL traefik.enable=true

LABEL traefik.http.middlewares.ASAFEFEF-stripprefixregex.stripprefixregex.regex="/api/[a-z0-9]?/"
LABEL traefik.http.routers.auth.rule="Path(`/api/auth/`)"
LABEL traefik.http.routers.auth.middlewares=ASAFEFEF-stripprefixregex
#LABEL traefik.port=80
#LABEL traefik.http.routers.auth-route.rule="Host(`localhost`) && Path(`/auth`)"
#LABEL traefik.http.services.netdata_srv.loadbalancer.server.port=80
##LABEL traefik.http.middlewares.auth-route.redirectregex.regex=.*
#
#LABEL traefik.http.middlewares.test-redirectregex.redirectregex.regex=^http://api.localhost/(.*)
#LABEL traefik.http.middlewares.test-redirectregex.redirectregex.replacement=http://localhost/$${1}
#
#LABEL traefik.http.middlewares.test-replacepathregex.replacepathregex.regex=^/auth(.*)
#LABEL traefik.http.middlewares.test-replacepathregex.replacepathregex.replacement=/$$1

COPY --chown=1001:0 src/main/liberty/config /config/
COPY --chown=1001:0 ./config/server.env /config/
COPY --chown=1001:0 ./target/resource_jars /opt/ol/wlp/usr/shared/resources/
COPY --chown=1001:0 ./target/auth-1.0.war /config/apps

EXPOSE 9085 9448