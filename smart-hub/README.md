# Smart-hub
Lockr is a Spring Boot application that contains implementation for the Smart Hub which is responsible for receiving client communications and proposing transactions for HLF.

This folder is divided in 2 projects in case the Smart-Hub will be executed in a PC or in a RPi.

In the lockr folder are all the required files to run this application. There are some important notices to this application, mainly in the src/main/resources folder which contains 4 files.

#### 1. HFCSampletest.properties

This file contains the necessary information to implement persistency to the application. This way, when the network is restarted, this file should be manually removed.

#### 2. netty-tcnative-boringssl-static-2.0.9.Final-linux-arm_32.jar

This file is the compiled dependency for ARM32 in order to deploy the Smart Hub in a Raspberry Pi 3b+. In the case of deployment of a PC, no special measures are required. In the case of deployment in a Raspberry Pi, the dependency that uses this jar file should be uncommented from `pom.xml`.

#### 3. application.properties

This file contains all the information required related to the certificate to be used.

#### 4. ssl-server.jks
This keystore contains the key to be used for the communications with the network. This keystore was generated using keytool with the following commands:

```sh
# Generating the keystore
keytool -genkeypair -alias sslserver -keyalg RSA -keysize 2048 -keystore ssl-server.jks -validity 356
# Viewing keystore's content
keytool -list -v -keystore ssl-server.jks
```

Obs: when generating the certificate it is important to set the common name of the certificate as the address of the machine where the HLF network is deployed.

### Pre-requisites
It is necessary to install the following libraries on the Raspberry Pi:

maven golang ninja cmake perl libssl-dev libapr1-dev autoconf automake libtool 

### Instructions:

To change the address in which the network is running change the value of HOST_ADDR in Config.java

In the root directory of the project run:

```sh
# Running the application
mvn spring-boot:run
```