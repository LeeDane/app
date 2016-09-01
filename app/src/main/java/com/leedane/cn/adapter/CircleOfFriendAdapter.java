package com.leedane.cn.adapter;

import android.content.Context;
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
            viewHolder.setmTime((TextView) view.findViewById(R.id.circle_time));
            viewHolder.setmContent((TextView) view.findViewById(R.id.circle_content));
            viewHolder.setmSource((TextView) view.findViewById(R.id.circle_source));
            viewHolder.setmFrom((TextView) view.findViewById(R.id.circle_from));
            viewHolder.setmUserName((TextView) view.findViewById(R.id.circle_user_name));
            viewHolder.setmUserPic((CircularImageView) view.findViewById(R.id.circle_user_pic));
            viewHolder.setmUserInfo((LinearLayout)view.findViewById(R.id.circle_user_info));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(timeLineBean.getCreateTime())));
        viewHolder.getmUserName().setText(timeLineBean.getAccount());

        viewHolder.getmFrom().setText("来自：" +timeLineBean.getFroms());

        viewHolder.getmUserInfo().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, timeLineBean.getCreateUserId());
            }
        });

        if(timeLineBean.getCreateUserId() == loginUserId){
            if(StringUtil.isNotNull(loginUserPicPath))
                ImageCacheManager.loadImage(loginUserPicPath, viewHolder.getmUserPic(), 45, 45);
            else{
                viewHolder.getmUserPic().setImageResource(R.drawable.no_pic);
            }
        }else{
            if(timeLineBean.getUserPicPath() != null)
                ImageCacheManager.loadImage(timeLineBean.getUserPicPath(), viewHolder.getmUserPic(), 45, 45);
            else{
                viewHolder.getmUserPic().setImageResource(R.drawable.no_pic);
            }
        }

        if(StringUtil.isNotNull(timeLineBean.getSource())){
            viewHolder.getmSource().setText(timeLineBean.getSource());
            AppUtil.textviewShowImg(mContext, viewHolder.getmSource());
            viewHolder.getmSource().setVisibility(View.VISIBLE);
        }else{
            viewHolder.getmSource().setVisibility(View.GONE);
        }

        viewHolder.getmContent().setText(timeLineBean.getContent());
        AppUtil.textviewShowImg(mContext, viewHolder.getmContent());
        return view;
    }

    private class ViewHolder{
        private LinearLayout mUserInfo;
        private CircularImageView mUserPic;
        private TextView mUserName;
        private TextView mTime;
        private TextView mContent;
        private TextView mSource;
        private TextView mFrom;

        public LinearLayout getmUserInfo() {
            return mUserInfo;
        }

        public void setmUserInfo(LinearLayout mUserInfo) {
            this.mUserInfo = mUserInfo;
        }

        public TextView getmContent() {
            return mContent;
        }

        public void setmContent(TextView mContent) {
            this.mContent = mContent;
        }

        public TextView getmFrom() {
            return mFrom;
        }

        public void setmFrom(TextView mFrom) {
            this.mFrom = mFrom;
        }

        public TextView getmSource() {
            return mSource;
        }

        public void setmSource(TextView mSource) {
            this.mSource = mSource;
        }

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }

        public TextView getmUserName() {
            return mUserName;
        }

        public void setmUserName(TextView mUserName) {
            this.mUserName = mUserName;
        }

        public CircularImageView getmUserPic() {
            return mUserPic;
        }

        public void setmUserPic(CircularImageView mUserPic) {
            this.mUserPic = mUserPic;
        }
    }
}
