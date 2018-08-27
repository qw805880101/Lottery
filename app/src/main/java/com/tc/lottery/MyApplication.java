package com.tc.lottery;

import android.os.Build;
import android.os.StrictMode;

import com.psylife.wrmvplibrary.BaseApplication;
import com.tc.lottery.bean.TerminalLotteryInfo;
import com.tc.lottery.util.GetUUID;

import java.util.List;

/**
 * Created by admin on 2017/8/30.
 */

public class MyApplication extends BaseApplication {

    //    public static String URL = "https://www.new-orator.com/gateway/";
//    public static String URL = "https://dev.new-orator.com/gateway/front/";
//    public static String URL = "http://172.21.10.69:8998/cp-pay/mpi/";
    public static String URL = "https://www.new-orator.com/gateway/front/";

//    public static String UUID = GetUUID.getUUID();
    public static String UUID = "882c1ea7681700000000";

    /**
     * 00 设备正常
     * 01 设备卡票
     * 02 票未取走
     * 03 传感器故障
     * 04 电机故障
     */
    public static String status; //设备状态

//    public static List<TerminalLotteryInfo> mTerminalLotteryInfos; //初始化返回的票种列表

    public static TerminalLotteryInfo mTerminalLotteryInfo;

    /**
     * 设备状态
     * 0 待激活
     * 1 已激活
     * 2 待维修
     * 3 已暂停
     * 4 设备无票
     */
    public static String terminalLotteryStatus; //设备状态

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
