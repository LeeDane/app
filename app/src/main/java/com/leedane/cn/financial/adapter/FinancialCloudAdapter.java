package com.leedane.cn.financial.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.OneLevelCategoryEdit;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 与云端同步列表数据展示的adapter对象
 * Created by LeeDane on 2016/8/26.
 */
public class FinancialCloudAdapter extends BaseRecyclerViewAdapter<FinancialBean>{

    public FinancialCloudAdapter(Context context, List<FinancialBean> financialBeans){
        super(context, financialBeans);
    }

    /**
     * 局部刷新单个数据
     * @param financialBean
     * @param position
     */
    public void refresh(FinancialBean financialBean, int position){
        //mFinancialBeans.remove(position);// 先移除后添加
        //mFinancialBeans.add(position, financialBean);
        //notifyItemChanged(position);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financila_list_cloud, parent, false);
            return new ContentHolder(layout);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(getItemViewType(position) == TYPE_HEADER)
            return;
        if(getItemViewType(position) == TYPE_FOOTER){
            return;
        }

        if(viewHolder instanceof ContentHolder && !CommonUtil.isEmpty(mDatas)) {
            final int pos = getRealPosition(viewHolder);
            final FinancialBean data = mDatas.get(pos);

            ContentHolder holder = ((ContentHolder) viewHolder);
            //收入
            if(data.getModel() == IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME){
                holder.model.setImageResource(R.drawable.ic_trending_up_blue_a200_18dp);
            }else{
                holder.model.setImageResource(R.drawable.ic_trending_down_pink_a200_18dp);
            }
            String category = "";
            if(StringUtil.isNotNull(data.getOneLevel())){
                category = data.getOneLevel();
            }
            if(StringUtil.isNotNull(data.getTwoLevel())){
                category = category + " >> " +data.getTwoLevel();
            }

            if(data.getStatus() == ConstantsUtil.STATUS_NORMAL){
                holder.status.setText(mContext.getString(R.string.normal));
            }else if(data.getStatus() == ConstantsUtil.STATUS_DRAFT){
                holder.status.setText(mContext.getString(R.string.draft));
            }else if(data.getStatus() == ConstantsUtil.STATUS_DELETE){
                holder.status.setText(mContext.getString(R.string.delete));
            }else if(data.getStatus() == ConstantsUtil.STATUS_DISABLE){
                holder.status.setText(mContext.getString(R.string.disable));
            }else {
                holder.status.setText("未知");
            }

            if(data.isSynchronous()){
                holder.synchronous.setText("已同步");
            }else{
                holder.synchronous.setText("未同步");
            }

            holder.category.setText(category);
            holder.money.setText("￥" +String.valueOf(data.getMoney()));
            if(StringUtil.isNotNull(data.getAdditionTime())){
                holder.addTime.setText(data.getAdditionTime().substring(0, 10));
            }

            if(StringUtil.isNotNull(data.getSynchronousTip())){
                holder.tip.setText(Html.fromHtml(data.getSynchronousTip()));
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(pos, String.valueOf(data.getMoney()));
                }
            });
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder {
        ImageView model;
        TextView category;
        TextView money;
        TextView addTime;
        TextView status;
        TextView synchronous; //是否同步
        TextView tip;
        public ContentHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            model = (ImageView)itemView.findViewById(R.id.financial_cloud_model);
            category = (TextView) itemView.findViewById(R.id.financial_cloud_category);
            money = (TextView) itemView.findViewById(R.id.financial_cloud_money);
            addTime = (TextView) itemView.findViewById(R.id.financial_cloud_time);
            status = (TextView)itemView.findViewById(R.id.financial_cloud_status);
            synchronous = (TextView)itemView.findViewById(R.id.financial_cloud_synchronous);
            tip = (TextView)itemView.findViewById(R.id.financial_cloud_synchronous_tip);
        }
    }
}
