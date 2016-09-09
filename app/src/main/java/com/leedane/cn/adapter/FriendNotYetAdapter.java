package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.FriendBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 暂时还未是好友列表数据展示的adapter对象
 * Created by LeeDane on 2016/6/23.
 */
public class FriendNotYetAdapter extends BaseListAdapter<FriendBean>{
    public FriendNotYetAdapter(Context context, List<FriendBean> friendBeans) {
        super(context, friendBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final FriendBean friendBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_no_yet_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.userPic = (ImageView) view.findViewById(R.id.friend_no_yet_user_pic);
            viewHolder.account = (TextView) view.findViewById(R.id.friend_no_yet_user_name);
            viewHolder.time = (TextView) view.findViewById(R.id.friend_no_yet_time);
            TextView introduce = (TextView)view.findViewById(R.id.friend_no_yet_introduce);
            introduce.setSelected(true);
            viewHolder.introduce = introduce;
            viewHolder.operate = (TextView) view.findViewById(R.id.friend_no_yet_operate);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        if(StringUtil.isNotNull(friendBean.getUserPicPath()))
            ImageCacheManager.loadImage(friendBean.getUserPicPath(), viewHolder.userPic, 30, 30);
        if(StringUtil.isNotNull(friendBean.getAccount()) && !friendBean.getAccount().equals(friendBean.getRemark())){
            viewHolder.account.setText(friendBean.getAccount() + (StringUtil.isNotNull(friendBean.getRemark()) ? "(" + friendBean.getRemark() + ")" : ""));
        }else{
            viewHolder.account.setText(StringUtil.changeNotNull(friendBean.getRemark()));
        }
        if(StringUtil.isNotNull(friendBean.getCreateTime()))
            viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(friendBean.getCreateTime())));
        else
            viewHolder.time.setText("");
        viewHolder.introduce.setText(StringUtil.changeNotNull(friendBean.getIntroduce()));

        if(friendBean.getStatus() == 0){
            viewHolder.operate.setText("同意添加");
        }else{
            viewHolder.operate.setText("发送邮件提醒TA");
        }
        //设置动画效果
        setAnimation(view, position);
        return view;
    }

    static class ViewHolder{
        ImageView userPic;
        TextView account;
        TextView time;
        TextView introduce;
        TextView operate;
    }
}
