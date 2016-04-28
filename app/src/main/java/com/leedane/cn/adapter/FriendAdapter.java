package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.bean.CommentOrTransmitBean;
import com.leedane.cn.bean.FriendBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 好友列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/19.
 */
public class FriendAdapter extends BaseAdapter{
    private Context mContext;
    private List<FriendBean> mFriendBeans;
    public FriendAdapter(Context context, List<FriendBean> friendBeans) {
        this.mContext = context;
        this.mFriendBeans = friendBeans;
    }

    @Override
    public int getCount() {
        return mFriendBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mFriendBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final FriendBean friendBean = mFriendBeans.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_comment_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmContent((TextView) view.findViewById(R.id.comment_content));
            viewHolder.setmFrom((TextView) view.findViewById(R.id.comment_from));
            viewHolder.setmTime((TextView) view.findViewById(R.id.comment_time));

            viewHolder.setmSource((TextView) view.findViewById(R.id.comment_source));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        return view;
    }

    public void refreshData(List<FriendBean> friendBeans){
        this.mFriendBeans.clear();
        this.mFriendBeans.addAll(friendBeans);
        this.notifyDataSetChanged();
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
