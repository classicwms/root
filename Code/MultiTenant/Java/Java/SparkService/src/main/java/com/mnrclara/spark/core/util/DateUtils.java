package com.mnrclara.spark.core.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class DateUtils {


    public static Date[] addTimeToDatesForSearch(Date startDate, Date endDate) {
        Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        startCalendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        endCalendar.setTime(endDate);

        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 999);

        return new Date[]{startCalendar.getTime(), endCalendar.getTime()};
    }


//    public static Date[] addTimeToDatesForSearch(Date startDate, Date endDate) {
//        // Convert startDate and endDate to LocalDateTime
//        LocalDateTime sLocalDateTime = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
//        LocalDateTime eLocalDateTime = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
//
//        // Truncate startDate to the beginning of the day
//        sLocalDateTime = sLocalDateTime.toLocalDate().atStartOfDay();
//
//        // Set endDate to the end of the day
//        eLocalDateTime = eLocalDateTime.toLocalDate().atTime(23, 59, 59, 999999999);
//
//        // Convert LocalDateTime back to Date
//        Date sDate = Date.from(sLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
//        Date eDate = Date.from(eLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
//
//        // Return the adjusted dates
//        return new Date[]{sDate, eDate};
//    }

}
