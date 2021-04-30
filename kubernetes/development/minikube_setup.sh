#!/bin/bash

minikube start --cpus=4 --addons=[dashboard, ingress, metrics-server]
minikube addons enable ingress
minikube addons enable metrics-server

# open issue: https://github.com/kubernetes/ingress-nginx/issues/5401
kubectl delete -A ValidatingWebhookConfiguration ingress-nginx-admission

minikube ssh  "sudo mkdir -p -m 777 /mnt/data/fishapp/{jwt-pubkey,images}/ " &> /dev/null  &
