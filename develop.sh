#!/bin/bash
export COMPOSE_DOCKER_CLI_BUILD=1
export DOCKER_BUILDKIT=1 

if [  -f .env ]; then
	docker-compose -f docker-compose.yml -f docker-compose.development.yml up --build -d api database
else
	echo "Environment file .env is missing, please generate it by running './generate.sh'."
fi
