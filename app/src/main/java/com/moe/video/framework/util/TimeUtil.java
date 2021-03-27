package com.moe.video.framework.util;

public class TimeUtil {
    
    public static String getTime(int time)
    {
        time = Math.abs(time);
        if (time == 0)return "00:00";
        time /= 1000;
        if (time < 60)
            return "00:".concat(getFormat(time));
        int second=time % 60;
        time /= 60;
        if (time < 60)
        {
            return getFormat(time).concat(":").concat(getFormat(second));
        }
        int minute=time % 60;
        return getFormat(time / 60).concat(":").concat(getFormat(minute)).concat(":").concat(getFormat(second));

    }
    private static String getFormat(int time)
    {
        String time_=String.valueOf(time);
        if (time_.length() == 1)
            return "0" + time_;
        return time_;
	}
    
}
