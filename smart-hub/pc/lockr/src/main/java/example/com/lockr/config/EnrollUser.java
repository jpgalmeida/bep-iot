package example.com.lockr.config;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

public class EnrollUser {
    
	public static void reenrollUser(SampleStore sampleStore) throws Exception {

		for (SampleOrg sampleOrg : Config.testSampleOrgs) {
			
			HFCAClient ca = sampleOrg.getCAClient();
			ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

			final String orgName = sampleOrg.getName();

			SampleUser admin = sampleStore.getMember(Config.TEST_ADMIN_NAME, orgName);
			sampleOrg.setAdmin(admin); // The admin of this org.

			sampleOrg.setPeerAdmin(sampleStore.getMember(orgName + "Admin", orgName));

			try {
				admin.setSecretKey(Util.generateSymmetricKey());
				admin.setIV(Util.generateIV());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static void enrollUsersSetup(SampleStore sampleStore) throws Exception {
		////////////////////////////
		// Set up USERS

		// SampleUser can be any implementation that implements
		// org.hyperledger.fabric.sdk.User Interface

		////////////////////////////
		// get users for all orgs

		for (SampleOrg sampleOrg : Config.testSampleOrgs) {

			HFCAClient ca = sampleOrg.getCAClient();

			final String orgName = sampleOrg.getName();
			final String mspid = sampleOrg.getMSPID();
			ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

			if (Config.testConfig.isRunningFabricTLS()) {
				// This shows how to get a client TLS certificate from Fabric CA
				// we will use one client TLS certificate for orderer peers etc.
				final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
				enrollmentRequestTLS.addHost(Config.HOST_ADDR);
				enrollmentRequestTLS.setProfile("tls");
				final Enrollment enroll = ca.enroll("admin", "adminpw", enrollmentRequestTLS);
				final String tlsCertPEM = enroll.getCert();
				final String tlsKeyPEM = Util.getPEMStringFromPrivateKey(enroll.getKey());

				final Properties tlsProperties = new Properties();

				tlsProperties.put("clientKeyBytes", tlsKeyPEM.getBytes(UTF_8));
				tlsProperties.put("clientCertBytes", tlsCertPEM.getBytes(UTF_8));
				Config.clientTLSProperties.put(sampleOrg.getName(), tlsProperties);
				// Save in samplestore for follow on tests.
				sampleStore.storeClientPEMTLCertificate(sampleOrg, tlsCertPEM);
				sampleStore.storeClientPEMTLSKey(sampleOrg, tlsKeyPEM);
			}

			HFCAInfo info = ca.info(); // just check if we connect at all.

			String infoName = info.getCAName();
			if (infoName != null && !infoName.isEmpty()) {
				System.out.println(ca.getCAName().equals(infoName));
			}

			SampleUser admin = sampleStore.getMember(Config.TEST_ADMIN_NAME, orgName);
			if (!admin.isEnrolled()) { // Preregistered admin only needs to be enrolled with Fabric caClient.
				admin.setEnrollment(ca.enroll(admin.getName(), "adminpw"));
				admin.setMspId(mspid);
			}

			sampleOrg.setAdmin(admin); // The admin of this org --
			
			try {
				admin.setSecretKey(Util.generateSymmetricKey());
				admin.setIV(Util.generateIV());
			} catch (Exception e) {
				e.printStackTrace();
			}			

			for(int i = 1; i < 15; i++) {
								SampleUser user = sampleStore.getMember("user" + i, "peerOrg1");
								if (!user.isRegistered()) { // users need to be registered AND enrolled
									RegistrationRequest rr = new RegistrationRequest(user.getName(), "org1.department1");
									user.setEnrollmentSecret(ca.register(rr, admin));
								}
								if (!user.isEnrolled()) {
									user.setEnrollment(ca.enroll(user.getName(), user.getEnrollmentSecret()));
									user.setMspId(mspid);
								}
								sampleOrg.addUser(user); // Remember user belongs to this Org
				//				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + user.getName());
							}

			final String sampleOrgName = sampleOrg.getName();
			final String sampleOrgDomainName = sampleOrg.getDomainName();
			System.out.println("SampleOrgName: " + sampleOrgName);
			System.out.println("SampleOrgDomainName: " + sampleOrgDomainName);

			// src/test/fixture/sdkintegration/e2e-2Orgs/channel/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/

			Path ola = Paths.get(Config.testConfig.getTestChannelPath(), "crypto-config/peerOrganizations/",
					sampleOrgDomainName, format("/users/Admin@%s/msp/keystore", sampleOrgDomainName));
			System.out.println("uri:" + ola.toString());

			SampleUser peerOrgAdmin = sampleStore.getMember(sampleOrgName + "Admin", sampleOrgName,
					sampleOrg.getMSPID(),
					Util.findFileSk(Paths
							.get(Config.testConfig.getTestChannelPath(), "crypto-config/peerOrganizations/",
									sampleOrgDomainName, format("/users/Admin@%s/msp/keystore", sampleOrgDomainName))
							.toFile()),
					Paths.get(Config.testConfig.getTestChannelPath(), "crypto-config/peerOrganizations/", sampleOrgDomainName,
							format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem", sampleOrgDomainName,
									sampleOrgDomainName))
							.toFile());

			sampleOrg.setPeerAdmin(peerOrgAdmin); // A special user that can create channels, join peers and install
													// chaincode

		}

	}

	
}
