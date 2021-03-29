#!/bin/bash

docker build ./../media/core -t fishapp-auth:latest


docker run  \
  --network=fishapp_network \
  --rm \
  --name  fishapp_media \
  fishapp-auth:latest