package com.leedane.cn.adapter;

import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
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
public class ChatAdapter extends BaseListAdapter<ChatBean>{

    public static final String TAG = "ChatAdapter";
    public ChatAdapter(List<ChatBean> list, Context context){
        super(context, list);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MyHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_listview, null);
            viewHolder = new MyHolder();
            viewHolder.createTime = (TextView) view.findViewById(R.id.chat_time);
            viewHolder.account = (TextView) view.findViewById(R.id.chat_account);
            viewHolder.userPicPath = (CircularImageView) view.findViewById(R.id.chat_user_pic);
            viewHolder.content = (TextView) view.findViewById(R.id.chat_content);
            viewHolder.noReadNumber = (TextView)view.findViewById(R.id.chat_no_read);
            view.setTag(viewHolder);
        }else{
            viewHolder = (MyHolder)view.getTag();
        }
        ChatBean chatBean = mDatas.get(position);

        String createTime = chatBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            viewHolder.createTime.setText("");
        }else{
            viewHolder.createTime.setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        String userPicPath = chatBean.getUserPicPath();
        if(StringUtil.isNotNull(userPicPath)){
            ImageCacheManager.loadImage(userPicPath, viewHolder.userPicPath);
        }else{
            viewHolder.userPicPath.setImageResource(R.drawable.no_pic);
        }
        viewHolder.account.setText(chatBean.getAccount());
        viewHolder.content.setText(chatBean.getContent());

        Spannable spannable= AppUtil.textviewShowImg(mContext, chatBean.getContent());
        viewHolder.content.setText(spannable);

        if(StringUtil.changeObjectToInt(chatBean.getNoReadNumber()) > 0){
            viewHolder.noReadNumber.setBackgroundResource(R.drawable.chat_has_number_tip_bg);
        }else{
            viewHolder.noReadNumber.setBackgroundResource(R.drawable.chat_no_number_tip_bg);
        }
        viewHolder.noReadNumber.setText(StringUtil.changeObjectToInt(chatBean.getNoReadNumber()) + "");
        //设置动画效果
        //setAnimation(view, position);
        return view;
    }

    static class MyHolder{
        /**
         * 聊天的内容
         */
        TextView content;

        /**
         * 用户的头像
         */
        CircularImageView userPicPath;

        /**
         * 用户的账号名称
         */
        TextView account;

        /**
         * 创建时间
         */
        TextView createTime;

        /**
         * 未读取数量
         */
        TextView noReadNumber;

    }
}
