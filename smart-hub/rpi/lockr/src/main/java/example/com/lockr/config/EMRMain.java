package example.com.lockr.config;
//package pt.ulisboa.tecnico.emr;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
//import org.hyperledger.fabric.sdk.Channel;
//import org.hyperledger.fabric.sdk.HFClient;
//import org.hyperledger.fabric.sdk.security.CryptoSuite;
//import org.hyperledger.fabric_ca.sdk.HFCAClient;
//
//public class EMRMain {
//
//	public static void main(String[] args) {
//		try {
//			runFabric();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void runFabric() throws Exception {
//
//		Util.out("\n\n\nRUNNING: %s.\n", Config.testName);
//		// configHelper.clearConfig();
//		// assertEquals(256, Config.getConfig().getSecurityLevel());
//		TestUtils.resetConfig();
//		Config.configHelper.customizeConfig();
//
//		Config.testSampleOrgs = Config.testConfig.getIntegrationTestsSampleOrgs();
//		// Set up hfca for each sample org
//
//		for (SampleOrg sampleOrg : Config.testSampleOrgs) {
//			String caName = sampleOrg.getCAName(); // Try one of each name and no name.
//			if (caName != null && !caName.isEmpty()) {
//				sampleOrg.setCAClient(
//						HFCAClient.createNewInstance(caName, sampleOrg.getCALocation(), sampleOrg.getCAProperties()));
//			} else {
//				sampleOrg.setCAClient(
//						HFCAClient.createNewInstance(sampleOrg.getCALocation(), sampleOrg.getCAProperties()));
//			}
//		}
//
//		// Persistence is not part of SDK. Sample file store is for demonstration
//		// purposes only!
//		// MUST be replaced with more robust application implementation (Database, LDAP)
//
//		if (Config.sampleStoreFile.exists()) { // For testing start fresh
//			Config.sampleStoreFile.delete();
//		}
//		Config.sampleStore = new SampleStore(Config.sampleStoreFile);
//
//		EnrollUser.enrollUsersSetup(Config.sampleStore); // This enrolls users with fabric ca and setups sample store to
//															// get users later.
//		runFabricTest(Config.sampleStore); // Runs Fabric tests with constructing channels,
//		// joining peers, exercising chaincode
//
//	}
//
//	public static void runFabricTest(final SampleStore sampleStore) throws Exception {
//
//		////////////////////////////
//		// Setup client
//
//		// Create instance of client.
//		HFClient client = HFClient.createNewInstance();
//
//		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
//
//		////////////////////////////
//		// Construct and run the channels
//		SampleOrg sampleOrg = Config.testConfig.getIntegrationTestsSampleOrg("peerOrg1");
//		Channel fooChannel = CreateChannel.constructChannel(Config.FOO_CHANNEL_NAME, client, sampleOrg);
//		sampleStore.saveChannel(fooChannel);
//		RunChannel.runChannel(client, fooChannel, true, sampleOrg);
//
//		assertFalse(fooChannel.isShutdown());
//		fooChannel.shutdown(true); // Force foo channel to shutdown clean up resources.
//		assertTrue(fooChannel.isShutdown());
//
//		assertNull(client.getChannel(Config.FOO_CHANNEL_NAME));
//		Util.out("\n");
//
//		// sampleOrg = testConfig.getIntegrationTestsSampleOrg("peerOrg2");
//		// Channel barChannel = constructChannel(BAR_CHANNEL_NAME, client, sampleOrg);
//		// assertTrue(barChannel.isInitialized());
//		// /**
//		// * sampleStore.saveChannel uses {@link Channel#serializeChannel()}
//		// */
//		// sampleStore.saveChannel(barChannel);
//		// assertFalse(barChannel.isShutdown());
//		// runChannel(client, barChannel, true, sampleOrg, 100); // run a newly
//		// constructed bar channel with different b
//		// // value!
//		// // let bar channel just shutdown so we have both scenarios.
//		//
//		// out("\nTraverse the blocks for chain %s ", barChannel.getName());
//		//
//		// blockWalker(client, barChannel);
//		//
//		// assertFalse(barChannel.isShutdown());
//		// assertTrue(barChannel.isInitialized());
//		Util.out("That's all folks!");
//
//	}
//
//}
