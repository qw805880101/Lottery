package com.tc.lottery.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private Map orderMap; //订单map

    private Bitmap bitCode; //支付二维码

    private Handler handler = new Handler();
    private Runnable runnable; //查询订单交易状态线程

    BuyDialog dialog; //支付弹框

    OutTicketDialog mBuyOkDialog; //支付成功弹框

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
        dialog = new BuyDialog(this);
        dialog.builder();
        dialog.setCancelable(false); //对话框点击不可消失
        dialog.setCanceledOnTouchOutside(false); //对话框点击不可消失

        mBuyOkDialog = new OutTicketDialog(this);
        mBuyOkDialog.builder();
        mBuyOkDialog.setCancelable(false); //对话框点击不可消失
        mBuyOkDialog.setCanceledOnTouchOutside(false); //对话框点击不可消失
    }

    @Override
    public void initdata() {
        Intent intent = this.getIntent();
        mTerminalLotteryInfos = (List<TerminalLotteryInfo>) intent.getSerializableExtra("TerminalLotteryInfo");
        terminalLotteryStatus = intent.getStringExtra("TerminalLotteryStatus");

//        mTxtTerminalStatus.setText("设备状态：" + getTerminalStatus(terminalLotteryStatus));
//        mTxtLotteryAmt.setText("单价" + mTerminalLotteryInfos.get(0).getLotteryAmt());

        mTerminalLotteryInfo = mTerminalLotteryInfos.get(0);

        mTxtSurplusNum.setText("剩余 " + mTerminalLotteryInfo.getSurplus() + " 张");

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
        Observable<BaseBean<OrderInfo>> register = mApi.prepOrder(requestBody).compose(RxUtil.<BaseBean<OrderInfo>>rxSchedulerHelper());
        mRxManager.add(register.subscribe(new Action1<BaseBean<OrderInfo>>() {
            @Override
            public void call(BaseBean<OrderInfo> baseBean) {
                stopProgressDialog();
                if (baseBean.getRespCode().equals("00")) {
                    OrderInfo orderInfo = baseBean.getResponseData();

                    if ("00".equals(orderInfo.getRespCode()) && !"".equals(orderInfo.getQrCode())) {
                        bitCode = QRCodeUtil.createQRCodeBitmap(orderInfo.getQrCode(), 300, 300);
                        startQueryOrder();
                        showDialog("" + lotteryNum, "" + lotteryTotalAmt, payType, bitCode);
                    }

                } else {
                    toastMessage(baseBean.getRespCode(), baseBean.getRespDesc());
                }
            }
        }, this));
    }

    private void queryOrder() {
        Map sendMap = Utils.getRequestData("queryOrder.Req");
        sendMap.put("merOrderId", orderMap.get("merOrderId"));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Utils.getSendMsg(sendMap));
        Observable<BaseBean<OrderInfo>> register = mApi.queryOrder(requestBody).compose(RxUtil.<BaseBean<OrderInfo>>rxSchedulerHelper());
        mRxManager.add(register.subscribe(new Action1<BaseBean<OrderInfo>>() {
            @Override
            public void call(BaseBean<OrderInfo> baseBean) {
//                stopProgressDialog();
                if (baseBean.getRespCode().equals("00")) {
                    OrderInfo orderInfo = baseBean.getResponseData();
                    if ("00".equals(orderInfo.getRespCode())) {

                        if ("1".equals(orderInfo.getOrderStatus())) { //交易成功关闭订单查询
                            handler.removeCallbacks(runnable);
                            ToastUtils.showToast(BuyActivity.this, "交易成功");
                        }
                    }
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
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(runnable, 5000);
    }

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
        dialog.setRecyclerViewNum(numList);
        dialog.setRecyclerViewAmt(amtList);
        dialog.setImagePayIcon(payType.equals("01") ? R.mipmap.zf_icon_alipay : R.mipmap.zf_icon_wx);
        dialog.setImageCode(bitCode);
        dialog.setBtBack(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showOkDialog();
            }
        });
        dialog.show();
    }

    private void showOkDialog() {
        mBuyOkDialog.startAnim();
        mBuyOkDialog.show();
    }

}
