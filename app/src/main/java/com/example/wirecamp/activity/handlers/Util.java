package com.example.wirecamp.activity.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Rahul Tiwari on 21-06-2015.
 */
public class Util {

    public static String encodeBase64(String data) {
        byte[] bytes = null;
        try {
            bytes = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] encodedBytes = Base64.encode(bytes, Base64.DEFAULT);
        return new String(encodedBytes);
    }

    /*public static String getSessionId(Activity activity) {
        if (Util.isEmpty(GlobalData.getInstance().getSessionId())) {
            String session_id = Util.getLocalStringVariable(activity, "session_id");
            if (!Util.isEmpty(session_id)) {
                GlobalData.getInstance().setSessionId(session_id);
            }
        }
        return GlobalData.getInstance().getSessionId();
    }*/

    /*public static String getLocalStringVariable(Activity activity, String key) {
        if (Util.isEmpty(key))
            return null;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String value = sharedPref.getString(key, null);
        if (Util.isEmpty(value)) {
            DatabaseHandler handler = DatabaseHandler.getInstance(activity);
            List<BaseResource> list = handler.getAll(new User());
            if (Util.isEmpty(list))
                return null;
            User _user = (User) list.get(0);
            if (key.equals("username")) {
                value =  _user.getName();
            }
            if (key.equals("email_id")) {
                value =  _user.getEmail_id();
            }
            if (key.equals("user_id")) {
                value = _user.getKey();
            }
            if (key.equals("session_id")) {
                value = _user.getSessionId();
            }
            if (key.equals("social_login")) {
                value = _user.getSocial_login();
            }
        }
        if (Util.isEmpty(value))
            setLocalStringVariable(activity,key,value);
        return value;
    }*/

    public static void removeLocalStringVariable(Activity activity, String key) {
        if (Util.isEmpty(key))
            return;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.commit();
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if ((networkInfo != null) && (networkInfo.isConnected())) {
            return true;
        }
        return false;
    }


    public static String getTimeFormated(long timeinMs) {

        Date time = new Date(timeinMs);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);

        long hr = cal.get(Calendar.HOUR_OF_DAY);
        long mm = cal.get(Calendar.MINUTE);
        long ss = cal.get(Calendar.SECOND);

        long day = cal.get(Calendar.DATE);
        long month = cal.get(Calendar.MONTH);
        long year = cal.get(Calendar.YEAR);
        long calculation = year * 365 + month * 12 + day;

        Date time_current = new Date();
        cal = Calendar.getInstance();
        cal.setTime(time_current);

        long day_current = cal.get(Calendar.DATE);
        long month_current = cal.get(Calendar.MONTH);
        long year_current = cal.get(Calendar.YEAR);
        long calculation_current = year_current * 365 + month_current * 12 + day_current;
        String time_string = "";
        if ((day_current == day) && (month_current == month_current) && (year_current == year)) {
            time_string = String.format("Today %02d:%02d", hr, mm);
        } else if (calculation_current == (calculation - 1)) {
            time_string = String.format("Yesterday %02d:%02d", hr, mm);
        } else {
            time_string = String.format("%02d-%02d-%04d %02d:%02d", day, month, year, hr, mm);
        }
        return time_string;
    }

    public static  String getDateFormatted(long dateMs) {
        Date date = new Date(dateMs * 1000);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("IST"));
        String formatted = format.format(date);
        return formatted;
    }

    public static String ago(long time) {
        Date endTime = new Date();
        long elapsedTime = endTime.getTime() - time;
        long second = elapsedTime / 1000L;
        long min = 0;
        long hrs = 0;
        long day = 0;
        String str = "";
        if (second > 60) {
            min = second / 60L;
            second = second % 60;
        }
        if (min > 60) {
            hrs = min / 60L;
            min = min % 60;
        }
        if (hrs > 24) {
            day = hrs / 24L;
            hrs = hrs % 24;
        }

        if (day > 0) {
            str = str + day + " days ";
        }

        if (hrs > 0) {
            str = str + hrs + " hrs ";
        }
        if (min > 0) {
            str = str + min + " mins";
        }
        if ((str.length() == 0) && (second > 0)) {
            str = str + second + " secs ";
        }
        if (str.length() > 0) {
            str = str + " ago";
        }
        return str;
    }

    public static boolean isEmpty(List list) {
        if (list == null)
            return true;
        if (list.size() == 0)
            return true;
        return false;
    }

    public static boolean isEmpty(String str) {
        if (str == null)
            return true;
        if (str.length() == 0)
            return true;
        if (str.equals(""))
            return true;
        if (str.trim().equals(""))
            return true;
        if (str.trim().equals("null")) {
            return true;
        }
        return false;
    }

}
