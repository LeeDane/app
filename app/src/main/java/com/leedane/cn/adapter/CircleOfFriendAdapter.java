package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.TimeLineBean;
import com.leedane.cn.customview.CircularImageView;
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
 * 朋友圈列表数据展示的adapter对象
 * Created by LeeDane on 2018/3/27.
 */
public class CircleOfFriendAdapter extends BaseRecyclerViewAdapter<TimeLineBean> {
    private int loginUserId;
    private String loginUserPicPath;

    public CircleOfFriendAdapter(Context context, List<TimeLineBean> timeLineBeans) {
        super(context, timeLineBeans);
        loginUserId = BaseApplication.getLoginUserId();
        loginUserPicPath = BaseApplication.getLoginUserPicPath();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_circle_of_friend_listview, parent, false);
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
            final TimeLineBean timeLineBean = mDatas.get(pos);
            ContentHolder holder = ((ContentHolder) viewHolder);
            holder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(timeLineBean.getCreateTime())));
            holder.userName.setText(timeLineBean.getAccount());

            holder.from.setText("来自：" + timeLineBean.getFroms());

            holder.userInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, timeLineBean.getCreateUserId());
                }
            });

            if(timeLineBean.getCreateUserId() == loginUserId){
                if(StringUtil.isNotNull(loginUserPicPath))
                    ImageCacheManager.loadImage(loginUserPicPath, holder.userPic, 45, 45);
                else{
                    holder.userPic.setImageResource(R.drawable.no_pic);
                }
            }else{
                if(timeLineBean.getUserPicPath() != null)
                    ImageCacheManager.loadImage(timeLineBean.getUserPicPath(), holder.userPic, 45, 45);
                else{
                    holder.userPic.setImageResource(R.drawable.no_pic);
                }
            }

            if(StringUtil.isNotNull(timeLineBean.getSource())){
                Spannable spannable= AppUtil.textviewShowImg(mContext, timeLineBean.getSource());
                holder.source.setText(spannable);
                holder.source.setVisibility(View.VISIBLE);
            }else{
                holder.source.setVisibility(View.GONE);
            }

            Spannable spannable= AppUtil.textviewShowImg(mContext, timeLineBean.getContent());
            holder.content.setText(spannable);
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
        LinearLayout userInfo;
        CircularImageView userPic;
        TextView userName;
        TextView time;
        TextView content;
        TextView source;
        TextView from;
        public ContentHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            userInfo = (LinearLayout)itemView.findViewById(R.id.circle_user_info);
            userPic = (CircularImageView)itemView.findViewById(R.id.circle_user_pic);
            userName = (TextView)itemView.findViewById(R.id.circle_user_name);
            time = (TextView)itemView.findViewById(R.id.circle_time);
            content = (TextView)itemView.findViewById(R.id.circle_content);
            source = (TextView)itemView.findViewById(R.id.circle_source);
            from = (TextView)itemView.findViewById(R.id.circle_from);
        }
    }
}
