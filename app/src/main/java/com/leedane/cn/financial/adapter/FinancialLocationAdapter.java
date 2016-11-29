package com.leedane.cn.financial.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialLocationBean;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.List;

/**
 * 记账位置列表数据展示的adapter对象
 * Created by LeeDane on 2016/11/22.
 */
public class FinancialLocationAdapter extends BaseRecyclerViewAdapter<FinancialLocationBean>{

    public FinancialLocationAdapter(Context context, List<FinancialLocationBean> locationBeans){
        super(context, locationBeans);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financila_location_list, parent, false);
            return new ContentHolder(layout);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        int viewType = getItemViewType(position);
        if(viewType == TYPE_HEADER)
            return;
        if(viewType == TYPE_FOOTER){
            return;
        }
        if(viewHolder instanceof ContentHolder && !CommonUtil.isEmpty(mDatas)) {
            Log.i("ViewAdapter", "position="+position);
            final int pos = getRealPosition(viewHolder);
            final FinancialLocationBean data = mDatas.get(pos);
            ContentHolder holder = ((ContentHolder) viewHolder);

            holder.index.setText((position + 1) +"");
            if(data.getStatus() == ConstantsUtil.STATUS_NORMAL){
                holder.status.setText("已启用");
            }else{
                holder.status.setText(Html.fromHtml("<font color='red'>禁用</font>"));
            }
            holder.location.setText(data.getLocation());
            String desc = data.getLocationDesc();
            if(StringUtil.isNotNull(desc)){
                holder.desc.setVisibility(View.VISIBLE);
                holder.desc.setText(desc);
            } else{
                holder.desc.setVisibility(View.GONE);
            }

            holder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(data.getCreateTime())));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(pos, data);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mOnItemLongClickListener == null)
                        return false;
                    mOnItemLongClickListener.onItemLongClick(position);
                    return true;
                }
            });

            //setAnimation(holder.itemView, position);
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder {
        TextView index;
        TextView location;
        TextView time;
        TextView desc;
        TextView status;

        public ContentHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            index = (TextView)itemView.findViewById(R.id.location_list_index);
            location = (TextView) itemView.findViewById(R.id.location_list_location);
            time = (TextView) itemView.findViewById(R.id.location_list_createtime);
            desc = (TextView)itemView.findViewById(R.id.location_list_desc);
            status = (TextView)itemView.findViewById(R.id.location_list_status);
        }
    }
}
