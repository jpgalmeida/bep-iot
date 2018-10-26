package main
 
import (
	"encoding/json"
	"fmt"
	"strings"
	
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"github.com/hyperledger/fabric/bccsp/factory"
	
)

// SimpleChaincode example simple Chaincode implementation
type SimpleChaincode struct {
}

type sectorPrice struct {
	ObjectType string `json:"docType"` //docType is used to distinguish the various types of objects in state database
	Sector     string `json:"sector"`    //the fieldtags are needed to keep case from bouncing around
	Price   string `json:"price"`

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
	if function == "initPrice" { //create a new sector
		return t.initPrice(stub, args)
	
	} else if function == "readSectorPrice" {
		return t.readSectorPrice(stub, args)
		
	} else if function == "getSectorsByRange" {
		return t.getSectorsByRange(stub, args)
		
	} else {

	}
	
	fmt.Println("invoke did not find func: " + function) //error
	return shim.Error("Received unknown function invocation")
}

// ============================================================
// initPrice - create a new sector, store into chaincode state
// ============================================================
func (t *SimpleChaincode) initPrice(stub shim.ChaincodeStubInterface, args []string) pb.Response{
	var err error

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	// ==== Input sanitation ====
	fmt.Println("- start init sector")
	if len(args[0]) <= 0 {
		return shim.Error("1st argument must be a non-empty string")
	}
	if len(args[1]) <= 0 {
		return shim.Error("2nd argument must be a non-empty string")
	}
	sector := strings.ToLower(args[0])
	price := strings.ToLower(args[1])

	// ==== Create sector object and marshal to JSON ====
	objectType := "sectorPrice"
	sectorPrice := &sectorPrice{objectType, sector, price}
	sectorPriceJSONasBytes, err := json.Marshal(sectorPrice)
	if err != nil {
		return shim.Error(err.Error())
	}

	//err = stub.PutState(sector, sectorPriceJSONasBytes)
	err2 := Encrypter(stub, sector, sectorPriceJSONasBytes)
	if err2 != nil {
			return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

// // ===============================================
// // readSectorPrice - read a sector from chaincode state
// // ===============================================
func (t *SimpleChaincode) readSectorPrice(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	var sector string
	var err error

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting sector to query")
	}

	sector = args[0]

    val, err2 := Decrypter(stub, sector)
    if err2 != nil {
        return shim.Error(err.Error())
    }
    return shim.Success(val)
}

func (t *SimpleChaincode) getSectorsByRange(stub shim.ChaincodeStubInterface, args []string) pb.Response {

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


