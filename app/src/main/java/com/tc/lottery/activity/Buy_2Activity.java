package com.tc.lottery.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.psylife.wrmvplibrary.utils.LogUtil;
import com.psylife.wrmvplibrary.utils.ToastUtils;
import com.psylife.wrmvplibrary.utils.helper.RxUtil;
import com.psylife.wrmvplibrary.utils.timeutils.DateUtil;
import com.tc.lottery.MyApplication;
import com.tc.lottery.R;
import com.tc.lottery.base.BaseActivity;
import com.tc.lottery.bean.BaseBean;
import com.tc.lottery.bean.OrderInfo;
import com.tc.lottery.bean.TerminalLotteryInfo;
import com.tc.lottery.bean.UpdateOutTicketStatusInfo;
import com.tc.lottery.util.MotorSlaveUtils;
import com.tc.lottery.util.QRCodeUtil;
import com.tc.lottery.util.Utils;

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

import static com.tc.lottery.util.MotorSlaveUtils.QUERY_STATUS;

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
    /**
     * 返回主界面
     */
    @BindView(R.id.bt_back)
    Button btBackMain;
    /**
     * 彩票图片
     */
    @BindView(R.id.image_lottery_image)
    ImageView imageLottery;
    /**
     * 彩票最高金额
     */
    @BindView(R.id.image_lottery_image_02)
    ImageView imageLottery02;


    private TerminalLotteryInfo mTerminalLotteryInfo; //第一条票种

    private int lotteryNum = 1; //彩票票数
    private double lotteryTotalAmt = 0; //彩票总金额
    private String lotteryAmt = "500"; //彩票单价(分)
    private String lotteryId = "10"; //彩票id
    private int surplus = 0; //余票

    private Map orderMap; //订单map

    private Bitmap bitCode; //支付二维码

    private Runnable queryRunnable; //查询订单交易状态线程

    private boolean isCloseOrder = false; //是否点击关闭订单

    private int closeQueryNum = 0; //点击关闭时的查询订单次数

    private boolean isPayOlder = true; //是否可以下单

    private MotorSlaveUtils motorSlaveUtils; //机头工具类

    private Handler mMotorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            motorSlaveUtils.open();
            Bundle bundle = msg.getData();
            /**
             * 查询状态命令返回
             */
            if (QUERY_STATUS.equals(bundle.getString("type"))) {
                terminalUpdate("00");
//                if (bundle.getBoolean("0") && bundle.getBoolean("1") && bundle.getBoolean("2")) {
//                    /* 掉票处无票， 执行出票命令 */
//                    terminalUpdate("00");
//                } else {
//                    stopProgressDialog();
//                    /* 掉票处有票，执行设备状态检查命令 */
//                    ToastUtils.showToast(MainActivity.this, "掉票处有票, 请先取下已出票");
//                }
            }

        }
    };

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

        motorSlaveUtils = new MotorSlaveUtils(mMotorHandler);

