package customview;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

public class DownLoadProgressbar {

    private Context mContext;

    private ProgressDialog pd;

    public static DownLoadProgressbar mDownLoadProgressbar;

    public static DownLoadProgressbar getDownLoadProgressbar(Context mContext) {
        if (mDownLoadProgressbar == null) {
            mDownLoadProgressbar = new DownLoadProgressbar(mContext);
        }
        return mDownLoadProgressbar;
    }

    public DownLoadProgressbar(Context mContext) {
        this.mContext = mContext;
        pd = new ProgressDialog(mContext);
        begin();
    }

    public void begin() {
        pd.setTitle("请稍等");
        //设置对话进度条样式为水平
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        //设置提示信息
        pd.setMessage("正在下载中......");
        //设置对话进度条显示在屏幕顶部
        pd.getWindow().setGravity(Gravity.CENTER);
        pd.setMax(100);
        pd.show();//调用show方法显示进度条对话框
    }

    public void setProgress(int value) {
        pd.setProgress(value);
        if (value == 100) {
            pd.dismiss();
        }
    }

}
