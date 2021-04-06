#!/bin/bash

mvn -f ./../store/pom.xml package && \
docker build ./../store/core -t fishapp-store:latest && \
docker run  \
  -d -t \
  --network=fishapp_network \
  --rm \
  --add-host host.docker.internal:host-gateway \
  --name  fishapp_store \
  fishapp-store:latest
trap 'docker container kill fishapp_store &> /dev/null &' INT
docker logs -f fishapp_store 2> /dev/null