#!/bin/bash

NETWORK=fishapp_network

docker network create $NETWORK

docker run  \
  -e DB_NAMES='auth_db user_db media_db store_db chat_db checkout_db' \
  -e POSTGRES_PASSWORD='kjdsfhalkshfkdsjfh' \
  -e USER_USERNAME='fishmarket' \
  -e USER_PASSWORD='lkjfdsblkdfjglksjdfhglkdjf' \
  -v $PWD/.script_files/_init-user-db.sh:/docker-entrypoint-initdb.d/_init-user-db.sh \
  --expose 5432 \
  --hostname=fishapp_db \
  --network=$NETWORK \
  --name=fishapp_db \
  --rm \
  postgres
