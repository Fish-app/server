#!/bin/bash


mvn -f ./../frontend/pom.xml package && \
docker build ./../frontend/ -t fishapp_frontend:latest && \
docker run  \
  -d -t \
  --network=fishapp_network \
  --rm \
  --add-host host.docker.internal:host-gateway \
  --name  fishapp_frontend \
  fishapp_frontend:latest
trap 'docker container kill fishapp_frontend &> /dev/null &' INT
docker logs -f fishapp_frontend 2> /dev/null
