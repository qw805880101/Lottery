package com.tc.lottery.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.psylife.wrmvplibrary.utils.StringUtils;
import com.tc.lottery.MyApplication;
import com.tc.lottery.R;
import com.tc.lottery.base.BaseActivity;
import com.tc.lottery.util.GetUUID;
import com.tc.lottery.util.QRCodeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceActivity extends BaseActivity {

    @BindView(R.id.image_device_code)
    ImageView mImageDeviceCode;
    @BindView(R.id.txt_device_code)
    TextView mTxtDeviceCode;
    @BindView(R.id.bt_back)
    Button mBtBack;

    String uuid;

    @Override
    public View getTitleView() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_device_info;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        if (StringUtils.isEmpty(MyApplication.UUID)) {
            uuid = "未获取到设备号";
        } else {
            uuid = MyApplication.UUID;
            mImageDeviceCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(uuid, 300, 300));
        }
        mTxtDeviceCode.setText(uuid);
    }

    @Override
    public void initdata() {

    }

    @OnClick(R.id.bt_back)
    public void onViewClicked() {
        this.finish();
    }
}
