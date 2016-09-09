package com.leedane.cn.adapter;

import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.AttentionBean;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 关注列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/6.
 */
public class AttentionAdapter extends BaseListAdapter<AttentionBean>{
    public AttentionAdapter(Context context, List<AttentionBean> attentionBeans) {
        super(context, attentionBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        AttentionBean attentionBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_attention_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) view.findViewById(R.id.attention_time);
            viewHolder.source = (TextView)view.findViewById(R.id.attention_source);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.time.setTypeface(typeface);
        viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(attentionBean.getCreateTime())));

        if(StringUtil.isNotNull(attentionBean.getSource())){
            viewHolder.source.setVisibility(View.VISIBLE);
            Spannable spannable= AppUtil.textviewShowImg(mContext, attentionBean.getSource());
            viewHolder.source.setText(spannable);
        }else{
            viewHolder.source.setVisibility(View.GONE);
        }
        //设置动画效果
        setAnimation(view, position);
        return view;
    }


    static class ViewHolder{
        TextView time;
        TextView source;
    }
}
