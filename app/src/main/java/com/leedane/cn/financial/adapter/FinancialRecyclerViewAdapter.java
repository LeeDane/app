package com.leedane.cn.financial.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 记账列表数据展示的adapter对象
 * Created by LeeDane on 2016/7/23.
 */
public class FinancialRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<FinancialBean> mFinancialBeans = new ArrayList<>();
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_EMPTY = 2;//数据为空的展示
    private View mHeaderView;
    private OnItemClickListener mListener;
    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }
    public View getHeaderView() {
        return mHeaderView;
    }

    public FinancialRecyclerViewAdapter(){

    }
    public FinancialRecyclerViewAdapter(List<FinancialBean> financialBeans){
        this.mFinancialBeans = financialBeans;
        //notifyDataSetChanged();
    }
    public void addDatas(List<FinancialBean> datas) {
        mFinancialBeans.addAll(datas);
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) {
            if(CommonUtil.isEmpty(mFinancialBeans)){
                return TYPE_EMPTY;
            }
            return TYPE_NORMAL;
        }else{
            if(position == 0)
                return TYPE_HEADER;

            //返回空数据类型
            if(CommonUtil.isEmpty(mFinancialBeans))
                return TYPE_EMPTY;

            return TYPE_NORMAL;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new Holder(mHeaderView);
        else if(CommonUtil.isEmpty(mFinancialBeans)){
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financila_list_recyclerview_empty, parent, false);
            return new EmptyHodler(layout);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financila_list_recyclerview, parent, false);
            return new Holder(layout);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(getItemViewType(position) == TYPE_HEADER)
            return;
        if(getItemViewType(position) == TYPE_EMPTY){
            EmptyHodler holder = ((EmptyHodler) viewHolder);
            holder.empty.setText("暂无数据");
            return;
        }
        final int pos = getRealPosition(viewHolder);
        final FinancialBean data = mFinancialBeans.get(pos);
        if(viewHolder instanceof Holder) {
            Holder holder = ((Holder) viewHolder);
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
            holder.money.setText(String.valueOf(data.getMoney()));
            if(StringUtil.isNotNull(data.getAdditionTime())){
                holder.addTime.setText(data.getAdditionTime().substring(0, 10));
            }
            if(mListener == null) return;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos, String.valueOf(data.getMoney()));
                }
            });
        }
    }
    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }
    @Override
    public int getItemCount() {
        int count = 0;
        if(mHeaderView == null){
            if(CommonUtil.isEmpty(mFinancialBeans)){
                count = 1;
            }else
                count = mFinancialBeans.size();
        }else{
            if(CommonUtil.isEmpty(mFinancialBeans)){
                count = 2; //2是因为加上头部和空的提示
            }else
                count = mFinancialBeans.size() + 1;
        }
        return count;
    }
    class Holder extends RecyclerView.ViewHolder {
        ImageView model;
        TextView category;
        TextView money;
        TextView addTime;
        public Holder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView)
                return;
            model = (ImageView)itemView.findViewById(R.id.financial_list_model);
            category = (TextView) itemView.findViewById(R.id.financial_list_category);
            money = (TextView) itemView.findViewById(R.id.financial_list_money);
            addTime = (TextView) itemView.findViewById(R.id.financial_list_time);
        }
    }

    class EmptyHodler extends RecyclerView.ViewHolder{
        TextView empty;
        public EmptyHodler(View itemView){
            super(itemView);
            if(itemView == mHeaderView)
                return;
            empty = (TextView)itemView.findViewById(R.id.financial_list_empty);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position, String data);
    }
}
