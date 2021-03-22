#!/bin/bash

NETWORK=fishapp_network
DB_NAMES='auth_db user_db'

docker network create $NETWORK

docker run  \
  -e DB_NAMES='auth_db user_db' \
  -e POSTGRES_PASSWORD='kjdsfhalkshfkdsjfh' \
  -e USER_USERNAME='fishmarket' \
  -e USER_PASSWORD='lkjfdsblkdfjglksjdfhglkdjf' \
  -v $PWD/_init-user-db.sh:/docker-entrypoint-initdb.d/_init-user-db.sh \
  --hostname=fishapp_db \
  --network=$NETWORK \
  --name=fishapp_db \
  --rm \
  postgres
