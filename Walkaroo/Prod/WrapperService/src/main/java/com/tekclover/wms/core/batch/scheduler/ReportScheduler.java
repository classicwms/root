package com.tekclover.wms.core.batch.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReportScheduler {

	private static final String GET_URL = "https://dev.classicwms.com/webportal/";
	private static final String USER_AGENT = "Mozilla/5.0";
	
//	@Scheduled(fixedDelay = 10000)
	public void sendGET() {
		try {
			URL obj = new URL(GET_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			int responseCode = con.getResponseCode();
			log.info("GET Response Code :: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				log.info("Result: " + response.toString());
			} else {
				log.info("GET request did not work.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
