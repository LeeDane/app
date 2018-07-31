package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.NotificationBean;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 通知消息列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/27.
 */
public class NotificationAdapter extends BaseRecyclerViewAdapter<NotificationBean> {
    public NotificationAdapter(Context context, List<NotificationBean> notificationBeans) {
        super(context, notificationBeans);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_listview, parent, false);
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
            final NotificationBean notificationBean = mDatas.get(pos);
            ContentHolder holder = ((ContentHolder) viewHolder);
            holder.userName.setText(notificationBean.getToUserAccount());

            holder.userInfo.setVisibility(View.VISIBLE);
            holder.userInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, notificationBean.getFromUserId());
                }
            });
            if(StringUtil.isNotNull(notificationBean.getToUserPicPath())){
                ImageCacheManager.loadImage(notificationBean.getToUserPicPath(), holder.userPic);
                holder.userPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonHandler.startPersonalActivity(mContext, notificationBean.getFromUserId());
                    }
                });
            }else{
                holder.userPic.setImageResource(R.drawable.no_pic);
            }

            Spannable spannable= AppUtil.textviewShowImg(mContext, StringUtil.changeNotNull(notificationBean.getContent()));
            holder.content.setText(spannable);

            String source = notificationBean.getSource();
            if(StringUtil.isNotNull(source)){
                holder.source.setVisibility(View.VISIBLE);
                Spannable spannable1 = AppUtil.textviewShowImg(mContext, source);
                holder.source.setText(spannable1);
            }else{
                holder.source.setVisibility(View.GONE);
            }

            holder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(notificationBean.getCreateTime())));
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mOnItemLongClickListener != null)
                        mOnItemLongClickListener.onItemLongClick(position);
                    return true;
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(position, null);
                }
            });
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView source;
        ImageView userPic;
        TextView userName;
        TextView time;
        LinearLayout userInfo;
        public ContentHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            content = (TextView)itemView.findViewById(R.id.notification_content);
            source = (TextView)itemView.findViewById(R.id.notification_source);
            userPic = (ImageView)itemView.findViewById(R.id.notification_user_pic);
            userName = (TextView)itemView.findViewById(R.id.notification_user_name);
            time = (TextView)itemView.findViewById(R.id.notification_time);
            userInfo = (LinearLayout)itemView.findViewById(R.id.notification_user_info);
        }
    }
}
