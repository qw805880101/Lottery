package com.tc.lottery.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.tc.lottery.bean.InitInfo;
import com.tc.lottery.bean.TerminalLotteryInfo;
import com.tc.lottery.bean.UpdateOutTicketStatusInfo;
import com.tc.lottery.util.MotorSlaveUtils;
import com.tc.lottery.util.Utils;
import com.tc.lottery.view.SuccessView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Action1;

import static com.tc.lottery.util.MotorSlaveUtils.OUT_TICKET;
import static com.tc.lottery.util.MotorSlaveUtils.QUERY_FAULT;
import static com.tc.lottery.util.MotorSlaveUtils.QUERY_STATUS;

public class PaySuccessActivity extends BaseActivity {

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

    private MotorSlaveUtils motorSlaveUtils; //机头工具类

    private int lotteryNum = 2; //彩票数量
    private int outTicketNum = 1; //已出数量

    private TranslateAnimation anim; //彩票动画

    private UpdateOutTicketStatusInfo mUpdateOutTicketStatusInfo; //出票状态参数

    Runnable mAnimRunnable;

    Runnable mTicketNumRunnable;

    Runnable mBackRunnable;

    Handler mOutTicketAnimHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                imageTicket.startAnimation(anim);
            }
            if (msg.what == 1) {
                if (outTicketNum <= lotteryNum) {
                    mTxtOutTicketNum.setText("支付成功！正在出票...（" + outTicketNum + "/" + lotteryNum + "）");
                    outTicketNum++;
                } else {
                    //出票完成
                    outTicketSuccess("1");
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
                    mBackHandle.removeCallbacks(mBackRunnable);
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
        motorSlaveUtils = new MotorSlaveUtils(mOutTicketHandler);
    }

    @Override
    public void initdata() {
        Intent intent = this.getIntent();
        lotteryNum = intent.getIntExtra("lotteryNum", 1);
        mUpdateOutTicketStatusInfo = (UpdateOutTicketStatusInfo) intent.getSerializableExtra("outTicket");
//
        mTxtOutTicketNum.setText("支付成功！正在出票...（" + outTicketNum + "/" + lotteryNum + "）");

        startAnim();
//        startTicketNum();
        motorSlaveUtils.setTicketLen(Integer.parseInt(MyApplication.mTerminalLotteryInfo.getTicketLen() != null &&
                !MyApplication.mTerminalLotteryInfo.getTicketLen().equals("") ? MyApplication.mTerminalLotteryInfo.getTicketLen() : "0"));
        queryStatus(motorSlaveUtils.mIDCur);
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
    private void outTicketSuccess(String ticketStatus) {
        if ("2".equals(ticketStatus)) {
            mTxtOutTicketNum.setText("出票失败，请联系工作人员");
            mLinAllLottery.setVisibility(View.GONE);
            mBtHow.setVisibility(View.GONE);
        } else {
            mTxtOutTicketNum.setVisibility(View.GONE);
            mLinAllLottery.setVisibility(View.VISIBLE);
            successView.setVisibility(View.VISIBLE);
            mTxtAllNum.setText("" + lotteryNum);
        }
        mOutTicketAnimHandler.removeCallbacks(mTicketNumRunnable);
        mOutTicketAnimHandler.removeCallbacks(mAnimRunnable);
        linTips.setVisibility(View.GONE);
        startBackNum();
        mLinBt.setVisibility(View.VISIBLE);
        outTicket(ticketStatus);
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
                    for (int i = 0; i < initInfo.getTerminalLotteryDtos().size(); i++) {
                        if (initInfo.getTerminalLotteryDtos().get(i).getBoxId().equals("1")) {
                            MyApplication.mTerminalLotteryInfo = initInfo.getTerminalLotteryDtos().get(i);
                        }
                    }
//                    queryFault(mIDCur);
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
                mOutTicketAnimHandler.sendEmptyMessage(0);
                mOutTicketAnimHandler.postDelayed(this, 2000);
            }
        };
        mOutTicketAnimHandler.postDelayed(mAnimRunnable, 0);
    }

    /**
     * 开始出票
     */
    private void startTicketNum() {
        mTicketNumRunnable = new Runnable() {
            @Override
            public void run() {
                mOutTicketAnimHandler.sendEmptyMessage(1);
                mOutTicketAnimHandler.postDelayed(this, 1500);
            }
        };
        mOutTicketAnimHandler.postDelayed(mTicketNumRunnable, 1500);
    }

    int backNum = 90;

    /**
     * 开始返回倒计时
     */
    private void startBackNum() {
        backNum = 90;
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
        motorSlaveUtils.close();
        mBackHandle.removeCallbacks(mBackRunnable);
        mBackHandle.removeMessages(0);
    }

    /**
     * 出票方法
     *
     * @param nID
     */
    private void onTransOne(int nID) {
        if (motorSlaveUtils.mBusy)
            return;
        motorSlaveUtils.setmIDCur(nID);
        mOutTicketAnimHandler.sendEmptyMessage(1);
        new Thread(motorSlaveUtils.transmitoneS).start();
    }

    /**
     * 查询状态
     *
     * @param nID
     */
    private void queryStatus(int nID) {
        if (motorSlaveUtils.mBusy)
            return;
        motorSlaveUtils.setmIDCur(nID);
        new Thread(motorSlaveUtils.ReadStatusRunnable).start();
    }

    /**
     * 查询设备故障
     *
     * @param nID
     */
    private void queryFault(int nID) {
        if (motorSlaveUtils.mBusy)
            return;
        motorSlaveUtils.setmIDCur(nID);
        new Thread(motorSlaveUtils.QueryFaultRunnable).start();
    }

    Handler mOutTicketHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            motorSlaveUtils.open();
            Bundle bundle = msg.getData();
            /**
             * 出票命令返回
             */
            if (OUT_TICKET.equals(bundle.getString("type"))) {
                String[] results = bundle.getStringArray("result");
                if (results[7].equals("01")) { //出票成功
                    if (outTicketNum <= lotteryNum)
                        queryStatus(motorSlaveUtils.mIDCur);
                    else
                        mOutTicketAnimHandler.sendEmptyMessage(1);
                }
                if (results[7].equals("00")) { //出票失败
                    ToastUtils.showToast(PaySuccessActivity.this, "出票失败，请联系工作人员");
                    outTicketSuccess("2");
                }
            }

            /**
             * 查询状态命令返回
             */
            if (QUERY_STATUS.equals(bundle.getString("type"))) {
                if (bundle.getBoolean("2")) {
                    /* 掉票处无票， 执行出票命令 */
                    onTransOne(motorSlaveUtils.mIDCur);
                } else {
                    /* 掉票处有票，执行设备状态检查命令 */
                    queryStatus(motorSlaveUtils.mIDCur);
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


}
