package com.psylife.wrmvplibrary.webview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.psylife.wrmvplibrary.widget.ProgressView;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by tianchao on 2018/4/10.
 */

public class Html5WebView extends WebView {
    private ProgressView progressView;//进度条
    private Context context;

    private ChangeStatusListener changeStatusListener;

    public Html5WebView(Context context) {
        this(context, null);
    }

    public Html5WebView(Context context, AttributeSet attrs) {
        this(context, attrs, Resources.getSystem().getIdentifier("webViewStyle", "attr", "android"));
    }

    public Html5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void setChangeStatusListener(ChangeStatusListener changeStatusListener) {
        this.changeStatusListener = changeStatusListener;
    }

    private void init() {
        //初始化进度条
        progressView = new ProgressView(context);
        progressView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(context, 2)));
        progressView.setColor(Color.GREEN);
        progressView.setProgress(10);
        //把进度条加到Webview中
        addView(progressView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (this.getScrollY() <= 0) {
                    this.scrollTo(0, 1);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (changeStatusListener != null) {
            changeStatusListener.changeStatus(t);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public ProgressView getProgressView() {
        return progressView;
    }

    /**
     * dp转换成px
     *
     * @param context Context
     * @param dp      dp
     * @return px值
     */
    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}