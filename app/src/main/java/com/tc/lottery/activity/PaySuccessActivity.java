package com.tc.lottery.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.psylife.wrmvplibrary.utils.LogUtil;
import com.psylife.wrmvplibrary.utils.ToastUtils;
import com.psylife.wrmvplibrary.utils.helper.RxUtil;
import com.tc.lottery.MyApplication;
import com.tc.lottery.R;
import com.tc.lottery.base.BaseActivity;
import com.tc.lottery.bean.BaseBean;
import com.tc.lottery.bean.InitInfo;
import com.tc.lottery.bean.OrderInfo;
import com.tc.lottery.bean.TerminalLotteryInfo;
import com.tc.lottery.bean.UpdateOutTicketStatusInfo;
import com.tc.lottery.util.Utils;
import com.tc.lottery.view.SuccessView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Motor.HexUtil;
import Motor.MotorSlaveS32;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Action1;

public class PaySuccessActivity extends BaseActivity {

    public final static String QUERY_STATUS = "queryStatus"; //查询出票机头状态
    public final static String OUT_TICKET = "outTicket"; //出票状态
    public final static String QUERY_FAULT = "queryFault"; //查询设备故障

    @BindView(R.id.txt_all_num)
    TextView mTxtAllNum;
    @BindView(R.id.lin_all_lottery)
    LinearLayout mLinAllLottery;
    @BindView(R.id.txt_out_ticket_num)
    TextView mTxtOutTicketNum;
    @BindView(R.id.bt_how)
    Button mBtHow;
    @BindView(R.id.bt_back)
    Button mBtBack;
    @BindView(R.id.lin_bt)
    LinearLayout mLinBt;
    @BindView(R.id.image_ticket)
    ImageView imageTicket;
    @BindView(R.id.success_view)
    SuccessView successView;
    @BindView(R.id.lin_tips)
    LinearLayout linTips;

    private boolean mBusy = false; //标记位 判断设备是否被占用运行
    protected int mIDCur = 1; //暂不明用处
    protected int mTicketLen = 102;//暂不明用处
    protected MotorSlaveS32 mMotorSlave = null; //调用设备出票

    private int lotteryNum = 2; //彩票数量
    private int outTicketNum = 1; //已出数量

    private TranslateAnimation anim; //彩票动画

    private UpdateOutTicketStatusInfo mUpdateOutTicketStatusInfo; //出票状态参数

    Runnable mAnimRunnable;

    Runnable mTicketNumRunnable;

