package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.CollectionBean;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 收藏列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/6.
 */
public class CollectionAdapter extends BaseRecyclerViewAdapter<CollectionBean>{

    public CollectionAdapter(Context context, List<CollectionBean> collectionBeans) {
        super(context, collectionBeans);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection_listview, parent, false);
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
            Log.i("ViewAdapter", "position=" + position);
            final int pos = getRealPosition(viewHolder);
            final CollectionBean collectionBean = mDatas.get(pos);
            ContentHolder holder = ((ContentHolder) viewHolder);
            holder.time.setTypeface(typeface);
            holder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(collectionBean.getCreateTime())));
            if(StringUtil.isNotNull(collectionBean.getSource())){
                holder.source.setVisibility(View.VISIBLE);
                Spannable spannable= AppUtil.textviewShowImg(mContext, collectionBean.getSource());
                holder.source.setText(spannable);
            }else{
                holder.source.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(pos, null);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemLongClickListener == null)
                        return true;

                    mOnItemLongClickListener.onItemLongClick(pos);
                    return true;
                }
            });
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder{
        private TextView time;
        private TextView source;
        public ContentHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            time = (TextView) itemView.findViewById(R.id.collection_time);
            source = (TextView)itemView.findViewById(R.id.collection_source);
        }
    }
}
