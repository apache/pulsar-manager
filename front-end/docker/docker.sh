#! /bin/sh

ROOT_DIR=$(git rev-parse --show-toplevel)
CONTAINER_NAME="pulsar-manager-ui"
CONTAINER_ID=$(docker ps | grep $CONTAINER_NAME | awk '{print $1}')

if [ -n "$CONTAINER_ID" ]; then
    docker exec -it $CONTAINER_NAME nginx -s reload
else
    docker run --name $CONTAINER_NAME -p 80:80 -v $ROOT_DIR/front-end/dist:/usr/share/nginx/html -v $ROOT_DIR/front-end/docker/conf.d:/etc/nginx/conf.d nginx:1.18.0-alpine
fi

echo "Website is running: http://localhost"
