#!/bin/sh

if [ -d /app/mysql ]; then
  echo "[i] MySQL directory already present, skipping creation"
else
  echo "[i] MySQL data directory not found, creating initial DBs"

  mysql_install_db --user=root > /dev/null
  mkdir -p /app/mysql

  if [ ! -d "/run/mysqld" ]; then
    mkdir -p /run/mysqld
  fi

  /usr/bin/mysqld --user=root --bootstrap --verbose=0 < /pulsar-manager/init_db.sql
fi

mysqld_safe &


