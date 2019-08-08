#!/bin/bash

echo 'Starting Pulsar Manager Front end'
nginx

echo 'Starting Pulsar Manager Back end'

supervisord -c /etc/supervisord.conf -n

