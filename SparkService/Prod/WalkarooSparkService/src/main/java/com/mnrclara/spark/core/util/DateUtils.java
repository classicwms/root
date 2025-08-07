package com.mnrclara.spark.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

//	public static Date[] addTimeToDates(Date startDate, Date endDate) throws ParseException {
//		try {
			// 03-23-2023 -> 03-23-2023 00:00:00
			// 03-23-2023 -> 03-23-2023 23:59:59
//			LocalDate sLocalDate = LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
//			LocalDate eLocalDate = LocalDate.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
//
//			LocalDateTime sLocalDateTime = sLocalDate.atTime(0, 0, 0);
//			LocalDateTime eLocalDateTime = eLocalDate.atTime(23, 59, 0);
//
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//			String sConvertedDateTime = formatter.format(sLocalDateTime);
//			String eConvertedDateTime = formatter.format(eLocalDateTime);
//
//			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date sDate = dateFormatter.parse(sConvertedDateTime);
//			Date eDate = dateFormatter.parse(eConvertedDateTime);
//
//			Date[] dates = new Date[] { sDate, eDate };
//			return dates;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
