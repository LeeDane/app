package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.ChatBean;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 聊天首页列表的适配器
 * Created by LeeDane on 2016/5/4.
 */
public class ChatAdapter extends BaseAdapter{

    public static final String TAG = "ChatAdapter";

    public List<ChatBean> mList;  //所有聊天列表
    private Context mContext; //上下文对象

    public ChatAdapter(List<ChatBean> list, Context context){
        super();
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshData(List<ChatBean> chatBeans){
        this.mList.clear();
        this.mList.addAll(chatBeans);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_listview, null);
            viewHolder = new MyHolder();
            viewHolder.setmCreateTime((TextView) convertView.findViewById(R.id.chat_time));
            viewHolder.setmAccount((TextView) convertView.findViewById(R.id.chat_account));
            viewHolder.setmUserPicPath((CircularImageView) convertView.findViewById(R.id.chat_user_pic));
            viewHolder.setmContent((TextView) convertView.findViewById(R.id.chat_content));
            viewHolder.setmNoReadNumber((TextView)convertView.findViewById(R.id.chat_no_read));
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (MyHolder)convertView.getTag();
        }
        ChatBean chatBean = mList.get(position);

        String createTime = chatBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            viewHolder.getmCreateTime().setText("");
        }else{
            viewHolder.getmCreateTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        String userPicPath = chatBean.getUserPicPath();
        if(StringUtil.isNotNull(userPicPath)){
            ImageCacheManager.loadImage(userPicPath, viewHolder.getmUserPicPath());
        }else{
            viewHolder.getmUserPicPath().setImageResource(R.drawable.no_pic);
        }
        viewHolder.getmAccount().setText(chatBean.getAccount());
        viewHolder.getmContent().setText(chatBean.getContent());
        AppUtil.textviewShowImg(mContext, viewHolder.getmContent());
        if(StringUtil.changeObjectToInt(chatBean.getNoReadNumber()) > 0){
            viewHolder.getmNoReadNumber().setBackgroundResource(R.drawable.chat_has_number_tip_bg);
        }else{
            viewHolder.getmNoReadNumber().setBackgroundResource(R.drawable.chat_no_number_tip_bg);
        }
        viewHolder.getmNoReadNumber().setText(StringUtil.changeObjectToInt(chatBean.getNoReadNumber()) +"");
        return convertView;
    }

    private class MyHolder{
        /**
         * 聊天的内容
         */
        private TextView mContent;

        /**
         * 用户的头像
         */
        private CircularImageView mUserPicPath;

        /**
         * 用户的账号名称
         */
        private TextView mAccount;

        /**
         * 创建时间
         */
        private TextView mCreateTime;

        /**
         * 未读取数量
         */
        private TextView mNoReadNumber;

        public TextView getmAccount() {
            return mAccount;
        }

        public void setmAccount(TextView mAccount) {
            this.mAccount = mAccount;
        }

        public TextView getmContent() {
            return mContent;
        }

        public void setmContent(TextView mContent) {
            this.mContent = mContent;
        }

        public TextView getmCreateTime() {
            return mCreateTime;
        }

        public void setmCreateTime(TextView mCreateTime) {
            this.mCreateTime = mCreateTime;
        }

        public CircularImageView getmUserPicPath() {
            return mUserPicPath;
        }

        public void setmUserPicPath(CircularImageView mUserPicPath) {
            this.mUserPicPath = mUserPicPath;
        }

        public TextView getmNoReadNumber() {
            return mNoReadNumber;
        }

        public void setmNoReadNumber(TextView mNoReadNumber) {
            this.mNoReadNumber = mNoReadNumber;
        }
    }
}
