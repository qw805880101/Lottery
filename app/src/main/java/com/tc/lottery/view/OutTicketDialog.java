package com.tc.lottery.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.tc.lottery.R;

/**
 * 出票中弹框
 */
public class OutTicketDialog {

    private Context context;
    private Dialog dialog;

    /* 彩票动画 */
    private ImageView imageTicket;
    /* 彩票数量 */
    private TextView txtTicketNum;

    private Display display;

    private int outTicketNum = 1; //已出票
    private int ticketNum = 10; //总票数

    private OutTicketSuccess mOutTicketSuccess;

    public OutTicketDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    @SuppressWarnings("deprecation")
    public OutTicketDialog builder() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_out_ticket, null);
        view.setMinimumWidth(display.getWidth());

        imageTicket = view.findViewById(R.id.image_ticket);

        txtTicketNum = view.findViewById(R.id.txt_ticket_num);

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

    public void startAnim() {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.chupiao_bg_piao);
        anim = new TranslateAnimation(0.0f, 0.0f, -bitmap.getHeight() - 10, 0);
        anim.setDuration(1500);
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
            }
        });
        startAnimation();
    }

    Runnable mAnimRunnable;

    Runnable mTicketNumRunnable;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                imageTicket.startAnimation(anim);
            }
            if (msg.what == 1) {
                if (outTicketNum <= ticketNum) {
                    txtTicketNum.setText("支付成功！正在出票...（" + outTicketNum + "/" + ticketNum + "）");
                    outTicketNum++;
                } else {
                    mHandler.removeCallbacks(mTicketNumRunnable);
                    mHandler.removeCallbacks(mAnimRunnable);
                    mOutTicketSuccess.onSuccess(outTicketNum - 1);
                }
            }
        }
    };

    private void startAnimation() {
        mAnimRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
                mHandler.postDelayed(this, 2000);
            }
        };
        mHandler.postDelayed(mAnimRunnable, 0);
    }

    /**
     * 关闭动画
     */
    private void closeAnimation() {
        mHandler.removeCallbacks(mAnimRunnable);
    }

    public void setTicketNum(int ticketNum) {
        this.outTicketNum = 1;
        this.ticketNum = ticketNum;
        txtTicketNum.setText("支付成功！正在出票...（1/" + ticketNum + "）");
        startTicketNum();
    }

    private void startTicketNum() {
        mTicketNumRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(1);
                mHandler.postDelayed(this, 1500);
            }
        };
        mHandler.postDelayed(mTicketNumRunnable, 1500);
    }

    public OutTicketDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public OutTicketDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }


    public void setOutTicketSuccess(OutTicketSuccess mOutTicketSuccess) {
        this.mOutTicketSuccess = mOutTicketSuccess;
    }

    public interface OutTicketSuccess {
        /**
         * 出票完成
         *
         * @param outNum 出票数
         */
        void onSuccess(int outNum);
    }
}
