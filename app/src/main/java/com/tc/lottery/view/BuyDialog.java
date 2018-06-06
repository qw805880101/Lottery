package com.tc.lottery.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tc.lottery.R;
import com.tc.lottery.adapter.DialogBuyAdapter;

import java.util.List;

public class BuyDialog {

    private Context context;
    private Dialog dialog;
    /* 支付icon， 支付二维码 */
    private ImageView imagePayIcon, imageCode;
    /* 返回 */
    private Button btBack;
    /* 数量，金额 */
    private LinearLayout mLinearLayoutNum, mLinearLayoutAmt;

    private Display display;

    private DialogBuyAdapter dialogBuyAdapter;

    private OnSheetItemClickListener mClickListener;

    private int backNum = 20; //返回按钮读秒

    private Runnable runnable;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (backNum > 0) {
                    btBack.setText("返回（" + backNum + ")");
                } else {
                    mHandler.removeCallbacks(runnable);
                    btBack.setEnabled(true);
                    btBack.setText("返回");
                }
            }
        }
    };

    public BuyDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    @SuppressWarnings("deprecation")
    public BuyDialog builder() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_buy, null);
        view.setMinimumWidth(display.getWidth());

//        mRecyclerViewNum = view.findViewById(R.id.recycler_num);
//        mRecyclerViewNum.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        mLinearLayoutNum = view.findViewById(R.id.lin_num);
        mLinearLayoutAmt = view.findViewById(R.id.lin_amt);

        imagePayIcon = view.findViewById(R.id.image_pay_icon);

        imageCode = view.findViewById(R.id.image_code);

        btBack = view.findViewById(R.id.bt_back);

        dialog = new Dialog(context, R.style.buyDialog);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        return this;
    }

    public BuyDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public BuyDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    /**
     * 设置购买数量
     *
     * @param list
     */
    public void setRecyclerViewNum(List<String> list) {
        mLinearLayoutNum.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(getImageNum(list.get(i)));
            mLinearLayoutNum.addView(imageView);
        }
    }

    /**
     * 设置付款金额
     *
     * @param list
     */
    public void setRecyclerViewAmt(List<String> list) {
        mLinearLayoutAmt.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(getImageNum(list.get(i)));
            mLinearLayoutAmt.addView(imageView);
        }
    }

    /**
     * 设置支付icon
     *
     * @param icon
     */
    public void setImagePayIcon(int icon) {
        imagePayIcon.setImageResource(icon);
    }

    /**
     * 设置支付二维码
     *
     * @param bitmap
     */
    public void setImageCode(Bitmap bitmap) {
        imageCode.setImageBitmap(bitmap);
    }

    /**
     * 返回按钮
     */
    public void setBtBack(OnClickListener onClickListener) {
        btBack.setEnabled(false);
        btBack.setOnClickListener(onClickListener);
        startBackNum();
    }

    /**
     * 开始返回倒计时
     */
    private void startBackNum() {
        backNum = 20;
        btBack.setText("返回" + backNum);
        runnable = new Runnable() {
            @Override
            public void run() {
                backNum--;
                mHandler.sendEmptyMessage(0);
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(runnable, 1000);
    }

    public void setOnClick(OnSheetItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface OnSheetItemClickListener {
        void onClick(int which);
    }

    /**
     * 获取数字图片
     *
     * @param s
     * @return
     */
    public int getImageNum(String s) {
        if ("0".equals(s)) {
            return R.mipmap.num_0;
        }
        if ("1".equals(s)) {
            return R.mipmap.num_1;
        }
        if ("2".equals(s)) {
            return R.mipmap.num_2;
        }
        if ("3".equals(s)) {
            return R.mipmap.num_3;
        }
        if ("4".equals(s)) {
            return R.mipmap.num_4;
        }
        if ("5".equals(s)) {
            return R.mipmap.num_5;
        }
        if ("6".equals(s)) {
            return R.mipmap.num_6;
        }
        if ("7".equals(s)) {
            return R.mipmap.num_7;
        }
        if ("8".equals(s)) {
            return R.mipmap.num_8;
        }
        if ("9".equals(s)) {
            return R.mipmap.num_9;
        }
        if (".".equals(s)) {
            return R.mipmap.num_dian;
        }
        return 0;
    }

}
