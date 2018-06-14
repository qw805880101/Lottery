package com.tc.lottery.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.psylife.wrmvplibrary.utils.ToastUtils;
import com.tc.lottery.MyApplication;
import com.tc.lottery.R;
import com.tc.lottery.util.QRCodeUtil;

import java.util.HashMap;
import java.util.Map;

import Motor.MotorSlaveS32;

import static com.tc.lottery.activity.PaySuccessActivity.OUT_TICKET;
import static com.tc.lottery.activity.PaySuccessActivity.QUERY_FAULT;
import static com.tc.lottery.activity.PaySuccessActivity.QUERY_STATUS;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "test";

    private Button btZfb, btWx, btSuccess;
    private EditText etNum;

    private int price = 5; //票面单价
    private int totalAmt;// 票总价
    private int num; //票张数

    private boolean mBusy = false; //标记位 判断设备是否被占用运行
    protected int mIDCur = 1; //暂不明用处
    protected int mTicketLen = 102;//暂不明用处
    protected MotorSlaveS32 mMotorSlave = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btZfb = findViewById(R.id.bt_zfb);
        btZfb.setOnClickListener(this);

        btWx = findViewById(R.id.bt_wx);
        btWx.setOnClickListener(this);

        btSuccess = findViewById(R.id.bt_success);
        btSuccess.setOnClickListener(this);

        etNum = findViewById(R.id.et_num);

        mMotorSlave = MotorSlaveS32.getInstance();
    }

    @Override
    public void onClick(View v) {
        num = Integer.parseInt(etNum.getText().toString().trim());
        totalAmt = num * price;
        if (v == btZfb) {
            Toast.makeText(this, "支付" + totalAmt + "元", Toast.LENGTH_SHORT).show();
            ImageView mImageView = findViewById(R.id.iv);
            Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap("https://www.baidu.com", 480, 480);
            mImageView.setImageBitmap(mBitmap);
        }
        if (v == btWx) {
            queryStatus(mIDCur);
            Toast.makeText(this, "支付" + totalAmt + "元", Toast.LENGTH_SHORT).show();
        }
        if (v == btSuccess) {
            onTransOne(mIDCur);
        }
    }

    /**
     * 出票方法
     *
     * @param nID
     */
    private void onTransOne(int nID) {
        if (mBusy)
            return;
        mIDCur = nID;
        new Thread(transmitoneS).start();
    }

    /**
     * 出票线程
     */
    Runnable transmitoneS = new Runnable() {
        @Override
        public void run() {
            //runonce();

            mBusy = true;
            try {
                StringBuilder s1 = new StringBuilder();
                StringBuilder s2 = new StringBuilder();
                mMotorSlave.TransOneSimpleS(mIDCur, mTicketLen, s1, s2, num);
                Log.d(TAG, "发送  " + s1.toString());
                Log.d(TAG, "接收 " + s2.toString());

                Bundle bundle = new Bundle();
                bundle.putStringArray("result", s2.toString().split(" "));
                sendMsg(bundle, OUT_TICKET);
//                SendMsg(1, "ssend", s1.toString());
//                SendMsg(1, "ssend", s2.toString());
            } catch (Exception exp) {

            }
            mBusy = false;

        }
    };


    /**
     * 查询状态
     *
     * @param nID
     */
    private void queryStatus(int nID) {
        if (mBusy)
            return;
        mIDCur = nID;
        new Thread(ReadStatusRunnable).start();
    }

    /**
     * 查询设备故障
     *
     * @param nID
     */
    private void queryFault(int nID) {
        if (mBusy)
            return;
        mIDCur = nID;
        new Thread(QueryFaultRunnable).start();
    }

    Handler mOutTicketHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            /**
             * 出票命令返回
             */
            if (OUT_TICKET.equals(bundle.getString("type"))) {
                String[] results = bundle.getStringArray("result");
                if (results[7].equals("01")) { //出票成功
                    queryStatus(mIDCur);
                }
                if (results[7].equals("00")) { //出票失败
                    ToastUtils.showToast(TestActivity.this, "出票失败，请联系工作人员");
                }
            }

            /**
             * 查询状态命令返回
             */
            if (QUERY_STATUS.equals(bundle.getString("type"))) {
                if (bundle.getBoolean("2")) {
                    /* 掉票处无票， 执行出票命令 */
                    onTransOne(mIDCur);
                } else {
                    /* 掉票处有票，执行设备状态检查命令 */
                    queryStatus(mIDCur);
                }
            }

            /**
             * 查询故障命令返回
             */
            if (QUERY_FAULT.equals(bundle.getString("type"))) {
                String status = bundle.getString("result");
                if ("00".equals(status)) {
                    //设备正常
                    MyApplication.status = "00";
                } else if ("01".equals(status)) {
                    //设备卡票
                    MyApplication.status = "01";
                } else if ("02".equals(status)) {
                    //票未取走
                    MyApplication.status = "02";
                } else if ("03".equals(status)) {
                    //传感器故障
                    MyApplication.status = "03";
                } else if ("04".equals(status)) {
                    //电机故障
                    MyApplication.status = "04";
                }

            }
        }
    };


    /**
     * 查询机头状态
     */
    Runnable ReadStatusRunnable = new Runnable() {
        @Override
        public void run() {
            mBusy = true;
            try {
                StringBuilder s1 = new StringBuilder();
                StringBuilder s2 = new StringBuilder();
                HashMap<Integer, Boolean> status = mMotorSlave.ReadStatus(mIDCur, s1, s2);
                Log.d(TAG, "发送 ----" + s1.toString());
                Log.d(TAG, "接收 " + s2.toString());

                Bundle bundle = new Bundle();
                for (Map.Entry<Integer, Boolean> e : status.entrySet()) {
                    bundle.putBoolean("" + e.getKey(), e.getValue());
                }
                sendMsg(bundle, QUERY_STATUS);
            } catch (Exception exp) {

            }
            mBusy = false;
        }
    };

    /**
     * 查询设备故障
     */
    Runnable QueryFaultRunnable = new Runnable() {
        @Override
        public void run() {
            mBusy = true;
            try {
                StringBuilder s1 = new StringBuilder();
                StringBuilder s2 = new StringBuilder();
                String status = mMotorSlave.queryFault(mIDCur, s1, s2);
                Log.d(TAG, "发送 ----" + s1.toString());
                Log.d(TAG, "接收 " + s2.toString());

                Bundle bundle = new Bundle();
                bundle.putString("result", status);
                sendMsg(bundle, QUERY_FAULT);
            } catch (Exception exp) {

            }
            mBusy = false;
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
        mOutTicketHandler.sendMessage(message);
    }
}
