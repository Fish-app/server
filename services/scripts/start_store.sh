#!/bin/bash

docker build ./../store/core -t fishapp-store:latest


docker run  \
  --network=fishapp_network \
  --rm \
  --add-host host.docker.internal:host-gateway \
  --name  fishapp_store \
  fishapp-store:latest