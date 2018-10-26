#!/bin/bash

echo "# generate server private key and keystore"
keytool -keystore server.keystore.jks -alias kafka0 -validity 365 -genkey -keyalg RSA -keysize 2048 -storepass test1234 -dname "cn=kafka0" -keypass test1234
keytool -keystore server1.keystore.jks -alias kafka1 -validity 365 -genkey -keyalg RSA -keysize 2048 -storepass test1234 -dname "cn=kafka1" -keypass test1234
keytool -keystore server2.keystore.jks -alias kafka2 -validity 365 -genkey -keyalg RSA -keysize 2048 -storepass test1234 -dname "cn=kafka2" -keypass test1234
keytool -keystore server3.keystore.jks -alias kafka3 -validity 365 -genkey -keyalg RSA -keysize 2048 -storepass test1234 -dname "cn=kafka3" -keypass test1234
keytool -keystore server4.keystore.jks -alias kafka4 -validity 365 -genkey -keyalg RSA -keysize 2048 -storepass test1234 -dname "cn=kafka4" -keypass test1234
keytool -keystore server5.keystore.jks -alias kafka5 -validity 365 -genkey -keyalg RSA -keysize 2048 -storepass test1234 -dname "cn=kafka5" -keypass test1234
keytool -keystore server6.keystore.jks -alias kafka6 -validity 365 -genkey -keyalg RSA -keysize 2048 -storepass test1234 -dname "cn=kafka6" -keypass test1234

echo "# generate client private key and keystore"
keytool -keystore client.keystore.jks -alias orderer -validity 365 -genkey -keyalg RSA -keysize 2048 -storepass test1234 -dname "cn=orderer" -keypass test1234
keytool -keystore client1.keystore.jks -alias orderer1 -validity 365 -genkey -keyalg RSA -keysize 2048 -storepass test1234 -dname "cn=orderer1" -keypass test1234
keytool -keystore client2.keystore.jks -alias orderer2 -validity 365 -genkey -keyalg RSA -keysize 2048 -storepass test1234 -dname "cn=orderer2" -keypass test1234

echo "# create self signed certificate for certificate authority"
openssl req -new -x509 -keyout ca-key.pem -out ca-cert.pem -days 365 -subj "/CN=FAB5226" -nodes

echo "# create truststores for client and server"
keytool -keystore server.truststore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server1.truststore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server2.truststore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server3.truststore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server4.truststore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server5.truststore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server6.truststore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt

keytool -keystore client.truststore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore client1.truststore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore client2.truststore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt

echo "# create server certificate signing request"
keytool -keystore server.keystore.jks -alias kafka0 -certreq -file server-cert-signing-request.pem -storepass test1234
keytool -keystore server1.keystore.jks -alias kafka1 -certreq -file server1-cert-signing-request.pem -storepass test1234
keytool -keystore server2.keystore.jks -alias kafka2 -certreq -file server2-cert-signing-request.pem -storepass test1234
keytool -keystore server3.keystore.jks -alias kafka3 -certreq -file server3-cert-signing-request.pem -storepass test1234
keytool -keystore server4.keystore.jks -alias kafka4 -certreq -file server4-cert-signing-request.pem -storepass test1234
keytool -keystore server5.keystore.jks -alias kafka5 -certreq -file server5-cert-signing-request.pem -storepass test1234
keytool -keystore server6.keystore.jks -alias kafka6 -certreq -file server6-cert-signing-request.pem -storepass test1234

