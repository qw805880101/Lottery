package com.tc.lottery.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.psylife.wrmvplibrary.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

import Motor.MotorSlaveS32;

public class MotorSlaveUtils {

    public final static String QUERY_STATUS = "queryStatus"; //查询出票机头状态
    public final static String OUT_TICKET = "outTicket"; //出票状态
    public final static String QUERY_FAULT = "queryFault"; //查询设备故障

    public boolean mBusy = false; //标记位 判断设备是否被占用运行
    public int mIDCur = 1; //暂不明用处
    public int mTicketLen = 102;//暂不明用处
    public MotorSlaveS32 mMotorSlave = null; //调用设备出票
    private Handler mHandler;

    public MotorSlaveUtils(Handler mHandler) {
        if (mMotorSlave == null)
            mMotorSlave = MotorSlaveS32.getInstance();
        this.mHandler = mHandler;
    }


    /**
     * 出票线程
     */
    public Runnable transmitoneS = new Runnable() {
        @Override
        public void run() {
            //runonce();
            mBusy = true;
            try {
                StringBuilder s1 = new StringBuilder();
                StringBuilder s2 = new StringBuilder();
                mMotorSlave.TransOneSimpleS(mIDCur, mTicketLen, s1, s2, 1);
                LogUtil.d("发送 ----" + s1.toString());
                LogUtil.d("接收 " + s2.toString());

                Bundle bundle = new Bundle();
                bundle.putStringArray("result", s2.toString().split(" "));
                sendMsg(bundle, OUT_TICKET);
            } catch (Exception exp) {

            }
        }
    };

    /**
     * 查询机头状态
     */
    public Runnable ReadStatusRunnable = new Runnable() {
        @Override
        public void run() {
            mBusy = true;
            try {
                StringBuilder s1 = new StringBuilder();
                StringBuilder s2 = new StringBuilder();
                HashMap<Integer, Boolean> status = mMotorSlave.ReadStatus(mIDCur, s1, s2);
                LogUtil.d("发送 ----" + s1.toString());
                LogUtil.d("接收 " + s2.toString());

                Bundle bundle = new Bundle();
                for (Map.Entry<Integer, Boolean> e : status.entrySet()) {
                    LogUtil.d("" + e.getKey() + ":" + e.getValue());
                    bundle.putBoolean("" + e.getKey(), e.getValue());
                }
                sendMsg(bundle, QUERY_STATUS);
            } catch (Exception exp) {

            }
        }
    };

    /**
     * 查询设备故障
     */
    public Runnable QueryFaultRunnable = new Runnable() {
        @Override
        public void run() {
            mBusy = true;
            try {
                StringBuilder s1 = new StringBuilder();
                StringBuilder s2 = new StringBuilder();
                String status = mMotorSlave.queryFault(mIDCur, s1, s2);
                LogUtil.d("发送 ----" + s1.toString());
                LogUtil.d("接收 " + s2.toString());

                Bundle bundle = new Bundle();
                bundle.putString("result", status);
                sendMsg(bundle, QUERY_FAULT);
            } catch (Exception exp) {

            }
        }
    };

    /**
     * 回调参数
     *
     * @param bundle
     * @param type
     */
    private void sendMsg(Bundle bundle, String type) {
        Message message = new Message();
        bundle.putString("type", type);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    /**
     * 关闭传输命令
     */
    public void close() {
        mBusy = true;
    }

    /**
     * 打开传输命令
     */
    public void open() {
        mBusy = false;
    }

    public void setmIDCur(int idCur) {
        this.mIDCur = idCur;
    }

}
