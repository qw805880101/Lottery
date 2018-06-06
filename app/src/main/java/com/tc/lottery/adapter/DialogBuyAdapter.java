package com.tc.lottery.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tc.lottery.R;

import java.util.List;

/**
 * 数量
 * <p>
 * Created by tc on 2017/8/20.
 */

public class DialogBuyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public DialogBuyAdapter(List<String> list) {
        super(R.layout.item_num, list);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, String mInfo) {
//        baseViewHolder.setText(R.id.txt_debit_card, mInfo.getName());
//        if (null != mInfo.getFlag() && mInfo.getFlag().equals("1")) {
//            baseViewHolder.setVisible(R.id.view_line, false);
//        }
//
//        if (mInfo.getTextColor() != 0){
//            baseViewHolder.setTextColor(R.id.txt_debit_card, mInfo.getTextColor());
//        }
        ImageView i = baseViewHolder.getView(R.id.image_num);
        i.setImageResource(getImageNum(mInfo));
    }

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