    Runnable mBackRunnable;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                imageTicket.startAnimation(anim);
            }
            if (msg.what == 1) {
                if (outTicketNum <= lotteryNum) {
                    mTxtOutTicketNum.setText("支付成功！正在出票...（" + outTicketNum + "/" + lotteryNum + "）");
                    queryStatus(mIDCur);
                } else {
                    //出票完成
                    outTicketSuccess();
                }
            }
            if (msg.what == 2) {
                if (backNum > 0) {
                    mBtBack.setText("返回主界面(" + backNum + ")");
                } else {
                    mHandler.removeCallbacks(mBackRunnable);
                    mBtBack.setEnabled(true);
                    mBtBack.setText("返回主界面");
                }
            }

        }
    };

    Handler mBackHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (backNum > 0) {
                    mBtBack.setText("返回主界面(" + backNum + ")");
                } else {
                    mHandler.removeCallbacks(mBackRunnable);
                    mBtBack.setEnabled(true);
                    mBtBack.setText("返回主界面");
                    startActivity(new Intent(PaySuccessActivity.this, MainActivity.class));
                    finish();
                }
            }

        }
    };

    @Override
    public View getTitleView() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pay_success;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mMotorSlave = MotorSlaveS32.getInstance();
    }

    @Override
    public void initdata() {
        Intent intent = this.getIntent();
//        lotteryNum = intent.getIntExtra("lotteryNum", 1);
//        mUpdateOutTicketStatusInfo = (UpdateOutTicketStatusInfo) intent.getSerializableExtra("outTicket");
//
        mTxtOutTicketNum.setText("支付成功！正在出票...（" + outTicketNum + "/" + lotteryNum + "）");

        startAnim();
//        startTicketNum();
        onTransOne(mIDCur);
    }

    @OnClick({R.id.bt_how, R.id.bt_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_how:
                startActivity(new Intent(this, HowActivity.class));
                finish();
                break;
            case R.id.bt_back:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }

    /**
     * 出票完成
     */
    private void outTicketSuccess() {
        mHandler.removeCallbacks(mTicketNumRunnable);
        mHandler.removeCallbacks(mAnimRunnable);
        successView.setVisibility(View.VISIBLE);
        mTxtOutTicketNum.setVisibility(View.GONE);
        mLinAllLottery.setVisibility(View.VISIBLE);
        mLinBt.setVisibility(View.VISIBLE);
        linTips.setVisibility(View.GONE);
        startBackNum();
        mTxtAllNum.setText("" + lotteryNum);
        outTicket("1");
    }

    /**
     * 更新出票状态
     *
     * @param ticketStatus 1 出票成功
     *                     2 出票异常
     */
    private void outTicket(String ticketStatus) {
        Map sendMap = Utils.getRequestData("outTicket.Req");
        TerminalLotteryInfo terminalLotteryInfo = mUpdateOutTicketStatusInfo.getTerminalLotteryDtos().get(0);
        terminalLotteryInfo.setNum("" + lotteryNum);
        terminalLotteryInfo.setTicketStatus(ticketStatus);

        List<TerminalLotteryInfo> lotteryInfos = new ArrayList<>();
        lotteryInfos.add(terminalLotteryInfo);

        sendMap.put("merOrderId", mUpdateOutTicketStatusInfo.getMerOrderId());
        sendMap.put("terminalLotteryDtos", lotteryInfos);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Utils.getSendMsg(sendMap));
        Observable<InitInfo> register = mApi.outTicket(requestBody).compose(RxUtil.<InitInfo>rxSchedulerHelper());
        mRxManager.add(register.subscribe(new Action1<InitInfo>() {
            @Override
            public void call(InitInfo initInfo) {
//                stopProgressDialog();
                if (initInfo.getRespCode().equals("00")) {
//                    surplus -= lotteryNum;
//                    if (surplus == 0) {
//                        mImageSoldOut.setVisibility(View.VISIBLE);
//                    }
//                    mTxtSurplusNum.setText("剩余 " + surplus + " 张");
                    MyApplication.mTerminalLotteryInfos = initInfo.getTerminalLotteryDtos();
                    queryFault(mIDCur);
                    LogUtil.d("状态更新成功");
                } else {
                    toastMessage(initInfo.getRespCode(), initInfo.getRespDesc());
                }
            }
        }, this));
    }


    /**
     * 开始出票动画
     */
    public void startAnim() {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.buy_step2_ticket);
        anim = new TranslateAnimation(0.0f, 0.0f, -bitmap.getHeight(), 0);
        anim.setDuration(1500);
        anim.setFillAfter(true);
        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageTicket.clearAnimation();
            }
        });
        startAnimation();
    }

    private void startAnimation() {
        mAnimRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
                mHandler.postDelayed(this, 2000);
            }
        };
        mHandler.postDelayed(mAnimRunnable, 0);
    }

    /**
     * 开始出票
     */
    private void startTicketNum() {
        mTicketNumRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(1);
                mHandler.postDelayed(this, 1500);
            }
        };
        mHandler.postDelayed(mTicketNumRunnable, 1500);
    }

    int backNum = 90;

    /**
     * 开始返回倒计时
     */
    private void startBackNum() {
        backNum = 90;
        mBtBack.setEnabled(false);
        mBtBack.setText("返回主界面(" + backNum + ")");
        mBackRunnable = new Runnable() {
            @Override
            public void run() {
                backNum--;
                mBackHandle.sendEmptyMessage(0);
                mBackHandle.postDelayed(this, 1000);
            }
        };
        mBackHandle.postDelayed(mBackRunnable, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBackHandle.removeCallbacks(mBackRunnable);
        mBackHandle.removeMessages(0);
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
                outTicketNum++;
                if (results[7].equals("01")) { //出票成功
                    mHandler.sendEmptyMessage(1);
                }
                if (results[7].equals("00")) { //出票失败
                    ToastUtils.showToast(PaySuccessActivity.this, "出票失败，请联系工作人员");
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
                mMotorSlave.TransOneSimpleS(mIDCur, mTicketLen, s1, s2, 1);
                Log.d(TAG, "发送 ----" + s1.toString());
                Log.d(TAG, "接收 " + s2.toString());

                Bundle bundle = new Bundle();
                bundle.putStringArray("result", s2.toString().split(" "));
                sendMsg(bundle, OUT_TICKET);
            } catch (Exception exp) {

            }
            mBusy = false;
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
                for (Entry<Integer, Boolean> e : status.entrySet()) {
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
