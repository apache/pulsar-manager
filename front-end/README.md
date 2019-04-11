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

#### Set route redirect
Add [proxyTable](https://github.com/streamnative/pulsar-manager/blob/master/front-end/config/index.js)
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
To do 

#### Development

You can start a pulsar standalone. The Pulsar Admin UI will automatically connect to pulsar standalone via `http://localhost:8080`. Now you try to go to `tenants` page and create `tenant`.