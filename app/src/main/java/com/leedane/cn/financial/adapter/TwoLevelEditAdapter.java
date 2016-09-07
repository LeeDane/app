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
import com.leedane.cn.financial.bean.TwoLevelCategoryEdit;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 二级分类编辑列表数据展示的adapter对象
 * Created by LeeDane on 2016/8/24.
 */
public class TwoLevelEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    private List<TwoLevelCategoryEdit> mTwoLevelCategoryEdits = new ArrayList<>();
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

    public TwoLevelEditAdapter(){

    }
    public TwoLevelEditAdapter(Context context, List<TwoLevelCategoryEdit> twoLevelCategoryEdit, OnStartDragListener dragStartListener){
        this.mTwoLevelCategoryEdits = twoLevelCategoryEdit;
        this.mContext = context;
        mDragStartListener = dragStartListener;
        //notifyDataSetChanged();
    }
    public void addDatas(List<TwoLevelCategoryEdit> datas) {
        mTwoLevelCategoryEdits.clear();
        mTwoLevelCategoryEdits.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 移除数据
     * @param position
     */
    public void remove(int position) {
        mTwoLevelCategoryEdits.remove(position);
        notifyItemRemoved(position);
        if(position != mTwoLevelCategoryEdits.size()){
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }

    /**
     * 局部刷新单个数据
     * @param position
     */
    public void refresh(TwoLevelCategoryEdit twoLevelCategoryEdit, int position){
        mTwoLevelCategoryEdits.remove(position); //先移除后添加
        mTwoLevelCategoryEdits.add(position, twoLevelCategoryEdit);
        notifyItemChanged(position);
    }

    /**
     * 添加数据
     * @param twoLevelCategoryEdit
     * @param position
     */
    public void add(TwoLevelCategoryEdit twoLevelCategoryEdit, int position) {
        mTwoLevelCategoryEdits.add(position, twoLevelCategoryEdit);
        notifyItemInserted(position);
        if(position != mTwoLevelCategoryEdits.size()){
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_NORMAL;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financila_two_level_operation, parent, false);
        return new Holder(layout);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final TwoLevelCategoryEdit data = mTwoLevelCategoryEdits.get(position);
        if(viewHolder instanceof Holder) {
            final Holder holder = ((Holder) viewHolder);

            if(data.getIcon() > 0){
                holder.icon.setImageResource(data.getIcon());
            }

            holder.name.setText(Html.fromHtml(data.getValue() + (data.isDefault() ? "   <font color='red'>默认</font>" : "")));
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
        return mTwoLevelCategoryEdits.size();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        ToastUtil.success(mContext, "onItemMove:fromPosition->"+fromPosition+",toPosition->"+toPosition);
        Collections.swap(mTwoLevelCategoryEdits, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        ToastUtil.success(mContext, "onItemDismiss:position->"+position);
        mTwoLevelCategoryEdits.remove(position);
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
            icon = (ImageView)itemView.findViewById(R.id.financial_two_level_icon);
            model = (TextView) itemView.findViewById(R.id.financial_two_level_model);
            name = (TextView) itemView.findViewById(R.id.financial_two_level_name);
            budget = (TextView) itemView.findViewById(R.id.financial_two_level_budget);
            status = (TextView)itemView.findViewById(R.id.financial_two_level_status);
            right = (ImageView)itemView.findViewById(R.id.financila_list_right);
            root = (LinearLayout)itemView.findViewById(R.id.financial_two_level_root);
        }

        @Override
        public void onItemSelected(int position) {
            ToastUtil.success(mContext, "select:"+position);
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
