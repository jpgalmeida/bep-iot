#!/bin/bash +x
#
# Copyright IBM Corp. All Rights Reserved.
#
# SPDX-License-Identifier: Apache-2.0
#


#set -e

CHANNEL_NAME1=$1
: ${CHANNEL_NAME1:="mychannel"}
echo $CHANNEL_NAME1

export PATH=$GOPATH/src/github.com/hyperledger/fabric/build/bin:${PWD}/../bin:${PWD}:$PATH
export FABRIC_CFG_PATH=${PWD}
echo 

## Generates Org certs using cryptogen tool
function generateCerts (){
	./bin/cryptogen generate --config=./crypto-config.yaml
	if [ "$?" -ne 0 ]; then
  		echo "Failed to generate crypto material..."
  		exit 1
	fi
}

## Generate orderer genesis block , channel configuration transaction and anchor peer update transactions
function generateChannelArtifacts() {

	echo "##########################################################"
	echo "#########  Generating Orderer Genesis block ##############"
	echo "##########################################################"
	# Note: For some unknown reason (at least for now) the block file can't be
	# named orderer.genesis.block or the orderer will fail to launch!
	./bin/configtxgen -profile TwoOrgsOrdererGenesis -outputBlock ./channel-artifacts/genesis.block

	echo
	echo "#################################################################"
	echo "### Generating channel configuration transaction 'channel.tx' ###"
	echo "#################################################################"
	./bin/configtxgen -profile TwoOrgsChannel -outputCreateChannelTx ./channel-artifacts/mychannel.tx -channelID $CHANNEL_NAME1
	
	echo
	echo "#################################################################"
	echo "#######    Generating anchor peer update for Org1MSP   ##########"
	echo "#################################################################"
	./bin/configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/Org1MSPanchors$CHANNEL_NAME1.tx -channelID $CHANNEL_NAME1 -asOrg Org1MSP
	echo
	echo "#################################################################"
	echo "#######    Generating anchor peer update for Org2MSP   ##########"
	echo "#################################################################"
	./bin/configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/Org2MSPanchors$CHANNEL_NAME1.tx -channelID $CHANNEL_NAME1 -asOrg Org2MSP
	echo
	echo
	echo "#################################################################"
	echo "#######    Generating anchor peer update for Org3MSP   ##########"
	echo "#################################################################"
	./bin/configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/Org3MSPanchors$CHANNEL_NAME1.tx -channelID $CHANNEL_NAME1 -asOrg Org3MSP
	echo
}

generateCerts
generateChannelArtifacts