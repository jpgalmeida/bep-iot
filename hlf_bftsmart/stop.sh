#!/bin/bash
set -e

# Shut down the Docker containers that might be currently running.
while true; do
    read -p "What are you running (Y for Kafka HLF, N for BFTsmart HLF)?" yn
    case $yn in
        [Yy]* ) docker-compose -f docker-compose.yaml -f docker-compose-couch.yaml -f docker-compose-kafka.yaml stop; break;;
        [Nn]* ) docker-compose -f docker-compose.yaml -f docker-compose-couch.yaml -f docker-compose-bftsmart.yaml stop; break;;
        * ) echo "Please answer Y or N.";;
    esac
done
