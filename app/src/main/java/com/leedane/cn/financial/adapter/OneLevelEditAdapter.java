package com.leedane.cn.financial.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.Helper.ItemTouchHelperAdapter;
import com.leedane.cn.financial.Helper.ItemTouchHelperViewHolder;
import com.leedane.cn.financial.Helper.OnStartDragListener;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.OneLevelCategoryEdit;
import com.leedane.cn.util.ConstantsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 一级分类编辑列表数据展示的adapter对象
 * Created by LeeDane on 2016/8/23.
 */
public class OneLevelEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    private List<OneLevelCategoryEdit> mOneLevelGategoryEdits = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private Context mContext;
    public static final int TYPE_NORMAL = 1;
    private  OnStartDragListener mDragStartListener;
    private int lastPosition = -1;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public OneLevelEditAdapter(){

    }
    public OneLevelEditAdapter(Context context, List<OneLevelCategoryEdit> oneLevelGategoryEdit, OnStartDragListener dragStartListener){
        this.mOneLevelGategoryEdits = oneLevelGategoryEdit;
        this.mContext = context;
        mDragStartListener = dragStartListener;
        //notifyDataSetChanged();
    }
    public void addDatas(List<OneLevelCategoryEdit> datas) {
        mOneLevelGategoryEdits.clear();
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
        if(position != mOneLevelGategoryEdits.size()){
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }

    /**
     * 局部刷新单个数据
     * @param oneLevelGategoryEdit
     * @param position
     */
    public void refresh(OneLevelCategoryEdit oneLevelGategoryEdit, int position){
        mOneLevelGategoryEdits.remove(position);// 先移除后添加
        mOneLevelGategoryEdits.add(position, oneLevelGategoryEdit);
        notifyItemChanged(position);
    }

    /**
     * 添加数据
     * @param oneLevelGategoryEdit
     * @param position
     */
    public void add(OneLevelCategoryEdit oneLevelGategoryEdit, int position) {
        mOneLevelGategoryEdits.add(position, oneLevelGategoryEdit);
        notifyItemInserted(position);
        if(position != mOneLevelGategoryEdits.size()){
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_NORMAL;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financila_one_level_operation, parent, false);
        return new Holder(layout);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final OneLevelCategoryEdit data = mOneLevelGategoryEdits.get(position);
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

            holder.name.setText(Html.fromHtml(data.getValue() + (data.isDefault()? "   <font color='red'>默认</font>": "")));
            holder.budget.setText("￥" +String.valueOf(data.getBudget()));
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
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClick(position);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnItemLongClickListener != null)
                            mOnItemLongClickListener.onItemLongClick(position);
                        return true;
                    }
                });
                setAnimation(holder.root, position);
            }
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = null;
            if(position % 2 == 0){
                animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R
                        .anim.item_anim_in_from_left);
            }else
                animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R
                    .anim.item_anim_in_from_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        //if(holder instanceof Holder) {
        //  ((Holder) holder).root.clearAnimation();
        //}
    }

    @Override
    public int getItemCount() {
        return mOneLevelGategoryEdits.size();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //ToastUtil.success(mContext, "onItemMove:fromPosition->"+fromPosition+",toPosition->"+toPosition);
        Collections.swap(mOneLevelGategoryEdits, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        //ToastUtil.success(mContext, "onItemDismiss:position->"+position);
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
        ImageView right; //右侧的点击图标
        LinearLayout root;
        public Holder(View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.financial_one_level_icon);
            model = (TextView) itemView.findViewById(R.id.financial_one_level_model);
            name = (TextView) itemView.findViewById(R.id.financial_one_level_name);
            budget = (TextView) itemView.findViewById(R.id.financial_one_level_budget);
            status = (TextView)itemView.findViewById(R.id.financial_one_level_status);
            right = (ImageView)itemView.findViewById(R.id.financila_list_right);
            root = (LinearLayout)itemView.findViewById(R.id.financial_one_level_root);
        }

        @Override
        public void onItemSelected(int position) {
            //ToastUtil.success(mContext, "select:"+position);
            //right.setImageResource(R.drawable.ic_list_white_18dp);
            //itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear(int oldPosition , int position) {

            //right.setImageResource(R.mipmap.right_sign);
            //itemView.setBackgroundColor(0);
        }
    }

    /**
     * item的点击事件
     */
    public interface OnItemClickListener {
        void onItemClick(int oneLevelId);
    }

    /**
     * item的长按事件
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}
