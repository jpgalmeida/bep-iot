package example.com.lockr.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;

public class Config {

	public static final TestConfig2 testConfig = TestConfig2.getConfig();
	public static final String TEST_ADMIN_NAME = "admin";
	public static final String TESTUSER_1_NAME = "user1";
	public static final String TESTUSER_2_NAME = "user2";
	public static final String TESTUSER_3_NAME = "user3";
	public static final String TESTUSER_4_NAME = "user4";
	public static final String TESTUSER_5_NAME = "user5";
	public static final String TESTUSER_6_NAME = "user6";
	public static final String TESTUSER_7_NAME = "user7";
	public static final String TESTUSER_8_NAME = "user8";
	public static final String TESTUSER_9_NAME = "user9";
	public static final String TESTUSER_10_NAME = "user10";
	public static final String TESTUSER_11_NAME = "user11";
	public static final String TESTUSER_12_NAME = "user12";
	public static final String TESTUSER_13_NAME = "user13";
	public static final String TESTUSER_14_NAME = "user14";
	
	public static final String TEST_FIXTURES_PATH = ".";

	public static final String FOO_CHANNEL_NAME = "mychannel";
	
	public static final String HOST_ADDR = "localhost";

	public static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	public static final String EXPECTED_EVENT_NAME = "event";
	public static final Map<String, String> TX_EXPECTED;

	public static String testName = "End2endIT";

	public static String CHAIN_CODE_FILEPATH_RENTAL = "/gocc/rental";
	public static String CHAIN_CODE_NAME_RENTAL = "rental_chaincode_go";
	public static String CHAIN_CODE_PATH = "github.com/example_cc";
	public static String CHAIN_CODE_VERSION = "1";
	public static Type CHAIN_CODE_LANG = Type.GO_LANG;
	
	public static String CHAIN_CODE_FILEPATH_PRICE = "/gocc/price";
	public static String CHAIN_CODE_NAME_PRICE = "price_chaincode_go";
	
	static {
		TX_EXPECTED = new HashMap<>();
		TX_EXPECTED.put("readset1", "Missing readset for channel bar block 1");
		TX_EXPECTED.put("writeset1", "Missing writeset for channel bar block 1");
	}

	public static final TestConfigHelper2 configHelper = new TestConfigHelper2();
	public static String testTxID = null; // save the CC invoke TxID and use in queries
	public static SampleStore sampleStore = null;
	public static Collection<SampleOrg> testSampleOrgs;

	public static Map<String, Properties> clientTLSProperties = new HashMap<>();


	public static File sampleStoreFile = new File("./src/main/resources/HFCSampletest.properties");

	public static HFClient hfClient;

	public static Channel fooChannel;
	
	public static SampleOrg sampleOrg;
	
	public static Collection<ProposalResponse> successful;
	public static Collection<ProposalResponse> failed;
	
	public static Map<String, ChaincodeID> chaincodeID = new HashMap<>();
}
