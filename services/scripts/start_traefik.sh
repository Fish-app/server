#!/bin/bash

docker run -d --rm -p 8080:8080 -p 80:80 -p 5432:5432 \
  -v $PWD/.script_files/traefik.yml:/etc/traefik/traefik.yml \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --name=traefik \
  traefik:v2.0