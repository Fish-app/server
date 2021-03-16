#!/bin/bash
export COMPOSE_DOCKER_CLI_BUILD=1
export DOCKER_BUILDKIT=1 
cancel=true

echo "You are about to run the production script, do you think this is an accident and want to cancel this? "
read -e -p "To continue, please erase yes and write [NO]: " -i "yes"  answer

if [ $answer = "NO" ]; then
	cancel=false
	echo "OK"
fi

if  [ -f .env ] && [ "$cancel" = false ]; then
	echo "your call, now running production!"
	docker-compose -f docker-compose.yml up -d 
#	echo "The was the api was rebuilt if any files changed, use CTRL-C to detach from the log console"
	#docker-compose -f docker-compose.yml logs -f
else
	echo "You have falled off the chair. Be careful!"
fi
