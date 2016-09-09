package com.leedane.cn.adapter;

import android.content.Context;
import android.text.Spannable;
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
            viewHolder.userName = (TextView) view.findViewById(R.id.notification_user_name);
            viewHolder.userPic = (ImageView) view.findViewById(R.id.notification_user_pic);
            viewHolder.time = (TextView) view.findViewById(R.id.notification_time);
            viewHolder.userInfo = (LinearLayout) view.findViewById(R.id.notification_user_info);
            viewHolder.content = (TextView) view.findViewById(R.id.notification_content);
            viewHolder.source = (TextView)view.findViewById(R.id.notification_source);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.userName.setText(notificationBean.getToUserAccount());

        viewHolder.userInfo.setVisibility(View.VISIBLE);
        viewHolder.userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startPersonalActivity(mContext, notificationBean.getFromUserId());
            }
        });
        if(StringUtil.isNotNull(notificationBean.getToUserPicPath())){
            ImageCacheManager.loadImage(notificationBean.getToUserPicPath(), viewHolder.userPic);
            viewHolder.userPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, notificationBean.getFromUserId());
                }
            });
        }else{
            viewHolder.userPic.setImageResource(R.drawable.no_pic);
        }

        Spannable spannable= AppUtil.textviewShowImg(mContext, StringUtil.changeNotNull(notificationBean.getContent()));
        viewHolder.content.setText(spannable);

        String source = notificationBean.getSource();
        if(StringUtil.isNotNull(source)){
            viewHolder.source.setVisibility(View.VISIBLE);
            Spannable spannable1 = AppUtil.textviewShowImg(mContext, source);
            viewHolder.source.setText(spannable1);
        }else{
            viewHolder.source.setVisibility(View.GONE);
        }

        viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(notificationBean.getCreateTime())));
        return view;
    }

    public void refreshData(List<NotificationBean> NotificationBeans){
        this.mNotificationBeans.clear();
        this.mNotificationBeans.addAll(NotificationBeans);
        this.notifyDataSetChanged();
    }

    static class ViewHolder{
        TextView content;
        TextView source;
        ImageView userPic;
        TextView userName;
        TextView time;
        LinearLayout userInfo;
    }
}
