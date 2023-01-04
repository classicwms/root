package com.ustorage.api.trans.util;



import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtils {

    /**
     *
     * @param date
     * @return
     */
    public static Date convertSQLtoUtilDate(java.sql.Date date) {
        if (date != null) {
            java.util.Date utilDate = new java.util.Date(date.getTime());
            return utilDate;
        }
        return null;
    }

    /**
     *
     * @param input
     * @return
     * @throws ParseException
     */
    public static String dateConvFromUI (String input) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = inputFormat.parse(input); //"2018-04-10T04:00:00.000Z");
        String formattedDate = outputFormat.format(date);
        System.out.println(formattedDate); // prints 10-04-2018
        return formattedDate;
    }

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

    public static String getCurrentDateTime () {
        DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime datetime = LocalDateTime.now();
        String currentDatetime = datetime.format(newPattern);
        return currentDatetime;
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    public static Date[] addTimeToDatesForSearch (Date startDate, Date endDate) throws ParseException {
        try {
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
        } catch (Exception e) {
            log.info("Exception in DateUtils: " + e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param strDate
     * @return
     * @throws ParseException
     */
    public static Date convertStringToDate(String strDate) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
        return date;
    }

    /**
     *
     * @param strDate
     * @return
     * @throws ParseException
     */
    public static Date convertQBDate(String strDate) throws Exception {
//		String strDate = "2022-09-16T15:44:20-06:00";
        strDate = strDate.replace('T',  ' ');
        strDate = strDate.substring(0, strDate.lastIndexOf('-'));
        System.out.println(strDate);

        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
        System.out.println(date);

        log.info("convertStringToDate-------> : " + date);
        return date;
    }

    /**
     *
     * @param strDate
     * @return
     * @throws ParseException
     */
    public static Date convertQBPaymentReceiveDate(String strDate) throws ParseException {
//		String strDate = "2022-07-18";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
        return date;
    }

    /**
     *
     * @return
     * @throws ParseException
     */
    public static Date[] getCurrentMonth () throws ParseException {
        LocalDate currentdate = LocalDate.now();
        LocalDate monthBegin = currentdate.withDayOfMonth(1);
        LocalDate monthEnd = currentdate.plusMonths(1).withDayOfMonth(1).minusDays(1);

        log.info("monthBegin: " + monthBegin);
        log.info("monthEnd: " + monthEnd);

        Date[] currentMonth = addTimeToDatesForSearch(monthBegin, monthEnd);
        return currentMonth;
    }

    /**
     *
     * @return
     * @throws ParseException
     */
    public static Date[] getPreviousMonth () throws ParseException {
        YearMonth thisMonth = YearMonth.now();
        YearMonth lastMonth = thisMonth.minusMonths(1);
        LocalDate monthBegin = lastMonth.atDay(1);
        LocalDate monthEnd = lastMonth.atEndOfMonth();

        log.info("monthBegin: " + monthBegin);
        log.info("monthEnd: " + monthEnd);

        Date[] currentMonth = addTimeToDatesForSearch(monthBegin, monthEnd);
        return currentMonth;
    }

    /**
     *
     * @return
     * @throws ParseException
     */
    public static Date[] getCurrentYear() throws ParseException {
        YearMonth thisMonth = YearMonth.now();
        YearMonth firstMonth = thisMonth.withMonth(1);
        YearMonth lastMonth = firstMonth.withMonth(12);

        LocalDate yearBegin = firstMonth.atDay(1);
        LocalDate yearEnd = lastMonth.atEndOfMonth();

        log.info("yearBegin: " + yearBegin);
        log.info("yearEnd: " + yearEnd);

        Date[] currentMonth = addTimeToDatesForSearch(yearBegin, yearEnd);
        return currentMonth;
    }

    /**
     *
     * @return
     * @throws ParseException
     */
    public static Date[] getPreviousYear() throws ParseException {
        YearMonth currentMonth = YearMonth.now();
        YearMonth preFirstMonth = currentMonth.minusYears(1).withMonth(1);
        YearMonth preLastMonth = preFirstMonth.withMonth(12);

        LocalDate yearBegin = preFirstMonth.atDay(1);
        LocalDate yearEnd = preLastMonth.atEndOfMonth();

        log.info("yearBegin: " + yearBegin);
        log.info("yearEnd: " + yearEnd);

        Date[] preYearDates = addTimeToDatesForSearch(yearBegin, yearEnd);
        return preYearDates;
    }

    /**
     *
     * @return
     * @throws ParseException
     */
    public static Date[] getCurrentQuarter () throws ParseException {
        LocalDate localDate = LocalDate.now();
        LocalDate firstDayOfQuarter = localDate.with(localDate.getMonth().firstMonthOfQuarter())
                .with(TemporalAdjusters.firstDayOfMonth());

        LocalDate lastDayOfQuarter = firstDayOfQuarter.plusMonths(2)
                .with(TemporalAdjusters.lastDayOfMonth());

        log.info("firstDayOfQuarter: " + firstDayOfQuarter);
        log.info("lastDayOfQuarter: " + lastDayOfQuarter);

        Date[] preYearDates = addTimeToDatesForSearch(firstDayOfQuarter, lastDayOfQuarter);
        return preYearDates;
    }

    /**
     *
     * @return
     * @throws ParseException
     */
    public static Date[] getPreviousQuarter () throws ParseException {
        LocalDate localDate = LocalDate.now();
        LocalDate firstDayOfQuarter = localDate.with(localDate.minusMonths(1).getMonth().firstMonthOfQuarter())
                .with(TemporalAdjusters.firstDayOfMonth());

        LocalDate lastDayOfQuarter = firstDayOfQuarter.plusMonths(2)
                .with(TemporalAdjusters.lastDayOfMonth());

        log.info("firstDayOfQuarter: " + firstDayOfQuarter);
        log.info("lastDayOfQuarter: " + lastDayOfQuarter);

        Date[] preYearDates = addTimeToDatesForSearch(firstDayOfQuarter, lastDayOfQuarter);
        return preYearDates;
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    public static Date[] addTimeToDatesForSearch (LocalDate startDate, LocalDate endDate) throws ParseException {
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
     * @param startDate
     * @return
     * @throws ParseException
     */
    public static Date addTimeToSingleFromDateForSearch (LocalDate startDate) throws ParseException {
        log.info("LocalDate1------->  " + startDate.atTime(0, 0, 0));

        LocalDateTime sLocalDateTime = startDate.atTime(0, 0, 0);
        log.info("LocalDate1---##----> " + sLocalDateTime);

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
    public static Date addTimeToSingleToDateForSearch (LocalDate startDate) throws ParseException {
        log.info("LocalDate1------->  " + startDate.atTime(0, 0, 0));

        LocalDateTime sLocalDateTime = startDate.atTime(23, 59, 59);
        log.info("LocalDate1---##----> " + sLocalDateTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String sConvertedDateTime = formatter.format(sLocalDateTime);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date sDate = dateFormatter.parse(sConvertedDateTime);

        return sDate;
    }

    /**
     *
     * @param date1
     * @param date2
     * @return
     * @throws Exception
     */
    public static boolean dateCompare (String date1, String date2) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFormatted1 = sdf.parse(date1);
        Date dateFormatted2 = sdf.parse(date2);

        if (dateFormatted1.equals(dateFormatted2)) {
            System.out.println("Date1 is equal Date2");
            return true;
        }
        return false;
    }

    /**
     *
     * @param date
     * @return
     */
    public static String dateToString (Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateToStr = dateFormat.format(date);
            return dateToStr;
        }
        return null;
    }

    /*
     * Current - 0, 30	-> 0
     * 30, 60			-> 1
     * 60, 90			-> 2
     * 90, 120			-> 3
     * 120 & abv		-> 4
     */
    public static List<Date[]> buildAgingDates (Date fromDate1, Date toDate1) throws ParseException {
        LocalDate fromDate =  LocalDate.ofInstant(fromDate1.toInstant(), ZoneId.systemDefault());
        LocalDate toDate =  LocalDate.ofInstant(toDate1.toInstant(), ZoneId.systemDefault());
        int fromMonth = fromDate.getMonth().getValue();
        int toMonth = toDate.getMonth().getValue();

        log.info("month : " + fromMonth + "," + toMonth);
        List<Date[]> dates = new ArrayList<>();
//		for (int i = fromMonth; i<=toMonth; i++) {
        for (int i = toMonth; i<=fromMonth; i++) {
            YearMonth thisMonth = YearMonth.of(2022, i);
            LocalDate monthBegin = thisMonth.atDay(1);
            LocalDate monthEnd = thisMonth.atEndOfMonth();
            Date[] convDates = addTimeToDatesForSearch(monthBegin, monthEnd);
            log.info("Date: " + convDates[0] + "," + convDates[1]);
            dates.add(convDates);
        }
        Collections.reverse(dates);
        return dates;
    }

    /**
     *
     * @param sDate
     * @return
     * @throws ParseException
     */
    public static Date[] minusNumberOfDays (int numberOfDays) throws ParseException {
        LocalDate sLocalDate =  LocalDate.now();
        LocalDate toDate = sLocalDate.minusDays(numberOfDays);
        Date[] dates = addTimeToDatesForSearch (toDate, toDate);
        log.info("convertedDate---##----> " + dates[0] + "------" + dates[1]);
        return dates;
    }

    /**
     *
     * @return
     */
    public static Date getLastDayOfPreviuosMonth (Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        localDate = localDate.minusMonths(1);
        YearMonth yearMonth = YearMonth.from(localDate);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        Date convertedDate = Date.from(lastDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return convertedDate;
    }

    /**
     *
     * Current - 0, 30	-> 0
     * 30, 60			-> 1
     * 60, 90			-> 2
     * 90, 120			-> 3
     * 120 & abv		-> 4
     *
     * @param fromDate1
     * @return
     * @throws ParseException
     */
    public static int findBucket (Date fromDate1) throws ParseException {
        Date CURRENT_DATE = new Date();
        LocalDate inputDate =  LocalDate.ofInstant(fromDate1.toInstant(), ZoneId.systemDefault());
        LocalDate currentDate =  LocalDate.ofInstant(CURRENT_DATE.toInstant(), ZoneId.systemDefault());

        int fromMonth = inputDate.getMonth().getValue();
        int toMonth = currentDate.getMonth().getValue();
        log.info("====> : " + fromMonth + "," + toMonth);
        log.info("month : " + (toMonth - fromMonth));
        int bucket = (toMonth - fromMonth);
        if (bucket == 0) {
            bucket = 1;
        }
        return bucket;
    }

    /**
     *
     * @param args
     * @throws ParseException
     */
    public static void main(String[] args) {
        try {
//			LocalDate sLocalDate =  LocalDate.now();
//			LocalDate toDate = sLocalDate.minusDays(20);
//			Date convertedDate = Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//			log.info("convertedDate---##----> " + convertedDate);
//			findBucket (convertedDate);

            System.out.println(convertStringToDate ("2022-01-23"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
