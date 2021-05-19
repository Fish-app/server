#!/bin/bash



mvn -f ./../user/pom.xml package && \
docker build ./../user/core -t fishapp-user:latest && \
docker run  \
  -d -t \
  --network=fishapp_network \
  --rm \
  --add-host host.docker.internal:host-gateway \
  --name  fishapp_user \
  fishapp-user:latest
trap 'docker container kill fishapp_user &> /dev/null &' INT
docker logs -f fishapp_user 2> /dev/null
