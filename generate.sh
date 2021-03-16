#! /bin/bash
# copy defaults if no env file is in place
if [ ! -f .env ]; then
    echo "Generated '.env' from example file."
    cp .env-example .env
    echo "\nThe contents of the .env:"
    cat .env
    echo "\nYou can now run './develop.sh'.\n"
else
	echo "SKIPPED: .env already exsists!"
fi
