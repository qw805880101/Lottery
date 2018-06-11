package com.tc.lottery.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.psylife.wrmvplibrary.utils.LogUtil;
import com.psylife.wrmvplibrary.utils.ToastUtils;
import com.psylife.wrmvplibrary.utils.helper.RxUtil;
import com.psylife.wrmvplibrary.utils.timeutils.DateUtil;
import com.tc.lottery.R;
import com.tc.lottery.base.BaseActivity;
import com.tc.lottery.bean.BaseBean;
import com.tc.lottery.bean.OrderInfo;
import com.tc.lottery.bean.TerminalLotteryInfo;
import com.tc.lottery.bean.UpdateOutTicketStatusInfo;
import com.tc.lottery.util.QRCodeUtil;
import com.tc.lottery.util.Utils;
import com.tc.lottery.view.BuyDialog;
import com.tc.lottery.view.OutTicketDialog;
import com.tc.lottery.view.OutTicketDialog.OutTicketSuccess;
import com.tc.lottery.view.PaySuccessDialog;
import com.tc.lottery.view.SoldOutDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Action1;

public class Buy_2Activity extends BaseActivity {
    /**
     * 添加按钮
     */
    @BindView(R.id.bt_add)
    ImageButton mBtAdd;
    /**
     * 彩票总数量
     */
    @BindView(R.id.txt_lottery_num)
    TextView mTxtLotteryNum;
    /**
     * 减少按钮
     */
    @BindView(R.id.bt_reduce)
    ImageButton mBtReduce;
    /**
     * 添加5个按钮
     */
    @BindView(R.id.bt_add_five)
    Button mBtAddFive;
    /**
     * 添加10个按钮
     */
    @BindView(R.id.bt_add_ten)
    Button mBtAddTen;
    /**
     * 清空按钮
     */
    @BindView(R.id.bt_clear)
    Button mBtClear;
    /**
     * 总金额
     */
    @BindView(R.id.txt_total_amt)
    TextView mTxtTotalAmt;
    /**
     * 微信支付
     */
    @BindView(R.id.image_bt_wx_pay)
    ImageButton mBtPayWx;
    /**
     * 支付宝支付
     */
    @BindView(R.id.image_bt_zfb_pay)
    ImageButton mBtPayZfb;
    /**
     * 单价
     */
    @BindView(R.id.image_amt)
    ImageView mImageAmt;
    /**
     * 剩余
     */
    @BindView(R.id.txt_lottery_surplus)
    TextView mTxtSurplusNum;
    /**
     * 已售罄图片
     */
    @BindView(R.id.image_sold_out)
    ImageView mImageSoldOut;
    /**
     * 订单界面
     */
    @BindView(R.id.rl_order)
    RelativeLayout rlOrder;
    /**
     * 订单界面背景
     */
    @BindView(R.id.rl_order_bg)
    RelativeLayout rlOrderBg;
    /**
     * 支付类型图标
     */
    @BindView(R.id.image_pay_icon)
    ImageView mImageViewPayIcon;
    /**
     * 支付二维码
     */
    @BindView(R.id.image_code)
    ImageView mImageViewCode;
    /**
     * 关闭支付
     */
    @BindView(R.id.txt_back)
    TextView mTxtBack;

    private String terminalLotteryStatus; //票箱状态

    private TerminalLotteryInfo mTerminalLotteryInfo; //第一条票种

    List<TerminalLotteryInfo> mTerminalLotteryInfos = new ArrayList<>(); //初始化返回的票种列表

    private int lotteryNum = 1; //彩票票数
    private double lotteryTotalAmt = 0; //彩票总金额
    private int surplus = 0; //余票

    private Map orderMap; //订单map

    private Bitmap bitCode; //支付二维码

    private Handler handler = new Handler();
    private Runnable runnable; //查询订单交易状态线程

    /* 出订单动画 */
    private TranslateAnimation orderAnim;

