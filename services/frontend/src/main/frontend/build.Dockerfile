
FROM node:15-buster

RUN apt update && apt install -y yarn

VOLUME /node_app
WORKDIR /node_app

CMD yarn build && chmod -rf 777 /build