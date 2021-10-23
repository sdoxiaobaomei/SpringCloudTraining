package org.chai.resolver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeStampUtil {

    public static String getTimeStamp(String format) {
        SimpleDateFormat timeStamp = new SimpleDateFormat(format);
        return timeStamp.format(new Date());
    }

    public static String getYesterdayYearMonthDay(String format) {
        SimpleDateFormat timeStamp = new SimpleDateFormat(format);
        Calendar calender = Calendar.getInstance();
        calender.setTime(new Date());
        calender.add(Calendar.DAY_OF_MONTH, -1);
        return timeStamp.format(calender.getTime());
    }
}
