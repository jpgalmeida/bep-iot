package main

import (
	"encoding/json"
	// "bytes"

    "github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/core/chaincode/shim/ext/entities"
	
	// pb "github.com/hyperledger/fabric/protos/peer"
)

// getStateAndDecrypt retrieves the value associated to key,
// decrypts it with the supplied entity and returns the result
// of the decryption
func getStateAndDecrypt(stub shim.ChaincodeStubInterface, ent entities.Encrypter, key string) ([]byte, error) {
        // at first we retrieve the ciphertext from the ledger
        ciphertext, err := stub.GetState(key)
        if err != nil {
                return nil, err
        }

        // if len(ciphertext) == 0 {
        //         return nil, 0
        // }

        return ent.Decrypt(ciphertext)
}

// encryptAndPutState encrypts the supplied value using the
// supplied entity and puts it to the ledger associated to
// the supplied KVS key
func encryptAndPutState(stub shim.ChaincodeStubInterface, ent entities.Encrypter, key string, value []byte) error {
        // at first we use the supplied entity to encrypt the value
        ciphertext, err := ent.Encrypt(value)
        if err != nil {
                return err
        }

        return stub.PutState(key, ciphertext)
}

type keyValuePair struct {
	Key   string `json:"Key"`
	Value string `json:"Record"`
}

// getStateByRangeAndDecrypt retrieves a range of KVS pairs from the
// ledger and decrypts each value with the supplied entity; it returns
// a json-marshalled slice of keyValuePair
func getStateByRangeAndDecrypt(stub shim.ChaincodeStubInterface, ent entities.Encrypter, startKey, endKey string) ([]byte, error) {
	// we call get state by range to go through the entire range
	iterator, err := stub.GetStateByRange(startKey, endKey)
	if err != nil {
		return nil, err
	}
	defer iterator.Close()

	// we decrypt each entry - the assumption is that they have all been encrypted with the same key
	keyvalueset := []keyValuePair{}
	for iterator.HasNext() {
		el, err := iterator.Next()
		if err != nil {
			return nil, err
		}

		v, err := ent.Decrypt(el.Value)
		if err != nil {
			return nil, err
		}

		keyvalueset = append(keyvalueset, keyValuePair{el.Key, string(v)})
	}

	bytes, err := json.Marshal(keyvalueset)
	if err != nil {
		return nil, err
	}

	return bytes, nil
}