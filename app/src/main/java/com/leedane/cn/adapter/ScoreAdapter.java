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
            viewHolder.setmTime((TextView) view.findViewById(R.id.score_time));
            TextView tvDesc = (TextView) view.findViewById(R.id.score_desc);
            tvDesc.setSelected(true);
            viewHolder.setmDesc(tvDesc);
            viewHolder.setmNumber((TextView) view.findViewById(R.id.score_number));
            viewHolder.setmStatus((TextView)view.findViewById(R.id.score_status));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(scoreBean.getCreateTime())));
        viewHolder.getmDesc().setText("描述:" +scoreBean.getDesc());
        viewHolder.getmNumber().setText("当/总:" +scoreBean.getScore() +"/" +scoreBean.getTotalScore());
        viewHolder.getmStatus().setText("状态:" +getStatusText(scoreBean.getStatus()));

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

    private class ViewHolder{
        private TextView mTime;
        private TextView mDesc;
        private TextView mNumber;
        //private TextView mTotalNumber;
        private TextView mStatus;

        public TextView getmDesc() {
            return mDesc;
        }

        public void setmDesc(TextView mDesc) {
            this.mDesc = mDesc;
        }

        public TextView getmNumber() {
            return mNumber;
        }

        public void setmNumber(TextView mNumber) {
            this.mNumber = mNumber;
        }

        public TextView getmStatus() {
            return mStatus;
        }

        public void setmStatus(TextView mStatus) {
            this.mStatus = mStatus;
        }

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }
    }
}
