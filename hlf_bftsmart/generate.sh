#!/bin/bash
#set -e

export PATH=$GOPATH/src/github.com/hyperledger/fabric/build/bin:${PWD}/../bin:${PWD}:$PATH
export FABRIC_CFG_PATH=${PWD}
CHANNEL_NAME=mychannel

# remove previous crypto material and config transactions
rm -rf channel-artifacts/*
rm -rf crypto-config/*

# generate crypto material
./bin/cryptogen generate --config=./crypto-config.yaml
if [ "$?" -ne 0 ]; then
  echo "Failed to generate crypto material..."
  exit 1
fi


./bin/configtxgen -profile 2P1OGenesis_BFTsmart -outputBlock ./channel-artifacts/genesis.block
if [ "$?" -ne 0 ]; then
  echo "Failed to generate orderer genesis block..."
  exit 1
fi


# generate channel configuration transaction
./bin/configtxgen -profile 2PSecureChannel -outputCreateChannelTx ./channel-artifacts/mychannel.tx -channelID $CHANNEL_NAME
if [ "$?" -ne 0 ]; then
  echo "Failed to generate channel configuration transaction..."
  exit 1
fi


# generate anchor peer A transactions
./bin/configtxgen -profile 2PSecureChannel -outputAnchorPeersUpdate ./channel-artifacts/Peers1MSPanchors.tx -channelID $CHANNEL_NAME -asOrg Peers1
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for Peers1MSP..."
   exit 1
fi


# generate anchor peer B transactions
#./bin/configtxgen -profile 2PSecureChannel -outputAnchorPeersUpdate ./channel-artifacts/Peers2MSPanchors.tx -channelID $CHANNEL_NAME -asOrg Peers2
#if [ "$?" -ne 0 ]; then
#  echo "Failed to generate anchor peer update for Peers2MSP..."
#   exit 1
#fi

#copy key and certificates in case of bft smart
mkdir crypto-config/bftsmart
cp -r crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/msp/keystore crypto-config/bftsmart/
cd crypto-config/bftsmart/keystore
mv $(ls) key.pem
cd ../../..
cp -r crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/msp/signcerts crypto-config/bftsmart/
cd crypto-config/bftsmart/signcerts
mv $(ls) peer.pem


#copy key to easier name in case of users
cd ../../../crypto-config/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/keystore
cp $(ls) User1@org1.example.com-priv.pem


cd /home/jalmeida/hlf_bftsmart/crypto-config
chmod -R 777 .

cd /home/jalmeida/hlf_bftsmart/channel-artifacts
chmod -R 777 .
