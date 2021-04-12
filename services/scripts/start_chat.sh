#!/bin/bash

mvn -f ./../chat/pom.xml package && \
docker build ./../chat/core -t fishapp-chat:latest && \
docker run  \
  -d -t \
  --network=fishapp_network \
  --rm \
  --add-host host.docker.internal:host-gateway \
  --name  fishapp_chat \
  fishapp-chat:latest
trap 'docker container kill fishapp_chat &> /dev/null &' INT
docker logs -f fishapp_chat 2> /dev/null