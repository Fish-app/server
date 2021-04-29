#!/bin/bash

minikube start
minikube addons enable ingress
minikube addons enable metrics-server
minikube ssh  "nohup sudo mkdir mkdir -p -m 777 /mnt/data/fishapp/{jwt-pubkey, images}/ && " &
