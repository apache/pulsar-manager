# Pulsar Admin UI

The admin UI is based on [vue-element-admin](https://panjiachen.github.io/vue-element-admin/#/dashboard).

## Compatibility

The pulsar-manager is tested with Chrome browser.

### Set cross domain
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

## Deploy production environment

* Add the following parameters to the Nginx server configuration file [prod.env.js](https://github.com/streamnative/pulsar-manager/blob/master/front-end/config/prod.env.js).
```
module.exports = {
  NODE_ENV: '"production"',
  ENV_CONFIG: '"prod"',
  BASE_API: '"your nginx server address"',
  USE_TLS: 'false',
  GRAFANA_ADDRESS: '""',
  PROMETHEUS_ADDRESS: '""',
  GRAFANA_ENABLE: 'false',
  GRAFANA_TOKEN: '""'
}
```

### Add configuration to the configuration file on Nginx server

```
  listen       9526;
  server_name  localhost;


  location / {
    root   /usr/share/nginx/html/dist;
    index  index.html index.htm;
  }

  location /admin {
    proxy_pass http://pulsar-service:8080;
  }
```

### Build file for production

```
cd front-end
npm run build:prod
```
Copy file of dist to Nginx server /usr/share/nginx/html/dist

### Copy dist file to Nginx server and restart Nginx
Open a browser and visit the following address:

http://nginx-server:9526/#/login
