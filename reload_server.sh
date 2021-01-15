#!/bin/sh
./maven_build.sh
docker-compose --env-file ./config_files/main.env up -d --force-recreate --no-deps --build -V server