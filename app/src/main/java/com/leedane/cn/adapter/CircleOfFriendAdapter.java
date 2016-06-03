package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.AttentionBean;
import com.leedane.cn.bean.TimeLineBean;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 朋友圈列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/15.
 */
public class CircleOfFriendAdapter extends BaseAdapter{
    private Context mContext;
    private List<TimeLineBean> mTimeLineBeans;
    private int loginUserId;
    private String loginUserPicPath;

    public CircleOfFriendAdapter(Context context, List<TimeLineBean> timeLineBeans) {
        this.mContext = context;
        this.mTimeLineBeans = timeLineBeans;
        loginUserId = BaseApplication.getLoginUserId();
        loginUserPicPath = BaseApplication.getLoginUserPicPath();
    }

    @Override
    public int getCount() {
        return mTimeLineBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mTimeLineBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final TimeLineBean timeLineBean = mTimeLineBeans.get(position);
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

        viewHolder.getmFrom().setText(timeLineBean.getFroms());

        viewHolder.getmUserInfo().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, timeLineBean.getCreateUserId());
            }
        });

        if(timeLineBean.getCreateUserId() == loginUserId){
            if(StringUtil.isNotNull(loginUserPicPath))
                ImageCacheManager.loadImage(loginUserPicPath, viewHolder.getmUserPic(), 30, 30);
            else{
                viewHolder.getmUserPic().setImageResource(R.drawable.no_pic);
            }
        }else{
            if(timeLineBean.getUserPicPath() != null)
                ImageCacheManager.loadImage(timeLineBean.getUserPicPath(), viewHolder.getmUserPic(), 30, 30);
            else{
                viewHolder.getmUserPic().setImageResource(R.drawable.no_pic);
            }
        }

        if(StringUtil.isNotNull(timeLineBean.getSource())){
            viewHolder.getmSource().setText(timeLineBean.getSource());
            viewHolder.getmSource().setVisibility(View.VISIBLE);
        }else{
            viewHolder.getmSource().setVisibility(View.GONE);
        }

        viewHolder.getmContent().setText(timeLineBean.getContent());
        return view;
    }

    public void refreshData(List<TimeLineBean> timeLineBeans){
        this.mTimeLineBeans.clear();
        this.mTimeLineBeans.addAll(timeLineBeans);
        this.notifyDataSetChanged();
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
