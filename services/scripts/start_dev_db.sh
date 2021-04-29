#!/bin/bash

NETWORK=fishapp_network

docker network create $NETWORK

docker run  \
  -e DB_NAMES='auth_db user_db media_db store_db chat_db' \
  -e POSTGRES_PASSWORD='kjdsfhalkshfkdsjfh' \
  -e USER_USERNAME='fishmarket' \
  -e USER_PASSWORD='lkjfdsblkdfjglksjdfhglkdjf' \
  -v $PWD/.script_files/_init-user-db.sh:/docker-entrypoint-initdb.d/_init-user-db.sh \
  --ip="192.168.0.123" \
  --hostname=fishapp_db \
  --name=fishapp_db \
  --rm \
  postgres
#  --network=$NETWORK \
#   --network="host" \