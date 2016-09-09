package com.leedane.cn.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.MenuBean;

import java.util.List;

/**
 * 我的发现列表的adapter
 * Created by LeeDane on 2016/4/15.
 */
public class FindAdapter extends RecyclerView.Adapter{

    private List<MenuBean> list;

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
            this.onRecyclerViewListener = onRecyclerViewListener;
    }



    /**
     * 构造器
     * @param findBeans
     */
    public FindAdapter(List<MenuBean> findBeans){
        this.list = findBeans;
    }

    /**
     * 这个方法主要生成为每个Item inflater出一个View，但是该方法返回的是一个ViewHolder。
     * 方法是把View直接封装在ViewHolder中，然后我们面向的是ViewHolder这个实例，
     * 当然这个ViewHolder需要我们自己去编写。直接省去了当初的convertView.setTag(holder)和convertView.getTag()这些繁琐的步骤。
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_listview, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new FindViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FindViewHolder findViewHolder = (FindViewHolder) holder;
        findViewHolder.position = position;
        MenuBean findBean = list.get(position);
        findViewHolder.titleTV.setText(findBean.getTitle());
        findViewHolder.iconIV.setImageResource(findBean.getIconId());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class FindViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        View rootView;
        ImageView iconIV;
        TextView titleTV;
        int position;
        /**
         * 整个子项的根View
         * @param itemView
         */
        FindViewHolder(View itemView){
            super(itemView);
            rootView = itemView.findViewById(R.id.recyclerview_root);
            iconIV = (ImageView)itemView.findViewById(R.id.recyclerview_icon);
            titleTV = (TextView)itemView.findViewById(R.id.recyclerview_title);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(position);
            }

        }

        @Override
        public boolean onLongClick(View v) {
            if(null != onRecyclerViewListener){
                return onRecyclerViewListener.onItemLongClick(position);
            }
            return false;
        }
    }
}
