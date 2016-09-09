package com.leedane.cn.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.CommentOrTransmitBean;
import com.leedane.cn.customview.MoodTextView;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.AppUtil;
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
public class CommentOrTransmitAdapter extends BaseListAdapter<CommentOrTransmitBean>{
    private boolean showUserInfo;

    public CommentOrTransmitAdapter(Context context, List<CommentOrTransmitBean> commentOrTransmitBeans) {
        super(context, commentOrTransmitBeans);
    }

    public CommentOrTransmitAdapter(Context context, List<CommentOrTransmitBean> commentOrTransmitBeans, boolean showUserInfo) {
        super(context, commentOrTransmitBeans);
        this.showUserInfo = showUserInfo;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final CommentOrTransmitBean commentOrTransmitBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_comment_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.content = (MoodTextView) view.findViewById(R.id.comment_content);
            viewHolder.from = (TextView) view.findViewById(R.id.comment_from);
            viewHolder.time = (TextView) view.findViewById(R.id.comment_time);
            if(showUserInfo){
                viewHolder.userInfo = (LinearLayout)view.findViewById(R.id.comment_user_info);
                viewHolder.userName = (TextView) view.findViewById(R.id.comment_user_name);
                viewHolder.userPic = (ImageView) view.findViewById(R.id.comment_user_pic);
            }
            viewHolder.source = (TextView) view.findViewById(R.id.comment_source);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();

        Spannable spannable= AppUtil.textviewShowImg(mContext, commentOrTransmitBean.getContent());
        spannable = AppUtil.textviewShowTopic(mContext, spannable, new AppUtil.ClickTextAction() {
            @Override
            public void call(String str) {
                ToastUtil.success(mContext, "点击："+str);
            }
        });
        viewHolder.content.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.content.setFocusable(false);
        viewHolder.content.setDispatchToParent(true);
        viewHolder.content.setLongClickable(false);
        viewHolder.content.setText(spannable);

        viewHolder.from.setTypeface(typeface);
        viewHolder.from.setText("来自：" + StringUtil.changeNotNull(commentOrTransmitBean.getFroms()));

        viewHolder.time.setTypeface(typeface);
        viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(commentOrTransmitBean.getCreateTime())));
        if(StringUtil.isNotNull(commentOrTransmitBean.getSource())){
            viewHolder.source.setVisibility(View.VISIBLE);
            Spannable spannable1= AppUtil.textviewShowImg(mContext, commentOrTransmitBean.getSource());
            viewHolder.source.setText(spannable1);
        }else{
            viewHolder.source.setVisibility(View.GONE);
        }

        if(showUserInfo){
            viewHolder.userInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, commentOrTransmitBean.getCreateUserId());
                }
            });
            viewHolder.userInfo.setVisibility(View.VISIBLE);
            viewHolder.userName.setText(commentOrTransmitBean.getAccount());
            if(commentOrTransmitBean.getUserPicPath() != null)
                ImageCacheManager.loadImage(commentOrTransmitBean.getUserPicPath(), viewHolder.userPic, 30, 30);
            else
                viewHolder.userPic.setImageResource(R.drawable.no_pic);
        }
        //设置动画效果
        setAnimation(view, position);
        return view;
    }

    static class ViewHolder{
        MoodTextView content;
        ImageView userPic;
        TextView userName;
        TextView from;
        TextView time;
        TextView source;
        LinearLayout userInfo;
    }
}
