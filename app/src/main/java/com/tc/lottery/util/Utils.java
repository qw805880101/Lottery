package com.tc.lottery.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.psylife.wrmvplibrary.utils.LogUtil;
import com.psylife.wrmvplibrary.utils.helper.RxUtil;
import com.psylife.wrmvplibrary.utils.timeutils.DateUtil;
import com.tc.lottery.BuildConfig;
import com.tc.lottery.MyApplication;
import com.tc.lottery.bean.BaseBean;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by tianchao on 2018/4/3.
 */

public class Utils {

    public static void log(String tag, String str) {
        Log.d(tag, str);
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

    /**
     * 获取错误类型提示
     *
     * @param throwable
     * @return
     */
    public static String getErrorMessage(Throwable throwable) {

        String errorMessage = "网络错误";

        if (throwable instanceof SocketException) { //该异常在客户端和服务器均可能发生。异常的原因是己方主动关闭了连接后（调用了Socket的close方法）再对网络连接进行读写操作。
            errorMessage = "网络已断开，请重试";
        } else if (throwable instanceof SocketTimeoutException) { //网络连接超时
            errorMessage = "网络连接超时,请稍后再试";
        }

        return errorMessage;
    }

    /**
     * 获取公共参数map
     *
     * @param application 应用名称
     * @return
     */
    public static Map getRequestData(String application) {
        Map requestData = new HashMap();
        requestData.put("version", BuildConfig.VERSION_NAME); //接口版本号
        requestData.put("application", application); //应用名称
        requestData.put("sendTime", DateUtil.format(new Date(), "yyyymmddhhmmss")); //发送时间
        requestData.put("terminalCode", "0002"); //终端类型
//        requestData.put("terminalId", GetUUID.getUUID()); //终端编号
        requestData.put("terminalId", "037100000100002"); //终端编号
        return requestData;
    }

    /**
     * 获取请求json
     *
     * @param requestData
     * @return
     */
    public static String getSendMsg(Map requestData) {
        try {
            Map map = new HashMap();
            String sign = SignUtil.sign(JSON.toJSONString(requestData, SerializerFeature.DisableCircularReferenceDetect), MyApplication.
                    PRIVATE_KEY);

            LogUtil.d(sign);
            map.put("sign", sign.substring(0, sign.length() - 1));
            map.put("requestData", requestData);
            return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
