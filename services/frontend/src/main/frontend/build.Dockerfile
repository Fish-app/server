
FROM node:15-buster

RUN apt update && apt install -y yarn

ARG UID=1000
ARG GID=1000

#USER ${UID}:${GID}

VOLUME /node_app
WORKDIR /node_app

CMD yarn install  && yarn build && chmod 777 -R /node_app/node_modules && chmod 777 -R /node_app/build