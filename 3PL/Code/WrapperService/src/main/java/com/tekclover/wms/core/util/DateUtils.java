package com.tekclover.wms.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
	public static String getCurrentTimestamp() {
		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("MMddyyyy_HHmmss");
		LocalDateTime datetime = LocalDateTime.now();
		String currentDatetime = datetime.format(newPattern);
		return currentDatetime;
	}

	public static String getCurrentDateWithoutTimestamp() {
		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDateTime datetime = LocalDateTime.now();
		Date date = DateUtils.convertStringToDateFormat(datetime.format(newPattern));
		LocalDate sLocalDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
		String currentDate = sLocalDate.format(newPattern);
		return currentDate;
	}

	public static Date convertStringToDateFormat(String strDate) {
//		String str = "01-08-2022";
		strDate += " 00:00:00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(strDate, formatter);
		Date out = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		log.info("dbMatterGenAcc--PriorityDate-------> : " + out);
		return out;
	}

	/**
	 * getCurrentMonthFirstAndLastDates - Using by Dashboard
	 * 
	 * @return
	 */
	public static String[] getCurrentMonthFirstAndLastDates() {
		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime datetime = LocalDateTime.now();
		String currentDatetime = datetime.format(newPattern);

		LocalDate lastDateOfMonth = LocalDate.parse(currentDatetime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		lastDateOfMonth = lastDateOfMonth
				.withDayOfMonth(lastDateOfMonth.getMonth().length(lastDateOfMonth.isLeapYear()));
		String lastDateOfCurrentMonth = lastDateOfMonth.format(newPattern);
		System.out.println(lastDateOfCurrentMonth);

		String date = "01";
		int month = datetime.getMonthValue();
		int year = datetime.getYear();
		date = year + "-" + month + "-" + date;
		System.out.println(date);

		return new String[] { date, lastDateOfCurrentMonth };
	}

	/**
	 * 
	 * @param date
	 * @param
	 * @return
	 */
	public static LocalDateTime convertDateToLocalDateTime(Date date, String timeFlag) {
		LocalDate sLocalDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
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
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String[] convertDateForSearch(Date startDate, Date endDate) {
		LocalDate sLocalDate = LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
		LocalDate eLocalDate = LocalDate.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
		log.info("LocalDate1------->  " + sLocalDate.atTime(0, 0, 0));
		log.info("LocalDate2------->  " + eLocalDate.atTime(23, 59, 0));

		LocalDateTime sLocalDateTime = sLocalDate.atTime(0, 0, 0);
		LocalDateTime eLocalDateTime = eLocalDate.atTime(23, 59, 0);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		String sConvertedDateTime = formatter.format(sLocalDateTime).replace("T", " ");
		String eConvertedDateTime = formatter.format(eLocalDateTime).replace("T", " ");

		log.info("---@--> " + sConvertedDateTime);
		log.info("---$--> " + eConvertedDateTime);

		String[] dates = new String[] { sConvertedDateTime, eConvertedDateTime };
		return dates;
	}

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 */
	public static Date[] addTimeToDatesForSearch(Date startDate, Date endDate) throws ParseException {
		LocalDate sLocalDate = LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
		LocalDate eLocalDate = LocalDate.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
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

		Date[] dates = new Date[] { sDate, eDate };
		return dates;
	}

	/**
	 * 
	 * @param startDate
	 * @return
	 * @throws ParseException
	 */
	public static Date addTimeToDate(Date startDate) throws ParseException {
		LocalDate sLocalDate = LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
		LocalDateTime sLocalDateTime = sLocalDate.atTime(0, 0, 0);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		String sConvertedDateTime = formatter.format(sLocalDateTime);

		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date sDate = dateFormatter.parse(sConvertedDateTime);
		return sDate;
	}

	public static Date[] addTimeToDatesForSearch(LocalDate startDate, LocalDate endDate) throws ParseException {
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

		Date[] dates = new Date[] { sDate, eDate };
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
		LocalDate date1 = LocalDate.ofInstant(dateParam1.toInstant(), ZoneId.systemDefault());
		LocalDate date2 = LocalDate.ofInstant(dateParam2.toInstant(), ZoneId.systemDefault());
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
	 */
	public static Date convertStringToDate(String strDate) {
//		String str = "01-08-2022"; 
		strDate += " 00:00:00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(strDate, formatter);
		Date out = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		log.info("dbMatterGenAcc--PriorityDate-------> : " + out);
		return out;
	}

	/**
	 * 
	 * @param strDate
	 * @return
	 * @throws Exception 
	 */
	public static Date convertStringToDate2(String strDate)  {
		try {
			SimpleDateFormat sdf3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
//			Date date = sdf3.parse("Wed Apr 12 16:16:33 IST 2023");
			Date date = sdf3.parse(strDate);
			System.out.println("check..." + date);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws ParseException {
//		String str = "01-08-2022"; 
//		str += " 00:00:00";
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
//		LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
//		Date out = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
//		log.info("dbMatterGenAcc--PriorityDate-------> : " + out);

		log.info ("date: " + new Date());
		SimpleDateFormat sdf3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
		try {
			Date d1 = sdf3.parse("Wed Apr 12 16:16:33 IST 2023");
			System.out.println("check..." + d1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date convertStringToYYYYMMDD(String strDate) throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
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
	public static String addTimeToDate2 (Date inputDate, int numberOfDays) throws ParseException {
		try {

			LocalDateTime localDateTime = LocalDateTime.ofInstant(inputDate.toInstant(), ZoneId.systemDefault());
//			localDateTime = localDateTime.plusHours(numberOfDays);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String sConvertedDateTime = formatter.format(localDateTime);
			return sConvertedDateTime;
		} catch (Exception e) {
			log.info("Exception in DateUtils: " + e);
			e.printStackTrace();
		}
		return null;
	}

	public static Date getCurrentKWTDateTime() throws ParseException {

		ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Kuwait")) ;
		LocalDateTime kwtLocalDateTime = zdt.toLocalDateTime();
		System.out.println(kwtLocalDateTime);
		log.info("kwt time: " + kwtLocalDateTime);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		String sConvertedDateTime = formatter.format(kwtLocalDateTime);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date kwtDate = dateFormatter.parse(sConvertedDateTime);
		System.out.println(kwtDate);
		log.info("kwt date time: " + kwtDate);
		return kwtDate;
	}

	/**
	 *
	 * @param date
	 * @return
	 */
	public static String date2String_YYYYMMDD (Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = dateFormat.format(date);
		System.out.println(strDate);
		return strDate;
	}
}
