#/bin/bash


#docker build . -t mavan_docker_builder:latest

if ! [ -d "./.built_war" ]; then
    mkdir "./.built_war"
fi
docker run --rm \
-v $PWD:/app \
-v mavan_docker_builder_m2_files:/root/.m2 \
-w /app \
--name=mavan_docker_builder \
maven:3.6.3-openjdk-11 /bin/sh -c "mvn clean compile package && mv -f target/*.war ./.built_war/ROOT.war &&  chown nobody:nogroup ./.built_war/* && chmod 777 ./.built_war/*"
#/bin/bash


