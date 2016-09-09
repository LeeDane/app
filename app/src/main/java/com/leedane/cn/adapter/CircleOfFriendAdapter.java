package com.leedane.cn.adapter;

import android.content.Context;
import android.text.Spannable;
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
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 朋友圈列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/15.
 */
public class CircleOfFriendAdapter extends BaseListAdapter<TimeLineBean>{
    private int loginUserId;
    private String loginUserPicPath;

    public CircleOfFriendAdapter(Context context, List<TimeLineBean> timeLineBeans) {
        super(context, timeLineBeans);
        loginUserId = BaseApplication.getLoginUserId();
        loginUserPicPath = BaseApplication.getLoginUserPicPath();
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final TimeLineBean timeLineBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_circle_of_friend_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) view.findViewById(R.id.circle_time);
            viewHolder.content = (TextView) view.findViewById(R.id.circle_content);
            viewHolder.source = (TextView) view.findViewById(R.id.circle_source);
            viewHolder.from = (TextView) view.findViewById(R.id.circle_from);
            viewHolder.userName = (TextView) view.findViewById(R.id.circle_user_name);
            viewHolder.userPic = (CircularImageView) view.findViewById(R.id.circle_user_pic);
            viewHolder.userInfo = (LinearLayout)view.findViewById(R.id.circle_user_info);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(timeLineBean.getCreateTime())));
        viewHolder.userName.setText(timeLineBean.getAccount());

        viewHolder.from.setText("来自：" + timeLineBean.getFroms());

        viewHolder.userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startPersonalActivity(mContext, timeLineBean.getCreateUserId());
            }
        });

        if(timeLineBean.getCreateUserId() == loginUserId){
            if(StringUtil.isNotNull(loginUserPicPath))
                ImageCacheManager.loadImage(loginUserPicPath, viewHolder.userPic, 45, 45);
            else{
                viewHolder.userPic.setImageResource(R.drawable.no_pic);
            }
        }else{
            if(timeLineBean.getUserPicPath() != null)
                ImageCacheManager.loadImage(timeLineBean.getUserPicPath(), viewHolder.userPic, 45, 45);
            else{
                viewHolder.userPic.setImageResource(R.drawable.no_pic);
            }
        }

        if(StringUtil.isNotNull(timeLineBean.getSource())){
            Spannable spannable= AppUtil.textviewShowImg(mContext, timeLineBean.getSource());
            viewHolder.source.setText(spannable);
            viewHolder.source.setVisibility(View.VISIBLE);
        }else{
            viewHolder.source.setVisibility(View.GONE);
        }

        Spannable spannable= AppUtil.textviewShowImg(mContext, timeLineBean.getContent());
        viewHolder.content.setText(spannable);
        return view;
    }

    static class ViewHolder{
        LinearLayout userInfo;
        CircularImageView userPic;
        TextView userName;
        TextView time;
        TextView content;
        TextView source;
        TextView from;
    }
}
