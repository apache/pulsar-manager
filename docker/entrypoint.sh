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
/pulsar-manager/pulsar-manager/bin/pulsar-manager \
    --redirect.host=${REDIRECT_HOST} \
    --redirect.port=${REDIRECT_PORT} \
    --spring.datasource.driver-class-name=${DRIVER_CLASS_NAME} \
    --spring.datasource.url=${URL} \
    --spring.datasource.username=${USERNAME} \
    --spring.datasource.password=${PASSWORD} \
    --spring.datasource.initialization-mode=never \
    --logging.level.org.apache=${LOG_LEVEL} \
    --backend.jwt.token=${JWT_TOKEN}
    --tls.enabled=${TLS:=false}