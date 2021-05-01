#!/bin/bash

docker build ./.. -t localhost:32000/fishapp-dev-db:latest
docker push localhost:32000/fishapp-dev-db:latest
