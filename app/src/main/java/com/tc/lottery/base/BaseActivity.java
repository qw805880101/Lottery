package com.tc.lottery.base;

import com.psylife.wrmvplibrary.RxManager;
import com.psylife.wrmvplibrary.base.WRBaseActivity;
import com.psylife.wrmvplibrary.data.net.RxService;
import com.psylife.wrmvplibrary.utils.LogUtil;
import com.psylife.wrmvplibrary.utils.StatusBarUtil;
import com.psylife.wrmvplibrary.utils.ToastUtils;
import com.tc.lottery.MyApplication;
import com.tc.lottery.api.Api;
import com.tc.lottery.util.Utils;

import rx.functions.Action1;

/**
 * Created by admin on 2017/8/23.
 */

public abstract class BaseActivity extends WRBaseActivity implements Action1<Throwable> {

    public Api mApi = RxService.createApiDecode(Api.class, MyApplication.URL);

    public RxManager mRxManager = new RxManager();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxManager.clear();
    }
}
