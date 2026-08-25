package com.tekclover.wms.api.transaction.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtils {

	public static String dateConv(String input) throws ParseException {
//		String input = "2017-01-18 20:10:00";
		DateTimeFormatter oldPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

		LocalDateTime datetime = LocalDateTime.parse(input, oldPattern);
		String output = datetime.format(newPattern);
		System.out.println("Date in old format (java 8) : " + input);
		System.out.println("Date in new format (java 8) : " + output);
		return output;
	}

	/**
	 * 
	 * @return
	 */
	public static String getCurrentDateTime() {
		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
		LocalDateTime datetime = LocalDateTime.now();
		String currentDatetime = datetime.format(newPattern);
		return currentDatetime;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getCurrentTimestamp () {
		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("MMddyyyy_HHmmss");
		LocalDateTime datetime = LocalDateTime.now();
		String currentDatetime = datetime.format(newPattern);
		return currentDatetime;
	}
	//current Time without Date
	public static int getCurrentTime () {
		LocalDateTime dateTime = LocalDateTime.now(ZoneId.systemDefault());
		int currentHour = dateTime.getHour();
		return currentHour;
	}
	
	/**
	 * getCurrentMonthFirstAndLastDates - Using by Dashboard
	 * @return
	 */
	public static String[] getCurrentMonthFirstAndLastDates() {
		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime datetime = LocalDateTime.now();
		String currentDatetime = datetime.format(newPattern);
		
		LocalDate lastDateOfMonth = LocalDate.parse(currentDatetime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		lastDateOfMonth = lastDateOfMonth.withDayOfMonth(
				lastDateOfMonth.getMonth().length(lastDateOfMonth.isLeapYear()));
		String lastDateOfCurrentMonth = lastDateOfMonth.format(newPattern);
		System.out.println(lastDateOfCurrentMonth);
		
		String date = "01";
		int month = datetime.getMonthValue();
		int year = datetime.getYear();
		date = year + "-" + month + "-" + date;
		System.out.println(date);
		
		return new String [] {date, lastDateOfCurrentMonth};
	}

	/**
	 *
	 * @param date
	 * @param timeFlag
	 * @return
	 */
	public static LocalDateTime convertDateToLocalDateTime(Date date, String timeFlag) {
		LocalDate sLocalDate =  LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
		log.info("--------input Date----------> " + sLocalDate);
		if (timeFlag.equalsIgnoreCase("START")) {
			log.info("--------conv Date--1--------> " + sLocalDate.atStartOfDay());
			return sLocalDate.atStartOfDay();
		} else {
			LocalDateTime nextTime = sLocalDate.atStartOfDay();
			log.info("--------conv Date--2--------> " + nextTime.plusHours(12));
			return nextTime.plusHours(12);
		}
	}
	
	/**
	 * convertDateForSearch
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String[] convertDateForSearch (Date startDate, Date endDate) {
		LocalDate sLocalDate =  LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
		LocalDate eLocalDate =  LocalDate.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
		log.info("LocalDate1------->  " + sLocalDate.atTime(0, 0, 0));
		log.info("LocalDate2------->  " + eLocalDate.atTime(23, 59, 0));
		
		LocalDateTime sLocalDateTime = sLocalDate.atTime(0, 0, 0);
		LocalDateTime eLocalDateTime = eLocalDate.atTime(23, 59, 0);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		String sConvertedDateTime = formatter.format(sLocalDateTime).replace("T", " ");
		String eConvertedDateTime = formatter.format(eLocalDateTime).replace("T", " ");
		
		log.info("---@--> " + sConvertedDateTime);
		log.info("---$--> " + eConvertedDateTime);
		
		String[] dates = new String[] {
			sConvertedDateTime,
			eConvertedDateTime
		};
		return dates;
	}
	
	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 */
	public static Date[] addTimeToDatesForSearch (Date startDate, Date endDate) throws ParseException {
		LocalDate sLocalDate =  LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
		LocalDate eLocalDate =  LocalDate.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
		log.info("LocalDate1------->  " + sLocalDate.atTime(0, 0, 0));
		log.info("LocalDate2------->  " + eLocalDate.atTime(23, 59, 0));
		
		LocalDateTime sLocalDateTime = sLocalDate.atTime(0, 0, 0);
		LocalDateTime eLocalDateTime = eLocalDate.atTime(23, 59, 0);
		log.info("LocalDate1---##----> " + sLocalDateTime);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		
		String sConvertedDateTime = formatter.format(sLocalDateTime);
		String eConvertedDateTime = formatter.format(eLocalDateTime);
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
		Date sDate = dateFormatter.parse(sConvertedDateTime);
		Date eDate = dateFormatter.parse(eConvertedDateTime);
		
		Date[] dates = new Date[] {
			sDate,
			eDate
		};
		return dates;
	}
	
	/**
	 * 
	 * @param startDate
	 * @return
	 * @throws ParseException
	 */
	public static Date addTimeToDate (Date startDate) throws ParseException {
		LocalDate sLocalDate =  LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
		LocalDateTime sLocalDateTime = sLocalDate.atTime(0, 0, 0);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		String sConvertedDateTime = formatter.format(sLocalDateTime);
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
		Date sDate = dateFormatter.parse(sConvertedDateTime);
		return sDate;
	}
	
	/**
	 * 
	 * @param startDate
	 * @return
	 * @throws ParseException
	 */
	public static Date addDayEndTimeToDate (Date startDate) throws ParseException {
		LocalDate sLocalDate =  LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
		LocalDateTime sLocalDateTime = sLocalDate.atTime(23, 59, 0);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		String sConvertedDateTime = formatter.format(sLocalDateTime);
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
		Date sDate = dateFormatter.parse(sConvertedDateTime);
		return sDate;
	}
	
	/**
	 * 
	 * @param startDate
	 * @return
	 * @throws ParseException
	 */
	public static Date addTimeToStartDate (Date startDate, int hour, int min, int sec) throws ParseException {
		LocalDate sLocalDate =  LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
		LocalDateTime sLocalDateTime = sLocalDate.atTime(hour, min, sec);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		String sConvertedDateTime = formatter.format(sLocalDateTime);
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
		Date sDate = dateFormatter.parse(sConvertedDateTime);
		return sDate;
	}
	
	/**
	 * 
	 * @param startDate
	 * @return
	 * @throws ParseException
	 */
	public static Date addTimeToEndDate (Date startDate, int hour, int min, int sec) throws ParseException {
		LocalDate sLocalDate =  LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
		LocalDateTime sLocalDateTime = sLocalDate.atTime(hour, min, sec);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		String sConvertedDateTime = formatter.format(sLocalDateTime);
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
		Date sDate = dateFormatter.parse(sConvertedDateTime);
		return sDate;
	}
	
	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 */
	public static Date[] addTimeToDatesForSearch (LocalDate startDate, LocalDate endDate) throws ParseException {
//		LocalDate sLocalDate =  LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
//		LocalDate eLocalDate =  LocalDate.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
		log.info("LocalDate1------->  " + startDate.atTime(0, 0, 0));
		log.info("LocalDate2------->  " + endDate.atTime(23, 59, 0));
		
		LocalDateTime sLocalDateTime = startDate.atTime(0, 0, 0);
		LocalDateTime eLocalDateTime = endDate.atTime(23, 59, 0);
		log.info("LocalDate1---##----> " + sLocalDateTime);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		
		String sConvertedDateTime = formatter.format(sLocalDateTime);
		String eConvertedDateTime = formatter.format(eLocalDateTime);
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
		Date sDate = dateFormatter.parse(sConvertedDateTime);
		Date eDate = dateFormatter.parse(eConvertedDateTime);
		
		Date[] dates = new Date[] {
			sDate,
			eDate
		};
		return dates;
	}

	/**
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 * @throws ParseException
	 */
	public static boolean compareLocalDates(LocalDate date1, LocalDate date2) throws ParseException {
		if (date1.compareTo(date2) == 0) {
			return true;
		}
		return false;
	}
	
	public static boolean compareDates(Date dateParam1, Date dateParam2) throws ParseException {
		LocalDate date1 =  LocalDate.ofInstant(dateParam1.toInstant(), ZoneId.systemDefault());
		LocalDate date2 =  LocalDate.ofInstant(dateParam2.toInstant(), ZoneId.systemDefault());
		if (date1.compareTo(date2) == 0) {
			return true;
		}
		return false;
	}
	
	public static boolean compareLocalDateTime(LocalDateTime date1, LocalDateTime date2) throws ParseException {
		if (date1.compareTo(date2) == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param strDate
	 * @return
	 * @throws ParseException 
	 */
	public static Date convertStringToDate(String strDate) throws ParseException {
		LocalTime time = LocalTime.now();
		System.out.println("---------> " + time);
		strDate = strDate + " " + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();
		Date date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(strDate);
//		log.info("convertStringToDate-------> : " + date);
		return date;
	}

	/**
	 *
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date convertStringToDate2(String strDate) throws ParseException {
		LocalTime time = LocalTime.now();
		System.out.println("---------> " + time);
		strDate = strDate + " " + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
//		log.info("convertStringToDate-------> : " + date);
		return date;
	}

	/**
	 *
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date convertStringToDateWithTime(String strDate) throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		return date;
	}
	
	/**
	 * 
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date convertStringToDate_end(String strDate) throws ParseException {
		strDate = strDate + " 23:59:00";
		Date date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(strDate);
		log.info("convertStringToDate-------> : " + date);
		return date;
	}
	
	public static Date convertStringToDate_start(String strDate) throws ParseException {
		strDate = strDate + " 00:00:01";
		Date date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(strDate);
		log.info("convertStringToDate-------> : " + date);
		return date;
	}

	/**
	 *
	 * @param strDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 */
	public static Date[] convertStringToDate(String strDate, String endDate) throws ParseException {
		strDate = strDate + " 00:00:00";
		endDate = endDate + " 23:59:59";
		Date sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		Date eDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);
		log.info("convertStringToDate-------> : " + sDate + ", " + eDate);
		Date[] dates = new Date[] {
				sDate,
				eDate
		};
		return dates;
	}
	
	public static String convertDate2String(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");  
		String strDate = dateFormat.format(date);  
		System.out.println(strDate);
		return strDate;
	}
	
	public static String date2String_MMDDYYYY (Date date) {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");  
		String strDate = dateFormat.format(date);  
		System.out.println(strDate);
		return strDate;
	}
	
	/**
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static Date getCurrentKWTDateTime() throws ParseException {
		ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Kuwait")) ;
		LocalDateTime kwtLocalDateTime = zdt.toLocalDateTime();
		System.out.println(kwtLocalDateTime);
//		log.info("Kuwait Time: " + kwtLocalDateTime);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		String sConvertedDateTime = formatter.format(kwtLocalDateTime);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
		Date kwtDate = dateFormatter.parse(sConvertedDateTime);
		System.out.println(kwtDate);
		log.info("Kuwait Date Time: " + kwtDate);
		return kwtDate;
	}
	
	/**
	 * 
	 * @param date
	 * @param hh
	 * @param mi
	 * @param ss
	 * @return
	 * @throws Exception
	 */
	public static Date addTimeToDate(String date, int hh, int mi, int ss) throws Exception {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        LocalDateTime sLocalDateTime = localDate.atTime(hh, mi, ss);
		log.info("LocalDate1---##----> " + sLocalDateTime);
		
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		String sConvertedDateTime = formatter1.format(sLocalDateTime);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date sDate = dateFormatter.parse(sConvertedDateTime);
		log.info("sDate---##----> " + sLocalDateTime);
		
		Instant instant = sDate.toInstant();
		ZoneId defaultZoneId = ZoneId.systemDefault();
		ZonedDateTime zonedDateTime = instant.atZone(defaultZoneId);
        System.out.println("zonedDateTime : " + zonedDateTime);
        Date dateC = Date.from(zonedDateTime.toInstant());
        System.out.println("dateC : " + dateC);
		return dateC;
	}
	
	/**
	 * 
	 * @param localDate
	 * @param hh
	 * @param mi
	 * @param ss
	 * @return
	 * @throws Exception
	 */
	public static Date addTimeToDate(LocalDate localDate, int hh, int mi, int ss) throws Exception {
        LocalDateTime sLocalDateTime = localDate.atTime(hh, mi, ss);
		log.info("LocalDate1---##----> " + sLocalDateTime);
		
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		String sConvertedDateTime = formatter1.format(sLocalDateTime);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date sDate = dateFormatter.parse(sConvertedDateTime);
		log.info("sDate---##----> " + sLocalDateTime);
		
		Instant instant = sDate.toInstant();
		ZoneId defaultZoneId = ZoneId.systemDefault();
		ZonedDateTime zonedDateTime = instant.atZone(defaultZoneId);
        System.out.println("zonedDateTime : " + zonedDateTime);
        Date dateC = Date.from(zonedDateTime.toInstant());
        System.out.println("dateC : " + dateC);
		return dateC;
	}
	
	/**
	 * 
	 * @param fromDeliveryDate
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public static Date convertStringToDateWithT(String fromDeliveryDate) throws NumberFormatException, Exception {
		// String fromDeliveryDate = "01-08-2022T03:12:12"; --> with "T"
		String dateAlone = fromDeliveryDate.substring(0, fromDeliveryDate.indexOf('T'));
		String[] time = fromDeliveryDate.substring(fromDeliveryDate.indexOf('T')+1).split(":");
		log.info("time: " + time[0] + "," + time[1] + "," + time[2]);
		
		return addTimeToDate (dateAlone, Integer.valueOf(time[0]), Integer.valueOf(time[1]), Integer.valueOf(time[2]));
	}
	
	public static void main(String[] args) throws Exception {
//		String str = "01-08-2022"; 
//		str += " 00:00:00";
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
//		LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
//		Date out = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
//		log.info("dbMatterGenAcc--PriorityDate-------> : " + out);
		
//		LocalTime time = LocalTime.now();
//		System.out.println("---------> " + time);
//		String str = "04-30-2022";// + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond(); 
//		convertStringToDate (str);
		
//		LocalDate today = LocalDate.now();
//		System.out.println("First day: " + today.withDayOfMonth(1));
//		System.out.println("Last day: " + today.withDayOfMonth(today.lengthOfMonth()));
//		
//		Date date = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
//		System.out.println("---------> " + date);
//		date2String_MMDDYYYY (new Date());
		
//		ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Kuwait")) ;
//		LocalDateTime kwtLocalDateTime = zdt.toLocalDateTime();
//		System.out.println(kwtLocalDateTime);
//		
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//		String sConvertedDateTime = formatter.format(kwtLocalDateTime);
//		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
//		Date kwtDate = dateFormatter.parse(sConvertedDateTime);
//		System.out.println(kwtDate);
		
//		String fromDeliveryDate = "01-08-2022T03:12:12"; 
//		String dateAlone = fromDeliveryDate.substring(0, fromDeliveryDate.indexOf('T'));
//		String[] time = fromDeliveryDate.substring(fromDeliveryDate.indexOf('T')+1).split(":");
//		log.info("time: " + time[0] + "," + time[1] + "," + time[2]);
//		
//		addTimeToDate (dateAlone, Integer.valueOf(time[0]), Integer.valueOf(time[1]), Integer.valueOf(time[2]));
//		addTimeToDate (fromDeliveryDate, 13, 59, 59);
		
		dateSubtract(1, 14, 0, 0);
		dateSubtract(0, 13, 59, 59);
		
		LocalDate today = LocalDate.now();
		LocalDate beginDayOfMonth = today.withDayOfMonth(1);
		addTimeToDate(beginDayOfMonth, 14, 0, 0);
		addTimeToDate(today, 13, 59, 59);
	}

	/**
	 * 
	 * @param noOfDays
	 * @return
	 */
	public static Date dateSubtract (int noOfDays) {
//		fromDeliveryDate_d = DateUtils.addTimeToDate (fromDeliveryDate, 14, 0, 0);
//		toDeliveryDate_d = DateUtils.addTimeToDate(toDeliveryDate, 13, 59, 59);
		try {
			LocalDate today = LocalDate.now().minusDays(noOfDays);
			return addTimeToDate(today, 14, 0, 0);
			
//			return Date.from(fromDeliveryDate_d.atStartOfDay(ZoneId.systemDefault()).toInstant());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param noOfDays
	 * @param hh
	 * @param mi
	 * @param ss
	 * @return
	 */
	public static Date dateSubtract (int noOfDays, int hh, int mi, int ss) {
		try {
			LocalDate today = LocalDate.now().minusDays(noOfDays);
			return addTimeToDate(today, hh, mi, ss);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * @param inputDate
	 * @param numberOfDays
	 * @return
	 * @throws ParseException
	 */
	public static Date addTimeToDate(Date inputDate, int numberOfDays) throws ParseException {
		try {
			LocalDateTime localDateTime = LocalDateTime.ofInstant(inputDate.toInstant(), ZoneId.systemDefault());
			localDateTime = localDateTime.plusHours(numberOfDays);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			String sConvertedDateTime = formatter.format(localDateTime);

			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date sDate = dateFormatter.parse(sConvertedDateTime);
			return sDate;
		} catch (Exception e) {
			log.info("Exception in DateUtils: " + e);
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date convertStringToDateByYYYYMMDD(String strDate) throws ParseException {
		strDate = strDate + " 00:00:00";
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		log.info("convertStringToDate-------> : " + date);
		return date;
	}
	/**
	 *
	 * @return
	 */
	public static Long getCurrentYear() {
		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("yyyy");
		LocalDateTime datetime = LocalDateTime.now();
		Long currentDatetime = Long.valueOf(datetime.format(newPattern));
		return currentDatetime;
	}

	/**
	 *
	 * @param inputDate
	 * @return
	 */
	public static Date dateSubtract (Date inputDate) {
		try {

			LocalDate eLocalDate =  LocalDate.ofInstant(inputDate.toInstant(), ZoneId.systemDefault()).minusDays(1);
			log.info("LocalDate2------->  " + eLocalDate.atTime(23, 59, 0));

			LocalDateTime eLocalDateTime = eLocalDate.atTime(23, 59, 0);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

			String eConvertedDateTime = formatter.format(eLocalDateTime);

			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date eDate = dateFormatter.parse(eConvertedDateTime);

			return eDate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * @param input
	 * @return
	 * @throws ParseException
	 */
	public static LocalDate dateConv2(String input) throws ParseException {
		DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
				.parseCaseInsensitive().parseLenient()
				.appendPattern("[yyyy-MM-dd]")
				.appendPattern("[M/dd/yyyy]")
				.appendPattern("[M/d/yyyy]")
				.appendPattern("[MM/dd/yyyy]")
				.appendPattern("[MMM dd yyyy]");

		DateTimeFormatter formatter2 = builder.toFormatter(Locale.ENGLISH);
		try {
			LocalDate date = LocalDate.parse(input, formatter2);
			System.out.printf("%s%n", date);
			return date;
		} catch (Exception e) {
			System.out.printf("%s is not parsable! %n", input);
			throw e;
		}
	}
}
