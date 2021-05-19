#!/bin/bash

export DOCKER_BUILDKIT=0

pushd ./../../../services
mvn package install

docker build -t localhost:32000/fishapp-auth:latest     auth/core
docker build -t localhost:32000/fishapp-user:latest     user/core
docker build -t localhost:32000/fishapp-chat:latest     chat/core
docker build -t localhost:32000/fishapp-checkout:latest checkout/core
docker build -t localhost:32000/fishapp-media:latest    media/core
docker build -t localhost:32000/fishapp-store:latest    store/core
docker build -t localhost:32000/fishapp-frontend:latest frontend

docker push localhost:32000/fishapp-auth:latest
docker push localhost:32000/fishapp-user:latest
docker push localhost:32000/fishapp-chat:latest
docker push localhost:32000/fishapp-checkout:latest
docker push localhost:32000/fishapp-media:latest
docker push localhost:32000/fishapp-store:latest
docker push localhost:32000/fishapp-frontend:latest