//        closeBt("999");
    }

    @Override
    public void initdata() {
//        Intent intent = this.getIntent();
//        terminalLotteryStatus = intent.getStringExtra("TerminalLotteryStatus");

//        mTxtTerminalStatus.setText("设备状态：" + getTerminalStatus(terminalLotteryStatus));
//        mTxtLotteryAmt.setText("单价" + mTerminalLotteryInfos.get(0).getLotteryAmt());

        mTerminalLotteryInfo = MyApplication.mTerminalLotteryInfo;

        surplus = Integer.parseInt(mTerminalLotteryInfo.getSurplus());

        mTxtSurplusNum.setText("剩余 " + surplus + " 张");

        if (mTerminalLotteryInfo.getSurplus().equals("0")/* && "4".equals(MyApplication.terminalLotteryStatus)*/) {
            mImageSoldOut.setVisibility(View.VISIBLE);
        }

        if (!mTerminalLotteryInfo.getLotteryAmt().equals(""))
            lotteryAmt = mTerminalLotteryInfo.getLotteryAmt();

        if (!mTerminalLotteryInfo.getLotteryId().equals(""))
            lotteryId = mTerminalLotteryInfo.getLotteryId();

        mImageAmt.setImageResource(getAmtPic(lotteryAmt));

        String[] lotteryImages = null;
        if (mTerminalLotteryInfo.getLotteryImg() != null)
            lotteryImages = mTerminalLotteryInfo.getLotteryImg().split(",");
        if (lotteryImages != null) {
            if (lotteryImages.length >= 1 && null != lotteryImages[0] && !"".equals(lotteryImages[0])) {
                Utils.loadHeadIcon(this, lotteryImages[0], imageLottery);
            }
            if (lotteryImages.length >= 2 && null != lotteryImages[1] && !"".equals(lotteryImages[1])) {
                Utils.loadHeadIcon(this, lotteryImages[1], imageLottery02);
            } else {
                imageLottery02.setImageResource(getTopPic(lotteryId));
            }
        }


        updateLotteryNum(0);
    }

    /**
     * 获取最高奖金图片
     *
     * @param id
     * @return
     */
    private int getTopPic(String id) {
        if (id.equals("10") || id.equals("16") || id.equals("19") ) { //10w
            return R.mipmap.buy_step1_jiangjin;
        }
        if (id.equals("12")) { //15w
            return R.mipmap.buy_step1_jiangjin15;
        }
        if (id.equals("13") || id.equals("14")) { //100w
            return R.mipmap.buy_step1_jiangjin100;
        }
        if (id.equals("18")) { //25w
            return R.mipmap.buy_step1_jiangjin25;
        }
        return R.mipmap.buy_step1_jiangjin;
    }

    /**
     * 获取金额图片
     *
     * @param amt
     * @return
     */
    private int getAmtPic(String amt) {
        if (amt.equals("200")) {
            return R.mipmap.buy_step1_2yuan;
        }
        if (amt.equals("500")) {
            return R.mipmap.buy_step1_5yuan;
        }
        if (amt.equals("1000")) {
            return R.mipmap.buy_step1_10yuan;
        }
        if (amt.equals("2000")) {
            return R.mipmap.buy_step1_20yuan;
        }
        if (amt.equals("3000")) {
            return R.mipmap.buy_step1_30yuan;
        }
        return R.mipmap.buy_step1_5yuan;
    }

    @OnClick({R.id.bt_add, R.id.bt_reduce, R.id.bt_add_five, R.id.bt_add_ten, R.id.bt_clear
            , R.id.image_bt_wx_pay, R.id.image_bt_zfb_pay, R.id.txt_back, R.id.bt_back})
    public void onViewClicked(View view) {
        if (!"1".equals(MyApplication.terminalLotteryStatus)) {
            ToastUtils.showToast(this, getStatus(MyApplication.terminalLotteryStatus));
            return;
        }
        switch (view.getId()) {
            case R.id.bt_add:
                updateLotteryNum(1);
                break;
            case R.id.bt_reduce:
                if (lotteryNum <= 1) {
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
                updateLotteryNum(-(lotteryNum));
                break;
            case R.id.image_bt_wx_pay:
                if (lotteryNum == 0) {
                    ToastUtils.showToast(this, "至少购买一张");
                    return;
                }
                prepOrder("02");
                break;
            case R.id.image_bt_zfb_pay:
                if (lotteryNum == 0) {
                    ToastUtils.showToast(this, "至少购买一张");
                    return;
                }
                prepOrder("01");
                break;
            case R.id.txt_back:
                closeQueryNum = queryNum;
                isCloseOrder = true;
                isPayOlder = false;
                startCloseAnim();
                mBackNumHandler.removeCallbacks(mBackRunnable);
                mBackNumHandler.removeMessages(0);
                mTxtBack.setText("关闭");
                break;
            case R.id.bt_back:
                startActivity(new Intent(this, MainActivity.class));
                this.finish();
                break;
        }
    }

    /**
     * 查询状态
     *
     * @param nID
     */
    private void queryStatus(int nID) {
        if (motorSlaveUtils.mBusy)
            return;
        startProgressDialog(this);
        motorSlaveUtils.setmIDCur(nID);
        new Thread(motorSlaveUtils.ReadStatusRunnable).start();
    }

    /**
     * 终端状态同步
     */
    private void terminalUpdate(String status) {
//        startProgressDialog(this);
        Map sendMap = Utils.getRequestData("terminalUpdate.Req");
        /**
         * 01 公众号
         02 终端
         */
        sendMap.put("reqType", "02");
        /**
         * 00 正常
         01 设备故障
         02 票箱故障
         03 票箱无票
         */
        sendMap.put("status", status);
        /**
         * 如终端状态为02，03上送
         1,2,3,4 用，号分割
         */
        sendMap.put("boxStatus", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Utils.getSendMsg(sendMap));
        Observable<BaseBean> register = mApi.terminalUpdate(requestBody).compose(RxUtil.<BaseBean>rxSchedulerHelper());
        mRxManager.add(register.subscribe(new Action1<BaseBean>() {
            @Override
            public void call(BaseBean baseBean) {
                stopProgressDialog();
                if ("00".equals(baseBean.getRespCode())) {
                    ToastUtils.showToast(Buy_2Activity.this, "终端状态同步成功");
                    openBt();
                } else {
                    toastMessage(baseBean.getRespCode(), baseBean.getRespDesc());
                }
            }
        }, this));
    }

    /**
     * 设备状态
     * 0 待激活
     * 1 已激活
     * 2 待维修
     * 3 已暂停
     * 4 设备无票
     */
    private String getStatus(String type) {
        if ("0".equals(type)) {
            return "设备待激活";
        }
        if ("2".equals(type)) {
            return "设备待维修";
        }
        if ("3".equals(type)) {
            return "设备已暂停";
        }
        if ("4".equals(type)) {
            return "设备无票";
        }
        return "设备故障";
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
        mTxtTotalAmt.setText("¥ " + lotteryTotalAmt);
    }

    /**
     * 下单
     *
     * @param payType 01 支付宝  02 微信
     */
    private void prepOrder(final String payType) {

        if (!isPayOlder) {
            ToastUtils.showToast(this, "请稍候");
            return;
        }

        startProgressDialog(this);
        TerminalLotteryInfo terminalLotteryInfo = MyApplication.mTerminalLotteryInfo;
        terminalLotteryInfo.setNum("" + lotteryNum);
        List<TerminalLotteryInfo> lotteryInfos = new ArrayList<>();
        lotteryInfos.add(terminalLotteryInfo);
        orderMap = Utils.getRequestData("prepOrder.Req");
        orderMap.put("merOrderId", DateUtil.format(new Date(), "yyyyMMddHHmmss") + payType + (lotteryTotalAmt * 100)); //订单规则：日期+交易类型+交易金额
        orderMap.put("merOrderTime", DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        orderMap.put("orderAmt", "" + (int) (lotteryTotalAmt * 100));
        orderMap.put("terminalLotteryDtos", lotteryInfos);
        orderMap.put("payType", payType);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Utils.getSendMsg(orderMap));
        Observable<OrderInfo> register = mApi.prepOrder(requestBody).compose(RxUtil.<OrderInfo>rxSchedulerHelper());
        mRxManager.add(register.subscribe(new Action1<OrderInfo>() {
            @Override
            public void call(OrderInfo orderInfo) {
                stopProgressDialog();
                orderHandler.removeCallbacks(queryRunnable);
                if (orderInfo.getRespCode().equals("00")) {
                    if (!"".equals(orderInfo.getQrCode())) { //下单成功
                        bitCode = QRCodeUtil.createQRCodeBitmap(orderInfo.getQrCode(), 300, 300);
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
                        Intent intent = new Intent(Buy_2Activity.this, PaySuccessActivity.class);
                        intent.putExtra("lotteryNum", lotteryNum);
                        intent.putExtra("outTicket", outTicket());
                        startActivity(intent);
                        finish();
                    }
                    /* 订单状态未支付，并且点击关闭按钮，且关闭后已执行两次订单查询 */
                    if ("0".equals(orderInfo.getOrderStatus()) && isCloseOrder && queryNum >= (closeQueryNum + 2)) {
                        isCloseOrder = false;
                        orderHandler.removeCallbacks(queryRunnable);
                        queryNum = 0;
                        isPayOlder = true;
                        return;
//                        startCloseAnim();
                    }

                    if ("0".equals(orderInfo.getOrderStatus()) && queryNum >= 31) {
                        orderHandler.removeCallbacks(queryRunnable);
                        isPayOlder = true;
//                        mBackNumHandler.removeCallbacks(mBackRunnable);
//                        mBackNumHandler.removeMessages(0);
//                        startCloseAnim();
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
        TerminalLotteryInfo terminalLotteryInfo = MyApplication.mTerminalLotteryInfo;
        terminalLotteryInfo.setNum("" + lotteryNum);
        List<TerminalLotteryInfo> lotteryInfos = new ArrayList<>();
        lotteryInfos.add(terminalLotteryInfo);
        updateOutTicketStatusInfo.setMerOrderId(orderMap.get("merOrderId").toString());
        updateOutTicketStatusInfo.setTerminalLotteryDtos(lotteryInfos);
        return updateOutTicketStatusInfo;
    }

    private Handler orderHandler = new Handler();

    /**
     * 开始查询订单交易状态
     */
    private void startQueryOrder() {
        queryRunnable = new Runnable() {
            @Override
            public void run() {
                //查询交易订单，以实现每两五秒实现一次
                queryOrder();
                queryNum++;
                orderHandler.postDelayed(this, 3000);
            }
        };
        orderHandler.postDelayed(queryRunnable, 7000);
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

    Handler mBackNumHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (backNum > 0) {
                    mTxtBack.setText("关闭(" + backNum + ")");
//                    if (backNum == 75)
//                        mTxtBack.setEnabled(true);
                } else {
                    mBackNumHandler.removeCallbacks(mBackRunnable);
//                    handler.removeCallbacks(queryRunnable);
                    mTxtBack.setText("关闭");
                    startCloseAnim();
                    isPayOlder = false;
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

    int backNum = 90;
    int queryNum = 0; //查询订单计数

    /**
     * 开始返回倒计时
     */
    private void startBackNum() {
        startQueryOrder();
        backNum = 90;
        queryNum = 0;
//        mTxtBack.setEnabled(false);
        mTxtBack.setText("关闭(90)");
        mBackRunnable = new Runnable() {
            @Override
            public void run() {
                backNum--;
                mBackNumHandler.sendEmptyMessage(0);
                mBackNumHandler.postDelayed(this, 1000);
            }
        };
        mBackNumHandler.postDelayed(mBackRunnable, 1000);
    }

    @Override
    protected void onResume() {
        super.isBuyActivity = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBackNumHandler.removeMessages(0);
        mBackNumHandler.removeCallbacks(mBackRunnable);
        orderHandler.removeCallbacks(queryRunnable);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        motorSlaveUtils.open();
    }

    @Override
    protected void onStop() {
        super.onStop();
        motorSlaveUtils.close();
    }
}
