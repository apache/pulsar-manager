#!/bin/sh

echo 'Starting Mysql Server'

/pulsar-manager/startup.sh

echo 'Starting Pulsar Manager Front end'
nginx

echo 'Starting Pulsar Manager Back end'

supervisord -c /etc/supervisord.conf -n

