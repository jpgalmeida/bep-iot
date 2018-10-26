package example.com.lockr.config;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.crypto.SecretKey;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

public class SendProposal {

	public static String proposal(HFClient client, Channel channel, SampleOrg sampleOrg, ChaincodeID chaincodeID, 
			String function, String... args) throws InvalidArgumentException, UnsupportedEncodingException,
	InterruptedException, ExecutionException, TimeoutException, ProposalException {

		Config.successful = new LinkedList<>();
		Config.failed = new LinkedList<>();
		
		
		client.setUserContext(sampleOrg.getAdmin());
		
		
		///////////////
		/// Send transaction proposal to all peers
		TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
		transactionProposalRequest.setChaincodeID(chaincodeID);
		transactionProposalRequest.setChaincodeLanguage(Config.CHAIN_CODE_LANG);

		transactionProposalRequest.setFcn(function);
		transactionProposalRequest.setProposalWaitTime(Config.testConfig.getProposalWaitTime());

		transactionProposalRequest.setArgs(args);

		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); // Just some
		// extra junk in
		// transient map
		tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
		tm2.put("result", ":)".getBytes(UTF_8)); // This should be returned see chaincode why.
		tm2.put(Config.EXPECTED_EVENT_NAME, Config.EXPECTED_EVENT_DATA); // This should trigger an event see chaincode
		// why.
		
		tm2.put("ENCKEY", sampleOrg.getAdmin().getSecretKey().getEncoded());
		tm2.put("IV", sampleOrg.getAdmin().getIV());
		
		transactionProposalRequest.setTransientMap(tm2);

