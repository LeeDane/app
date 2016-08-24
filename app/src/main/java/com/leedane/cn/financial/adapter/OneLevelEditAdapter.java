package com.leedane.cn.financial.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.Helper.ItemTouchHelperAdapter;
import com.leedane.cn.financial.Helper.ItemTouchHelperViewHolder;
import com.leedane.cn.financial.Helper.OnStartDragListener;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.OneLevelGategory;
import com.leedane.cn.financial.bean.OneLevelGategoryEdit;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 一级分类编辑列表数据展示的adapter对象
 * Created by LeeDane on 2016/8/23.
 */
public class OneLevelEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    private List<OneLevelGategoryEdit> mOneLevelGategoryEdits = new ArrayList<>();
    private OnItemClickListener mListener;
    private Context mContext;
    public static final int TYPE_NORMAL = 1;
    private  OnStartDragListener mDragStartListener;
    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    public OneLevelEditAdapter(){

    }
    public OneLevelEditAdapter(Context context, List<OneLevelGategoryEdit> oneLevelGategoryEdit, OnStartDragListener dragStartListener){
        this.mOneLevelGategoryEdits = oneLevelGategoryEdit;
        this.mContext = context;
        mDragStartListener = dragStartListener;
        //notifyDataSetChanged();
    }
    public void addDatas(List<OneLevelGategoryEdit> datas) {
        mOneLevelGategoryEdits.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 移除数据
     * @param position
     */
    public void remove(int position) {
        mOneLevelGategoryEdits.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 添加数据
     * @param oneLevelGategoryEdit
     * @param position
     */
    public void add(OneLevelGategoryEdit oneLevelGategoryEdit, int position) {
        mOneLevelGategoryEdits.add(position, oneLevelGategoryEdit);
        notifyItemInserted(position);
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_NORMAL;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financila_one_level_edit, parent, false);
        return new Holder(layout);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final OneLevelGategoryEdit data = mOneLevelGategoryEdits.get(position);
        if(viewHolder instanceof Holder) {
            final Holder holder = ((Holder) viewHolder);
            //收入
            if(data.getModel() == IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME){
                holder.model.setText(mContext.getString(R.string.income));
                holder.icon.setImageResource(R.drawable.ic_trending_up_blue_a200_18dp);
            }else{
                holder.model.setText(mContext.getString(R.string.spend));
                holder.icon.setImageResource(R.drawable.ic_trending_down_pink_a200_18dp);
            }

            if(data.getIcon() > 0){
                holder.icon.setImageResource(data.getIcon());
            }

            holder.name.setText(StringUtil.changeNotNull(data.getValue()));
            holder.budget.setText(String.valueOf(data.getBudget()));
            if(data.getStatus() == 1){
                holder.status.setText("正常");
            }else{
                holder.status.setText("禁用");
            }
            //if(mListener == null) return;
            /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                private boolean isShow = true;

                @Override
                public void onClick(View v) {
                    if (isShow){
                        holder.order.setVisibility(View.VISIBLE);
                        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
                        holder.right.setVisibility(View.GONE);
                        isShow = false;
                    }else{
                        holder.order.setVisibility(View.GONE);
                        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        holder.right.setVisibility(View.VISIBLE);
                        isShow = true;
                    }
                    if (mListener == null) return;
                    mListener.onItemClick(position, String.valueOf(data.getValue()));
                }
            });*/
            // Start a drag whenever the handle view it touched
            if(data.isEdit()){
                holder.right.setImageResource(R.drawable.ic_list_white_18dp);
                holder.itemView.setBackgroundColor(Color.LTGRAY);
                holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                            mDragStartListener.onStartDrag(holder);
                        }
                        return true;
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.success(mContext, "点击1：" + holder.name.getText().toString());
                    }
                });
            }else{
                holder.right.setImageResource(R.mipmap.right_sign);
                holder.itemView.setBackgroundColor(0);
                holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.success(mContext, "点击："+holder.name.getText().toString());
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mOneLevelGategoryEdits.size();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mOneLevelGategoryEdits, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mOneLevelGategoryEdits.remove(position);
        notifyItemRemoved(position);
    }

    class Holder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        ImageView icon; //图表
        TextView name; //分类名称
        TextView model; //类型
        TextView budget; //预算
        TextView status; //状态
        ImageView order; //排序
        ImageView right; //右侧的点击图标
        public Holder(View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.financial_one_level_icon);
            model = (TextView) itemView.findViewById(R.id.financial_one_level_model);
            name = (TextView) itemView.findViewById(R.id.financial_one_level_name);
            budget = (TextView) itemView.findViewById(R.id.financial_one_level_budget);
            status = (TextView)itemView.findViewById(R.id.financial_one_level_status);
            order = (ImageView)itemView.findViewById(R.id.financila_list_order);
            right = (ImageView)itemView.findViewById(R.id.financila_list_right);
        }

        @Override
        public void onItemSelected(int position) {
            ToastUtil.success(mContext, "select:"+position);
            //right.setImageResource(R.drawable.ic_list_white_18dp);
            //itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear(int oldPosition , int position) {
            ToastUtil.success(mContext, "Clear:oldPosition->"+oldPosition+",position->"+position);
            //right.setImageResource(R.mipmap.right_sign);
            //itemView.setBackgroundColor(0);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position, String data);
    }
}
