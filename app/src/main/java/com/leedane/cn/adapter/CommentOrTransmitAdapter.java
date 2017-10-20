package com.leedane.cn.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.activity.TopicActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.CommentOrTransmitBean;
import com.leedane.cn.customview.MoodTextView;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 评论和转发列表数据展示的adapter对象
 * Created by LeeDane on 2015/11/14.
 */
public class CommentOrTransmitAdapter extends BaseRecyclerViewAdapter<CommentOrTransmitBean> {
    private boolean showUserInfo;

    public CommentOrTransmitAdapter(Context context, List<CommentOrTransmitBean> commentOrTransmitBeans) {
        super(context, commentOrTransmitBeans);
    }

    public CommentOrTransmitAdapter(Context context, List<CommentOrTransmitBean> commentOrTransmitBeans, boolean showUserInfo) {
        super(context, commentOrTransmitBeans);
        this.showUserInfo = showUserInfo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_listview, parent, false);
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
            final CommentOrTransmitBean commentOrTransmitBean = mDatas.get(pos);
            ContentHolder holder = ((ContentHolder) viewHolder);

            holder.content.setMovementMethod(LinkMovementMethod.getInstance());
            holder.content.setFocusable(false);
            holder.content.setDispatchToParent(true);
            holder.content.setLongClickable(false);
            holder.content.setText(AppUtil.textParsing(mContext, commentOrTransmitBean.getContent()));

            holder.from.setTypeface(typeface);
            holder.from.setText(StringUtil.changeNotNull("来自：", commentOrTransmitBean.getFroms()));

            holder.time.setTypeface(typeface);
            holder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(commentOrTransmitBean.getCreateTime())));
            if(StringUtil.isNotNull(commentOrTransmitBean.getSource())){
                holder.source.setVisibility(View.VISIBLE);
                //Spannable spannable1= AppUtil.textviewShowImg(mContext, commentOrTransmitBean.getSource());
                holder.source.setText(AppUtil.textParsing(mContext, commentOrTransmitBean.getSource()));
            }else{
                holder.source.setVisibility(View.GONE);
            }

            if(showUserInfo){
                holder.userInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonHandler.startPersonalActivity(mContext, commentOrTransmitBean.getCreateUserId());
                    }
                });
                holder.userInfo.setVisibility(View.VISIBLE);
                holder.userName.setText(commentOrTransmitBean.getAccount());
                if(StringUtil.isNotNull(commentOrTransmitBean.getUserPicPath()))
                    ImageCacheManager.loadImage(commentOrTransmitBean.getUserPicPath(), holder.userPic, 30, 30);
                else
                    holder.userPic.setImageResource(R.drawable.no_pic);
            }
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
        MoodTextView content;
        ImageView userPic;
        TextView userName;
        TextView from;
        TextView time;
        TextView source;
        LinearLayout userInfo;
        public ContentHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            content = (MoodTextView) itemView.findViewById(R.id.comment_content);
            from = (TextView) itemView.findViewById(R.id.comment_from);
            time = (TextView) itemView.findViewById(R.id.comment_time);
            if(showUserInfo){
                userInfo = (LinearLayout)itemView.findViewById(R.id.comment_user_info);
                userName = (TextView) itemView.findViewById(R.id.comment_user_name);
                userPic = (ImageView) itemView.findViewById(R.id.comment_user_pic);
            }
            source = (TextView) itemView.findViewById(R.id.comment_source);
        }
    }
}
