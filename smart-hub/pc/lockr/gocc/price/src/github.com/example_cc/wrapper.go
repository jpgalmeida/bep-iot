package main

import (
        "crypto/md5"
        "encoding/hex"
        // "fmt"
        "github.com/hyperledger/fabric/bccsp"
        "github.com/hyperledger/fabric/bccsp/factory"
        "github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/core/chaincode/shim/ext/entities"
        pb "github.com/hyperledger/fabric/protos/peer"

)

// EncCC example simple Chaincode implementation of a chaincode that uses encryption/signatures
type EncCC struct {
        bccspInst bccsp.BCCSP
}

// Encrypter encrypts the data and writes to the ledger
func Encrypter(stub shim.ChaincodeStubInterface, key string, valueAsBytes []byte) error {
        factory.InitFactories(nil)
        encCC := EncCC{factory.GetDefault()}

        tMap, err := stub.GetTransient()
        encKey := tMap["ENCKEY"]

        iv := tMap["IV"]

        ent, err := entities.NewAES256EncrypterEntity("ID", encCC.bccspInst, encKey, iv)
        if err != nil {
                return err
        }

        return encryptAndPutState(stub, ent, key, valueAsBytes)
}

// Decrypter decrypts the data and writes to the ledger
func Decrypter(stub shim.ChaincodeStubInterface, key string) ([]byte, error) {
        factory.InitFactories(nil)
        encCC := EncCC{factory.GetDefault()}

        tMap, err := stub.GetTransient()
        decKey := tMap["ENCKEY"]
        iv := tMap["IV"]

        ent, err := entities.NewAES256EncrypterEntity("ID", encCC.bccspInst, decKey, iv)
        if err != nil {
                return nil, err
        }

        return getStateAndDecrypt(stub, ent, key)
}

// RangeDecrypter shows how range queries may be satisfied by using the decrypter
// entity directly to decrypt previously encrypted key-value pairs
func (t *EncCC) RangeDecrypter(stub shim.ChaincodeStubInterface, startKey, endKey string) pb.Response{
	// create the encrypter entity - we give it an ID, the bccsp instance and the key
	factory.InitFactories(nil)
	encCC := EncCC{factory.GetDefault()}

        tMap, err := stub.GetTransient()
        decKey := tMap["ENCKEY"]

	ent, err := entities.NewAES256EncrypterEntity("ID", encCC.bccspInst, decKey, nil)
	if err != nil {
		return shim.Error("entities.NewAES256EncrypterEntity failed, err %s")
	}

	bytes, err := getStateByRangeAndDecrypt(stub, ent, startKey, endKey)
	if err != nil {
		return shim.Error("getStateByRangeAndDecrypt failed, err %+v")
	}

	return shim.Success(bytes)
}

func GetMD5Hash(text string) string {
        hasher := md5.New()
        hasher.Write([]byte(text))
        return hex.EncodeToString(hasher.Sum(nil))
}