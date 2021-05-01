#!/bin/bash


# Apply the cert-manager deployment
microk8s kubectl apply --validate=false -f https://github.com/jetstack/cert-manager/releases/download/v1.3.0/cert-manager.yaml

microk8s kubectl apply -f ./production_issuer.yaml
microk8s kubectl apply -f ./staging_issuer.yaml

