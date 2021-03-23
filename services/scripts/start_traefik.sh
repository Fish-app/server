#!/bin/bash

Jør til compose
docker run  --rm -p 8080:8080 -p 80:80 -p 5432:5432 \
  -v $PWD/.script_files/traefik.yml:/etc/traefik/traefik.yml \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --name=traefik \
  traefik:2.4.7 &

docker run  --name jaeger \
  -p 5775:5775/udp \
  -p 6831:6831/udp \
    -p 6831:6831/tcp \
  -p 6832:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 14268:14268 \
  -p 14250:14250 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.22