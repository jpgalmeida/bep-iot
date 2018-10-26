package example.com.lockr.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.ChaincodeEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

public class RunChannel {

	private static SecretKey secretKey;
	private static byte[] iv;
	
	public static void installChaincode(HFClient client, Channel channel, boolean installChaincode,
			SampleOrg sampleOrg, String chaincodeFilePath, String chaincodeFileName) {

		class ChaincodeEventCapture { // A test class to capture chaincode events
			final String handle;
			final BlockEvent blockEvent;
			final ChaincodeEvent chaincodeEvent;

			ChaincodeEventCapture(String handle, BlockEvent blockEvent, ChaincodeEvent chaincodeEvent) {
				this.handle = handle;
				this.blockEvent = blockEvent;
				this.chaincodeEvent = chaincodeEvent;
			}
		}

		Vector<ChaincodeEventCapture> chaincodeEvents = new Vector<>(); // Test list to capture chaincode events.
		
//		KeyGenerator keyGen;
//		try {
//			keyGen = KeyGenerator.getInstance("AES");
//			keyGen.init(256); // for example
//			secretKey = keyGen.generateKey();
//		} catch (NoSuchAlgorithmException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		SecureRandom random = new SecureRandom();
//		iv = new byte [16];
//		random.nextBytes( iv );
		
		try {

			final String channelName = channel.getName();
			boolean isFooChain = Config.FOO_CHANNEL_NAME.equals(channelName);
			Util.out("Running channel %s", channelName);

			Collection<Orderer> orderers = channel.getOrderers();
			final ChaincodeID chaincodeID;
			Collection<ProposalResponse> responses;
			Collection<ProposalResponse> successful = new LinkedList<>();
			Collection<ProposalResponse> failed = new LinkedList<>();

			// Register a chaincode event listener that will trigger for any chaincode id
			// and only for EXPECTED_EVENT_NAME event.

			String chaincodeEventListenerHandle = channel.registerChaincodeEventListener(Pattern.compile(".*"),
					Pattern.compile(Pattern.quote(Config.EXPECTED_EVENT_NAME)),
					(handle, blockEvent, chaincodeEvent) -> {

						chaincodeEvents.add(new ChaincodeEventCapture(handle, blockEvent, chaincodeEvent));

						String es = blockEvent.getPeer() != null ? blockEvent.getPeer().getName()
								: blockEvent.getEventHub().getName();
						Util.out(
								"RECEIVED Chaincode event with handle: %s, chaincode Id: %s, chaincode event name: %s, "
										+ "transaction id: %s, event payload: \"%s\", from eventhub: %s",
								handle, chaincodeEvent.getChaincodeId(), chaincodeEvent.getEventName(),
								chaincodeEvent.getTxId(), new String(chaincodeEvent.getPayload()), es);

					});

			// For non foo channel unregister event listener to test events are not called.
			if (!isFooChain) {
				channel.unregisterChaincodeEventListener(chaincodeEventListenerHandle);
				chaincodeEventListenerHandle = null;

			}

			ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(chaincodeFileName)
					.setVersion(Config.CHAIN_CODE_VERSION);
			if (null != Config.CHAIN_CODE_PATH) {
				chaincodeIDBuilder.setPath(Config.CHAIN_CODE_PATH);

			}
			chaincodeID = chaincodeIDBuilder.build();

			if (installChaincode) {
				////////////////////////////
				// Install Proposal Request
				//

				client.setUserContext(sampleOrg.getPeerAdmin());

				Util.out("Creating install proposal");

				InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
				installProposalRequest.setChaincodeID(chaincodeID);

				if (isFooChain) {
					// on foo chain install from directory.

					//// For GO language and serving just a single user, chaincodeSource is mostly
					//// likely the users GOPATH
					installProposalRequest.setChaincodeSourceLocation(
							Paths.get(Config.TEST_FIXTURES_PATH, chaincodeFilePath).toFile());
				} else {
					// On bar chain install from an input stream.

					if (Config.CHAIN_CODE_LANG.equals(Type.GO_LANG)) {

						installProposalRequest.setChaincodeInputStream(Util.generateTarGzInputStream(
								(Paths.get(Config.TEST_FIXTURES_PATH, chaincodeFilePath, "src",
										Config.CHAIN_CODE_PATH).toFile()),
								Paths.get("src", Config.CHAIN_CODE_PATH).toString()));
					} else {
						installProposalRequest.setChaincodeInputStream(Util.generateTarGzInputStream(
								(Paths.get(Config.TEST_FIXTURES_PATH, chaincodeFilePath).toFile()), "src"));
					}
				}

				installProposalRequest.setChaincodeVersion(Config.CHAIN_CODE_VERSION);
				installProposalRequest.setChaincodeLanguage(Config.CHAIN_CODE_LANG);

				Util.out("Sending install proposal");

				////////////////////////////
				// only a client from the same org as the peer can issue an install request
				int numInstallProposal = 0;

				Collection<Peer> peers = channel.getPeers();
				numInstallProposal = numInstallProposal + peers.size();
				responses = client.sendInstallProposal(installProposalRequest, peers);

				for (ProposalResponse response : responses) {
					if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
						Util.out("Successful install proposal response Txid: %s from peer %s",
								response.getTransactionID(), response.getPeer().getName());
						successful.add(response);
					} else {
						failed.add(response);
					}
				}

				Util.out("Received %d install proposal responses. Successful+verified: %d . Failed: %d",
						numInstallProposal, successful.size(), failed.size());

				if (failed.size() > 0) {
					ProposalResponse first = failed.iterator().next();
					Util.out("Not enough endorsers for install :" + successful.size() + ".  " + first.getMessage());
				}
				
				
				
			}

			///////////////
			//// Instantiate chaincode.
			InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
			instantiateProposalRequest.setProposalWaitTime(Config.testConfig.getProposalWaitTime());
			instantiateProposalRequest.setChaincodeID(chaincodeID);
			instantiateProposalRequest.setChaincodeLanguage(Config.CHAIN_CODE_LANG);
			instantiateProposalRequest.setFcn("init");

			instantiateProposalRequest.setArgs(new String[] { "init" });
			Map<String, byte[]> tm = new HashMap<>();
			tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
			tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
			instantiateProposalRequest.setTransientMap(tm);

			/*
			 * policy OR(Org1MSP.member, Org2MSP.member) meaning 1 signature from someone in
			 * either Org1 or Org2 See README.md Chaincode endorsement policies section for
			 * more details.
			 */
			ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
			chaincodeEndorsementPolicy
					.fromYamlFile(new File(Config.TEST_FIXTURES_PATH + "/chaincodeendorsementpolicy.yaml"));
			instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);

