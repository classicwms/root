package com.ustorage.api.trans.util;

import java.beans.PropertyDescriptor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {

	public static enum DashboardTypes {
		AGREEMENT, AGREEMENT_TOTAL, AGREEMENT_SENT, AGREEMENT_RECEIVED, AGREEMENT_RESENT
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
	 * copy
	 * 
	 * @param fromBean
	 * @return
	 */
	public static Object copy(Object fromBean) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XMLEncoder out = new XMLEncoder(bos);
		out.writeObject(fromBean);
		out.close();
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		XMLDecoder in = new XMLDecoder(bis);
		Object toBean = in.readObject();
		in.close();
		return toBean;
	}

	public static void main(String[] args) {
			LocalDate localDate = LocalDate.now();
			System.out.println(localDate);
			
			DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime datetime = LocalDateTime.now();
			String currentDatetime = datetime.format(newPattern);
			System.out.println(currentDatetime);
	}
}
