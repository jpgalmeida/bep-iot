# hlf_kafka
In this folder are all the required files for the HLF network using Kafka as an ordering service. For running this network all the required Docker images will be pulled when running the starting script.

#### Prerequisites
In order to be able to run the Hyperledger Fabric network, it is necessary to install all the prerequisites associated with Hyperledger Fabric which are defined in https://hyperledger-fabric.readthedocs.io/en/release-1.1/prereqs.html.

#### Running

There are 2 important scripts:
- `generateArtifacts.sh`: which is responsible for the generation of cryptographic material.

- `fabric.sh`: which will execute generateArtifacts.sh and launch the network.

The HLF network is configured in YAML files. The YAML files are:

- `docker-compose-couch.yaml`: contains the definition of peers and couch instances.

- `docker-compose.yaml`: contains the definition of the Certificate Authority, Zookeepers, Kafka brokers, orderer, peers, configtxlator and ccenv.

- `base/docker-compose-base.yaml`: configuration of volumes and environment variables for Zookeeper, Kafka brokers, orderer and peers.

- `configtx.yaml`: definition of orderer profiles, organizations and orderer settings. 

- `crypto-config.yaml`: definition of the cryptographic information of orderers, peers and users.


Inside kafka folder it contains a scipt `script.sh` which when executed will generate the cryptographic material for Kafka.

For HLF Kafka network it were created different YAML configuration files. These files folllow the syntax `docker-compose_8P_1O_7K.yaml` which means that this network will use 8 peers, 1 orderer and 7 kafka brokers.

These YAML files can be used by changing the consumed file in the `fabric.sh` script. 


### Instructions:
```sh
# Generate Kafka's crypto material
.script.sh
# Running the network
./fabric.sh restart
```