			Util.out("Sending instantiateProposalRequest to all peers with arguments: init");
			successful.clear();
			failed.clear();

			if (isFooChain) { // Send responses both ways with specifying peers and by using those on the
								// channel.
				responses = channel.sendInstantiationProposal(instantiateProposalRequest, channel.getPeers());
			} else {
				responses = channel.sendInstantiationProposal(instantiateProposalRequest);
			}
			for (ProposalResponse response : responses) {
				if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
					successful.add(response);
					Util.out("Succesful instantiate proposal response Txid: %s from peer %s",
							response.getTransactionID(), response.getPeer().getName());
				} else {
					failed.add(response);
				}
			}
			Util.out("Received %d instantiate proposal responses. Successful+verified: %d . Failed: %d",
					responses.size(), successful.size(), failed.size());
			if (failed.size() > 0) {
				for (ProposalResponse fail : failed) {

					Util.out("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with "
							+ fail.getMessage() + ", on peer" + fail.getPeer());

				}
				ProposalResponse first = failed.iterator().next();
				Util.out("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with "
						+ first.getMessage() + ". Was verified:" + first.isVerified());
			}
			
			Config.chaincodeID.put(chaincodeFileName, chaincodeID);
			
			///////////////
			/// Send instantiate transaction to orderer
			Util.out("Sending instantiateProposalRequest to all peers with arguments: init");
			channel.sendTransaction(successful, orderers).thenApply(transactionEvent -> {

				waitOnFabric(0);

				Util.out("Finished instantiate transaction with transaction id %s",
						transactionEvent.getTransactionID());

				return transactionEvent;
			}).get(Config.testConfig.getTransactionWaitTime(),
					TimeUnit.SECONDS);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void waitOnFabric(int additional) {
		// NOOP today

	}

	public static String initRental(String lockerid, String sector, String duration, String... args) {
		try {
			String price =  readSectorPrice(sector);
			
			if(!price.equals("Error")) {
				if(args.length == 0) {
					return SendProposal.proposal(Config.hfClient, Config.fooChannel, Config.sampleOrg, Config.chaincodeID.get(Config.CHAIN_CODE_NAME_RENTAL), "initRental", lockerid, sector, duration, price);
				}else {
					SampleUser usr = Config.sampleStore.getMember(args[0], "peerOrg1");
					return SendProposal.proposal(Config.hfClient, Config.fooChannel, Config.sampleOrg, Config.chaincodeID.get(Config.CHAIN_CODE_NAME_RENTAL), usr, "initRental", lockerid, sector, duration, price);
				}
				
			}
		} catch (InvalidArgumentException | UnsupportedEncodingException | ProposalException | InterruptedException
				| ExecutionException | TimeoutException e) {
		}
		return "Error";
	}

	public static String getRentalsByRange(String startId, String endId, String... args) {
		try {
			if(args.length == 0) {
				return SendProposal.proposal(Config.hfClient, Config.fooChannel, Config.sampleOrg, Config.chaincodeID.get(Config.CHAIN_CODE_NAME_RENTAL), "getRentalsByRange", startId, endId);
			}else {
				SampleUser usr = Config.sampleStore.getMember(args[0], "peerOrg1");
				return SendProposal.proposal(Config.hfClient, Config.fooChannel, Config.sampleOrg, Config.chaincodeID.get(Config.CHAIN_CODE_NAME_RENTAL), usr, "getRentalsByRange", startId, endId);
			}
			
		} catch (InvalidArgumentException | UnsupportedEncodingException | ProposalException | InterruptedException
				| ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		return "Error";
	}
	
	public static String initPrice(String sector, String price, String... args) {
		try {
			if(args.length == 0) {
				return SendProposal.proposal(Config.hfClient, Config.fooChannel, Config.sampleOrg, Config.chaincodeID.get(Config.CHAIN_CODE_NAME_PRICE), "initPrice", sector, price);
			}else {
				SampleUser usr = Config.sampleStore.getMember(args[0], "peerOrg1");
				return SendProposal.proposal(Config.hfClient, Config.fooChannel, Config.sampleOrg, Config.chaincodeID.get(Config.CHAIN_CODE_NAME_PRICE), usr, "initPrice", sector, price);
			}
			
		} catch (InvalidArgumentException | UnsupportedEncodingException | ProposalException | InterruptedException
				| ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		return "Error";
	}
	
	public static String readSectorPrice(String sector, String... args) {
		try {

			HashMap<String, String> map = new HashMap<String, String>();
			String response = SendProposal.query(Config.hfClient, Config.fooChannel, Config.sampleOrg, "readSectorPrice", Config.chaincodeID.get(Config.CHAIN_CODE_NAME_PRICE), sector);
			map = JsonObject.parse(response, HashMap.class);
			
			String price = map.get("price");
			System.out.println("Price read: "+price);
			
			if (price != null)
				return price;
			 
//			return SendProposal.query(Config.hfClient, Config.fooChannel, "readSectorPrice", Config.chaincodeID.get(Config.CHAIN_CODE_NAME_PRICE), sector);
		} catch (InvalidArgumentException | ProposalException e) {
//			e.printStackTrace();
		}
		return "Error";
	}
	
	public static String getSectorsByRange(String startId, String endId, String... args) {
		try { 
			if(args.length == 0) {
				return SendProposal.proposal(Config.hfClient, Config.fooChannel, Config.sampleOrg, Config.chaincodeID.get(Config.CHAIN_CODE_NAME_PRICE), "getSectorsByRange", startId, endId);
			}else {
				SampleUser usr = Config.sampleStore.getMember(args[0], "peerOrg1");
				return SendProposal.proposal(Config.hfClient, Config.fooChannel, Config.sampleOrg, Config.chaincodeID.get(Config.CHAIN_CODE_NAME_PRICE), usr, "getSectorsByRange", startId, endId);
			}
			
		} catch (InvalidArgumentException | UnsupportedEncodingException | ProposalException | InterruptedException
				| ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		return "Error";
	}
	
	
		

}
