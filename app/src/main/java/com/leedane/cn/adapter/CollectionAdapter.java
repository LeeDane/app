package com.leedane.cn.adapter;

import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.CollectionBean;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 收藏列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/6.
 */
public class CollectionAdapter extends BaseListAdapter<CollectionBean>{

    public CollectionAdapter(Context context, List<CollectionBean> collectionBeans) {
        super(context, collectionBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        CollectionBean collectionBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_collection_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) view.findViewById(R.id.collection_time);
            viewHolder.source = (TextView)view.findViewById(R.id.collection_source);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();

        viewHolder.time.setTypeface(typeface);
        viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(collectionBean.getCreateTime())));
        if(StringUtil.isNotNull(collectionBean.getSource())){
            viewHolder.source.setVisibility(View.VISIBLE);
            Spannable spannable= AppUtil.textviewShowImg(mContext, collectionBean.getSource());
            viewHolder.source.setText(spannable);
        }else{
            viewHolder.source.setVisibility(View.GONE);
        }
        //设置动画效果
        setAnimation(view, position);
        return view;
    }

    static class ViewHolder{
        private TextView time;
        private TextView source;
    }
}
