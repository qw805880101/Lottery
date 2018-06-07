package com.psylife.wrmvplibrary.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by tc on 2017/6/16.
 */

public class MyWebView {

    private Html5WebView mWebView;
    private Context context;
    private ChangeStatusListener changeStatusListener;
    private Take take;

    public MyWebView(Context context, Html5WebView mWebView) {
        this.mWebView = mWebView;
        this.context = context;
    }

    public MyWebView(Context context, Html5WebView mWebView, Take take) {
        this.mWebView = mWebView;
        this.context = context;
        this.take = take;
    }

    public void setChangeStatusListener(ChangeStatusListener changeStatusListener) {
        this.changeStatusListener = changeStatusListener;
    }

    public void webSetting() {
        mWebView.setChangeStatusListener(changeStatusListener);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        if (take != null) {
            mWebView.setWebChromeClient(new WebChromeClient() {
                //Android > 5.0 调用这个方法
                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
                    take.setPhoto(valueCallback, null);
                    return true;
                }

                // Android > 4.1.1 调用这个方法
                public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                            String acceptType, String capture) {
                    take.setPhoto(null, uploadMsg);
                }

                // 3.0 + 调用这个方法
                public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                            String acceptType) {
                    take.setPhoto(null, uploadMsg);

                }

                // Android < 3.0 调用这个方法
                public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                    take.setPhoto(null, uploadMsg);
                }

                /**
                 * 获取标题title
                 * @param view
                 * @param title
                 */
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    take.setTitle(title);
                }

                /**
                 * 进度条
                 * @param view
                 * @param newProgress
                 */
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress == 100) {
                        //加载完毕进度条消失
                        mWebView.getProgressView().setVisibility(View.GONE);
                    } else {
                        //更新进度
                        mWebView.getProgressView().setProgress(newProgress);
                    }
                    super.onProgressChanged(view, newProgress);
                }
            });
        } else {
            mWebView.setWebChromeClient(new WebChromeClient() {
                /**
                 * 进度条
                 * @param view
                 * @param newProgress
                 */
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress == 100) {
                        //加载完毕进度条消失
                        mWebView.getProgressView().setVisibility(View.GONE);
                    } else {
                        //更新进度
                        mWebView.getProgressView().setProgress(newProgress);
                    }
                    super.onProgressChanged(view, newProgress);
                }
            });
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                System.out.println("url:" + url);
//                view.loadUrl(url);
                if (take != null)
                    return take.getUrl(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object obj, String name) {
        mWebView.addJavascriptInterface(obj, name);
    }

    public WebView getWebView() {
        return mWebView;
    }

    public interface Take {
        void setPhoto(ValueCallback<Uri[]> filePathCallback, ValueCallback<Uri> uploadMsg);

        void setTitle(String title);

        boolean getUrl(String url);

    }
}
