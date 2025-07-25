package com.tekclover.wms.api.idmaster.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
//	public static String getCurrentLocalDateTime() {
////		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//		LocalDateTime datetime = LocalDateTime.now();
//		Date date = DateUtils.convertStringToDateFormat(datetime.format(newPattern));
//		LocalDate sLocalDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
//		String currentDatetime = sLocalDate.format(newPattern);
//		return currentDatetime;
//	}
	
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
	
	public static String getCurrentDateWithoutTimestamp () {
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
	 * @param
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

	public static void main(String[] args) throws ParseException {
		String str = "01-08-2022"; 
		str += " 00:00:00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
		LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
		Date out = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		log.info("dbMatterGenAcc--PriorityDate-------> : " + out);
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
	 * @param inputDate
	 * @param hours
	 * @param minutes
	 * @return
	 * @throws Exception
	 */
	public static Date addTimeToDate (Date inputDate, int hours, int minutes) throws Exception {
		try {
			LocalDateTime localDateTime = LocalDateTime.ofInstant(inputDate.toInstant(), ZoneId.systemDefault());
			localDateTime = localDateTime.minusHours(hours).minusMinutes(minutes);
			
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

	/**
	 * Created for ReportPdf - 04/07/2025
	 * Aakash Vinayak
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

	/**
	 * Converts input ISO date strings to custom time range strings (2 PM previous day to 1:59 PM current day)
	 *
	 * @param startDateStr
	 * @param endDateStr
	 * @return Formatted string array with time window
	 */
	public static String[] pdfReportDate(String startDateStr, String endDateStr) {
		// Clean 'T' and parse input strings
		startDateStr = startDateStr.replace("T", " ");
		endDateStr = endDateStr.replace("T", " ");

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

		// Parse input strings to LocalDate
		LocalDate startLocalDate = LocalDateTime.parse(startDateStr, inputFormatter).toLocalDate();
		LocalDate endLocalDate = LocalDateTime.parse(endDateStr, inputFormatter).toLocalDate();

		// Adjust range: previous day at 2 PM to current day at 1:59 PM
		LocalDateTime sDateTime = startLocalDate.minusDays(1).atTime(14, 0);
		LocalDateTime eDateTime = endLocalDate.atTime(13, 59);

		String formattedStart = outputFormatter.format(sDateTime);
		String formattedEnd = outputFormatter.format(eDateTime);

		log.info("PdfReport---@--> Start: " + formattedStart);
		log.info("PdfReport---@--> End: " + formattedEnd);

		return new String[] { formattedStart, formattedEnd };
	}
}
