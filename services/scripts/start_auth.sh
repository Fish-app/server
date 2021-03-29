#!/bin/bash

docker build ./../auth/core -t fishapp-auth:latest


docker run  \
  --network=fishapp_network \
  --rm \
    --add-host host.docker.internal:host-gateway \
  --name  fishapp_auth \
  fishapp-auth:latest