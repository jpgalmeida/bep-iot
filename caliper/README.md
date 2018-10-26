# Caliper
Caliper was the benchmark tool that was used. This tool does the implemented requests, which in this case are a read and a write operation. The benchmark was developed for the Kafka network and for the BFT-SMaRt network. In this folder it is not provided the Caliper source code necessary for runnning the benchmarks. The used benchmarks are defined under `benchmark/drm`.Each benchmark is composed by 2 files:

#### 1. fabric.json

This file contains the network definition. It contains the necessary addresses of the network entities in order to complete the benchmark. It also contains the path for the chaincode that will be executed as well as the endorsement policies. The information related to the consensus process is defined in the bottom of this file. In order to successfully run this tool, the defined address will have to be changed for the address in which the network is deployed.

#### 2. config.json

In this file it is defined the start and end commands. Which could be the commands to start and stop the network in case both were running in the same machine. In the performed tests, it were running on different machines, this way it is irrelevant. There is a parameter that defines the batches of operations that will be made. In benchmarks performed, it will be done batches of 300 write and read operations at 100 transactions per second. This file also contains the name of the Docker containers that compose the network.

### Instructions:

It were written configuration files for Kafka and BFT-SMaRt networks with 2, 4, 8, 10, 20 and 30 peers. It is necessary to run the according files.

```sh
# Running the benchmark
node benchmark/drm/main.js -c config_bft_2p.json -n fabric_bft_2p.json
```


### License

The Caliper codebase is release under the Apache 2.0 license. Any documentation developed by the Caliper Project is licensed under the Creative Commons Attribution 4.0 International License. You may obtain a copy of the license, titled CC-BY-4.0, at http://creativecommons.org/licenses/by/4.0/.