#!/bin/bash

docker build ./../media/core -t fishapp-media:latest


docker run  \
  --network=fishapp_network \
  --rm \
    --add-host host.docker.internal:host-gateway \
  --name  fishapp_media \
  fishapp-media:latest