package com.tc.lottery.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;

import com.psylife.wrmvplibrary.utils.SpUtils;
import com.psylife.wrmvplibrary.utils.ToastUtils;
import com.psylife.wrmvplibrary.utils.helper.RxUtil;
import com.tc.lottery.BuildConfig;
import com.tc.lottery.R;
import com.tc.lottery.adapter.ViewPagerAdapter;
import com.tc.lottery.base.BaseActivity;
import com.tc.lottery.bean.BaseBean;
import com.tc.lottery.bean.InitInfo;
import com.tc.lottery.util.Utils;

import java.io.Serializable;
import java.util.Map;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Action1;
import util.UpdateAppUtils;

import static android.os.Build.VERSION_CODES.M;


/**
 * Created by tianchao on 2018/4/3.
 */

public class InitActivity extends BaseActivity {

    @BindView(R.id.vp)
    ViewPager mViewPager;

    public static final int EXTERNAL_STORAGE_REQ_CAMERA_CODE = 10;

    private ViewPagerAdapter mViewPagerAdapter;

    private int[] images = {};

    private static final String LOGIN_STATUS = "loginStatus";

    Handler mHandler = new Handler();

    @Override
    public View getTitleView() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_init;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(3000);
//                    Intent intent = new Intent(InitActivity.this, UserLongin_Activity.class);
//                    InitActivity.this.startActivity(intent);
//                    InitActivity.this.finish();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        requestCameraPerm();

        mViewPagerAdapter = new ViewPagerAdapter(this, images);

        mViewPager.setAdapter(mViewPagerAdapter);//对viewpager设置数据适配器

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //当是最后一个页面的时候将按钮显示出来
                if (position == images.length - 1) {
                } else {
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        boolean loginStatus = SpUtils.getBoolean(this, LOGIN_STATUS, false);
        if (loginStatus) {
            mViewPager.setVisibility(View.VISIBLE);
        } else {
            mViewPager.setVisibility(View.GONE);
        }
    }

    @Override
    public void initdata() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            return false;
        }
        return false;
    }

    private void requestCameraPerm() {
        if (android.os.Build.VERSION.SDK_INT >= M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_REQ_CAMERA_CODE);
            } else {
                start();
            }
        } else {
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_REQ_CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                start();
            } else {
                ToastUtils.showToast(this, "未获取权限");
                start();
            }
        }
    }

    private void start() {
//        startProgressDialog(this);
//        Map sendMap = Utils.getRequestData("terminalInit.Req");
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Utils.getSendMsg(sendMap));
//        Observable<BaseBean<InitInfo>> register = mApi.init(requestBody).compose(RxUtil.<BaseBean<InitInfo>>rxSchedulerHelper());
//        mRxManager.add(register.subscribe(new Action1<BaseBean<InitInfo>>() {
//            @Override
//            public void call(BaseBean<InitInfo> baseBean) {
//                stopProgressDialog();
//                if (baseBean.getRespCode().equals("00")) {
//                    InitInfo initInfo = baseBean.getResponseData();
//                    if (!initInfo.getUpdateStatus().equals("00")) { //需要更新
//                        UpdateAppUtils.from(InitActivity.this)
//                                .serverVersionName(initInfo.getVersion()) //服务器versionName
//                                .serverVersionCode(BuildConfig.VERSION_CODE + 1)
//                                .apkPath(initInfo.getUpdateAddress()) //最新apk下载地址
//                                .updateInfo(initInfo.getMsgExt())  //更新日志信息 String
////                                    .downloadBy(UpdateAppUtils.DOWNLOAD_BY_BROWSER) //下载方式：app下载、手机浏览器下载。默认app下载
//                                .isForce(initInfo.getUpdateStatus().equals("01") ? true : false) //是否强制更新，默认false 强制更新情况下用户不同意更新则不能使用app
//                                .update();
//                    }
//                    initInfo.setTerminalStatus("00");
//                    if ("00".equals(initInfo.getRespCode())) {
//                        Intent intent = new Intent(InitActivity.this, APiActivity.class);
//                        intent.putExtra("TerminalLotteryInfo", (Serializable) initInfo.getTerminalLotteryDtos());
//                        intent.putExtra("TerminalLotteryStatus", initInfo.getTerminalStatus());
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        toastMessage(initInfo.getRespCode(), initInfo.getRespDesc());
//                    }
//
//                } else {
//                    toastMessage(baseBean.getRespCode(), baseBean.getRespDesc());
//                }
//            }
//        }, InitActivity.this));
    }
}
