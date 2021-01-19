#! /bin/sh
# copy defaults if no env file is in place
if [ ! -f .env ]; then
  echo "You had no .env file, so I copied the example file."
  cp .env-example .env
  echo "\nThe contents of the .env:"
  cat .env
  echo "\nPlease wait...\n"
  sleep 10
  echo "From now on use the build and kill .sh scripts!"
  sleep 3
fi

./build-server-on.docker.sh