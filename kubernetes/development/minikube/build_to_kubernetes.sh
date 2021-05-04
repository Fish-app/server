#!/bin/bash
eval $(minikube docker-env -u)

export DOCKER_BUILDKIT=0

pushd ./../../services
mvn package install


eval $(minikube docker-env)
docker build -t fishapp-auth:latest auth/core  && \
docker build -t fishapp-user:latest user/core && \
docker build -t fishapp-chat:latest chat/core && \
docker build -t fishapp-media:latest media/core && \
docker build -t fishapp-store:latest store/core && \
docker build -t fishapp-frontend:latest frontend && \
popd

eval $(minikube docker-env -u)