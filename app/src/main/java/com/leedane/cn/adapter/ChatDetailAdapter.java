package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatDetailBean;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 聊天详情列表数据展示的adapter对象
 * Created by LeeDane on 2016/5/5.
 */
public class ChatDetailAdapter extends BaseListAdapter<ChatDetailBean>{
    private String userPicPath;
    private String toUserPicPath;

    public ChatDetailAdapter(Context context, List<ChatDetailBean> chatDetailBeans, String userPicPath, String toUserPicPath) {
        super(context, chatDetailBeans);
        this.userPicPath = userPicPath;
        this.toUserPicPath = toUserPicPath;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final ChatDetailBean chatDetailBean = mDatas.get(position);
        ViewHolder viewHolder;

        //对方使用left
        if(chatDetailBean.getCreateUserId() != BaseApplication.getLoginUserId()){
            if(view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_detail_listview_left, null);
                viewHolder = new ViewHolder();
                TextView tContent = (TextView) view.findViewById(R.id.chat_detail_content);
                tContent.setTextSize(MySettingConfigUtil.getChatTextSize());
                viewHolder.setmContent(tContent);
                viewHolder.setmUserPicPath((CircularImageView) view.findViewById(R.id.chat_detail_user_pic));
                viewHolder.setmTime((TextView)view.findViewById(R.id.chat_detail_time));
                view.setTag(R.string.chat_detail_layout_left, viewHolder);
            }
            viewHolder = (ViewHolder)view.getTag(R.string.chat_detail_layout_left);
            if(viewHolder == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_detail_listview_left, null);
                viewHolder = new ViewHolder();
                TextView tContent = (TextView) view.findViewById(R.id.chat_detail_content);
                tContent.setTextSize(MySettingConfigUtil.getChatTextSize());
                viewHolder.setmContent(tContent);
                viewHolder.setmUserPicPath((CircularImageView) view.findViewById(R.id.chat_detail_user_pic));
                viewHolder.setmTime((TextView) view.findViewById(R.id.chat_detail_time));
                view.setTag(R.string.chat_detail_layout_left, viewHolder);
            }

            viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(chatDetailBean.getCreateTime())));
            viewHolder.getmContent().setText(chatDetailBean.getContent());
            AppUtil.textviewShowImg(mContext, viewHolder.getmContent());
            viewHolder.getmUserPicPath().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, chatDetailBean.getCreateUserId());
                }
            });
            if(StringUtil.isNotNull(toUserPicPath))
                ImageCacheManager.loadImage(toUserPicPath, viewHolder.getmUserPicPath(), 30, 30);
            else{
                viewHolder.getmUserPicPath().setImageResource(R.drawable.no_pic);
            }
        }else{//自己使用right
            if(view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_detail_listview_right, null);
                viewHolder = new ViewHolder();
                TextView tContent = (TextView) view.findViewById(R.id.chat_detail_content);
                tContent.setTextSize(MySettingConfigUtil.getChatTextSize());
                viewHolder.setmContent(tContent);
                viewHolder.setmUserPicPath((CircularImageView) view.findViewById(R.id.chat_detail_user_pic));
                viewHolder.setmTime((TextView) view.findViewById(R.id.chat_detail_time));
                view.setTag(R.string.chat_detail_layout_right, viewHolder);
            }
            viewHolder = (ViewHolder)view.getTag(R.string.chat_detail_layout_right);
            if(viewHolder == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_detail_listview_right, null);
                viewHolder = new ViewHolder();
                TextView tContent = (TextView) view.findViewById(R.id.chat_detail_content);
                tContent.setTextSize(MySettingConfigUtil.getChatTextSize());
                viewHolder.setmContent(tContent);
                viewHolder.setmUserPicPath((CircularImageView) view.findViewById(R.id.chat_detail_user_pic));
                viewHolder.setmTime((TextView) view.findViewById(R.id.chat_detail_time));
                view.setTag(R.string.chat_detail_layout_right, viewHolder);
            }

            viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(chatDetailBean.getCreateTime())));
            viewHolder.getmContent().setText(chatDetailBean.getContent());
            AppUtil.textviewShowImg(mContext, viewHolder.getmContent());
            viewHolder.getmUserPicPath().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, chatDetailBean.getCreateUserId());
                }
            });
            if(userPicPath != null)
                //viewHolder.getmUserPicPath().setImageBitmap(userPicBitMap);
                ImageCacheManager.loadImage(userPicPath, viewHolder.getmUserPicPath(), 30, 30);
            else{
                viewHolder.getmUserPicPath().setImageResource(R.drawable.no_pic);
            }
        }
        return view;
    }

    private class ViewHolder{
        private TextView mTime;
        private CircularImageView mUserPicPath;
        private TextView mContent;

        public TextView getmContent() {
            return mContent;
        }

        public void setmContent(TextView mContent) {
            this.mContent = mContent;
        }

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }

        public CircularImageView getmUserPicPath() {
            return mUserPicPath;
        }

        public void setmUserPicPath(CircularImageView mUserPicPath) {
            this.mUserPicPath = mUserPicPath;
        }
    }
}
