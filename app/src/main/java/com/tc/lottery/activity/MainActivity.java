package com.tc.lottery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.psylife.wrmvplibrary.utils.ToastUtils;
import com.psylife.wrmvplibrary.utils.helper.RxUtil;
import com.tc.lottery.BuildConfig;
import com.tc.lottery.R;
import com.tc.lottery.base.BaseActivity;
import com.tc.lottery.bean.BaseBean;
import com.tc.lottery.bean.InitInfo;
import com.tc.lottery.util.GlideImageLoader;
import com.tc.lottery.util.Utils;
import com.tc.lottery.view.OutTicketDialog;
import com.tc.lottery.view.PaySuccessDialog;
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

public class MainActivity extends BaseActivity {
    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.bt_buy)
    ImageButton mBtBuy;
    @BindView(R.id.bt_prompt)
    ImageButton mBtPrompt;

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
        bannerImage.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526883546799&di=5acdd6bef77d8cc7f8db4dcb14dca803&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F13%2F21%2F22%2F71g58PICBQT_1024.jpg");
        bannerImage.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526883546798&di=34296c471267e8b590416f2dbd464a9d&imgtype=0&src=http%3A%2F%2Fpic2.16pic.com%2F00%2F07%2F67%2F16pic_767824_b.jpg");
        //设置图片集合
        mBanner.setImages(bannerImage);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        //设置轮播时间
        mBanner.setDelayTime(3000);
        //banner设置方法全部调用完毕时最后调用
        mBanner.start();
    }

    @Override
    public void initdata() {
        initStart();
    }

    @OnClick({R.id.bt_buy, R.id.bt_prompt})
    public void onViewClicked(View view) {
        if (!initStatus) {
            ToastUtils.showToast(this, "未初始化成功");
            return;
        }
        switch (view.getId()) {
            case R.id.bt_buy:
                Intent intent = new Intent(this, BuyActivity.class);
                intent.putExtra("TerminalLotteryInfo", (Serializable) initInfo.getTerminalLotteryDtos());
                intent.putExtra("TerminalLotteryStatus", initInfo.getTerminalStatus());
                startActivity(intent);
                break;
            case R.id.bt_prompt:
                startActivity(new Intent(this, HowActivity.class));
                break;
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
                    initStatus = true;
                } else {
                    toastMessage(initInfo.getRespCode(), initInfo.getRespDesc());
                }
            }
        }, this));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initStart();
    }
}
