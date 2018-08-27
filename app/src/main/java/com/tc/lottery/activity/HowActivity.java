package com.tc.lottery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tc.lottery.R;
import com.tc.lottery.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HowActivity extends BaseActivity {

    @BindView(R.id.txt_buy)
    TextView mTxtBuy;
    @BindView(R.id.txt_back)
    TextView mTxtBack;

    @Override
    public View getTitleView() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_how;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

    }

    @Override
    public void initdata() {

    }

    @OnClick({R.id.txt_buy, R.id.txt_back, R.id.bt_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_buy:
                startActivity(new Intent(this, Buy_2Activity.class));
                finish();
                break;
            case R.id.txt_back:
                finish();
                break;
            case R.id.bt_device:
                startActivity(new Intent(this, DeviceActivity.class));
                break;
        }
    }
}
