#!/bin/bash


mvn -f ./../auth/pom.xml package && \
docker build ./../auth/core -t fishapp-auth:latest && \
docker run  \
  -d -t \
  --network=fishapp_network \
  --rm \
  --add-host host.docker.internal:host-gateway \
  --name  fishapp_auth \
  fishapp-auth:latest
trap 'docker container kill fishapp_auth &> /dev/null &' INT
docker logs -f fishapp_auth 2> /dev/null
