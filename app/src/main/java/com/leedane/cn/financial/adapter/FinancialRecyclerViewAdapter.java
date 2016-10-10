package com.leedane.cn.financial.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 记账列表数据展示的adapter对象
 * Created by LeeDane on 2016/7/23.
 */
public class FinancialRecyclerViewAdapter extends BaseRecyclerViewAdapter<FinancialBean>{

    public FinancialRecyclerViewAdapter(Context context, List<FinancialBean> financialBeans){
        super(context, financialBeans);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financila_list_recyclerview, parent, false);
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
            Log.i("ViewAdapter", "position="+position);
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
            holder.category.setText(category);
            holder.money.setText("￥" +String.valueOf(data.getMoney()));
            if(StringUtil.isNotNull(data.getAdditionTime())){
                holder.addTime.setText(data.getAdditionTime().substring(0, 10));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(pos, String.valueOf(data.getMoney()));
                }
            });

            //setAnimation(holder.itemView, position);
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder {
        ImageView model;
        TextView category;
        TextView money;
        TextView addTime;
        public ContentHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            model = (ImageView)itemView.findViewById(R.id.financial_list_model);
            category = (TextView) itemView.findViewById(R.id.financial_list_category);
            money = (TextView) itemView.findViewById(R.id.financial_list_money);
            addTime = (TextView) itemView.findViewById(R.id.financial_list_time);
        }
    }
}
