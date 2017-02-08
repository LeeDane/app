package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.NearbyBean;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 附近人列表数据展示的adapter对象
 * Created by LeeDane on 2016/2/3.
 */
public class NearbyListAdapter extends BaseRecyclerViewAdapter<NearbyBean> {
    private boolean showUserInfo;

    public NearbyListAdapter(Context context, List<NearbyBean> nearbyBeans) {
        super(context, nearbyBeans);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nearby_list_listview, parent, false);
            return new ContentHolder(layout);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        int viewType = getItemViewType(position);
        if(viewType == TYPE_HEADER)
            return;
        if(viewType == TYPE_FOOTER){
            return;
        }

        if(viewHolder instanceof ContentHolder && !CommonUtil.isEmpty(mDatas)) {
            Log.i("ViewAdapter", "position=" + position);
            final int pos = getRealPosition(viewHolder);
            final NearbyBean nearbyBean = mDatas.get(pos);
            ContentHolder holder = ((ContentHolder) viewHolder);

            holder.account.setText(nearbyBean.getAccount());
            if(StringUtil.isNotNull(nearbyBean.getUserPicPath()))
                ImageCacheManager.loadImage(nearbyBean.getUserPicPath(), holder.userPic, 30, 30);
            else
                holder.userPic.setImageResource(R.drawable.no_pic);

            if(StringUtil.isNotNull(nearbyBean.getPersonalIntroduction())){
                holder.introduction.setVisibility(View.VISIBLE);
                holder.introduction.setText(nearbyBean.getPersonalIntroduction());
            }
            holder.time.setTypeface(typeface);
            holder.time.setText(RelativeDateFormat.format(nearbyBean.getTime()));

            holder.distance.setText(StringUtil.formatDistance(nearbyBean.getDistance()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(pos, null);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mOnItemLongClickListener == null)
                        return true;

                    mOnItemLongClickListener.onItemLongClick(pos);
                    return true;
                }
            });
            //设置动画效果
            //setAnimation(holder.itemView, position);
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder{
        CircularImageView userPic;
        TextView account;
        TextView introduction;
        TextView time;
        TextView distance;
        public ContentHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            userPic = (CircularImageView)itemView.findViewById(R.id.nearby_list_user_pic);
            account = (TextView) itemView.findViewById(R.id.nearby_list_account);
            introduction = (TextView) itemView.findViewById(R.id.nearby_list_personal_introduction);
            time = (TextView) itemView.findViewById(R.id.nearby_list_time);
            distance = (TextView)itemView.findViewById(R.id.nearby_list_distance);
        }
    }
}
