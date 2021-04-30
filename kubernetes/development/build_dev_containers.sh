#!/bin/bash

eval $(minikube docker-env)

docker build . -t fishapp-dev-db:latest

minikube image load fishapp-dev-db:latest
eval $(minikube docker-env -u)
