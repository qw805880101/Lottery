package com.tc.lottery;

import android.os.Build;
import android.os.StrictMode;

import com.psylife.wrmvplibrary.BaseApplication;
import com.psylife.wrmvplibrary.WRCoreApp;
import com.psylife.wrmvplibrary.utils.LogUtil;
import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by admin on 2017/8/30.
 */

public class MyApplication extends BaseApplication {

    public static final String USER_INFO = "userInfo";
    public static final String SEARCH_HISTORY = "searchHistory";

    public static String SESSION_ID = "";

    public static String MER_ID = "";

    public static int GOODS_NUM = 0;

    public static String URL = "https://new-orator.com/gateway/";

    public static String USER_ID = "26";

    public static String LEVEL = "0";

    public static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMTaCdoTAnMZQidqRZYH3xpEl46hPeYKSxfw43/g1e7D7QEZ40ZCAPRGszY7LRgWkbfOoxYukBKKvexJBO3x5r0HySXdawh7o38a1QSYBL6Rg3bgtrWqHM9+pRj7LxfU2ChqRN+JwSVzuTFPdXCpwf95u9EClm6LOLI4bDwazHNzAgMBAAECgYEAq/H8WwTxxdHRTBZys+sqQIqbi5ViOPbSwxXB0ih1FbsD4UtYjz0GEllTHtKvv/Ou0svm/nArnlacMLFTYfhDX10tzwA4nMtAewvI/jus+fgSCj8JZjdUI+vTkULU5WFcb0DLAuRyxsFGUG+vKhxUR18zQzofRngxTt5Gy4RFGIECQQD99Gpmn0GkbNzOEWfzkat7JxhnVkri8EtJ5P6fvQlIn3WeTpotMi/+RB45rFj1MNjL1WY27RGGTKto8Dgj9/WzAkEAxm/kZ7ayE+gAWXSa2JsHcAP7nr3oYUg4KgmqxRm3QxCTypHszORp2fu1xtWDJKEXNV9HuW+XYZwaRkadviYrQQJBAO4UNav/oYqEhHyr1MiDyD+sZzR5sbsPi4W7KPqYPhvXYm0HQ4MbieLV+YAYE03KfXSamzjjB4rgVdILYpZV4AECQDSYD3eVqpkwEnejOi9S16POynAGcYLnO0uZCFP5PuNdj25PQu4DVDLcTg+HI50fvSD+QepaM0tBro0VxlVRlIECQHKWNKJuo9OwuFqGuPVxXqGT45oUVKbcyRBHC+0Vy2HRRy7xXZNAbH8o9ELjNwJB/TxBgQAvZt3F6eqVN+z06ko=";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

//        //设置调试模式
//        JPushInterface.setDebugMode(true);
//
//        //初始化极光推送
//        JPushInterface.init(this);

//        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
//
//            @Override
//            public void onViewInitFinished(boolean arg0) {
//                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
//                LogUtil.d("app", " onViewInitFinished is " + arg0);
//            }
//
//            @Override
//            public void onCoreInitFinished() {
//            }
//        };
//        //x5内核初始化接口
//        QbSdk.initX5Environment(getApplicationContext(), cb);

    }
}
