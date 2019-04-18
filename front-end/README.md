## Pulsar Admin UI

The admin UI is based on [vue-element-admin](https://panjiachen.github.io/vue-element-admin/#/dashboard).

### Build

#### Installation


```bash

# clone repository from github
git clone https://github.com/streamnative/pulsar-manager
cd pulsar-manger/front-end

# install app's dependencies
$ npm install

```

#### Set cross domain
Add [proxyTable](https://github.com/streamnative/pulsar-manager/blob/master/front-end/config/index.js).
For localhost debug
```bash
    proxyTable: {
      '/admin/*': {
        target: 'http://localhost:8080/',
        changeOrigin: true
      }
    },
```

#### Usage

```bash

# start to serve with hot reload at localhost:9527

# start pulsar standalone
docker pull apachepulsar/pulsar:2.3.0
docker run -d -it -p 6650:6650 -p 8080:8080 -v $PWD/data:/pulsar/data --name pulsar-standalone apachepulsar/pulsar:2.3.0 bin/pulsar standalone

# build for production with minification
$ npm run dev

# open browers visit following address
# login
http://localhost:9527/#/login

account: admin
password: admin

```

#### Support grafana and prometheus

##### Start grafana and prometheus
[Apache Pulsar Grafana Dashboard](https://github.com/streamnative/apache-pulsar-grafana-dashboard)

##### Set admin UI for dev or prod
[dev.env.js](https://github.com/tuteng/pulsar-manager/blob/feature/add-readme/front-end/config/dev.env.js) or [prod.env.js](https://github.com/tuteng/pulsar-manager/blob/feature/add-readme/front-end/config/prod.env.js)

* GRAFANA_ADDRESS: Grafana service address.
* PROMETHEUS_ADDRESS: Grometheus service address.
* GRAFANA_ENABLE: To turn on service monitor. The default `false`.
* GRAFANA_TOKEN: Token to visit Grafana API. Get [Token](https://grafana.com/docs/http_api/auth/).
```
GRAFANA_ADDRESS: '"http://localhost:3000"',
PROMETHEUS_ADDRESS: '"http://localhost:9090"',
GRAFANA_ENABLE: 'false',
GRAFANA_TOKEN: '""'
```

#### Support permissions

* grant-permission for topics and namespaces
* revoke-permission for topics and namespaces

##### Prepare

* Start Pulsar with TLS
[Pulsar with TLS](http://pulsar.apache.org/docs/en/security-overview/)

* Start Nginx with TLS
[Nginx with TLS](http://nginx.org/en/docs/http/configuring_https_servers.html)

##### Set front end
Add USE_TLS in file [dev.env.js](https://github.com/tuteng/pulsar-manager/blob/feature/add-readme/front-end/config/dev.env.js) or [prod.env.js](https://github.com/tuteng/pulsar-manager/blob/feature/add-readme/front-end/config/prod.env.js)
```
USE_TLS: 'true'
```
