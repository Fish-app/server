#!/bin/sh
export COMPOSE_DOCKER_CLI_BUILD=1
export DOCKER_BUILDKIT=1 

if [  -f .env ]; then
	docker-compose -f docker-compose.yml -f docker-compose.development.yml up --build -d api database
	echo "The was the api was rebuilt if any files changed, use CTRL-C to detach from the log console"
#	docker-compose -f docker-compose.yml logs -f
else
	echo "Environment file .env is missing, please generate it by running './generate.sh'."
fi
