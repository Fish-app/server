# Fishapp server

- TODO: write an ok readme

## Requierments

to build the services'
maven ([linux](https://packages.debian.org/search?keywords=maven) [win](https://letmegooglethat.com/?q=How+to+install+maven+on+windows) [osx](https://formulae.brew.sh/formula/maven))
is used. [Docker](https://docs.docker.com/engine/install/) is used as the kubernetes container runtime, remember
the [post installation steps](https://docs.docker.com/engine/install/linux-postinstall/) (I always forget).

For controlling the cluster [kubectl](https://kubernetes.io/docs/tasks/tools/) is needed (if you user microk8s(which you should) than it's not needed because it is bundeled with the install ``microk8s kubectl``). 
You will need a kubernetes cluster to run on, if you are only testing locally [minikube](https://minikube.sigs.k8s.io/docs/start/) can be used but i wold recomend using [Microk8s](https://microk8s.io/) given that mikrokube does not have all the features requierd.

## Running locally

currentlu using microk8s

1. /kubernetes/development/MicroK8s/init_node.sh
1. /kubernetes/development/MicroK8s/build_dev_db.sh
1. /kubernetes/development/MicroK8s/build_containers.sh
1. The all the ``*.env.example`` files in the ``kubernetes/secrets`` needs to have the ``.example`` suffix removed. the default values shold work for dev with the exeption of the api key for the chrkout module. you can still run the cluster but most calls to the chekout modul will return ``500 -  somthing somthing server error`` a free test acount can be created [here](https://portal.dibspayment.eu/registration).

## Deployment

The server is ment to run on a kubernets cluster in production. Given the nature of kubernetes a single readme is not
going to suffice. But the key things to change is:

- the ```kubernetes/volumes.yaml``` file shod be changed to use whatever volumes you want to use in prod. All volumes ar
  currently set up via ``PersistentVolumeClaim``'s so changing the underlying volume should be trivial.
- the development database is not set up to persist data over restarts thi is intentionally, so the "dev" db does not
  suddenly become the "prod" db, through the power of laziness and convenience. The service's db access is configured
  individually ether through the ``services/<service>/core/config/server.env`` and be included in the war or by
  overwriting the variables when running the image, whichever is more convenient. (the latter can be done with
  kubernetes secrets)
- the service images are currently fetched locally if you have the images hosted elswhere this has to change
- The all the ``*.env.example`` files in the ``kubernetes/secrets`` has to be filled in and had the ``.example`` suffix removed. 
