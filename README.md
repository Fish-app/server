# Fishapp server

-TODO: write an ok readme

## Requierments

----
to build the services'
maven ([linux](https://packages.debian.org/search?keywords=maven) [win](https://letmegooglethat.com/?q=How+to+install+maven+on+windows) [osx](https://formulae.brew.sh/formula/maven))
is used. [Docker](https://docs.docker.com/engine/install/) is used as the kubernetes container runtime, remember
the [post installation steps](https://docs.docker.com/engine/install/linux-postinstall/) (I always forget).

For controlling the cluster [kubectl](https://kubernetes.io/docs/tasks/tools/) is needed. You will need a kubernetes
cluster to run on, if you are only testing locally [minikube](https://minikube.sigs.k8s.io/docs/start/) can be used

## Running locally

---

If the requermentes over are satesfied you can start the cluster (i.e the kub cluster not anything in it) and install
the cluster addons with the  ``kubernetes/development/minikube_setup.sh`` script. You can now run ``minikube dashboard``
to see the cluster dashboard. To build the temporary dev db, use the ``kubernetes/development/build_dev_containers.sh``
.  
To build everything else run the ``kubernetes/development/build_to_kubernetes.sh`` script, this will build all the
images in the local minikube environment. To start upp all kubernetes stuff run
the ``kubernetes/development/start_all_services.sh`` script.

If you did any of the steps out of order or have made the system fubar you can use
the ``` kubernetes/development/i_messed_up_reset_everything.sh``` to reset the minikube env.

Because your local kubernestes ingess pont probably does not resolve to fishapp.no you have to manually enter this in to
your hosts. On systems where it works you can use ```sudo echo fishapp.no $(minikube ip) >> /etc/hosts ```.

you can now use the fishapp if you did not do something wrong. There are test query's that can be imported in
to [insomnia api test thingy](https://insomnia.rest/download) from ```doc/api-requests.yaml```. The accompanying app is
located at: https://github.com/Fish-app/mobile-app.

## Deployment

----
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