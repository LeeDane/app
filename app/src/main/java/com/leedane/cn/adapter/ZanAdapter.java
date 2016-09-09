package com.leedane.cn.adapter;

import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.ZanBean;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 赞列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/5.
 */
public class ZanAdapter extends BaseListAdapter<ZanBean>{
    public ZanAdapter(Context context, List<ZanBean> zanBeans) {
        super(context, zanBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        ZanBean zanBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_zan_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.from = (TextView) view.findViewById(R.id.zan_from);
            viewHolder.time = (TextView) view.findViewById(R.id.zan_time);
            viewHolder.content = (TextView)view.findViewById(R.id.zan_content);
            viewHolder.source = (TextView)view.findViewById(R.id.zan_source);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();

        viewHolder.from.setTypeface(typeface);
        viewHolder.from.setText(StringUtil.changeNotNull(zanBean.getFroms()));

        viewHolder.time.setTypeface(typeface);
        viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(zanBean.getCreateTime())));

        viewHolder.content.setText(StringUtil.changeNotNull(zanBean.getContent()));
        if(StringUtil.isNotNull(zanBean.getSource())){
            viewHolder.source.setVisibility(View.VISIBLE);
            Spannable spannable= AppUtil.textviewShowImg(mContext, zanBean.getSource());
            viewHolder.source.setText(spannable);
        }else{
            viewHolder.source.setVisibility(View.GONE);
        }
        //设置动画效果
        setAnimation(view, position);
        return view;
    }

    static class ViewHolder{
        TextView from;
        TextView time;
        TextView content;
        TextView source;
    }
}
