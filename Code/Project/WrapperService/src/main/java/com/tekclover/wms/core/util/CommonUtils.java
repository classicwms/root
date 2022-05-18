package com.tekclover.wms.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {

	public static void main(String[] args) {
		String password = "test";
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(password);
		System.out.println(hashedPassword);
	}
	
	public String randomUUID () {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();
		
		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();

		System.out.println(generatedString);
		return generatedString;
	}
	
	public Map<String, String> prepareErrorResponse (String errorMsg) {
		errorMsg = errorMsg.substring(errorMsg.indexOf('['));
		JSONArray array = new JSONArray(errorMsg);
		Map<String, String> mapError = new HashMap<>();
		for(int i = 0; i < array.length(); i ++) {  
			JSONObject object = array.getJSONObject(i);
			for (Object key : object.names()) {
				mapError.put(String.valueOf(key), object.getString(String.valueOf(key)));
			}
		}
		return mapError;
	}
}
