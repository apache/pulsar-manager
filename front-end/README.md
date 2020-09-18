# Pulsar manager front end

* Front end is based on [vue-element-admin](https://panjiachen.github.io/vue-element-admin/#/dashboard).

## Compatibility

The pulsar-manager is tested with Chrome browser.

### Set cross domain
Add [proxyTable](https://github.com/apache/pulsar-manager/blob/master/front-end/config/index.js).
For localhost debug
```bash
    proxyTable: {
      '/admin/*': {
        target: 'http://localhost:7750/',
        changeOrigin: true
      },
      '/pulsar-manager/*': {
        target: 'http://localhost:7750/',
        changeOrigin: true
      },
      '/lookup/*': {
        target: 'http://localhost:7750/',
        changeOrigin: true
      }
    },
```


## Deploy production environment by Nginx server

* Add the following parameters to the Nginx server configuration file [prod.env.js](https://github.com/apache/pulsar-manager/blob/master/front-end/config/prod.env.js).
```
module.exports = {
  NODE_ENV: '"production"',
  ENV_CONFIG: '"prod"'
}
```

### Add configuration to the configuration file on Nginx server

* Replace `localhost` with the IP address of the backend service.

```
  listen       9527;
  server_name  0.0.0.0;


  location / {
    root   /usr/share/nginx/html/dist;
    index  index.html index.htm;
  }

  location /admin {
    proxy_pass http://localhost:7750;
  }

  location /pulsar-manager {
    proxy_pass http://localhost:7750;
  }

  location /lookup {
    proxy_pass http://localhost:7750;
  }

  location /bkvm {
    proxy_pass http://localhost:7750;
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

http://nginx-server:9527/#/login
