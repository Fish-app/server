#!/bin/bash
export COMPOSE_DOCKER_CLI_BUILD=1
export DOCKER_BUILDKIT=1 
export TRAEFIK_CERT_PATH="./config/traefik/cert.json"
export TRAEFIK_CONF_PATH="./config/traefik/config.toml"
cancel=true
configOk=false

echo "You are about to run the production script, do you think this is an accident and want to cancel this? "
read -e -p "To continue, please erase yes and write [NO]: " -i "yes"  answer

if [ $answer = "NO" ]; then
	cancel=false

	if [ -d $TRAEFIK_CERT_PATH ]; then
		# trafik is a empty dir, because of git/docker. delete folder first
		echo "Removed broken cert dir"
		rmdir $TRAEFIK_CERT_PATH
	fi

	if [ -f $TRAEFIK_CERT_PATH ] && [ -f $TRAEFIK_CONF_PATH ] && [ -f .env ]; then
		echo "Config is OK"
		configOk=true
	else
		echo "Certificate file missing, creating new"
		touch ./config/traefik/cert.json
		chmod 0600 ./config/traefik/cert.json
		configOk=true
	fi
fi



if  [ "$configOk" = true  ] && [ "$cancel" = false ]; then
	echo "your call, now running production!"



	docker-compose -f docker-compose.yml up -d 
	#	echo "The was the api was rebuilt if any files changed, use CTRL-C to detach from the log console"
	#docker-compose -f docker-compose.yml logs -f
else
	echo "You have falled off the chair. Be careful!"
fi
