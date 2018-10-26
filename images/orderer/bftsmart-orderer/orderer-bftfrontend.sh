#!/bin/bash

set -ev

# run java component: bft-smart frontend
cd ${GOPATH}/src/github.com/hyperledger/fabric/hyperledger-bftsmart
./startFrontend.sh $BFTSMART_FRONTEND_ID $BFTSMART_CONNECTION_POOL_SIZE $BFTSMART_RECVPORT & disown

# wait a few seconds to allow java component to boot up
sleep 40

# run go component: orderer binary
cd /opt/gopath/src/github.com/hyperledger/fabric
orderer
