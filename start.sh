#! /bin/bash
./maven_build.sh
docker-compose  --env-file ./config_files/main.env up --build -V