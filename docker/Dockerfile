FROM openjdk:8

WORKDIR /pulsar-manager

RUN curl -sL https://deb.nodesource.com/setup_10.16 | bash -

RUN apt-get install -y nodejs

RUN curl -sL https://deb.nodesource.com/setup_10.x | bash -

RUN ./gradlew build -x test

RUN cd front-end && npm install --save-dev