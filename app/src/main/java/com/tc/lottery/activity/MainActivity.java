package com.tc.lottery.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.psylife.wrmvplibrary.utils.ToastUtils;
import com.psylife.wrmvplibrary.utils.helper.RxUtil;
import com.tc.lottery.BuildConfig;
import com.tc.lottery.MyApplication;
import com.tc.lottery.R;
import com.tc.lottery.base.BaseActivity;
import com.tc.lottery.bean.BaseBean;
import com.tc.lottery.bean.InitInfo;
import com.tc.lottery.util.GlideImageLoader;
import com.tc.lottery.util.MotorSlaveUtils;
import com.tc.lottery.util.Utils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Action1;
import util.UpdateAppUtils;

import static com.tc.lottery.util.MotorSlaveUtils.QUERY_STATUS;

public class MainActivity extends BaseActivity {
    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.bt_buy)
    ImageButton mBtBuy;
    @BindView(R.id.bt_prompt)
    ImageButton mBtPrompt;
    @BindView(R.id.banner_image)
    ImageView mImageView;
    @BindView(R.id.main_sw)
    SwipeRefreshLayout mRefresh;

    private List<String> bannerImage = new ArrayList<>();

    private boolean initStatus = false; //初始化状态  true 成功 false 失败

    private InitInfo initInfo; //初始化参数

    @Override
    public View getTitleView() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        bannerImage.add("https://www.baidu.com/img/bd_logo1.png?qua=high&where=super");
//        //设置图片集合
        mBanner.setImages(bannerImage);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        //设置轮播时间
        mBanner.setDelayTime(3000);
        //banner设置方法全部调用完毕时最后调用
        mBanner.start();

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefresh.setRefreshing(false);
                initStart();
            }
        });
    }

    @Override
    public void initdata() {
        initStart();
    }

    final static int COUNTS = 5;//点击次数
    final static long DURATION = 3 * 1000;//规定有效时间
    long[] mHits = new long[COUNTS];

    @OnClick({R.id.bt_buy, R.id.bt_prompt, R.id.image_set})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.image_set) {
            /**
             * 实现双击方法
             * src 拷贝的源数组
             * srcPos 从源数组的那个位置开始拷贝.
             * dst 目标数组
             * dstPos 从目标数组的那个位子开始写数据
             * length 拷贝的元素的个数
             */
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            //实现左移，然后最后一个位置更新距离开机的时间，如果最后一个时间和最开始时间小于DURATION，即连续5次点击
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
//                String tips = "您已在[" + DURATION + "]ms内连续点击【" + mHits.length + "】次了！！！";
//                ToastUtils.showToast(MainActivity.this, tips);
                Intent intent = new Intent(this, SetActivity.class);
                startActivity(intent);
            }
        } else {
            if (!initStatus) {
                ToastUtils.showToast(this, "未初始化成功, 请重试");
                initStart();
                return;
            }
            switch (view.getId()) {
                case R.id.bt_buy:
                    Intent intent = new Intent(MainActivity.this, Buy_2Activity.class);
                    startActivity(intent);
                    break;
                case R.id.bt_prompt:
                    startActivity(new Intent(this, HowActivity.class));
                    break;
            }
        }
    }

    /**
     * 初始化接口
     */
    private void initStart() {
        startProgressDialog(this);
        Map sendMap = Utils.getRequestData("terminalInit.Req");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Utils.getSendMsg(sendMap));
        Observable<InitInfo> register = mApi.init(requestBody).compose(RxUtil.<InitInfo>rxSchedulerHelper());
        mRxManager.add(register.subscribe(new Action1<InitInfo>() {
            @Override
            public void call(InitInfo baseBean) {
                stopProgressDialog();
                initInfo = baseBean;
                if ("00".equals(initInfo.getRespCode())) {
//                        ToastUtils.showToast(MainActivity.this, "初始化成功");
                    if (initInfo.getImgs() != null) {
                        if (initInfo.getImgs().size() > 0) {
                            bannerImage.clear();
                            for (int i = 0; i < initInfo.getImgs().size(); i++) {
                                bannerImage.add(initInfo.getImgs().get(i));
                            }
                            //设置图片集合
                            mBanner.setImages(bannerImage);
                            mBanner.setVisibility(View.VISIBLE);
                            mImageView.setVisibility(View.GONE);
                            mBanner.start();
                        }
                    }
//                    MyApplication.mTerminalLotteryInfos = initInfo.getTerminalLotteryDtos();

                    if (initInfo.getTerminalLotteryDtos() != null) {
                        for (int i = 0; i < initInfo.getTerminalLotteryDtos().size(); i++) {
                            if (initInfo.getTerminalLotteryDtos().get(i).getBoxId().equals("1")) {
                                MyApplication.mTerminalLotteryInfo = initInfo.getTerminalLotteryDtos().get(i);
                            }
                        }
                    }

                    MyApplication.terminalLotteryStatus = initInfo.getTerminalStatus();
                    if (!initInfo.getUpdateStatus().equals("00")) { //需要更新
                        UpdateAppUtils.from(MainActivity.this)
                                .serverVersionName(initInfo.getVersion()) //服务器versionName
                                .serverVersionCode(BuildConfig.VERSION_CODE + 1)
                                .apkPath(initInfo.getUpdateAddress()) //最新apk下载地址
                                .updateInfo(initInfo.getMsgExt())  //更新日志信息 String
//                                    .downloadBy(UpdateAppUtils.DOWNLOAD_BY_BROWSER) //下载方式：app下载、手机浏览器下载。默认app下载
                                .isForce(initInfo.getUpdateStatus().equals("01") ? true : false) //是否强制更新，默认false 强制更新情况下用户不同意更新则不能使用app
                                .update();
                    }

                    if (initInfo.getUpdateStatus().equals("01")) {
                        return;
                    }

                    initStatus = true;
                } else {
                    toastMessage(initInfo.getRespCode(), initInfo.getRespDesc());
                }
            }
        }, this));
    }
}
