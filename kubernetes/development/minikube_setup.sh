#!/bin/bash

minikube start
minikube addons enable ingress
minikube addons enable metrics-server

# open issue: https://github.com/kubernetes/ingress-nginx/issues/5401
kubectl delete -A ValidatingWebhookConfiguration ingress-nginx-admission

minikube ssh  "nohup sudo mkdir -p -m 777 /mnt/data/fishapp/{jwt-pubkey,images}/ " &> /dev/null  &
