package example.com.lockr.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import example.com.lockr.config.*;


@RestController
@RequestMapping("rest")
public class SdkController {

	@PostConstruct
	public void setup() {
		try {
			Util.out("\n\n\nRUNNING: %s.\n", Config.testName);	

			TestUtils.resetConfig();
			Config.configHelper.customizeConfig();

			Config.testSampleOrgs = Config.testConfig.getIntegrationTestsSampleOrgs();
			// Set up hfca for each sample org

			for (SampleOrg sampleOrg : Config.testSampleOrgs) {
				String caName = sampleOrg.getCAName(); // Try one of each name and no name.
				if (caName != null && !caName.isEmpty()) {
					sampleOrg.setCAClient(HFCAClient.createNewInstance(caName, sampleOrg.getCALocation(),
							sampleOrg.getCAProperties()));
				} else {
					sampleOrg.setCAClient(
							HFCAClient.createNewInstance(sampleOrg.getCALocation(), sampleOrg.getCAProperties()));
				}
			}

			Config.sampleStore = new SampleStore(Config.sampleStoreFile);
		
			
			if (Config.sampleStoreFile.exists()) {
				
				Thread.sleep(5000);
				EnrollUser.reenrollUser(Config.sampleStore);
				Thread.sleep(5000);
				reconstructChannel();
			} else {
				EnrollUser.enrollUsersSetup(Config.sampleStore);
				Thread.sleep(5000);
				createchannel();
				Thread.sleep(5000);
				installchaincode();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reconstructChannel() {
		try {

			Config.hfClient = HFClient.createNewInstance();
			Config.hfClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

			////////////////////////////
			// Construct and run the channels
			Config.sampleOrg = Config.testConfig.getIntegrationTestsSampleOrg("peerOrg1");
			Config.fooChannel = CreateChannel.reconstructChannel(Config.FOO_CHANNEL_NAME, Config.hfClient,
					Config.sampleOrg, Config.sampleStore);
			Config.sampleStore.saveChannel(Config.fooChannel);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/enrolladmin", method = RequestMethod.GET)
	public void enrolladmin() {

		// This enrolls users with fabric ca and setups sample store to get users later.
		try {
			EnrollUser.enrollUsersSetup(Config.sampleStore);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@RequestMapping(value = "/createchannel", method = RequestMethod.GET)
	public void createchannel() {
		try {
			Config.hfClient = HFClient.createNewInstance();

			Config.hfClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

			////////////////////////////
			// Construct and run the channels
			Config.sampleOrg = Config.testConfig.getIntegrationTestsSampleOrg("peerOrg1");
			Config.fooChannel = CreateChannel.constructChannel(Config.FOO_CHANNEL_NAME, Config.hfClient,
					Config.sampleOrg);
			Config.sampleStore.saveChannel(Config.fooChannel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/installchaincode", method = RequestMethod.GET)
	public void installchaincode() {
		RunChannel.installChaincode(Config.hfClient, Config.fooChannel, true, Config.sampleOrg, Config.CHAIN_CODE_FILEPATH_RENTAL, Config.CHAIN_CODE_NAME_RENTAL);
		RunChannel.installChaincode(Config.hfClient, Config.fooChannel, true, Config.sampleOrg, Config.CHAIN_CODE_FILEPATH_PRICE, Config.CHAIN_CODE_NAME_PRICE);
		
	}


	@RequestMapping(value = "/initRental/{lockerid}/{sector}/{duration}", method = RequestMethod.POST)
	public ErrorInfo initRental(@PathVariable("lockerid") String lockerid,
			@PathVariable("sector") String sector, @PathVariable("duration") String duration) {
		
		String result = RunChannel.initRental(lockerid, sector,duration);	
		ErrorInfo res = new ErrorInfo(0, "", result);
		
		//"http://localhost:1880/openLocker"		
		
		try {
//			sendPost("openLocker");
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		return res;
	}
	
	@RequestMapping(value = "/endRental/{rentalId}", method = RequestMethod.POST)
	public ErrorInfo endRental(@PathVariable("rentalId") String rentalId) {
		System.out.println("End Rental");
		ErrorInfo res = new ErrorInfo(0, "", "Success");
		try {
			sendPost("closeLocker");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private static void disableSslVerification() {
	    try
	    {
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	            public void checkClientTrusted(X509Certificate[] certs, String authType) {
	            }
	            public void checkServerTrusted(X509Certificate[] certs, String authType) {
	            }
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}
	        }
	        };

	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };

	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (KeyManagementException e) {
	        e.printStackTrace();
	    }
	}
	private void sendPost(String op) throws Exception {
		
		disableSslVerification();
		
		System.out.println("Sending operation to locker: "+op);
		String url = "https://192.168.1.85:1880/"+op;
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");


		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		System.out.println(response.toString());

	}
	
	@RequestMapping(value = "/getRentalsByRange/{startId}/{endId}", method = RequestMethod.POST)
	public ErrorInfo getRentalByRange(@PathVariable("startId") String startId, @PathVariable("endId") String endId) {
		
		String result = RunChannel.getRentalsByRange(startId, endId);	
		ErrorInfo res = new ErrorInfo(0, "", result);
		return res;
	}
	

}