package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.NotificationBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 通知消息列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/27.
 */
public class NotificationAdapter extends BaseAdapter{
    private Context mContext;
    private List<NotificationBean> mNotificationBeans;

    int userId = 0;
    public NotificationAdapter(Context context, List<NotificationBean> notificationBeans) {
        this.mContext = context;
        this.mNotificationBeans = notificationBeans;
        userId = BaseApplication.getLoginUserId();
    }
    @Override
    public int getCount() {
        return mNotificationBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mNotificationBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final NotificationBean notificationBean = mNotificationBeans.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_notification_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmUserName((TextView) view.findViewById(R.id.notification_user_name));
            viewHolder.setmUserPic((ImageView) view.findViewById(R.id.notification_user_pic));
            viewHolder.setmTime((TextView) view.findViewById(R.id.notification_time));
            viewHolder.setmUserInfo((LinearLayout) view.findViewById(R.id.notification_user_info));
            viewHolder.setmContent((TextView) view.findViewById(R.id.notification_content));
            viewHolder.setmSource((TextView)view.findViewById(R.id.notification_source));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmUserName().setText(notificationBean.getToUserAccount());

        viewHolder.getmUserInfo().setVisibility(View.VISIBLE);
        viewHolder.getmUserInfo().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startPersonalActivity(mContext, notificationBean.getFromUserId());
            }
        });
        if(StringUtil.isNotNull(notificationBean.getToUserPicPath())){
            ImageCacheManager.loadImage(notificationBean.getToUserPicPath(), viewHolder.getmUserPic());
            viewHolder.getmUserPic().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, notificationBean.getFromUserId());
                }
            });
        }else{
            viewHolder.getmUserPic().setImageResource(R.drawable.no_pic);
        }
        viewHolder.getmContent().setText(StringUtil.changeNotNull(notificationBean.getContent()));
        AppUtil.textviewShowImg(mContext, viewHolder.getmContent());
        String source = notificationBean.getSource();
        if(StringUtil.isNotNull(source)){
            viewHolder.getmSource().setVisibility(View.VISIBLE);
            viewHolder.getmSource().setText(source);
            AppUtil.textviewShowImg(mContext, viewHolder.getmSource());
        }else{
            viewHolder.getmSource().setVisibility(View.GONE);
        }

        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(notificationBean.getCreateTime())));
        return view;
    }

    public void refreshData(List<NotificationBean> NotificationBeans){
        this.mNotificationBeans.clear();
        this.mNotificationBeans.addAll(NotificationBeans);
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        private TextView mContent;
        private TextView mSource;
        private ImageView mUserPic;
        private TextView mUserName;
        private TextView mTime;
        private LinearLayout mUserInfo;

        public TextView getmContent() {
            return mContent;
        }

        public void setmContent(TextView mContent) {
            this.mContent = mContent;
        }

        public TextView getmSource() {
            return mSource;
        }

        public void setmSource(TextView mSource) {
            this.mSource = mSource;
        }

        public ImageView getmUserPic() {
            return mUserPic;
        }

        public void setmUserPic(ImageView mUserPic) {
            this.mUserPic = mUserPic;
        }

        public TextView getmUserName() {
            return mUserName;
        }

        public void setmUserName(TextView mUserName) {
            this.mUserName = mUserName;
        }

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }

        public LinearLayout getmUserInfo() {
            return mUserInfo;
        }

        public void setmUserInfo(LinearLayout mUserInfo) {
            this.mUserInfo = mUserInfo;
        }
    }
}
