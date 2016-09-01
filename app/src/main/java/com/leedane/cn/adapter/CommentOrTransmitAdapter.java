package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.CommentOrTransmitBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
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
            viewHolder.setmContent((TextView) view.findViewById(R.id.comment_content));
            viewHolder.setmFrom((TextView) view.findViewById(R.id.comment_from));
            viewHolder.setmTime((TextView) view.findViewById(R.id.comment_time));
            if(showUserInfo){
                viewHolder.setmUserInfo((LinearLayout)view.findViewById(R.id.comment_user_info));
                viewHolder.setmUserName((TextView) view.findViewById(R.id.comment_user_name));
                viewHolder.setmUserPic((ImageView) view.findViewById(R.id.comment_user_pic));
            }

            viewHolder.setmSource((TextView) view.findViewById(R.id.comment_source));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmContent().setText(commentOrTransmitBean.getContent());
        AppUtil.textviewShowImg(mContext, viewHolder.getmContent());
        viewHolder.getmFrom().setText("来自：" +StringUtil.changeNotNull(commentOrTransmitBean.getFroms()));
        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(commentOrTransmitBean.getCreateTime())));
        if(StringUtil.isNotNull(commentOrTransmitBean.getSource())){
            viewHolder.getmSource().setText(commentOrTransmitBean.getSource());
            viewHolder.getmSource().setVisibility(View.VISIBLE);
            AppUtil.textviewShowImg(mContext, viewHolder.getmSource());
        }else{
            viewHolder.getmSource().setVisibility(View.GONE);
        }

        if(showUserInfo){
            viewHolder.getmUserInfo().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, commentOrTransmitBean.getCreateUserId());
                }
            });
            viewHolder.getmUserInfo().setVisibility(View.VISIBLE);
            viewHolder.getmUserName().setText(commentOrTransmitBean.getAccount());
            if(commentOrTransmitBean.getUserPicPath() != null)
                ImageCacheManager.loadImage(commentOrTransmitBean.getUserPicPath(), viewHolder.getmUserPic(), 30, 30);
            else
                viewHolder.getmUserPic().setImageResource(R.drawable.no_pic);
        }
        //设置动画效果
        setAnimation(view, position);
        return view;
    }

    private class ViewHolder{
        private TextView mContent;
        private ImageView mUserPic;
        private TextView mUserName;
        private TextView mFrom;
        private TextView mTime;
        private TextView mSource;
        private LinearLayout mUserInfo;

        public TextView getmContent() {
            return mContent;
        }

        public void setmContent(TextView mContent) {
            this.mContent = mContent;
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

        public TextView getmFrom() {
            return mFrom;
        }

        public void setmFrom(TextView mFrom) {
            this.mFrom = mFrom;
        }

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }

        public TextView getmSource() {
            return mSource;
        }

        public void setmSource(TextView mSource) {
            this.mSource = mSource;
        }

        public LinearLayout getmUserInfo() {
            return mUserInfo;
        }

        public void setmUserInfo(LinearLayout mUserInfo) {
            this.mUserInfo = mUserInfo;
        }
    }
}