echo "# sign the server certificate"
openssl x509 -req -CA ca-cert.pem -CAkey ca-key.pem -in server-cert-signing-request.pem -out server-cert-signed.pem -days 365 -CAcreateserial -passin pass:test1234
openssl x509 -req -CA ca-cert.pem -CAkey ca-key.pem -in server1-cert-signing-request.pem -out server1-cert-signed.pem -days 365 -CAcreateserial -passin pass:test1234
openssl x509 -req -CA ca-cert.pem -CAkey ca-key.pem -in server2-cert-signing-request.pem -out server2-cert-signed.pem -days 365 -CAcreateserial -passin pass:test1234
openssl x509 -req -CA ca-cert.pem -CAkey ca-key.pem -in server3-cert-signing-request.pem -out server3-cert-signed.pem -days 365 -CAcreateserial -passin pass:test1234
openssl x509 -req -CA ca-cert.pem -CAkey ca-key.pem -in server4-cert-signing-request.pem -out server4-cert-signed.pem -days 365 -CAcreateserial -passin pass:test1234
openssl x509 -req -CA ca-cert.pem -CAkey ca-key.pem -in server5-cert-signing-request.pem -out server5-cert-signed.pem -days 365 -CAcreateserial -passin pass:test1234
openssl x509 -req -CA ca-cert.pem -CAkey ca-key.pem -in server6-cert-signing-request.pem -out server6-cert-signed.pem -days 365 -CAcreateserial -passin pass:test1234

echo "# import server signed certificate and certificate authority certificate to server keystore"
keytool -keystore server.keystore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server1.keystore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server2.keystore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server3.keystore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server4.keystore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server5.keystore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt
keytool -keystore server6.keystore.jks -alias CARoot -import -file ca-cert.pem -storepass test1234 -noprompt

keytool -keystore server.keystore.jks -alias kafka0 -import -file server-cert-signed.pem -storepass test1234 -noprompt
keytool -keystore server1.keystore.jks -alias kafka1 -import -file server1-cert-signed.pem -storepass test1234 -noprompt
keytool -keystore server2.keystore.jks -alias kafka2 -import -file server2-cert-signed.pem -storepass test1234 -noprompt
keytool -keystore server3.keystore.jks -alias kafka3 -import -file server3-cert-signed.pem -storepass test1234 -noprompt
keytool -keystore server4.keystore.jks -alias kafka4 -import -file server4-cert-signed.pem -storepass test1234 -noprompt
keytool -keystore server5.keystore.jks -alias kafka5 -import -file server5-cert-signed.pem -storepass test1234 -noprompt
keytool -keystore server6.keystore.jks -alias kafka6 -import -file server6-cert-signed.pem -storepass test1234 -noprompt

echo "# create client certificate signing request"
keytool -keystore client.keystore.jks -alias orderer -certreq -file client-cert-signing-request.pem -storepass test1234
keytool -keystore client1.keystore.jks -alias orderer1 -certreq -file client1-cert-signing-request.pem -storepass test1234
keytool -keystore client2.keystore.jks -alias orderer2 -certreq -file client2-cert-signing-request.pem -storepass test1234

echo "# sign the client certificate"
openssl x509 -req -CA ca-cert.pem -CAkey ca-key.pem -in client-cert-signing-request.pem -out client-cert-signed.pem -days 365 -CAcreateserial -passin pass:test1234
openssl x509 -req -CA ca-cert.pem -CAkey ca-key.pem -in client1-cert-signing-request.pem -out client1-cert-signed.pem -days 365 -CAcreateserial -passin pass:test1234
openssl x509 -req -CA ca-cert.pem -CAkey ca-key.pem -in client2-cert-signing-request.pem -out client2-cert-signed.pem -days 365 -CAcreateserial -passin pass:test1234

echo "# convert JKS client keystore to PKCS12"
keytool -importkeystore -srckeystore client.keystore.jks -destkeystore client.keystore.p12 -deststoretype PKCS12 -storepass test1234 -srcstorepass test1234
keytool -importkeystore -srckeystore client1.keystore.jks -destkeystore client1.keystore.p12 -deststoretype PKCS12 -storepass test1234 -srcstorepass test1234
keytool -importkeystore -srckeystore client2.keystore.jks -destkeystore client2.keystore.p12 -deststoretype PKCS12 -storepass test1234 -srcstorepass test1234

echo "# export client private key to pem file"
openssl pkcs12 -in client.keystore.p12 -nodes -nocerts -out client-key.pem -passin pass:test1234
openssl pkcs12 -in client1.keystore.p12 -nodes -nocerts -out client1-key.pem -passin pass:test1234
openssl pkcs12 -in client2.keystore.p12 -nodes -nocerts -out client2-key.pem -passin pass:test1234