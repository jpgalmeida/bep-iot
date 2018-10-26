package example.com.lockr.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import example.com.lockr.config.*;


@RestController
@RequestMapping("rest")
public class TestController {

	@RequestMapping(value = "/initRental1/{lockerid}/{sector}/{duration}/{usr}", method = RequestMethod.POST)
	public ErrorInfo initRental(@PathVariable("lockerid") String lockerid,
			@PathVariable("sector") String sector, @PathVariable("duration") String duration, @PathVariable("usr") String usr) {
		
		String result = RunChannel.initRental(lockerid, sector, duration, usr);	
		ErrorInfo res = new ErrorInfo(0, "", result);
		
		System.out.println(">>> USER "+usr);
		//"http://localhost:1880/openLocker"		
		
		try {
//			sendPost("openLocker");
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("ERROR!!!!");
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
	public void sendPost(String op) throws Exception {
		
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
	
	@RequestMapping(value = "/endRental1/{rentalId}", method = RequestMethod.POST)
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
	
	
	
	@RequestMapping(value = "/getRentalsByRange1/{startId}/{endId}/{usr}", method = RequestMethod.POST)
	public ErrorInfo getRentalByRange(@PathVariable("startId") String startId, @PathVariable("endId") String endId, @PathVariable("usr") String usr) {
		
		String result = RunChannel.getRentalsByRange(startId, endId, usr);	
		ErrorInfo res = new ErrorInfo(0, "", result);
		return res;
	}
	
	@RequestMapping(value = "/initPrice1/{sector}/{price}/{usr}", method = RequestMethod.POST)
	public ErrorInfo initSectorPrice(@PathVariable("sector") String sector, @PathVariable("price") String price, @PathVariable("usr") String usr) {
		System.out.println(">>> USER "+usr);
		String result = RunChannel.initPrice(sector, price, usr);	
		ErrorInfo res = new ErrorInfo(0, "", result);
		return res;
	}
	
	@RequestMapping(value = "/readSectorPrice1/{sector}/{usr}", method = RequestMethod.GET)
	public ResponseInfo readSectorPrice(@PathVariable("sector") String sector, @PathVariable("usr") String usr) {
		System.out.println(">>> USER "+usr);
		String result = RunChannel.readSectorPrice(sector, usr);	
		
		ResponseInfo res = new ResponseInfo(0, "", result);
		return res;
	}
	
	@RequestMapping(value = "/getSectorsByRange1/{startId}/{endId}/{usr}", method = RequestMethod.POST)
	public ErrorInfo getSectorsByRange(@PathVariable("startId") String startId, @PathVariable("endId") String endId, @PathVariable("usr") String usr) {
		
		String result = RunChannel.getSectorsByRange(startId, endId, usr);	
		ErrorInfo res = new ErrorInfo(0, "", result);
		return res;
	}
	
}
