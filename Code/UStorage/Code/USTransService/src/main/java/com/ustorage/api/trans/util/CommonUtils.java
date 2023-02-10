package com.ustorage.api.trans.util;

import java.beans.PropertyDescriptor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import lombok.extern.slf4j.Slf4j;

import static com.ustorage.api.trans.util.DateUtils.convertDateToLocalDateTime;

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
	
    /**
     * Date From : 01-01-2023
     * Date To: 31-12-2023
     * Rent Period: Monthly
     * Rent value/ Period : 100
     * @param rentAmt
     */
    public static Rent calculateRent (String period, Date startDate, Date endDate, long rentAmt) {
		LocalDate today1 = convertDateToLocalDateTime(endDate);
		LocalDate today = today1.plusDays(1);
		LocalDate yesterday = convertDateToLocalDateTime(startDate);
		//LocalDate today = LocalDate.now();
		//LocalDate yesterday = today.minusMonths(2);
		long daysDiff = Duration.between(yesterday.atStartOfDay(), today.atStartOfDay()).toDays(); // another option
		log.info("daysDiff : " + daysDiff);
		
		long months = 0;
		long weeks = 0;
		long quarterly = 0;
		long halfyearly = 0;
		long yearly = 0;
		double totalAmount = 0;
		Rent rent = new Rent();
		
		if (period.equalsIgnoreCase("MONTHLY")) {
			for (long month = 1; month <= daysDiff/30; month++) {
				log.info("month : " + month);
				totalAmount += rentAmt;
				months = month;
			}
			rent.setRentPeriod("Monthly");
			rent.setPeriod(months);
			long days = daysDiff%30;
			double rentRem = Math.round(rentAmt/30) * days;
			totalAmount += rentRem;

			rent.setDays(days);
			rent.setTotalRent(totalAmount);
		}
		if (period.equalsIgnoreCase("Weekly")) {
			for (long week = 1; week <= daysDiff/7; week++) {
				log.info("week : " + week);
				totalAmount += rentAmt;
				weeks = week;
			}
			rent.setRentPeriod("Weekly");
			rent.setPeriod(weeks);
			long days = daysDiff%7;
			double rentRem = Math.round(rentAmt/7) * days;
			totalAmount += rentRem;

			rent.setDays(days);
			rent.setTotalRent(totalAmount);
		}
		if (period.equalsIgnoreCase("Quarterly")) {
			for (long quarter = 1; quarter <= daysDiff/91.25; quarter++) {
				log.info("quarter : " + quarter);
				totalAmount += rentAmt;
				quarterly = quarter;
			}
			rent.setRentPeriod("Quarterly");
			rent.setPeriod(quarterly);

			long days = daysDiff%91;
			long rentRem = Math.round(rentAmt/91.25) * days;
			totalAmount += rentRem;

			rent.setDays(days);
			rent.setTotalRent(totalAmount);
		}
		if (period.equalsIgnoreCase("HalfYearly")) {
			for (long halfyear = 1; halfyear <= daysDiff/182; halfyear++) {
				log.info("half yearly : " + halfyear);
				totalAmount += rentAmt;
				halfyearly = halfyear;
			}
			rent.setRentPeriod("HalfYearly");
			rent.setPeriod(halfyearly);

			long days = daysDiff%182;
			long rentRem = Math.round(rentAmt/182.5) * days;
			totalAmount += rentRem;

			rent.setDays(days);
			rent.setTotalRent(totalAmount);
		}
		if (period.equalsIgnoreCase("Yearly")) {
			for (long year = 1; year <= daysDiff/365; year++) {
				log.info("yearly : " + year);
				totalAmount += rentAmt;
				yearly = year;
			}
			rent.setRentPeriod("Yearly");
			rent.setPeriod(yearly);

			long days = daysDiff%365;
			double rentRem = Math.round(rentAmt/365) * days;
			totalAmount += rentRem;

			rent.setDays(days);
			rent.setTotalRent(totalAmount);
		}


			//log.info("rem days : " + days + ":" + Math.round(rentAmt/30) + "::" + rentRem + "-totalAmount :" + totalAmount);
			log.info("Rent:  " + rent);
			return rent;

    }

    /**
     * 
     * @param args
     */
	public static void main(String[] args) {
			LocalDate localDate = LocalDate.now();
			System.out.println(localDate);
			
			DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime datetime = LocalDateTime.now();
			String currentDatetime = datetime.format(newPattern);
			System.out.println(currentDatetime);
	}
}
