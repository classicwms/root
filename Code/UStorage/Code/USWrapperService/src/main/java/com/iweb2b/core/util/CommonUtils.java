package com.iweb2b.core.util;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {

	public static void main(String[] args) {
//		String password = "test";
//		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//		String hashedPassword = passwordEncoder.encode(password);
//		System.out.println(hashedPassword);
		
//		escapeComma();
		
		generateLifeTokenForAsyad ();
	}
	
	/**
	 * 
	 * @return
	 */
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
	
	/**
	 * 
	 * @param errorMsg
	 * @return
	 */
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
	
	/**
	 * getNullPropertyNames
	 * 
	 * @param source
	 * @return
	 */
	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}
	
	/**
	 * 
	 * @param escapeText
	 * @return
	 */
	public static String escapeComma (String escapeText) {
		String escaped = StringEscapeUtils.escapeCsv(escapeText); // I said "Hey, I am 5'10"."
		return escaped;
	}
	
	public static String generateLifeTokenForAsyad() {
		String password = "iweb2basyad2022";
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(password);
		System.out.println(hashedPassword);
		return hashedPassword;
	}
}
