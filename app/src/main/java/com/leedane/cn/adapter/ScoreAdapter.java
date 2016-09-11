package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.ScoreBean;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;

import java.util.List;

/**
 * 积分历史列表数据展示的adapter对象
 * Created by LeeDane on 2016/5/5.
 */
public class ScoreAdapter extends BaseRecyclerViewAdapter<ScoreBean> {

    public ScoreAdapter(Context context, List<ScoreBean> scoreBeans) {
        super(context, scoreBeans);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score_listview, parent, false);
            return new ContentHolder(layout);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == TYPE_HEADER)
            return;
        if(viewType == TYPE_FOOTER){
            return;
        }

        if(viewHolder instanceof ContentHolder && !CommonUtil.isEmpty(mDatas)) {
            Log.i("ViewAdapter", "position=" + position);
            final int pos = getRealPosition(viewHolder);
            final ScoreBean scoreBean = mDatas.get(pos);
            ContentHolder holder = ((ContentHolder) viewHolder);

            holder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(scoreBean.getCreateTime())));
            holder.desc.setText("描述:" + scoreBean.getDesc());
            holder.number.setText("当/总:" + scoreBean.getScore() + "/" + scoreBean.getTotalScore());
            holder.status.setText("状态:" + getStatusText(scoreBean.getStatus()));
        }
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

    class ContentHolder extends RecyclerView.ViewHolder{
        TextView time;
        TextView desc;
        TextView number;
        TextView status;
        public ContentHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            time = (TextView) itemView.findViewById(R.id.score_time);
            TextView tvDesc = (TextView) itemView.findViewById(R.id.score_desc);
            tvDesc.setSelected(true);
            desc = tvDesc;
            number = (TextView) itemView.findViewById(R.id.score_number);
            status = (TextView)itemView.findViewById(R.id.score_status);
        }
    }
}
