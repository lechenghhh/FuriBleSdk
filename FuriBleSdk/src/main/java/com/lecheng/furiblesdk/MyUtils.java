package com.lecheng.furiblesdk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cheng on 2017/8/26.
 */

public class MyUtils {
    //将时间戳转换为时间
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    // 将时间转换为时间戳
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    public static String getWeightUnit(String data) {
        if (data.indexOf("kg") != -1) {
            return "kg";
        } else if (data.indexOf("g") != -1) {
            return "g";
        } else if (data.indexOf("lb") != -1) {
            return "磅";
        } else if (data.indexOf("LB") != -1) {
            return "磅";
        } else if (data.indexOf("jl") != -1) {
            return "斤";
        } else if (data.indexOf("JL") != -1) {
            return "斤";
        }
        return "?";
    }

}
