## Pulsar Admin manager

### Feature

* Tenants Management
* Namespaces Management
* Topics Management
* Subscriptions Management
* Brokers Management
* Clusters Management
* Dynamic environments with multiple changes

### Feature preview

#### Login

default account `pulsar` default password: `pulsar`

![pulsar-manager-login](docs/img/pulsar-manager-login.gif)

#### Configuration environment

The puslar-manager supports multi-environment configuration and can conveniently manage multiple environments.

![pulsar-manager-environments](docs/img/pulsar-manager-environments.gif)

#### Manage tenants

![pulsar-manager-tenants](docs/img/pulsar-manager-tenants.gif)


#### Manage namespaces

![pulsar-manager-namespaces](docs/img/pulsar-manager-namespaces.gif)

#### Manage topics

![pulsar-manager-topics](docs/img/pulsar-manager-topics.gif)


#### Manage subscriptions

![pulsar-manager-subscriptions](docs/img/pulsar-manager-subscriptions.gif)

#### Manage clusters

![pulsar-manager-clusters](docs/img/pulsar-manager-clusters.gif)

#### Manage brokers

![pulsar-manager-brokers](docs/img/pulsar-manager-brokers.gif)


#### Topics monitoring

The pulsar-manager can do some simple monitoring on topics and subscriptions.

![pulsar-manager-topics-monitors](docs/img/pulsar-manager-topics-monitors.gif)


### Prerequisites
* Java 8 or later
* Node 10.15.3 or later
* Npm 6.4.1 or later
* Pulsar 2.4.0 or later
* Docker

### Preparation

#### Start Pulsar standalone

```
docker pull apachepulsar/pulsar:2.4.0
docker run -d -it -p 6650:6650 -p 8080:8080 -v $PWD/data:/pulsar/data --name pulsar-manager-standalone apachepulsar/pulsar:2.4.0 bin/pulsar standalone
```

### Build environment with Docker

```
docker pull streamnative/pulsar-manager
docker run -it  -p 9527:9527 -e REDIRECT_HOST=front-end-ip -e REDIRECT_PORT=front-end-port streamnative/pulsar-manager
```
* REDIRECT_HOST: the IP address of the front-end server.
* REDIRECT_PORT: the port of the front-end server.

### Open your browser and use the account and the password to access the following address.

username: pulsar
password: pulsar
http://localhost:9527/

Use the account and the password to sign in to the website as below.

### Build local environment

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


### Back end
For more information about the front end, see [pulsar-manager-backend](https://github.com/tuteng/pulsar-manager/blob/feature/add-readme-for-backend-and-front-end/src/README.md).


### Front end

For more information about the front end, see [front-end-deploy](https://github.com/streamnative/pulsar-manager/blob/master/front-end/README.md)

## To Do

* Support Schema
* Support Function, Source and Sink
* Support Bookie
* Support back-end paging query and filtering
* Support authentication and authorization
* Embeded Prometheus 和 Grafana
* Support peek messages (include single and batch)

