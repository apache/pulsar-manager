# Pulsar manager

Pulsar manager is a web-based GUI management tool for managing and monitoring Pulsar.

## Feature

* Tenants Management
* Namespaces Management
* Topics Management
* Subscriptions Management
* Brokers Management
* Clusters Management
* Dynamic environments with multiple changes

## Feature preview

### Log in

Use the default account (`pulsar`) and the default password (`pulsar`) to log in.

![pulsar-manager-login](docs/img/pulsar-manager-login.gif)

### Configure environment

The puslar-manager supports multiple environment configurations and can manage multiple environments conveniently. 

![pulsar-manager-environments](docs/img/pulsar-manager-environments.gif)

### Manage tenants

![pulsar-manager-tenants](docs/img/pulsar-manager-tenants.gif)


### Manage namespaces

![pulsar-manager-namespaces](docs/img/pulsar-manager-namespaces.gif)

### Manage topics

![pulsar-manager-topics](docs/img/pulsar-manager-topics.gif)


### Manage subscriptions

![pulsar-manager-subscriptions](docs/img/pulsar-manager-subscriptions.gif)

### Manage clusters

![pulsar-manager-clusters](docs/img/pulsar-manager-clusters.gif)

### Manage brokers

![pulsar-manager-brokers](docs/img/pulsar-manager-brokers.gif)


### Topics monitoring

The pulsar-manager can monitor topics and subscriptions.

![pulsar-manager-topics-monitors](docs/img/pulsar-manager-topics-monitors.gif)


## Prerequisites
* Java 8 or later
* Node 10.15.3 or later
* Npm 6.4.1 or later
* Pulsar 2.4.0 or later
* Docker

## Preparation

### Start Pulsar standalone

```
docker pull apachepulsar/pulsar:2.4.0
docker run -d -it -p 6650:6650 -p 8080:8080 -v $PWD/data:/pulsar/data --name pulsar-manager-standalone apachepulsar/pulsar:2.4.0 bin/pulsar standalone
```

### Build environment with Docker or Local

#### Build environment with Docker

```
docker pull streamnative/pulsar-manager
docker run -it  -p 9527:9527 -e REDIRECT_HOST=front-end-ip -e REDIRECT_PORT=front-end-port streamnative/pulsar-manager
```
* REDIRECT_HOST: the IP address of the front-end server.
* REDIRECT_PORT: the port of the front-end server.


#### Build local environment

1. Download source code.

```
git clone https://github.com/streamnative/pulsar-manager
```

2. Build and start the backend.
```
cd pulsar-manager
./gradlew build -x test
java -jar ./build/libs/pulsar-manager.jar
```

3. Build and start the front end.

```
cd pulsar-manager/front-end
npm install --save
npm run dev
```

## Access Pulsar manager website

Use the account and the password to log in to the Pulsar manager website.  
account: `pulsar`  
password: `pulsar`  

http://localhost:9527/


# Back end

For more information about the front end, see [pulsar-manager-backend](https://github.com/tuteng/pulsar-manager/blob/feature/add-readme-for-backend-and-front-end/src/README.md).


# Front end

For more information about the front end, see [pulsar-manager-frontend](https://github.com/tuteng/pulsar-manager/tree/feature/add-readme-for-backend-and-front-end/front-end)

## To Do

* Support Schema
* Support Function, Source and Sink
* Support Bookie
* Support back-end paging query and filtering
* Support authentication and authorization
* Embeded Prometheus and Grafana
* Support peek messages (include single and batch)

