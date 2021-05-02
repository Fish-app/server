#!/bin/bash


# https://plainice.com/microk8s-bash-completion
microk8s enable dns dashboard ingress metrics-server

# enables a local image repo
microk8s status --wait-ready
