package org.chai.resolver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeStampUtil {

    public static String getTimeStamp() {
        SimpleDateFormat timeStamp = new SimpleDateFormat("yyyyMMdd-HHmm");
        return timeStamp.format(new Date());
    }

    public static String getYesterdayYearMonthDay() {
        SimpleDateFormat timeStamp = new SimpleDateFormat("yyyyMMdd");
        Calendar calender = Calendar.getInstance();
        calender.setTime(new Date());
        calender.add(Calendar.DAY_OF_MONTH, -1);
        return timeStamp.format(calender.getTime());
    }
}
