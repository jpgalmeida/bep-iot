# images
All the images for Orderers and Replicas are in Docker Hub (https://hub.docker.com/u/jpgalmeida/) which means that it can be easily pulled. The created images are:

##### Replicas with signatures:

- jpgalmeida/bftsmart-replica-4

- jpgalmeida/bftsmart-replica-7

- jpgalmeida/bftsmart-replica-10

- jpgalmeida/bftsmart-replica-13


##### Byzantine replicas:

- jpgalmeida/bftsmart-replica-byz-4

- jpgalmeida/bftsmart-replica-byz-7

- jpgalmeida/bftsmart-replica-byz-10

- jpgalmeida/bftsmart-replica-byz-13


##### Replica w/o signatures

- jpgalmeida/bftsmart-replica-nosig-4


##### Orderer

- jpgalmeida/bftsmart-orderer

- jpgalmeida/bftsmart-orderer-nosig


However, this folder contains the necessary data for the Docker images creation. All the required Docker images are generated when running the HLF network for the first time, but these two images, orderer and replica are necessary for running the HLF network with BFT-SMaRt. There are 2 folders: 

#### 1. Orderer
The image in this folder will be for the orderer to use. There are 2 possible images for the orderer: bftsmart-orderer and bftsmart-orderer-sig. The difference is that by using bftsmart-orderer-sig communications will be signed.

##### Create Docker image:

```sh
docker build -t bftsmart-orderer
```

#### 2. Replica
The images in this folder will be created for replicas to use. There is the same distinction between bftsmart and bftsmart-sig for replicas that will exchange signed messages. For replicas that do not use signatures there it is only possible to use 4 replicas. For replicas that use message signatures it were also created several possibilities: using 4, 7, 10 and 13 replicas. 

##### Create Docker image:

```sh
docker build -t bftsmart
```

There is an extra folder named byzantine, which contains the images that were used for byzantine simulations. For the benchmarks it was used a certain number of replicas as byzantine (following n = 3f + 1).

##### Create Docker image:

```sh
docker build -t bftsmart
```