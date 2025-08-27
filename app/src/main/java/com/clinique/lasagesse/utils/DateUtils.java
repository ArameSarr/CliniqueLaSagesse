package com.clinique.lasagesse.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd";

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());
    private static final SimpleDateFormat dbDateFormatter = new SimpleDateFormat(DATABASE_DATE_FORMAT, Locale.getDefault());

    public static String formatDate(Date date) {
        if (date == null) return "";
        return dateFormatter.format(date);
    }

    public static String formatTime(String time) {
        if (time == null || time.isEmpty()) return "";
        return time;
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return dateTimeFormatter.format(date);
    }

    public static String formatDateForDatabase(Date date) {
        if (date == null) return "";
        return dbDateFormatter.format(date);
    }

    public static Date parseDate(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.isEmpty()) return null;
        return dateFormatter.parse(dateStr);
    }

    public static Date parseDatabaseDate(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.isEmpty()) return null;
        return dbDateFormatter.parse(dateStr);
    }

    public static boolean isDateInFuture(Date date) {
        if (date == null) return false;
        return date.after(new Date());
    }

    public static boolean isToday(Date date) {
        if (date == null) return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        cal2.setTime(new Date());
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String getTodayAsString() {
        return dbDateFormatter.format(new Date());
    }

    public static int getAge(Date dateNaissance) {
        if (dateNaissance == null) return 0;
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateNaissance);

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
}