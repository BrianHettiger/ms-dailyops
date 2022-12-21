package com.moblize.ms.dailyops.utils;


import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    /**
     * Find difference in two times in days
     * @param fromTime
     * @param toTime
     * @return
     */
    public static long daysBetween(long fromTime, long toTime) {
        long diffInMillies = Math.abs(toTime - fromTime);
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diff;
    }

    /**
     * Add days to a date
     * @param epochTime
     * @param days
     * @return
     */
    public static Date addDays(long epochTime, int days) {
        Date date = new Date(epochTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

}
