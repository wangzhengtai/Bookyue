package com.example.bookyue.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    //此方法计算当前date与当前时间的差值，然后返回合适的描述时间字符串
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String str){
        String fromDate = str.replace("T"," ").substring(0,str.length()-1);
        long from = 0;
        try {
            from = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(fromDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long to = System.currentTimeMillis();
        int minutes = (int) ((to - from)/(1000 * 60));
        int hours = minutes/60;
        int days = hours/24;
        int months = days/30;
        int years = days/365;

        if (years > 2){
            String date = fromDate.substring(0,10);
            String[] strings = date.split("-");
            return strings[0]+"年"+strings[1]+"月"+strings[2]+"日";
        }
        if (years > 0)
            return years+"年前";
        if (months > 6)
            return "半年前";
        if (months > 0)
            return months+"月前";
        if (days > 15)
            return "半个月前";
        if (days > 2)
            return days+"天前";
        if (days > 1)
            return "前天";
        if (days > 0)
            return "昨天";
        if (hours > 0)
            return hours+"小时前";
        if (minutes > 30)
            return "半小时前";
        if (minutes > 0)
            return minutes+"分钟前";
        return "---";
    }

    //获取当前的系统时间，并以HH:mm的形式返回
    @SuppressLint("SimpleDateFormat")
    public static String getSystemTime(){
        return new SimpleDateFormat("HH:mm").format(new Date());
    }
}
