## to build
1. Generate your environment with `./generate.sh`
1. Build during development with `./develop.sh`

## deploying
1. Login and clone this source code to your server.
1. Generate your environment with `./generate.sh`
1. **IMPORTANT:** Inspect and **adjust** your `.env` file; set new database passwords, and set the correct TLD (domainname) for SSL certificate enrollment.
1. Run the `./production.sh` script and read the instructions on-screen.
