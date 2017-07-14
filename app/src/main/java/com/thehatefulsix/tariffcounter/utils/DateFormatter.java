package com.thehatefulsix.tariffcounter.utils;


import android.content.Context;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("deprecation")
public final class DateFormatter {

    public static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";
    public static final String DATE_WITH_TIME_PATTERN = "dd.MM.yyyy hh:mm";

    public static String parseData(@NonNull Date date, @NonNull Context context){
        final Locale current = context.getResources().getConfiguration().locale;

        return new SimpleDateFormat(DATE_FORMAT_PATTERN, current).format(date);
    }

    public static String parseData(@NonNull Date date, @NonNull Context context,
                                   @NonNull String pattern){
        final Locale current = context.getResources().getConfiguration().locale;

        return new SimpleDateFormat(pattern, current).format(date);
    }

    public static Date parseString(@NonNull String date, @NonNull Context context){
        final Locale current = context.getResources().getConfiguration().locale;

        try {
            return new SimpleDateFormat(DATE_FORMAT_PATTERN, current).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static DateFormat getDateFormater(@NonNull Context context){
        final Locale current = context.getResources().getConfiguration().locale;

        return new SimpleDateFormat(DATE_FORMAT_PATTERN, current);
    }

    public static DateFormat getDateFormater(@NonNull Context context, @NonNull String pattern){
        final Locale current = context.getResources().getConfiguration().locale;

        return new SimpleDateFormat(pattern, current);
    }
}
