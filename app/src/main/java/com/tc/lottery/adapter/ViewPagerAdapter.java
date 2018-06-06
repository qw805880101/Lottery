package com.tc.lottery.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by tianchao on 2018/5/2.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private int[] images;
    private Context mContext;

    public ViewPagerAdapter(Context mContext, int[] images) {
        this.images = images;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView item = new ImageView(mContext);
        item.setScaleType(ImageView.ScaleType.FIT_XY);
        item.setImageResource(images[position]);
        container.addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }
}
