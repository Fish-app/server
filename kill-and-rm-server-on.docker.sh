#!/bin/sh
export COMPOSE_DOCKER_CLI_BUILD=1
export DOCKER_BUILDKIT=1 
export COMPOSE_FILE="docker-compose.yml"

echo "Stopping docker compose $COMPOSE_FILE"
docker-compose -f $COMPOSE_FILE down --remove-orphans
