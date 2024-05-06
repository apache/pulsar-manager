#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#


if [[ "$URL" = "jdbc:postgresql://127.0.0.1:5432/pulsar_manager" ]]
then
echo 'Starting PostGreSQL Server'

addgroup pulsar
adduser --disabled-password --ingroup pulsar pulsar
mkdir -p /run/postgresql
chown -R pulsar:pulsar /run/postgresql/
mkdir -p /data
chown -R pulsar:pulsar /data
chown pulsar:pulsar /pulsar-manager/init_db.sql
chmod 750 /data

su - pulsar -s /bin/sh /pulsar-manager/startup.sh
fi

echo 'Starting Pulsar Manager Front end'
nginx

echo 'Starting Pulsar Manager Back end'

touch /pulsar-manager/supervisor.sock
chmod 777 /pulsar-manager/supervisor.sock


if [[ -n "$JWT_TOKEN" ]] && [[ -n "$PUBLIC_KEY" ]] && [[ -n "$PRIVATE_KEY" ]]
then
  echo "Use public key and private key to init JWT."
  supervisord -c /etc/supervisord-private-key.conf -n
elif [[ -n "$JWT_TOKEN" ]] && [[ -n "$SECRET_KEY" ]]
then
  echo "Use secret key to init JWT."
  supervisord -c /etc/supervisord-secret-key.conf -n
elif [[ -n "$JWT_TOKEN" ]]
then
  echo "Enable JWT auth."
  supervisord -c /etc/supervisord-token.conf -n
elif [[ -n "$SPRING_CONFIGURATION_FILE" ]]
then
  echo "Start Pulsar Manager by specifying a configuration file."
  supervisord -c /etc/supervisord-configuration-file.conf -n
else
  echo "Start servie no enable JWT."
  supervisord -c /etc/supervisord.conf -n
fi
