# Fishapp Server
This server was developed as part of a bachelor thesis at NTNU Ålesund. The application used with this server can be found [here](https://github.com/Fish-app/mobile-app).

## Requirements

To build the services,
maven ([linux](https://packages.debian.org/search?keywords=maven) [win](https://letmegooglethat.com/?q=How+to+install+maven+on+windows) [osx](https://formulae.brew.sh/formula/maven))
is used. [Docker](https://docs.docker.com/engine/install/) is used as the kubernetes container runtime, remember
the [post installation steps](https://docs.docker.com/engine/install/linux-postinstall/) (I always forget).

For controlling the cluster, [kubectl](https://kubernetes.io/docs/tasks/tools/) is needed (if you user microk8s(which you should) than it's not needed because it is bundled with the install ``microk8s kubectl``). 
You will need a Kubernetes cluster to run on. If you are only testing locally, [minikube](https://minikube.sigs.k8s.io/docs/start/) can be used, but i would recommend using [Microk8s](https://microk8s.io/) given that mikrokube does not have all the features required.

## Running locally

The project is currently using microk8s.
These are the steps to run the project:

1. /kubernetes/development/MicroK8s/init_node.sh
2. /kubernetes/development/MicroK8s/build_dev_db.sh
3. /kubernetes/development/MicroK8s/build_containers.sh
4. Then all the ``*.env.example`` files in the ``kubernetes/secrets`` needs to have the ``.example``-suffix removed. sTthe default values shold work for dev with the exeption of the api key for the chekout module. You can still run the cluster, but most calls to the chekout modul will return ``500 -  somthing somthing server error``. A free test acount can be created [here](https://portal.dibspayment.eu/registration).

## Deployment

The server is meant to run on a Kubernets cluster in production. Given the nature of kubernetes a single readme is not
going to suffice. But the key things to change is:

- the ```kubernetes/volumes.yaml``` file should be changed to use whatever volumes you want to use in prod. All volumes are
  currently set up via ``PersistentVolumeClaim``'s so changing the underlying volume should be trivial.
- the development database is not set up to persist data over restarts. This is intentionally, so the "dev" db does not
  suddenly become the "prod" db, through the power of laziness and convenience. The service's db access is configured
  individually either through the ``services/<service>/core/config/server.env`` and be included in the war, or by
  overwriting the variables when running the image, whichever is more convenient. (the latter can be done with
  kubernetes secrets)
- the service images are currently fetched locally, if you have the images hosted elswhere this has to change
- The all the ``*.env.example`` files in the ``kubernetes/secrets`` has to be filled in and had the ``.example`` suffix removed. 
