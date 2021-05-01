#!/bin/bash

# --alsologtostderr -v=8
minikube start --cpus=10
minikube addons enable ingress
minikube addons enable metrics-server
minikube addons enable dashboard

# open issue: https://github.com/kubernetes/ingress-nginx/issues/5401
kubectl delete -A ValidatingWebhookConfiguration ingress-nginx-admission

minikube ssh  "sudo mkdir -p -m 777 /mnt/data/fishapp/{jwt-pubkey,images}/ " &> /dev/null  &
