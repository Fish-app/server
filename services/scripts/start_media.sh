#!/bin/bash

mvn -f ./../media/pom.xml package && \
docker build ./../media/core -t fishapp-media:latest && \
docker run  \
  -d -t \
  --mount type=volume,source=image_volume,target=/aplication_storage/images \
  --network=fishapp_network \
  --rm \
  --add-host host.docker.internal:host-gateway \
  --name  fishapp_media \
  fishapp-media:latest
trap 'docker container kill fishapp_media &> /dev/null &' INT
docker logs -f fishapp_media 2> /dev/null