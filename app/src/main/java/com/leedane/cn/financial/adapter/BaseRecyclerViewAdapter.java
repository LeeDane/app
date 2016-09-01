package com.leedane.cn.financial.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.StringUtil;

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
    protected static final int TYPE_EMPTY = 2;//数据为空的展示
    protected View mHeaderView;
    protected OnItemClickListener mListener;
    protected int lastPosition = -1;
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

    public BaseRecyclerViewAdapter(){

    }
    public BaseRecyclerViewAdapter(List<T> datas){
        this.mDatas = datas;
        notifyDataSetChanged();
    }
    public void addDatas(List<T> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 获取Recyclerview列表的布局ID
     */
    protected abstract int getListRecyclerviewId();
    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) {
            if(CommonUtil.isEmpty(mDatas)){
                return TYPE_EMPTY;
            }
            return TYPE_NORMAL;
        }else{
            if(position == 0)
                return TYPE_HEADER;

            //返回空数据类型
            if(CommonUtil.isEmpty(mDatas))
                return TYPE_EMPTY;

            return TYPE_NORMAL;
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
        return mHeaderView == null ? position : position - 1;
    }
    @Override
    public int getItemCount() {
        int count = 0;
        if(mHeaderView == null){
            if(CommonUtil.isEmpty(mDatas)){
                count = 1;
            }else
                count = mDatas.size();
        }else{
            if(CommonUtil.isEmpty(mDatas)){
                count = 2; //2是因为加上头部和空的提示
            }else
                count = mDatas.size() + 1;
        }
        return count;
    }
    protected class BaseHolder extends RecyclerView.ViewHolder {
        public BaseHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView)
                return;
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
