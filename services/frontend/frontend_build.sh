#!/bin/bash

export UID=$(id -u)
export GID=$(id -g)
docker build ./src/main/liberty \
  -f ./src/main/frontend/build.Dockerfile \
  -t fishapp-frontend_builder:latest \
  --build-arg UID=$UID \
  --build-arg GID=$GID \


docker run \
  -t \
  --rm \
  --mount type=bind,source=$PWD/src/main/frontend,target=/node_app \
  --name  fishapp_frontend_builder fishapp-frontend_builder:latest


#   --user $UID:$GID \