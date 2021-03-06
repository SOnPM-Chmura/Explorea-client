package com.sonpm_cloud.explorea.data_classes;

import android.content.Context;
import android.util.TypedValue;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Class for utilities and conversions
 */
public class U {

    /**
     * Translates <i>density-independent pixels</i> to screen <i>pixels</i>
     * @param dp Value in dp
     * @param context context used to retrieve screen dpi
     * @return Value in px that corresponds to dp value on this screen
     */
    public static float dp_px(float dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Translates <i>density-independent pixels</i> to screen <i>pixels</i>
     * @param dp Value in dp
     * @param context context used to retrieve screen dpi
     * @return Value in px that corresponds to dp value on this screen
     */
    public static float dp_px(int dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Translates screen <i>pixels</i><i> to density-independent pixels</i>
     * @param px Value in px
     * @param context context used to retrieve screen dpi
     * @return Value in dp that corresponds to px value on this screen
     */
    public static float px_dp(float px, Context context) {
        return px / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Translates screen <i>pixels</i><i> to density-independent pixels</i>
     * @param px Value in px
     * @param context context used to retrieve screen dpi
     * @return Value in dp that corresponds to px value on this screen
     */
    public static float px_dp(int px, Context context) {
        return px / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Gets current millisecond count from Epoch in UTC timezone
     * @return Time from Epoch in ms (UTC)
     */
    public static long getCurrentMillis() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
    }

    /**
     * One hour in milliseconds
     */
    public static final long HOUR = 60 * 60 * 1000;

    /**
     * Checkif hour has passed
     * @param from time which you test
     * @return {@code true} if hour has passed from {@code from}
     */
    public static boolean hasHourPassed(long from) {
        return  getCurrentMillis() > (from + HOUR);
    }
}
