package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.ScoreBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;

import java.util.List;

/**
 * 积分历史列表数据展示的adapter对象
 * Created by LeeDane on 2016/5/5.
 */
public class ScoreAdapter extends BaseListAdapter<ScoreBean>{

    public ScoreAdapter(Context context, List<ScoreBean> scoreBeans) {
        super(context, scoreBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        ScoreBean scoreBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_score_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) view.findViewById(R.id.score_time);
            TextView tvDesc = (TextView) view.findViewById(R.id.score_desc);
            tvDesc.setSelected(true);
            viewHolder.desc = tvDesc;
            viewHolder.number = (TextView) view.findViewById(R.id.score_number);
            viewHolder.status = (TextView)view.findViewById(R.id.score_status);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(scoreBean.getCreateTime())));
        viewHolder.desc.setText("描述:" + scoreBean.getDesc());
        viewHolder.number.setText("当/总:" + scoreBean.getScore() + "/" + scoreBean.getTotalScore());
        viewHolder.status.setText("状态:" + getStatusText(scoreBean.getStatus()));

        //设置动画效果
        setAnimation(view, position);
        return view;
    }

    /**
     * 处理状态信息
     * @param status
     * @return
     */
    private String getStatusText(int status){
        //积分的状态,1：正常，0:禁用，2、删除，3、审核中， 4、审核不通过
        switch (status){
            case 0:
                return "禁用";
            case 1:
                return "正常";
            case 2:
                return "已删除";
            case 3:
                return "审核中";
            case 4:
                return "审核不通过";
        }
        return "未知异常";
    }

    static class ViewHolder{
        TextView time;
        TextView desc;
        TextView number;
        TextView status;
    }
}
