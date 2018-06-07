package com.tc.lottery.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tc.lottery.R;
import com.tc.lottery.activity.HowActivity;

/**
 * 交易完成弹框
 */
public class PaySuccessDialog {

    private Context context;
    private Dialog dialog;

    /* 彩票动画 */
    private ImageView imageTicket;
    /* 彩票数量 */
    private TextView txtTicketNum;

    private SuccessView mSuccessView;

    private Button btBack, btHow;

    private Display display;

    private int backNum = 20; //返回按钮读秒

    private OnSheetItemClickListener mClickListener;

    Runnable mBackRunnable;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (backNum > 0) {
                    btBack.setText("返回（" + backNum + ")");
                } else {
                    mHandler.removeCallbacks(mBackRunnable);
                    btBack.setEnabled(true);
                    btBack.setText("返回");
                }
            }
        }
    };

    public PaySuccessDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    @SuppressWarnings("deprecation")
    public PaySuccessDialog builder() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_pay_ok, null);
        view.setMinimumWidth(display.getWidth());

        imageTicket = view.findViewById(R.id.image_pay_ticket);

        txtTicketNum = view.findViewById(R.id.txt_ticket_num);

        mSuccessView = view.findViewById(R.id.success);

        btBack = view.findViewById(R.id.bt_back);

        btHow = view.findViewById(R.id.bt_how);

        btHow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, HowActivity.class));
            }
        });

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

    TranslateAnimation anim;

    /**
     * 开始出票完成动画
     */
    public void startAnim() {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.chupiao2_piao);
        anim = new TranslateAnimation(0.0f, 0.0f, -bitmap.getHeight() - 10, 0);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageTicket.clearAnimation();
                mSuccessView.setVisibility(View.VISIBLE);
            }
        });
        imageTicket.startAnimation(anim);
    }

    /**
     * 设置总出票数
     *
     * @param ticketNum
     */
    public void setTicketNum(int ticketNum) {
        txtTicketNum.setText("" + ticketNum);
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
        btBack.setText("返回(" + backNum);
        mBackRunnable = new Runnable() {
            @Override
            public void run() {
                backNum--;
                mHandler.sendEmptyMessage(0);
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mBackRunnable, 1000);
    }

    public PaySuccessDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public PaySuccessDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }


    public void setOnClick(OnSheetItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface OnSheetItemClickListener {
        void onClick(int which);
    }
}
