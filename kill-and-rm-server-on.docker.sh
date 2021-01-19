#!/bin/sh
export COMPOSE_DOCKER_CLI_BUILD=1
export DOCKER_BUILDKIT=1 
export COMPOSE_DEV_FILE="docker-compose-dev.yml"

echo "Stopping docker compose $COMPOSE_DEV_FILE"
docker-compose -f $COMPOSE_DEV_FILE down --remove-orphans
