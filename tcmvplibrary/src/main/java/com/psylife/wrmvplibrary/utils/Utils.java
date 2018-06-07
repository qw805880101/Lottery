package com.psylife.wrmvplibrary.utils;

import android.app.Activity;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.psylife.wrmvplibrary.BaseApplication;
import com.psylife.wrmvplibrary.utils.HashUtil.MD5;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by tc on 2017/6/13.
 */

public class Utils {

    public static List<Activity> activities = new ArrayList<>();

    public static void finishActivity() {
        for (Activity a : activities) {
            a.finish();
        }
    }

    public static void systemOut(String title, String outputStr) {
        System.out.println(title + ":" + outputStr);
    }

    public static void clearActivity() {
        activities.clear();
    }

    /**
     * 格式化日期
     */
    public static String getSendTime() {
        Date d = new Date();
        // String str = d.toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String str1 = sdf.format(d);
        return str1;
    }

    public static String getCardNum(String str) {
        int num = str.length() / 4 + 1;
        String card = "";
        for (int i = 0; i < num; i++) {
            if (i != (num - 1)) {
                card += str.substring(i * 4, (i + 1) * 4) + " ";
            } else {
                card += str.substring(i * 4, str.length());
            }
        }
        return card;
    }

    public static void SystemOut(String msg) {
        // System.out.println("========>" + msg);
    }

    public static String getMyprivateKey() {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 24; i++) {
            // 获得随机数
            sb.append(Math.abs(random.nextInt()) % 10);
        }
        return sb.toString();
    }

    //图片转化成base64字符串
    public static String getImageStr(Bitmap bitmap) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        return Base64.encode(baos.toByteArray());
    }

    public static boolean isEmpty(String str) {
        boolean fal = false;
        if (str == null || str.equals("")) {
            fal = true;
        } else {
            fal = false;
        }
        return fal;
    }

}
