#!/bin/bash

microk8s kubectl create secret generic chekout-secrets --from-env-file=./app_config.env

microk8s kubectl create secret generic default-logins-secrets --from-env-file=./default_users.env

microk8s kubectl create secret generic inter-container-logins-secrets --from-env-file=./inter_container_users.env