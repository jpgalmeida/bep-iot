# hlf_bftsmart
In this folder are all the required files for the HLF network using BFT-SMaRt as an ordering service. For running this network all the required Docker images are the two in the images folders for the replica and orderer and the rest will be pulled when running the starting script.

#### Prerequisites
In order to be able to run the Hyperledger Fabric network, it is necessary to install all the prerequisites associated with Hyperledger Fabric BFT-SMaRt which are defined in https://github.com/bft-smart/fabric-orderingservice/wiki/Compiling. With the exception of the Hyperledger Fabric source code to be placed 

#### Running

There are 2 important scripts:

- `generate.sh`: which is responsible for the generation of cryptographic material.

- `start.sh`: which will execute generate.sh and launch the network.


The HLF network is configured in YAML files. The YAML files are:

- `docker-compose-couch.yaml`: contains the definition of peers and couch instances.

- `docker-compose.yaml`: contains the definition of the Certificate Authority, orderer, peers, configtxlator and ccenv.

- `docker-compose-bftsmart.yaml`: contains the definition of the replicas.

- `base/docker-compose-base.yaml`: configuration of volumes and environment variables for orderer and peers.

- `base/docker-compose-bftsmart-base.yaml`: configuration of volumes and environment variables for replicas. In this file it is also included the configuration for the byzantine replica.

- `configtx.yaml`: definition of orderer profiles, organizations and orderer settings. 

- `crypto-config.yaml`: definition of the cryptographic information of orderers, peers and users.



For HLF BFT-SMaRt network it were created different YAML configuration files. These files folllow the syntax `docker-compose_10P.yaml` which means that this network will use 10 peers. Or `docker-compose-bftsmart10R.yaml` indicating that it will be created 10 replicas.

These YAML files can be used by changing the consumed file in the `start.sh` script. 


### Instructions:
```sh
# Running the network
./start.sh
```