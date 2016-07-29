package com.leedane.cn.financial.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialBean;

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
    public void addDatas(List<FinancialBean> datas) {
        mFinancialBeans.addAll(datas);
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) return TYPE_NORMAL;
        if(position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER) return new Holder(mHeaderView);
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financila_list_recyclerview, parent, false);
        return new Holder(layout);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(getItemViewType(position) == TYPE_HEADER) return;
        final int pos = getRealPosition(viewHolder);
        final FinancialBean data = mFinancialBeans.get(pos);
        if(viewHolder instanceof Holder) {
            ((Holder) viewHolder).text.setText(String.valueOf(data.getMoney()));
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
        return mHeaderView == null ? mFinancialBeans.size() : mFinancialBeans.size() + 1;
    }
    class Holder extends RecyclerView.ViewHolder {
        TextView text;
        public Holder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView) return;
            text = (TextView) itemView.findViewById(R.id.recyclerview_test_text);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position, String data);
    }
}
