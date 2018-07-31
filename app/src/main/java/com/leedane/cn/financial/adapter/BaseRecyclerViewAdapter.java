package com.leedane.cn.financial.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 基本适配器数据展示的adapter对象
 * Created by LeeDane on 2016/8/31.
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected List<T> mDatas = new ArrayList<>();
    protected static final int TYPE_HEADER = 0;
    protected static final int TYPE_NORMAL = 1;
    protected static final int TYPE_FOOTER = 2;//数据为空的展示
    protected View mHeaderView;
    protected View mFooterView;
    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;
    protected int lastPosition = -1;
    protected Context mContext;
    protected Typeface typeface;
    /**
     * 唯一入口适配器
     * @param context
     * @param datas
     */
    public BaseRecyclerViewAdapter(Context context, List<T> datas){
        typeface = BaseApplication.getDefaultTypeface();
        this.mDatas = datas;
        this.mContext = context;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setHeaderView(View headerView) {
        //必须，在把headview添加到recycleview中时告诉recycleview期望的布局方式，
        // 也就是将一个认可的layoutParams传递进去，不然显示的不是全部宽度
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerView.setLayoutParams(layoutParams);
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public void setFooterView(View footer) {
        //必须，在把headview添加到recycleview中时告诉recycleview期望的布局方式，
        // 也就是将一个认可的layoutParams传递进去，不然显示的不是全部宽度
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        footer.setLayoutParams(layoutParams);
        this.mFooterView = footer;
        notifyItemInserted(getFooterPosition());
    }

    public void addDatas(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 获取Recyclerview列表的布局ID
            */
    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position);

    @Override
    public int getItemCount() {
        int count = 0;
        //计算头部跟底部的数量
        count = mHeaderView == null ? (mFooterView == null ? 0 : 1) : (mFooterView == null ? 1 : 2);
        //头部跟底部的数量
        int size = !CommonUtil.isEmpty(mDatas) ? mDatas.size(): 0;
        return count + size;
    }
    @Override
    public int getItemViewType(int position) {
        int type = TYPE_NORMAL;

        //什么时候展示头部
        if(mHeaderView != null && position == 0)
            type = TYPE_HEADER;

        int count = getItemCount();
        //什么时候展示底部
        if(mFooterView != null && count -1 == position)
            type = TYPE_FOOTER;
        return type;
    }

    /**
     * 执行动画
     * @param viewToAnimate
     * @param position
     */
    protected void setAnimation(View viewToAnimate, int position) {
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

   /**
     * 获取真正的索引位置(有可能有header)
     * @param holder
     * @return
     */
    protected int getRealPosition(RecyclerView.ViewHolder holder) {

        int position = holder.getLayoutPosition();
        if(mHeaderView == null){
            return position;
        }

        if(mHeaderView != null && !CommonUtil.isEmpty(mDatas)){
            if(mFooterView != null && position == getItemCount()-1){
                position = position - 2;
            }else
                position = position - 1;
        }
        return position;
    }

    /**
     * 移除数据
     * @param position
     */
    public void remove(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
        if(position != mDatas.size()){
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }

    /**
     * 局部刷新单个数据
     * @param t
     * @param position
     */
    public void refresh(T t, int position){
        mDatas.remove(position);// 先移除后添加
        mDatas.add(position, t);
        notifyItemChanged(position);
    }

    /**
     * 添加数据
     * @param t
     * @param position
     */
    public void add(T t, int position) {
        mDatas.add(position, t);
        notifyItemInserted(position);
        if(position != mDatas.size()){
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }

    /**
     * 获取底部控件的索引
     * @return
     */
    private int getFooterPosition(){
        if(mHeaderView == null){
            if(!CommonUtil.isEmpty(mDatas))
                return mDatas.size();
        }else{
            if(!CommonUtil.isEmpty(mDatas))
                return mDatas.size() + 1;
        }
        return 0;
    }

    public List<T> getmDatas() {
        return mDatas;
    }

    /**
     * item的单击事件
     */
    public interface OnItemClickListener {
        void onItemClick(int position, Object data);
    }

    /**
     * item的长按事件
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    /**
     * item的单击事件
     */
    public interface OnAfterDragListener {
        void after(int position, Object data);
    }
}
