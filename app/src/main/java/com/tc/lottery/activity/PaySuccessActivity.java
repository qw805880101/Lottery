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
import com.psylife.wrmvplibrary.utils.helper.RxUtil;
import com.tc.lottery.R;
import com.tc.lottery.base.BaseActivity;
import com.tc.lottery.bean.BaseBean;
import com.tc.lottery.bean.TerminalLotteryInfo;
import com.tc.lottery.bean.UpdateOutTicketStatusInfo;
import com.tc.lottery.util.Utils;
import com.tc.lottery.view.SuccessView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Action1;

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


    private int lotteryNum = 5; //彩票数量
    private int outTicketNum = 1; //已出数量

    private TranslateAnimation anim; //彩票动画

    private UpdateOutTicketStatusInfo mUpdateOutTicketStatusInfo; //出票状态参数

    Runnable mAnimRunnable;

    Runnable mTicketNumRunnable;

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
                    outTicketNum++;
                } else {
                    mHandler.removeCallbacks(mTicketNumRunnable);
                    mHandler.removeCallbacks(mAnimRunnable);
                    //出票完成
                    outTicketSuccess();
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

    }

    @Override
    public void initdata() {
        Intent intent = this.getIntent();
//        lotteryNum = intent.getIntExtra("lotteryNum", 1);
//        mUpdateOutTicketStatusInfo = (UpdateOutTicketStatusInfo) intent.getSerializableExtra("outTicket");
//
        mTxtOutTicketNum.setText("支付成功！正在出票...（" + outTicketNum + "/" + lotteryNum + "）");

        startAnim();
        startTicketNum();
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
        mTxtOutTicketNum.setVisibility(View.GONE);
        mLinAllLottery.setVisibility(View.VISIBLE);
        mLinBt.setVisibility(View.VISIBLE);

        mTxtAllNum.setText("" + lotteryNum);
//        outTicket("1");
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
        Observable<BaseBean> register = mApi.outTicket(requestBody).compose(RxUtil.<BaseBean>rxSchedulerHelper());
        mRxManager.add(register.subscribe(new Action1<BaseBean>() {
            @Override
            public void call(BaseBean baseBean) {
//                stopProgressDialog();
                if (baseBean.getRespCode().equals("00")) {
//                    surplus -= lotteryNum;
//                    if (surplus == 0) {
//                        mImageSoldOut.setVisibility(View.VISIBLE);
//                    }
//                    mTxtSurplusNum.setText("剩余 " + surplus + " 张");
                    LogUtil.d("状态更新成功");
                } else {
                    toastMessage(baseBean.getRespCode(), baseBean.getRespDesc());
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

}