    @Override
    public View getTitleView() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_buy_02;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
//        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.buy_step1_code_bg2);
//        rlOrder.setY(-bitmap.getHeight() + 30);
    }

    @Override
    public void initdata() {
        Intent intent = this.getIntent();
        mTerminalLotteryInfos = (List<TerminalLotteryInfo>) intent.getSerializableExtra("TerminalLotteryInfo");
        terminalLotteryStatus = intent.getStringExtra("TerminalLotteryStatus");

//        mTxtTerminalStatus.setText("设备状态：" + getTerminalStatus(terminalLotteryStatus));
//        mTxtLotteryAmt.setText("单价" + mTerminalLotteryInfos.get(0).getLotteryAmt());

        mTerminalLotteryInfo = mTerminalLotteryInfos.get(0);

        surplus = Integer.parseInt(mTerminalLotteryInfo.getSurplus());

        mTxtSurplusNum.setText("剩余 " + surplus + " 张");

        if (mTerminalLotteryInfo.getSurplus().equals("0")) {
            mImageSoldOut.setVisibility(View.VISIBLE);
        }

        updateLotteryNum(0);
    }

    @OnClick({R.id.bt_add, R.id.bt_reduce, R.id.bt_add_five, R.id.bt_add_ten, R.id.bt_clear
            , R.id.image_bt_wx_pay, R.id.image_bt_zfb_pay, R.id.txt_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_add:
                updateLotteryNum(1);
                break;
            case R.id.bt_reduce:
                if (lotteryNum == 1) {
                    return;
                }
                updateLotteryNum(-1);
                break;
            case R.id.bt_add_five:
                updateLotteryNum(5);
                break;
            case R.id.bt_add_ten:
                updateLotteryNum(10);
                break;
            case R.id.bt_clear:
                updateLotteryNum(-(lotteryNum - 1));
                break;
            case R.id.image_bt_wx_pay:
                prepOrder("02");
                break;
            case R.id.image_bt_zfb_pay:
                prepOrder("01");
                break;
            case R.id.txt_back:
                startCloseAnim();
                break;
        }
    }

    /**
     * 关闭按钮
     */
    private void closeBt(String payType) {
        if (payType.equals("01")) {
            mBtPayWx.setImageResource(R.mipmap.buy_zf_wx_gary);
            mBtPayZfb.setBackgroundResource(R.mipmap.buy_zf_on_bg);
        }
        if (payType.equals("02")) {
            mBtPayZfb.setImageResource(R.mipmap.buy_zf_alipay_gary);
            mBtPayWx.setBackgroundResource(R.mipmap.buy_zf_on_bg);
        }

        mBtAdd.setBackgroundResource(R.mipmap.buy_jia_btn_gary);
        mBtReduce.setBackgroundResource(R.mipmap.buy_jia_btn_gary);
        mBtAddFive.setBackgroundResource(R.mipmap.buy_sl_btn);
        mBtAddTen.setBackgroundResource(R.mipmap.buy_sl_btn);
        mBtClear.setBackgroundResource(R.mipmap.buy_sl_btn);

        mBtPayWx.setEnabled(false);
        mBtPayZfb.setEnabled(false);

        mBtAdd.setEnabled(false);
        mBtReduce.setEnabled(false);

        mBtAddFive.setEnabled(false);
        mBtAddTen.setEnabled(false);
        mBtClear.setEnabled(false);
    }

    /**
     * 打开按钮
     */
    private void openBt() {
        mBtPayWx.setImageResource(R.mipmap.buy_zf_wx);
        mBtPayWx.setBackgroundResource(0);
        mBtPayZfb.setImageResource(R.mipmap.buy_zf_alipay);
        mBtPayZfb.setBackgroundResource(0);

        mBtAdd.setBackgroundResource(R.drawable.bt_selector_reduce);
        mBtReduce.setBackgroundResource(R.drawable.bt_selector_reduce);
        mBtAddFive.setBackgroundResource(R.mipmap.buy_sl_btn_on);
        mBtAddTen.setBackgroundResource(R.mipmap.buy_sl_btn_on);
        mBtClear.setBackgroundResource(R.mipmap.buy_sl_btn_on);

        mBtPayWx.setEnabled(true);
        mBtPayZfb.setEnabled(true);

        mBtAdd.setEnabled(true);
        mBtReduce.setEnabled(true);
        mBtAddFive.setEnabled(true);
        mBtAddTen.setEnabled(true);
        mBtClear.setEnabled(true);
        rlOrder.clearAnimation();
    }

    /**
     * 更新彩票数量、金额
     */
    private void updateLotteryNum(int num) {
        lotteryNum += num;
        if (lotteryNum > surplus) {
            ToastUtils.showToast(this, "当前余票不足");
            lotteryNum = surplus;
        }

        if (lotteryNum > 10) {
            ToastUtils.showToast(this, "一次最多购买10张");
            lotteryNum = 10;
        }

        lotteryTotalAmt = lotteryNum * Double.parseDouble(mTerminalLotteryInfo.getLotteryAmt()) / 100;
        mTxtLotteryNum.setText("" + lotteryNum);
        mTxtTotalAmt.setText("" + lotteryTotalAmt);
    }

    /**
     * 下单
     *
     * @param payType 01 支付宝  02 微信
     */
    private void prepOrder(final String payType) {
        startProgressDialog(this);
        TerminalLotteryInfo terminalLotteryInfo = mTerminalLotteryInfos.get(0);
        terminalLotteryInfo.setNum("" + lotteryNum);
        List<TerminalLotteryInfo> lotteryInfos = new ArrayList<>();
        lotteryInfos.add(terminalLotteryInfo);
        orderMap = Utils.getRequestData("prepOrder.Req");
        orderMap.put("merOrderId", DateUtil.format(new Date(), "YYYYMMDDhhmmss") + payType); //订单规则：日期+交易类型+交易金额
        orderMap.put("merOrderTime", DateUtil.format(new Date(), "YYYYMMDDhhmmss"));
        orderMap.put("orderAmt", "" + (lotteryTotalAmt * 100));
        orderMap.put("terminalLotteryDtos", lotteryInfos);
        orderMap.put("payType", payType);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Utils.getSendMsg(orderMap));
        Observable<OrderInfo> register = mApi.prepOrder(requestBody).compose(RxUtil.<OrderInfo>rxSchedulerHelper());
        mRxManager.add(register.subscribe(new Action1<OrderInfo>() {
            @Override
            public void call(OrderInfo orderInfo) {
                stopProgressDialog();
                if (orderInfo.getRespCode().equals("00")) {
                    if (!"".equals(orderInfo.getQrCode())) { //下单成功
                        bitCode = QRCodeUtil.createQRCodeBitmap(orderInfo.getQrCode(), 300, 300);
                        startQueryOrder();
                        closeBt(payType);
                        startOpenAnim(bitCode, payType);
                    }

                } else {
                    toastMessage(orderInfo.getRespCode(), orderInfo.getRespDesc());
                }
            }
        }, this));
    }

    /**
     * 查询支付订单状态
     */
    private void queryOrder() {
        Map sendMap = Utils.getRequestData("queryOrder.Req");
        sendMap.put("merOrderId", orderMap.get("merOrderId"));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Utils.getSendMsg(sendMap));
        Observable<OrderInfo> register = mApi.queryOrder(requestBody).compose(RxUtil.<OrderInfo>rxSchedulerHelper());
        mRxManager.add(register.subscribe(new Action1<OrderInfo>() {
            @Override
            public void call(OrderInfo orderInfo) {
//                stopProgressDialog();
                if (orderInfo.getRespCode().equals("00")) {
                    if ("1".equals(orderInfo.getOrderStatus())) { //交易成功关闭订单查询
                        handler.removeCallbacks(runnable);
                        Intent intent = new Intent(Buy_2Activity.this, PaySuccessActivity.class);
                        intent.putExtra("lotteryNum", lotteryNum);
                        intent.putExtra("outTicket", outTicket());
                        startActivity(intent);
                        finish();
                    }
                } else {
                    toastMessage(orderInfo.getRespCode(), orderInfo.getRespDesc());
                }
            }
        }, this));
    }

    /**
     * 更新出票状态
     */
    private UpdateOutTicketStatusInfo outTicket() {
        UpdateOutTicketStatusInfo updateOutTicketStatusInfo = new UpdateOutTicketStatusInfo();
        TerminalLotteryInfo terminalLotteryInfo = mTerminalLotteryInfos.get(0);
        terminalLotteryInfo.setNum("" + lotteryNum);
        List<TerminalLotteryInfo> lotteryInfos = new ArrayList<>();
        lotteryInfos.add(terminalLotteryInfo);
        updateOutTicketStatusInfo.setMerOrderId(orderMap.get("merOrderId").toString());
        updateOutTicketStatusInfo.setTerminalLotteryDtos(lotteryInfos);
        return updateOutTicketStatusInfo;
    }

    /**
     * 开始查询订单交易状态
     */
    private void startQueryOrder() {
        runnable = new Runnable() {
            @Override
            public void run() {
                //查询交易订单，以实现每两五秒实现一次
                queryOrder();
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 7000);
    }

    /**
     * 下单成功弹出支付二维码动画
     *
     * @param bitCode
     * @param payType
     */
    public void startOpenAnim(Bitmap bitCode, String payType) {

        mImageViewCode.setImageBitmap(bitCode);
        mImageViewPayIcon.setImageResource(payType.equals("01") ? R.mipmap.zf_icon_alipay : R.mipmap.zf_icon_wx);

        ObjectAnimator translationX = new ObjectAnimator().ofFloat(rlOrder, "translationX", 0, 0);
        ObjectAnimator translationY = new ObjectAnimator().ofFloat(rlOrder, "translationY", 0, rlOrderBg.getHeight() - 55);

        AnimatorSet animatorSet = new AnimatorSet();  //组合动画
        animatorSet.playTogether(translationX, translationY); //设置动画
        animatorSet.setDuration(1500);  //设置动画时间
        animatorSet.start(); //启动
        animatorSet.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setBtBack(1);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * 关闭支付二维码动画
     */
    public void startCloseAnim() {
        ObjectAnimator translationX = new ObjectAnimator().ofFloat(rlOrder, "translationX", 0, 0);
        ObjectAnimator translationY = new ObjectAnimator().ofFloat(rlOrder, "translationY", rlOrderBg.getHeight() - 55, 0);

        AnimatorSet animatorSet = new AnimatorSet();  //组合动画
        animatorSet.playTogether(translationX, translationY); //设置动画
        animatorSet.setDuration(1000);  //设置动画时间
        animatorSet.start(); //启动
        animatorSet.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                openBt();
                setBtBack(0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    Runnable mBackRunnable;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (backNum > 0) {
                    mTxtBack.setText("关闭(" + backNum + ")");
                } else {
                    mHandler.removeCallbacks(mBackRunnable);
                    handler.removeCallbacks(runnable);
                    mTxtBack.setEnabled(true);
                    mTxtBack.setText("关闭");
                }
            }
        }
    };

    /**
     * 返回按钮
     *
     * @param type 1 打开  0 关闭
     */
    public void setBtBack(int type) {
        mTxtBack.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        if (type == 1)
            startBackNum();
    }

    int backNum = 10;

    /**
     * 开始返回倒计时
     */
    private void startBackNum() {
        mTxtBack.setEnabled(false);
        mTxtBack.setText("关闭(10)");
        mBackRunnable = new Runnable() {
            @Override
            public void run() {
                backNum--;
                mHandler.sendEmptyMessage(0);
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mBackRunnable, 1000);
    }

    @Override
    protected void onResume() {
        super.isBuyActivity = true;
        super.onResume();
    }
}