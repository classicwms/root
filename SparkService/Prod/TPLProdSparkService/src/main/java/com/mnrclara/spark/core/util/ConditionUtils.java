package com.mnrclara.spark.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ConditionUtils {

    // Input Filter for String
    public static void addCondition(List<String> conditions, String fieldName, List<String> values) {
        if (values != null && !values.isEmpty()) {
            if (values.size() == 1) {
                conditions.add(fieldName + " = '" + values.get(0) + "'");
            } else {
                String joinedValues = values.stream()
                        .map(val -> "'" + val + "'")
                        .collect(Collectors.joining(", "));
                conditions.add(fieldName + " IN (" + joinedValues + ")");
            }
        }
    }

    // Input Filter for Long Values
    public static void numericConditions(List<String> conditions, String fieldName, List<Long> values) {
        if (values != null && !values.isEmpty()) {
            if (values.size() == 1) {
                conditions.add(fieldName + " = " + values.get(0));
            } else {
                String joinedValues = values.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));
                conditions.add(fieldName + " IN (" + joinedValues + ")");
            }
        }
    }

    // Input Filter for Double Values
    public static void doubleConditions(List<String> conditions, String fieldName, List<Double> values) {
        if (values != null && !values.isEmpty()) {
            if (values.size() == 1) {
                conditions.add(fieldName + " = " + values.get(0));
            } else {
                String joinedValues = values.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));
                conditions.add(fieldName + " IN (" + joinedValues + ")");
            }
        }
    }

    /**
     * Date Filter
     *
     * @param conditions
     * @param fieldName
     * @param startDate
     * @param endDate
     */
    public static void addDateCondition(List<String> conditions, String fieldName, Date startDate, Date endDate) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(startDate != null && endDate != null) {
            //Start Date
            Calendar calStart = Calendar.getInstance();
            calStart.setTime(startDate);
            calStart.set(Calendar.HOUR_OF_DAY, 0);
            calStart.set(Calendar.MINUTE, 0);
            calStart.set(Calendar.SECOND, 0);
            calStart.set(Calendar.MILLISECOND, 0);
            Date startOfDay = calStart.getTime();

            //End Date
            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(endDate);
            calEnd.set(Calendar.HOUR_OF_DAY, 23);
            calEnd.set(Calendar.MINUTE, 59);
            calEnd.set(Calendar.SECOND, 59);
            calEnd.set(Calendar.MILLISECOND, 999);
            Date endOfDay = calEnd.getTime();

            if (startOfDay.equals(endOfDay)) {
                conditions.add(fieldName + " BETWEEN '" + dateTimeFormat.format(startOfDay) + "' AND '" + dateTimeFormat.format(endOfDay) + "'");
            } else {
                conditions.add(fieldName + " BETWEEN '" + dateTimeFormat.format(startOfDay) + "' AND '" + dateTimeFormat.format(endOfDay) + "'");
            }
        }

    }
}
