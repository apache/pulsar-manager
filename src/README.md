# Pulsar Admin Backend

Pulsar manager backend is a supplement and improvement to Pulsar broker.

* Handle complex query requests
* Route requests (add, delete, update) to brokers
* Support multi broker, dynamic change environment

### Backend supported configurations are as follows

| Name | Default |Description
| ------- | ------- | ------- |
| `server.port` | 7750 | Port of backend services |
| `pulsar-manager.account` | pulsar | Login account |
| `pulsar-manager.password` | pulsar | Login password |
| `redirect.host` | localhost | Ip address of front-end service |
| `redirect.port` | 9627 | Port address of front-end service |
| `insert.stats.interval` | 30000ms | Time interval for collecting statistical information |
| `clear.stats.interval` | 300000ms | Time interval for cleaning statistics |

### How to set parameters when starting back-end services

```
java -jar ./build/libs/pulsar-manager.jar --redirect.host=http://localhost --redirect.port=9527 insert.stats.interval=600000
```
