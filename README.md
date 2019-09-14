# Apache Pulsar manager

Apache Pulsar manager is a web-based GUI management tool for managing and monitoring Pulsar.

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
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fstreamnative%2Fpulsar-manager.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fstreamnative%2Fpulsar-manager?ref=badge_shield)


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

1. Start Pulsar standalone.

    ```
    docker pull apachepulsar/pulsar:2.4.0
    docker run -d -it -p 6650:6650 -p 8080:8080 -v $PWD/data:/pulsar/data --name pulsar-manager-standalone apachepulsar/pulsar:2.4.0 bin/pulsar standalone
    ```

2. Build an environment. 

    You can **build an environment with Docker** or **build a local environment**.

   * Build an environment with Docker

        * `REDIRECT_HOST`: the IP address of the front-end server.
            
        * `REDIRECT_PORT`: the port of the front-end server.

        * `DRIVER_CLASS_NAME`: the dirver class name of MySQL.

        * `URL`: the url of MySQL jdbc, example: jdbc:mysql://localhost:3306/pulsar_manager?useSSL=false

        * `USERNAME`: the username of MySQL

        * `PASSWORD`: the password of MySQL

        ```
        docker pull streamnative/pulsar-manager
        docker run -it -p 9527:9527 -e REDIRECT_HOST=front-end-ip -e REDIRECT_PORT=front-end-port -e DRIVER_CLASS_NAME=com.mysql.jdbc.Driver -e URL='jdbc-url' -e USERNAME=root -e PASSWORD=pulsar pulsar-manager /bin/sh
        ```

        This is an example:
        
        ```
        docker run -it -p 9527:9527 -e REDIRECT_HOST=http://192.168.0.104 -e REDIRECT_PORT=9527 -e DRIVER_CLASS_NAME=org.postgresql.Driver -e URL='jdbc:postgresql://127.0.0.1:5432/pulsar_manager' -e USERNAME=pulsar -e PASSWORD=pulsar -v $PWD:/data pulsar-manager:latest /bin/sh
        ```

   * Build a local environment

        (1) Download the source code.

        ```
        git clone https://github.com/streamnative/pulsar-manager
        ```

        (2) Build and start the backend.
        ```
        cd pulsar-manager
        ./gradlew build -x test
        java -jar ./build/libs/pulsar-manager.jar
        ```

        (3) Build and start the front end.

        ```
        cd pulsar-manager/front-end
        npm install --save
        npm run dev
        ```

3. Access Pulsar manager website.

    Use the account and the password to log in to the Pulsar manager website as below.  
          
   * Account: `pulsar`  
   * Password: `pulsar`  
   * Pulsar manager website: http://localhost:9527/


## Back end

For more information about the back end, see [pulsar-manager-backend](https://github.com/streamnative/pulsar-manager/blob/master/src/README.md).


## Front end

For more information about the front end, see [pulsar-manager-frontend](https://github.com/streamnative/pulsar-manager/blob/master/front-end/README.md).



## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fstreamnative%2Fpulsar-manager.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fstreamnative%2Fpulsar-manager?ref=badge_large)