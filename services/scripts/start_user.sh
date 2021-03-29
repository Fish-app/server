#!/bin/bash

docker build ./../user/core -t fishapp-user:latest


docker run  \
  --network=fishapp_network \
  --rm \
    --add-host host.docker.internal:host-gateway \
  --name  fishapp_user \
  fishapp-user:latest