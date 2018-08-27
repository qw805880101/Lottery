package com.tc.lottery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.psylife.wrmvplibrary.bean.BaseBeanInfo;
import com.psylife.wrmvplibrary.utils.HashUtil.MD5;
import com.psylife.wrmvplibrary.utils.StringUtils;
import com.psylife.wrmvplibrary.utils.TitleBuilder;
import com.psylife.wrmvplibrary.utils.ToastUtils;
import com.psylife.wrmvplibrary.utils.helper.RxUtil;
import com.tc.lottery.BuildConfig;
import com.tc.lottery.MyApplication;
import com.tc.lottery.R;
import com.tc.lottery.base.BaseActivity;
import com.tc.lottery.bean.InitInfo;
import com.tc.lottery.util.Base64Class;
import com.tc.lottery.util.Md5Util;
import com.tc.lottery.util.StringUtil;
import com.tc.lottery.util.Utils;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Action1;
import util.UpdateAppUtils;

public class SetActivity extends BaseActivity implements OnClickListener {
    @BindView(R.id.txt_device)
    TextView mTxtDevice;
    @BindView(R.id.txt_exit)
    TextView mTxtExit;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.lin_password)
    LinearLayout mLinPassword;
    @BindView(R.id.lin_exit)
    LinearLayout mLinExit;
    @BindView(R.id.bt_sign_out)
    Button mBtSignOut;
    @BindView(R.id.bt_cancel)
    Button mBtCancel;
    @BindView(R.id.bt_back)
    Button mBtBack;

    @Override
    public View getTitleView() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_set;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mTxtDevice.setOnClickListener(this);
        mTxtExit.setOnClickListener(this);
        mBtSignOut.setOnClickListener(this);
        mBtCancel.setOnClickListener(this);
        mBtBack.setOnClickListener(this);
    }

    @Override
    public void initdata() {

    }

    private void signOut() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
        System.exit(0);
    }

    /**
     * 退出接口
     */
    private void exit() {
        String password = mEtPassword.getText().toString().trim();
        if (StringUtils.isEmpty(password)) {
            ToastUtils.showToast(this, "请输入管理员密码");
            return;
        }
        try {
            startProgressDialog(this);
            Map sendMap = Utils.getRequestData("terminalLogin.Req");
            sendMap.put("password", StringUtil.byteArrayToHexString(Md5Util.MD5(password)));
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Utils.getSendMsg(sendMap));
            Observable<BaseBeanInfo> register = mApi.exit(requestBody).compose(RxUtil.<BaseBeanInfo>rxSchedulerHelper());
            mRxManager.add(register.subscribe(new Action1<BaseBeanInfo>() {
                @Override
                public void call(BaseBeanInfo baseBean) {
                    stopProgressDialog();
                    if ("00".equals(baseBean.getRespCode())) {
                        signOut();
                    } else {
                        toastMessage(baseBean.getRespCode(), baseBean.getRespDesc());
                    }
                }
            }, this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        if (view == mTxtDevice) {
            Intent intent = new Intent(this, DeviceActivity.class);
            startActivity(intent);
        }
        if (view == mTxtExit) {
            mLinPassword.setVisibility(View.VISIBLE);
            mLinExit.setVisibility(View.GONE);
        }
        if (view == mBtSignOut) {
            exit();
        }
        if (view == mBtCancel) {
            mLinPassword.setVisibility(View.GONE);
            mLinExit.setVisibility(View.VISIBLE);
        }
        if (view == mBtBack) {
            finish();
        }
    }
}
