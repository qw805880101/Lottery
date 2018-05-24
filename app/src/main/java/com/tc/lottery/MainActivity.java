package com.tc.lottery;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import Motor.MotorSlaveS32;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        setContentView(R.layout.activity_main);

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
        }
        if (v == btWx) {

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

//                SendMsg(1, "ssend", s1.toString());
//                SendMsg(1, "ssend", s2.toString());
            } catch (Exception exp) {

            }
            mBusy = false;

        }
    };
}
