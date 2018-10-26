package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"

	//Encrypt
	"github.com/hyperledger/fabric/bccsp/factory"
)

type SimpleChaincode struct {
}

type rental struct {
	ObjectType string `json:"docType"` //docType is used to distinguish the various types of objects in state database
	TxID     string `json:"txid"`    //the fieldtags are needed to keep case from bouncing around
	LockerID   string `json:"lockerid"`
	Sector   string    `json:"sector"`
	Duration   string    `json:"duration"`
	Price   string    `json:"price"`
}

// ===================================================================================
// Main
// ===================================================================================
func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}

// Init initializes chaincode
// ===========================
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

// Invoke - Our entry point for Invocations
// ========================================
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("invoke is running " + function)

	// Handle different functions
	if function == "initRental" { //create a new rental
		return t.initRental(stub, args)

	} else if function == "getRentalsByRange" {
		return t.getRentalsByRange(stub, args)
		
	} else {

	}

	fmt.Println("invoke did not find func: " + function) //error
	return shim.Error("Received unknown function invocation")
}

// ============================================================
// initRental - create a new rental, store into chaincode state
// ============================================================
func (t *SimpleChaincode) initRental(stub shim.ChaincodeStubInterface, args []string) pb.Response{
	var err error

	lockerid := args[0]
	sector := args[1]
	duration := args[2]

	priceI, err := strconv.Atoi(args[3])
	durationI, err := strconv.Atoi(duration)

	if err != nil {
		return shim.Error(err.Error())
	}

	value := strconv.Itoa(priceI * durationI / 60)
 
	txId := sector + lockerid

	// ==== Create rental object and marshal to JSON ====
	objectType := "rental"
	rental := &rental{objectType, txId, lockerid, sector, duration, value}
	rentalJSONasBytes, err := json.Marshal(rental)
	if err != nil {
		return shim.Error(err.Error())
	}

	err2 := Encrypter(stub, txId, rentalJSONasBytes)
	if err2 != nil {
			return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

// ===========================================================================================
// getRentalsByRange performs a range query based on the start and end keys provided.
func (t *SimpleChaincode) getRentalsByRange(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) < 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	startKey := args[0]
	endKey := args[1]

	factory.InitFactories(nil)
	encCC := EncCC{factory.GetDefault()}
	resultsIterator := encCC.RangeDecrypter(stub, startKey, endKey)

	return resultsIterator
	
}

