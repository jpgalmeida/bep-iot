#!/bin/bash
set -e

# artifact generation
#./generate.sh
#sleep 2

docker-compose -f docker-compose.yaml -f docker-compose-couch.yaml -f docker-compose-bftsmart.yaml down; 
docker volume prune -f;

lines=`docker images | grep 'dev-peer' | grep 'dev-peer' | wc -l`
  if [ "$lines" -gt 0 ]; then
    docker images | grep 'dev-peer' | awk '{print $1}' | xargs docker rmi -f
  fi

docker-compose -f docker-compose.yaml -f docker-compose-couch.yaml -f docker-compose-bftsmart.yaml up;