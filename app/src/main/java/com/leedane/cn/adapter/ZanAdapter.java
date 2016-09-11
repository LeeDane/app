package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.ZanBean;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 赞列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/5.
 */
public class ZanAdapter extends BaseRecyclerViewAdapter<ZanBean> {
    public ZanAdapter(Context context, List<ZanBean> zanBeans) {
        super(context, zanBeans);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zan_listview, parent, false);
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
            final ZanBean zanBean = mDatas.get(pos);
            ContentHolder holder = ((ContentHolder) viewHolder);

            holder.from.setTypeface(typeface);
            holder.from.setText(StringUtil.changeNotNull(zanBean.getFroms()));

            holder.time.setTypeface(typeface);
            holder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(zanBean.getCreateTime())));

            holder.content.setText(StringUtil.changeNotNull(zanBean.getContent()));
            if(StringUtil.isNotNull(zanBean.getSource())){
                holder.source.setVisibility(View.VISIBLE);
                Spannable spannable= AppUtil.textviewShowImg(mContext, zanBean.getSource());
                holder.source.setText(spannable);
            }else{
                holder.source.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(pos, null);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemLongClickListener == null)
                        return true;

                    mOnItemLongClickListener.onItemLongClick(pos);
                    return true;
                }
            });
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder{
        TextView from;
        TextView time;
        TextView content;
        TextView source;
        public ContentHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            from = (TextView)itemView.findViewById(R.id.zan_from);
            time = (TextView)itemView.findViewById(R.id.zan_time);
            content = (TextView)itemView.findViewById(R.id.zan_content);
            source = (TextView)itemView.findViewById(R.id.zan_source);
        }
    }
}
