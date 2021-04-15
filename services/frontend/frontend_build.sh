#!/bin/bash


docker build ./liberty -f ./build.Dockerfile -t fishapp-frontend_builder:latest
docker run -t --rm --mount type=bind,source=$PWD/src/main/frontend,target=/node_app --name  fishapp_frontend_builder fishapp-frontend_builder:latest
