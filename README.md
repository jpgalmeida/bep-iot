# Blockchain Enabled Platform for the Internet of Things
This repositpry contains the implementation of a Blockchain-enabled IoT Platform, as presented and discussed in the following MSc dissertation:

Blockchain Enabled Platforms for the Internet of Things
Dissertation submitted in partial fulfillment
of the requirements for the degree of
Master of Science in Computer Science and Informatics Engineering
Department of Informatics, FCT/UNL, September/2018
(See: report/Dissertation_MIEI_42009_JoaoAlmeida.pdf )

The repository contains all developed Software acoording to the software architecture proposed in
the dissertation. As a core component of the solution we use an extended version 
of the Hyperledger Fabric Blockchain, where the native ordering service 
(based in the Kafka solution) was enhanced with
a byzantive fault tolerant solution for the consensus service plane. The base for these
extensions are leveraged from the HLF-BFT-SMaRt design model and implemenation, as initially
addressed in the following paper:

João Sousa, Alysson Bessani, Marko Vukolic, 
A Byzantine Fault-Tolerant Ordering Service for the Hyperledger Fabric Blockchain Platform,
in DSN 2018 - Intl Conference on Dependable Systems and Networks, Luxembourgh, June/2018.
(See: http://www.di.fc.ul.pt/~bessani/publications/dsn18-hlfsmart.pdf)

Implementation Structure:
----------------------------
The repository is divided in 6 folders:

##### 1. images
All the necessary files for Docker image generation.
##### 2. hlf_kafka
Contains the Hyperledger Fabric network using Kafka as an ordering service.
##### 3. hlf_bftsmart
Contains the Hyperledger Fabric network using BFT-SMaRt as an ordering service.
##### 4. lockr
Spring Boot application for the Smart Hub.
Notice: in our architecture, clients and IoT devices use a smart-hub, as a gateway 
implementing a middleware appliance, intermediating the operations in the blockchain.
##### 5. mobile-app
Hybrid mobile application for user testing. 
Notice: this is a proof-of-concept final application, implementing an app used to support 
the operations and interactions from end-users and providers.
##### 6. caliper
Benchmarking tool.
(See: https://github.com/hyperledger/caliper )
##### 7. report
Contains the report for the referenced MSc dissertation.
