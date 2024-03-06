package vn.iotstart.userservice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String date2String(Date input, String format) throws IllegalArgumentException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(input);
    }

    public static Date string2Date(String input, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(input);
    }

}
