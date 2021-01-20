#!/bin/sh
export COMPOSE_DOCKER_CLI_BUILD=1
export DOCKER_BUILDKIT=1 
export COMPOSE_FILE="docker-compose.yml"

echo "Stopping docker compose $COMPOSE_FILE"
docker-compose -f $COMPOSE_FILE down --remove-orphans &&
docker-compose -f $COMPOSE_FILE up -d --build database api reverse-proxy
echo "The was the api was rebuilt if any files changed, use CTRL-C to detach from the log console"
docker-compose -f $COMPOSE_FILE logs -f
