FROM openjdk:8

RUN apt update

RUN apt install -y nginx python-pip

RUN pip install supervisor

WORKDIR /pulsar-manager

RUN curl -sL https://deb.nodesource.com/setup_10.x | bash -

RUN apt-get install -y nodejs

COPY build/libs/pulsar-manager.jar pulsar-manager.jar

COPY docker/supervisord.conf /etc/

COPY docker/default.conf /etc/nginx/conf.d/

COPY docker/entrypoint.sh /pulsar-manager/

COPY front-end/dist /usr/share/nginx/html/

ENTRYPOINT [ "/pulsar-manager/entrypoint.sh" ]