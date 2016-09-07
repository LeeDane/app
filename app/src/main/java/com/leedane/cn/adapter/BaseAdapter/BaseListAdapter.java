package com.leedane.cn.adapter.BaseAdapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;

import java.util.List;

/**
 * 基本的列表适配器
 * Created by Administrator on 2016/9/1.
 */
public class BaseListAdapter<T> extends BaseAdapter {
    protected List<T> mDatas;
    protected Context mContext;
    private int lastPosition = -1;
    protected Typeface typeface;
    public BaseListAdapter(){
        typeface = BaseApplication.getDefaultTypeface();
    }

    public BaseListAdapter(Context context, List<T> datas) {
        this.mDatas = datas;
        this.mContext = context;
        typeface = BaseApplication.getDefaultTypeface();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void refreshData(List<T> datas){
        this.mDatas.clear();
        this.mDatas.addAll(datas);
        this.notifyDataSetChanged();
    }

    protected void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = null;
            if(position % 2 == 0){
                animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R
                        .anim.item_anim_in_from_left);
            }else
                animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R
                        .anim.item_anim_in_from_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
