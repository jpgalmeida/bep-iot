package example.com.lockr.config;

import static org.hyperledger.fabric.sdk.Channel.PeerOptions.createPeerOptions;


import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static java.lang.String.format;

import org.hyperledger.fabric.protos.peer.Query.ChaincodeInfo;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.Peer.PeerRole;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;


public class CreateChannel {

	public static Channel constructChannel(String name, HFClient client, SampleOrg sampleOrg) throws Exception {
		////////////////////////////
		// Construct the channel
		//

		Util.out("Constructing channel %s", name);

		boolean doPeerEventing = !Config.testConfig.isRunningAgainstFabric10() && Config.FOO_CHANNEL_NAME.equals(name);

		//Only peer Admin org
		client.setUserContext(sampleOrg.getPeerAdmin());

		Collection<Orderer> orderers = new LinkedList<>();

		for (String orderName : sampleOrg.getOrdererNames()) {
			Util.out("ordname :  " + orderName);

			Properties ordererProperties = Config.testConfig.getOrdererProperties(orderName);

			//example of setting keepAlive to avoid timeouts on inactive http2 connections.
			// Under 5 minutes would require changes to server side to accept faster ping rates.
			ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
			ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});
			ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[] {true});


			orderers.add(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
					ordererProperties));
		}

		//Just pick the first orderer in the list to create the channel.
		
		Orderer anOrderer = orderers.iterator().next();
		orderers.remove(anOrderer);

		ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(Config.TEST_FIXTURES_PATH + "/channel-artifacts/" + name + ".tx"));

		//Create channel that has only one signer that is this orgs peer admin. If channel creation policy needed more signature they would need to be added too.
		Channel newChannel = client.newChannel(name, anOrderer, channelConfiguration, client.getChannelConfigurationSignature(channelConfiguration, sampleOrg.getPeerAdmin()));

		Util.out("Created channel %s", name);

		boolean everyother = true; //test with both cases when doing peer eventing.
		for (String peerName : sampleOrg.getPeerNames()) {
			String peerLocation = sampleOrg.getPeerLocation(peerName);

			Properties peerProperties = Config.testConfig.getPeerProperties(peerName); //test properties for peer.. if any.
			if (peerProperties == null) {
				peerProperties = new Properties();
			}


			//Example of setting specific options on grpc's NettyChannelBuilder
			peerProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);

			Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
			if (doPeerEventing && everyother) {
				newChannel.joinPeer(peer, createPeerOptions()); //Default is all roles.
			} else {
				// Set peer to not be all roles but eventing.
				newChannel.joinPeer(peer, createPeerOptions().setPeerRoles(PeerRole.NO_EVENT_SOURCE));
			}
			Util.out("Peer %s joined channel %s", peerName, name);
			everyother = !everyother;
		}

		for (Orderer orderer : orderers) { //add remaining orderers if any.
			newChannel.addOrderer(orderer);
		}

		for (String eventHubName : sampleOrg.getEventHubNames()) {

			final Properties eventHubProperties = Config.testConfig.getEventHubProperties(eventHubName);

			eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
			eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});


			EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
					eventHubProperties);
			newChannel.addEventHub(eventHub);
		}

		newChannel.initialize();

		Util.out("Finished initialization channel %s", name);

		//Just checks if channel can be serialized and deserialized .. otherwise this is just a waste :)
		byte[] serializedChannelBytes = newChannel.serializeChannel();
		newChannel.shutdown(true);

		return client.deSerializeChannel(serializedChannelBytes).initialize();

	}
	
	public static Channel reconstructChannel(String name, HFClient client, SampleOrg sampleOrg,
			SampleStore sampleStore) {
		Channel newChannel = null;
		try {
			client.setUserContext(sampleOrg.getPeerAdmin());
			
			
			/**
			 * sampleStore.getChannel uses {@link HFClient#deSerializeChannel(byte[])}
			 */
			newChannel = sampleStore.getChannel(client, name);

			// Make sure there is one of each type peer at the very least. see End2end for
			// how peers were constructed.
			// System.out.println((newChannel.getPeers(EnumSet.of(PeerRole.EVENT_SOURCE)).isEmpty()));
			// System.out.println(newChannel.getPeers(PeerRole.NO_EVENT_SOURCE).isEmpty());

			Util.out("Retrieved channel %s from sample store.", name);

			newChannel.initialize();
			for (Peer peer : newChannel.getPeers()) {
				Set<String> channels = client.queryChannels(peer);
				if (!channels.contains(name)) {
					throw new AssertionError(
							format("Peer %s does not appear to belong to channel %s", peer.getName(), name));
				}
			}

			client.setUserContext(sampleOrg.getPeerAdmin());

			for (Peer peer : newChannel.getPeers()) {
				if (!checkInstalledChaincode(client, peer, Config.CHAIN_CODE_NAME_PRICE, Config.CHAIN_CODE_PATH,
						Config.CHAIN_CODE_VERSION)) {
					throw new AssertionError(
							format("Peer %s is missing chaincode name: %s, path:%s, version: %s", peer.getName(),
									Config.CHAIN_CODE_NAME_PRICE, Config.CHAIN_CODE_PATH, Config.CHAIN_CODE_VERSION));
				}

				if (!checkInstalledChaincode(client, peer, Config.CHAIN_CODE_NAME_RENTAL,
						Config.CHAIN_CODE_PATH, Config.CHAIN_CODE_VERSION)) {
					throw new AssertionError(format("Peer %s is missing chaincode name: %s, path:%s, version: %s",
							peer.getName(), Config.CHAIN_CODE_NAME_RENTAL, Config.CHAIN_CODE_PATH,
							Config.CHAIN_CODE_VERSION));
				}

				if (!checkInstantiatedChaincode(newChannel, peer, Config.CHAIN_CODE_NAME_PRICE, Config.CHAIN_CODE_PATH,
						Config.CHAIN_CODE_VERSION)) {

					throw new AssertionError(format(
							"Peer %s is missing instantiated chaincode name: %s, path:%s, version: %s", peer.getName(),
							Config.CHAIN_CODE_NAME_PRICE, Config.CHAIN_CODE_PATH, Config.CHAIN_CODE_VERSION));
				}

				if (!checkInstantiatedChaincode(newChannel, peer, Config.CHAIN_CODE_NAME_RENTAL,
						Config.CHAIN_CODE_PATH, Config.CHAIN_CODE_VERSION)) {

					throw new AssertionError(format(
							"Peer %s is missing instantiated chaincode name: %s, path:%s, version: %s", peer.getName(),
							Config.CHAIN_CODE_NAME_RENTAL, Config.CHAIN_CODE_PATH, Config.CHAIN_CODE_VERSION));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newChannel;

	}
	
	private static boolean checkInstalledChaincode(HFClient client, Peer peer, String ccName, String ccPath,
			String ccVersion) throws InvalidArgumentException, ProposalException {

		Util.out("Checking installed chaincode: %s, at version: %s, on peer: %s", ccName, ccVersion, peer.getName());
		List<ChaincodeInfo> ccinfoList = client.queryInstalledChaincodes(peer);

		boolean found = false;

		for (ChaincodeInfo ccifo : ccinfoList) {

			if (ccPath != null) {
				found = ccName.equals(ccifo.getName()) && ccPath.equals(ccifo.getPath())
						&& ccVersion.equals(ccifo.getVersion());
				if (found) {
					break;
				}
			}

			found = ccName.equals(ccifo.getName()) && ccVersion.equals(ccifo.getVersion());
			if (found) {
				break;
			}

		}

		return found;
	}

	private static boolean checkInstantiatedChaincode(Channel channel, Peer peer, String ccName, String ccPath,
			String ccVersion) throws InvalidArgumentException, ProposalException {
		Util.out("Checking instantiated chaincode: %s, at version: %s, on peer: %s", ccName, ccVersion, peer.getName());
		List<ChaincodeInfo> ccinfoList = channel.queryInstantiatedChaincodes(peer);

		boolean found = false;

		for (ChaincodeInfo ccifo : ccinfoList) {

			if (ccPath != null) {
				found = ccName.equals(ccifo.getName()) && ccPath.equals(ccifo.getPath())
						&& ccVersion.equals(ccifo.getVersion());
				if (found) {
					ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(ccName).setVersion(ccVersion)
							.setPath(ccPath).build();
					Config.chaincodeID.put(ccName, chaincodeID);
					break;
				}
			}

			found = ccName.equals(ccifo.getName()) && ccVersion.equals(ccifo.getVersion());
			if (found) {
				ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(ccName).setVersion(ccVersion).setPath(ccPath)
						.build();
				Config.chaincodeID.put(ccName, chaincodeID);
				break;
			}

		}

		return found;
	}
	
}
