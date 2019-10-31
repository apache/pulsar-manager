# Pulsar manager backend

Pulsar manager backend is a supplement and improvement to Pulsar broker.

* Handle complex query requests
* Route requests (add, delete, update) to brokers
* Support multi broker, dynamic change environment

### Supported configurations of backend 

| Name | Default |Description
| ------- | ------- | ------- |
| `server.port` | 7750 | Port of backend service |
| `pulsar-manager.account` | pulsar | Login account |
| `pulsar-manager.password` | pulsar | Login password |
| `redirect.host` | localhost | IP address of front-end service |
| `redirect.port` | 9527 | Port of front-end service |
| `insert.stats.interval` | 30000ms | Time interval for collecting statistical information |
| `clear.stats.interval` | 300000ms | Time interval for cleaning statistics |

### How to set parameters when starting back-end services

```
java -jar ./build/libs/pulsar-manager.jar --redirect.host=http://localhost --redirect.port=9527 insert.stats.interval=600000
```

### Use custom databases

If you have a large amount of data, you can use a custom database. The following is an example of PostgreSQL.   

1. Initialize database and table structures using [file](https://github.com/streamnative/pulsar-manager/tree/master/src/main/resources/META-INF/sql/postgresql-schema.sql).

2. Modify the [configuration file](https://github.com/streamnative/pulsar-manager/blob/master/src/main/resources/application.properties) and add PostgreSQL configuration

```
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/pulsar_manager
spring.datasource.username=postgres
spring.datasource.password=postgres
```

3. Compile to generate a new executable jar package

```
./gradlew -x build -x test
```

### Enable JWT Auth

There are two ways to enable JWT authentication

* backend.jwt.token  Token for superuser, configuration during cluster initialization
* jwt.broker.token.mode  Two Methods of Generating token, SECRET and PRIVATE
* jwt.broker.public.key Configure this option if you are using the PRIVATE mode
* jwt.broker.private.key Configure this option if you are using the PRIVATE mode
* jwt.broker.secret.key Configure this option if you are using the SECRET mode

For more information, please refer to [Apache Pulsar](http://pulsar.apache.org/docs/en/security-token-admin/)

1. Use command line

```
java -jar ./build/libs/pulsar-manager.jar --redirect.host=http://localhost --redirect.port=9527 insert.stats.interval=600000 --backend.jwt.token=token --jwt.broker.token.mode=PRIVATE --jwt.broker.private.key=file:///path/broker-private.key --jwt.broker.public.key=file:///path/broker-public.key
```

2. Use configuration application.properties

```
backend.jwt.token=token

jwt.broker.token.mode=PRIVATE
jwt.broker.public.key=file:///path/broker-public.key
jwt.broker.private.key=file:///path/broker-private.key

or 
jwt.broker.token.mode=SECRET
jwt.broker.secret.key=file:///path/broker-secret.key
```