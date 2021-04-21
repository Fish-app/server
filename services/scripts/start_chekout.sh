#!/bin/bash

mvn -f ./../checkout/pom.xml package && \
docker build ./../checkout/core -t fishapp-checkout:latest && \
docker run  \
  -d -t \
  --network=fishapp_network \
  --rm \
  --mount source=imageVolume,target=/aplication_storage/images \
  --add-host host.docker.internal:host-gateway \
  --name  fishapp_checkout \
  fishapp-checkout:latest
trap 'docker container kill fishapp_checkout &> /dev/null &' INT
docker logs -f fishapp_checkout 2> /dev/null