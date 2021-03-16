#! /bin/sh
# copy defaults if no env file is in place
if [ ! -f .env ]; then
    echo "You had no .env file, so I copied the example file."
    cp .env-example .env
    echo "\nThe contents of the .env:"
    cat .env
    echo "\nYou can now run './develop.sh'.\n"
else
	echo "SKIPPED: .env already exsists!"
fi
