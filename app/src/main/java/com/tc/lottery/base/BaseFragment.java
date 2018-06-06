package com.tc.lottery.base;

import android.view.View;

import com.psylife.wrmvplibrary.RxManager;
import com.psylife.wrmvplibrary.base.WRBaseLazyFragment;
import com.psylife.wrmvplibrary.data.net.RxService;
import com.psylife.wrmvplibrary.utils.StatusBarUtil;
import com.psylife.wrmvplibrary.utils.ToastUtils;
import com.tc.lottery.MyApplication;
import com.tc.lottery.api.Api;

import rx.functions.Action1;

/**
 * Created by admin on 2017/8/30.
 */

public abstract class BaseFragment extends WRBaseLazyFragment implements Action1<Throwable> {

    public Api mXuanXingApi = RxService.createApi(Api.class, MyApplication.URL);

    public RxManager mRxManager = new RxManager();

    @Override
    public void setStatusBarColor() {
        StatusBarUtil.setTranslucent(this.getActivity());
    }

    @Override
    public View getTitleView() {
        return null;
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
        ToastUtils.showToast(this.getContext(), msg);
    }

    @Override
    public void call(Throwable throwable) {
        System.out.println(throwable.getMessage());
        stopProgressDialog();
        ToastUtils.showToast(this.getContext(), "网络错误");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRxManager.clear();
    }
}
