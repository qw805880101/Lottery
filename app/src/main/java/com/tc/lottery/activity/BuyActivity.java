package com.tc.lottery.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class BuyActivity extends BaseActivity {
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
    @BindView(R.id.bt_pay_wx)
    LinearLayout mBtPayWx;
    /**
     * 支付宝支付
     */
    @BindView(R.id.bt_pay_zfb)
    LinearLayout mBtPayZfb;
    /**
     * 单价
     */
    @BindView(R.id.image_amt)
    ImageView mImageAmt;
    /**
     * 剩余
     */
    @BindView(R.id.txt_surplus_num)
    TextView mTxtSurplusNum;

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

    private SoldOutDialog mSoldOutDialog; //已售罄弹框

    private BuyDialog mBuyDialog; //等待支付弹框

    private OutTicketDialog mOutTicketDialog; //支付成功出票中弹框

    private PaySuccessDialog mPaySuccessDialog; //交易完成弹框

    @Override
    public View getTitleView() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_buy;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mBuyDialog = new BuyDialog(this);
        mBuyDialog.builder();
        mBuyDialog.setCancelable(false); //对话框点击不可消失
        mBuyDialog.setCanceledOnTouchOutside(false); //对话框点击不可消失

        mOutTicketDialog = new OutTicketDialog(this);
        mOutTicketDialog.builder();
        mOutTicketDialog.setCancelable(false); //对话框点击不可消失
        mOutTicketDialog.setCanceledOnTouchOutside(false); //对话框点击不可消失

        mPaySuccessDialog = new PaySuccessDialog(this);
        mPaySuccessDialog.builder();
        mPaySuccessDialog.setCancelable(false); //对话框点击不可消失
        mPaySuccessDialog.setCanceledOnTouchOutside(false); //对话框点击不可消失


        mSoldOutDialog = new SoldOutDialog(this);
        mSoldOutDialog.builder();
        mSoldOutDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
//        mSoldOutDialog.setCancelable(false); //对话框点击不可消失
//        mSoldOutDialog.setCanceledOnTouchOutside(false); //对话框点击不可消失
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
            mSoldOutDialog.show();
        }

        updateLotteryNum(0);
    }

    @OnClick({R.id.bt_add, R.id.bt_reduce, R.id.bt_add_five, R.id.bt_add_ten, R.id.bt_clear, R.id.bt_pay_wx, R.id.bt_pay_zfb})
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
            case R.id.bt_pay_wx:
                prepOrder("02");
                break;
            case R.id.bt_pay_zfb:
                prepOrder("01");
                break;
        }
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
                    if (!"".equals(orderInfo.getQrCode())) {
                        bitCode = QRCodeUtil.createQRCodeBitmap(orderInfo.getQrCode(), 300, 300);
                        startQueryOrder();
                        showDialog("" + lotteryNum, "" + lotteryTotalAmt, payType, bitCode);
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
                        mBuyDialog.dismiss();
                        handler.removeCallbacks(runnable);
                        showOutTicketDialog(lotteryNum);
//                            ToastUtils.showToast(BuyActivity.this, "交易成功");
                    }
                } else {
                    toastMessage(orderInfo.getRespCode(), orderInfo.getRespDesc());
                }
            }
        }, this));
    }

    /**
     * 更新出票状态
     *
     * @param ticketStatus 1 出票成功
     *                     2 出票异常
     */
    private void outTicket(String ticketStatus) {
        TerminalLotteryInfo terminalLotteryInfo = mTerminalLotteryInfos.get(0);
        terminalLotteryInfo.setNum("" + lotteryNum);
        terminalLotteryInfo.setTicketStatus(ticketStatus);

        List<TerminalLotteryInfo> lotteryInfos = new ArrayList<>();
        lotteryInfos.add(terminalLotteryInfo);

        Map sendMap = Utils.getRequestData("outTicket.Req");
        sendMap.put("merOrderId", orderMap.get("merOrderId"));
        sendMap.put("terminalLotteryDtos", lotteryInfos);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Utils.getSendMsg(sendMap));
        Observable<BaseBean> register = mApi.outTicket(requestBody).compose(RxUtil.<BaseBean>rxSchedulerHelper());
        mRxManager.add(register.subscribe(new Action1<BaseBean>() {
            @Override
            public void call(BaseBean baseBean) {
//                stopProgressDialog();
                if (baseBean.getRespCode().equals("00")) {
                    surplus -= lotteryNum;
                    if (surplus == 0) {
                        mSoldOutDialog.show();
                    }
                    mTxtSurplusNum.setText("剩余 " + surplus + " 张");
                    LogUtil.d("状态更新成功");
                } else {
                    toastMessage(baseBean.getRespCode(), baseBean.getRespDesc());
                }
            }
        }, this));
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
     * 弹出等待支付弹框
     *
     * @param num
     * @param amt
     * @param payType
     * @param bitCode
     */
    private void showDialog(String num, String amt, String payType, Bitmap bitCode) {
        String[] nums = num.split("");
        String[] amts = amt.split("");
        List<String> numList = new ArrayList<>();
        List<String> amtList = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            numList.add(nums[i]);
        }
        for (int i = 0; i < amts.length; i++) {
            amtList.add(amts[i]);
        }
        mBuyDialog.setRecyclerViewNum(numList);
        mBuyDialog.setRecyclerViewAmt(amtList);
        mBuyDialog.setImagePayIcon(payType.equals("01") ? R.mipmap.zf_icon_alipay : R.mipmap.zf_icon_wx);
        mBuyDialog.setImageCode(bitCode);
        mBuyDialog.setBtBack(new OnClickListener() {
            @Override
            public void onClick(View view) { //返回，关闭交易查询线程
                mBuyDialog.dismiss();
                handler.removeCallbacks(runnable);
            }
        });
        mBuyDialog.show();
    }

    /**
     * 弹出出票弹框
     *
     * @param num 总票数
     */
    private void showOutTicketDialog(int num) {
        mOutTicketDialog.setTicketNum(num);
        mOutTicketDialog.startAnim();
        mOutTicketDialog.setOutTicketSuccess(new OutTicketSuccess() {
            @Override
            public void onSuccess(int outNum) {
                //出票完成回调接口
                mOutTicketDialog.dismiss();
                outTicket("1");
                showPaySuccessDialog(outNum);
            }
        });
        mOutTicketDialog.show();
    }

    /**
     * 交易完成弹框
     */
    private void showPaySuccessDialog(int outNum) {
        mPaySuccessDialog.setTicketNum(outNum);
        mPaySuccessDialog.startAnim();
        mPaySuccessDialog.setBtBack(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPaySuccessDialog.dismiss();
            }
        });
        mPaySuccessDialog.show();
    }

    @Override
    protected void onResume() {
        super.isBuyActivity = true;
        super.onResume();
    }
}
