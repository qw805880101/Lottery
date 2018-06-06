package com.tc.lottery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.psylife.wrmvplibrary.utils.LogUtil;
import com.psylife.wrmvplibrary.utils.helper.RxUtil;
import com.psylife.wrmvplibrary.utils.timeutils.DateUtil;
import com.tc.lottery.R;
import com.tc.lottery.base.BaseActivity;
import com.tc.lottery.bean.BaseBean;
import com.tc.lottery.bean.OrderInfo;
import com.tc.lottery.bean.TerminalLotteryInfo;
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

public class APiActivity extends BaseActivity {

    private final static String ZFB = "01";
    private final static String WX = "02";
    private final static String SUCCESS = "1"; //出票成功
    private final static String ERROR = "2"; //出票异常

    List<TerminalLotteryInfo> mTerminalLotteryInfos = new ArrayList<>();
    @BindView(R.id.bt_zfb)
    Button mBtZfb;
    @BindView(R.id.iv)
    ImageView mIv;
    @BindView(R.id.bt_wx)
    Button mBtWx;
    @BindView(R.id.et_num)
    EditText mEtNum;
    @BindView(R.id.bt_success)
    Button mBtSuccess;
    @BindView(R.id.txt_terminal_status)
    TextView mTxtTerminalStatus;
    @BindView(R.id.txt_lottery_amt)
    TextView mTxtLotteryAmt;
    @BindView(R.id.image_code)
    ImageView imageCode;
    @BindView(R.id.txt_pay_status)
    TextView mTxtPayStatus;
    @BindView(R.id.txt)
    TextView mtxt;

    private String terminalLotteryStatus;

    private TerminalLotteryInfo mTerminalLotteryInfo;

    private int num;

    private Map orderMap;

    private Handler handler = new Handler();
    private Runnable runnable;


    @Override
    public int getLayoutId() {
        return R.layout.activity_api;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

    }

    @Override
    public void initdata() {
        Intent intent = this.getIntent();
        mTerminalLotteryInfos = (List<TerminalLotteryInfo>) intent.getSerializableExtra("TerminalLotteryInfo");
        terminalLotteryStatus = intent.getStringExtra("TerminalLotteryStatus");

        mTxtTerminalStatus.setText("设备状态：" + getTerminalStatus(terminalLotteryStatus));
        mTxtLotteryAmt.setText("单价" + mTerminalLotteryInfos.get(0).getLotteryAmt());

        mTerminalLotteryInfo = mTerminalLotteryInfos.get(0);
    }

    /**
     * 获取设备状态
     *
     * @param code
     * @return
     */
    private String getTerminalStatus(String code) {
        if ("0".equals(code))
            return "待激活";
        if ("1".equals(code))
            return "已激活";
        if ("2".equals(code))
            return "待维修";
        if ("3".equals(code))
            return "已暂停";
        if ("4".equals(code))
            return "设备无票";
        return "";
    }

    /**
     * 获取订单状态
     *
     * @param code
     * @return
     */
    private String getOrderStatus(String code) {
        if ("0".equals(code))
            return "未处理";
        if ("1".equals(code))
            return "成功";
        if ("2".equals(code))
            return "失败";
        if ("3".equals(code))
            return "处理中";
        return "";
    }

    @OnClick({R.id.bt_zfb, R.id.bt_wx, R.id.bt_success})
    public void onViewClicked(View view) {
        num = Integer.parseInt(mEtNum.getText().toString().trim());
        mTerminalLotteryInfo.setNum("" + num);
        switch (view.getId()) {
            case R.id.bt_zfb:
                prepOrder(ZFB);
                break;
            case R.id.bt_wx:
                prepOrder(WX);
                break;
            case R.id.bt_success:
                break;
        }
    }

    /**
     * 下单
     *
     * @param payType 01 支付宝  02 微信
     */
    private void prepOrder(String payType) {
        startProgressDialog(this);
        orderMap = Utils.getRequestData("prepOrder.Req");
        orderMap.put("merOrderId", DateUtil.format(new Date(), "YYYYMMDDhhmmss") + payType);
        orderMap.put("merOrderTime", DateUtil.format(new Date(), "YYYYMMDDhhmmss"));
        orderMap.put("orderAmt", "" + (num * Double.parseDouble(mTerminalLotteryInfo.getLotteryAmt())));
        orderMap.put("terminalLotteryDtos", mTerminalLotteryInfos);
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
                        imageCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(orderInfo.getQrCode(), 300, 300));
                        startQueryOrder();
                    }

                } else {
                    toastMessage(baseBean.getRespCode(), baseBean.getRespDesc());
                }
            }
        }, this));
    }

    /**
     * 交易订单查询
     */
    int a = 0;

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
                        mtxt.setText("查询次数：" + (a++));
                        mTxtPayStatus.setText("订单状态：" + getOrderStatus(orderInfo.getOrderStatus()) +
                                "\n订单描述" + orderInfo.getOrderDesc() +
                                "\n应答描述" + orderInfo.getRespDesc());
                        LogUtil.d(orderInfo.getOrderStatus());
                        if ("1".equals(orderInfo.getOrderStatus())) { //交易成功关闭订单查询
                            handler.removeCallbacks(runnable);
                            outTicket(SUCCESS);
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
    public void startQueryOrder() {
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

    /**
     * 更新出票状态
     *
     * @param ticketStatus 1 出票成功
     *                     2 出票异常
     */
    private void outTicket(String ticketStatus) {
        TerminalLotteryInfo terminalLotteryInfo = mTerminalLotteryInfos.get(0);
        terminalLotteryInfo.setNum("" + num);
        terminalLotteryInfo.setTicketStatus(ticketStatus);

        List<TerminalLotteryInfo> lotteryInfos = new ArrayList<>();
        lotteryInfos.add(terminalLotteryInfo);

        Map sendMap = Utils.getRequestData("outTicket.Req");
        sendMap.put("merOrderId", orderMap.get("merOrderId"));
        sendMap.put("terminalLotteryDtos", lotteryInfos);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Utils.getSendMsg(sendMap));
        Observable<BaseBean<BaseBean>> register = mApi.outTicket(requestBody).compose(RxUtil.<BaseBean<BaseBean>>rxSchedulerHelper());
        mRxManager.add(register.subscribe(new Action1<BaseBean<BaseBean>>() {
            @Override
            public void call(BaseBean<BaseBean> baseBean) {
//                stopProgressDialog();
                if (baseBean.getRespCode().equals("00")) {
                    BaseBean beanInfo = baseBean.getResponseData();
                    if ("00".equals(beanInfo.getRespCode())) {
                        LogUtil.d("状态更新成功");
                    }
                } else {
                    toastMessage(baseBean.getRespCode(), baseBean.getRespDesc());
                }
            }
        }, this));
    }

}
