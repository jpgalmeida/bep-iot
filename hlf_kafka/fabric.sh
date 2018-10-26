#!/usr/bin/env bash
#
# Copyright IBM Corp. All Rights Reserved.
#
# SPDX-License-Identifier: Apache-2.0
#
# simple batch script making it easier to cleanup and start a relatively fresh fabric env.


if [ ! -e "docker-compose.yaml" ];then
  echo "docker-compose.yaml not found."
  exit 8
fi

if [ -d "./crypto-config" ]; then
    rm -rf channel-artifacts/*.block channel-artifacts/*.tx crypto-config
    echo "crypto-config directory already exists."
    source generateArtifacts.sh 'mychannel'
  else
    #Generate all the artifacts that includes org certs, orderer genesis block,
    # channel configuration transaction
    source generateArtifacts.sh 'mychannel'
fi

ORG_HYPERLEDGER_FABRIC_SDKTEST_VERSION=${ORG_HYPERLEDGER_FABRIC_SDKTEST_VERSION:-}

function clean(){

  rm -rf /var/hyperledger/*

  if [ -e "/tmp/HFCSampletest.properties" ];then
   rm -f "/tmp/HFCSampletest.properties"
  fi

  lines=`docker ps -a | grep 'dev-peer' | wc -l`

  if [ "$lines" -gt 0 ]; then
    docker ps -a | grep 'dev-peer' | awk '{print $1}' | xargs docker rm -f
  fi

  lines=`docker images | grep 'dev-peer' | grep 'dev-peer' | wc -l`
  if [ "$lines" -gt 0 ]; then
    docker images | grep 'dev-peer' | awk '{print $1}' | xargs docker rmi -f
  fi

  # remove orderer block and other channel configuration transactions and certs
  

}

function up(){
  docker-compose -f docker-compose0.yaml -f docker-compose-couch0.yaml up --force-recreate
}

function down(){
  docker-compose down;
}

function stop (){
  docker-compose stop;
}

function start (){
  docker-compose start;
}



for opt in "$@"
do

    case "$opt" in
        up)
            up
            ;;
        down)
            down
            ;;
        stop)
            stop
            ;;
        start)
            start
            ;;
        clean)
            clean
            ;;
        restart)
            down
            clean
            up
            ;;

        *)
            echo $"Usage: $0 {up|down|start|stop|clean|restart}"
            exit 1

esac
done
