package com.tc.lottery.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.psylife.wrmvplibrary.RxManager;
import com.psylife.wrmvplibrary.base.WRBaseActivity;
import com.psylife.wrmvplibrary.data.net.RxService;
import com.psylife.wrmvplibrary.utils.LogUtil;
import com.psylife.wrmvplibrary.utils.StatusBarUtil;
import com.psylife.wrmvplibrary.utils.ToastUtils;
import com.tc.lottery.MyApplication;
import com.tc.lottery.api.Api;
import com.tc.lottery.util.CountTimer;
import com.tc.lottery.util.Utils;

import rx.functions.Action1;

/**
 * Created by admin on 2017/8/23.
 */

public abstract class BaseActivity extends WRBaseActivity implements Action1<Throwable> {

    public Api mApi = RxService.createApiDecode(Api.class, MyApplication.URL);

    public RxManager mRxManager = new RxManager();

    private CountTimer countTimerView;

    public boolean isBuyActivity = false; //判断是否为购买彩票界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    /**
     * 主要的方法，重写dispatchTouchEvent
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            //获取触摸动作，如果ACTION_UP，计时开始。
            case MotionEvent.ACTION_UP:
                countTimerView.start();
                break;
            //否则其他动作计时取消
            default:
                countTimerView.cancel();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void init() {
        //初始化CountTimer，设置倒计时为3分钟。
        countTimerView = new CountTimer(180000, 1000, this);
    }

    private void timeStart() {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                countTimerView.start();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        countTimerView.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBuyActivity)
            timeStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxManager.clear();
    }

    /**
     * 显示错误日志
     *
     * @param code
     * @param msg
     */
    public void toastMessage(String code, String msg) {
        if (code.equals("1006")) {

        }
        ToastUtils.showToast(this, msg);
    }

    @Override
    public void call(Throwable throwable) {
        LogUtil.d(throwable.getMessage());
        stopProgressDialog();
        ToastUtils.showToast(this, Utils.getErrorMessage(throwable));
    }

}
