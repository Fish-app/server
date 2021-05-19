FROM node:15-buster
RUN apt update && apt install -y yarn
VOLUME /node_app
WORKDIR /node_app
CMD yarn install  && \
 yarn build && \
 chown $UID:$GID -R /node_app/node_modules && \
 chown $UID:$GID -R /node_app/build