		Util.out("sending transactionProposal to all peers with arguments: " + function + "(" + args[0] + ")");

		Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest,
				channel.getPeers());

		System.out.println("transaction prop response size: " + transactionPropResp.size());

		for (ProposalResponse response : transactionPropResp) {
			if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
				Util.out("Successful transaction proposal response Txid: %s from peer %s", response.getTransactionID(),
						response.getPeer().getName());
				Config.successful.add(response);
			} else {
				Config.failed.add(response);
			}
		}

		// Check that all the proposals are consistent with each other. We should have
		// only one set
		// where all the proposals above are consistent. Note the when sending to
		// Orderer this is done automatically.
		// Shown here as an example that applications can invoke and select.
		// See org.hyperledger.fabric.sdk.proposal.consistency_validation config
		// property.
		Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
				.getProposalConsistencySets(transactionPropResp);
		if (proposalConsistencySets.size() != 1) {
			Util.out(format("Expected only one set of consistent proposal responses but got %d",
					proposalConsistencySets.size()));
		}

		Util.out("Received %d transaction proposal responses. Successful+verified: %d . Failed: %d",
				transactionPropResp.size(), Config.successful.size(), Config.failed.size());
		if (Config.failed.size() > 0) {
			ProposalResponse firstTransactionProposalResponse = Config.failed.iterator().next();
			Util.out("Not enough endorsers for invoke" + function + "(" + args[0] + ") " + Config.failed.size()
			+ " endorser error: " + firstTransactionProposalResponse.getMessage() + ". Was verified: "
			+ firstTransactionProposalResponse.isVerified());
		}
		Util.out("Successfully received transaction proposal responses.");

		ProposalResponse resp = transactionPropResp.iterator().next();
		byte[] x = resp.getChaincodeActionResponsePayload(); // This is the data returned by the chaincode.
		String resultAsString = null;
		if (x != null) {
			resultAsString = new String(x, "UTF-8");
		}
		System.out.println("Result: " + resultAsString);
		System.out.println("chaincode action response status: " + resp.getChaincodeActionResponseStatus());

		////////////////////////////
		// Send Transaction Transaction to orderer
		Util.out("Sending chaincode transaction " + function + "(" + args[0] + ") " + "to orderer.");
		channel.sendTransaction(Config.successful).get(Config.testConfig.getTransactionWaitTime(), TimeUnit.SECONDS);
		return resultAsString;
	}

	public static String query(HFClient client, Channel channel, SampleOrg sampleOrg, String function, ChaincodeID chaincodeID, String... args)
			throws InvalidArgumentException, ProposalException {
		////////////////////////////
		// Send Query Proposal to all peers
		//

		String payload = "";
		Util.out("Now query chaincode for the value of sector.");
		QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
		queryByChaincodeRequest.setArgs(args);
		queryByChaincodeRequest.setFcn(function);
		queryByChaincodeRequest.setChaincodeID(chaincodeID);

		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
		tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
		
		tm2.put("ENCKEY", sampleOrg.getAdmin().getSecretKey().getEncoded());
		tm2.put("IV", sampleOrg.getAdmin().getIV());
		queryByChaincodeRequest.setTransientMap(tm2);

		Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest,
				channel.getPeers());
		for (ProposalResponse proposalResponse : queryProposals) {
			if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
				Util.out("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: "
						+ proposalResponse.getStatus() + ". Messages: " + proposalResponse.getMessage()
						+ ". Was verified : " + proposalResponse.isVerified());
			} else {
				Util.out("Query payload of user from peer %s returned %s", proposalResponse.getPeer().getName(),
						payload);

				return payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
			}

		}
		return payload;
	}
	
	
	public static String proposal(HFClient client, Channel channel, SampleOrg sampleOrg, ChaincodeID chaincodeID, SampleUser usr,
			String function, String... args) throws InvalidArgumentException, UnsupportedEncodingException,
	InterruptedException, ExecutionException, TimeoutException, ProposalException {

		Config.successful = new LinkedList<>();
		Config.failed = new LinkedList<>();
		
		
		client.setUserContext(usr);
		
		
		///////////////
		/// Send transaction proposal to all peers
		TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
		transactionProposalRequest.setChaincodeID(chaincodeID);
		transactionProposalRequest.setChaincodeLanguage(Config.CHAIN_CODE_LANG);

		transactionProposalRequest.setFcn(function);
		transactionProposalRequest.setProposalWaitTime(Config.testConfig.getProposalWaitTime());

		transactionProposalRequest.setArgs(args);

		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); // Just some
		// extra junk in
		// transient map
		tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
		tm2.put("result", ":)".getBytes(UTF_8)); // This should be returned see chaincode why.
		tm2.put(Config.EXPECTED_EVENT_NAME, Config.EXPECTED_EVENT_DATA); // This should trigger an event see chaincode
		// why.

		tm2.put("ENCKEY", sampleOrg.getAdmin().getSecretKey().getEncoded());
		tm2.put("IV", sampleOrg.getAdmin().getIV());
		transactionProposalRequest.setTransientMap(tm2);

		Util.out("sending transactionProposal to all peers with arguments: " + function + "(" + args[0] + ")");

		Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest,
				channel.getPeers());

		System.out.println("transaction prop response size: " + transactionPropResp.size());

		for (ProposalResponse response : transactionPropResp) {
			if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
				Util.out("Successful transaction proposal response Txid: %s from peer %s", response.getTransactionID(),
						response.getPeer().getName());
				Config.successful.add(response);
			} else {
				Config.failed.add(response);
			}
		}

		// Check that all the proposals are consistent with each other. We should have
		// only one set
		// where all the proposals above are consistent. Note the when sending to
		// Orderer this is done automatically.
		// Shown here as an example that applications can invoke and select.
		// See org.hyperledger.fabric.sdk.proposal.consistency_validation config
		// property.
		Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
				.getProposalConsistencySets(transactionPropResp);
		if (proposalConsistencySets.size() != 1) {
			Util.out(format("Expected only one set of consistent proposal responses but got %d",
					proposalConsistencySets.size()));
		}

		Util.out("Received %d transaction proposal responses. Successful+verified: %d . Failed: %d",
				transactionPropResp.size(), Config.successful.size(), Config.failed.size());
		if (Config.failed.size() > 0) {
			ProposalResponse firstTransactionProposalResponse = Config.failed.iterator().next();
			Util.out("Not enough endorsers for invoke" + function + "(" + args[0] + ") " + Config.failed.size()
			+ " endorser error: " + firstTransactionProposalResponse.getMessage() + ". Was verified: "
			+ firstTransactionProposalResponse.isVerified());
		}
		Util.out("Successfully received transaction proposal responses.");

		ProposalResponse resp = transactionPropResp.iterator().next();
		byte[] x = resp.getChaincodeActionResponsePayload(); // This is the data returned by the chaincode.
		String resultAsString = null;
		if (x != null) {
			resultAsString = new String(x, "UTF-8");
		}
		System.out.println("Result: " + resultAsString);
		System.out.println("chaincode action response status: " + resp.getChaincodeActionResponseStatus());

		////////////////////////////
		// Send Transaction Transaction to orderer
		Util.out("Sending chaincode transaction " + function + "(" + args[0] + ") " + "to orderer.");
		channel.sendTransaction(Config.successful).get(Config.testConfig.getTransactionWaitTime(), TimeUnit.SECONDS);
		return resultAsString;
	}
	
}
