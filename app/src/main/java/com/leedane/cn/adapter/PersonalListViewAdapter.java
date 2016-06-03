package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.leedaneAPP.R;

import java.util.List;

/**
 * 个人中心Tab数据展示的adapter对象
 * Created by LeeDane on 2015/11/19.
 */
public class PersonalListViewAdapter extends BaseAdapter{
    private Context mContext;
    private List<String> mData;

    public PersonalListViewAdapter(Context context, List<String> data) {
        this.mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_personal_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmCommentItem((TextView)view.findViewById(R.id.personal_listview_item));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmCommentItem().setText(mData.get(position));
        return view;
    }

    private class ViewHolder{
        private TextView mCommentItem;

        public TextView getmCommentItem() {
            return mCommentItem;
        }

        public void setmCommentItem(TextView mCommentItem) {
            this.mCommentItem = mCommentItem;
        }
    }
}
