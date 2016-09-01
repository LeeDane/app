package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.ZanUserBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 点赞用户列表数据展示的adapter对象
 * Created by LeeDane on 2016/5/21.
 */
public class ZanUserAdapter extends BaseListAdapter<ZanUserBean>{
    public ZanUserAdapter(Context context, List<ZanUserBean> zanUserBeans) {
        super(context, zanUserBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final ZanUserBean zanUserBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_zan_user_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmTime((TextView) view.findViewById(R.id.zan_user_time));
            viewHolder.setmUserName((TextView) view.findViewById(R.id.zan_user_name));
            viewHolder.setmUserPic((ImageView) view.findViewById(R.id.zan_user_pic));

            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();

        viewHolder.getmUserName().setText(zanUserBean.getAccount());
        viewHolder.getmUserName().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startPersonalActivity(mContext, zanUserBean.getCreateUserId());
            }
        });

        if(StringUtil.isNotNull(zanUserBean.getUserPicPath()))
            ImageCacheManager.loadImage(zanUserBean.getUserPicPath(), viewHolder.getmUserPic(), 40, 40);
        else{
            viewHolder.getmUserPic().setImageResource(R.drawable.no_pic);
        }

        viewHolder.getmUserPic().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startPersonalActivity(mContext, zanUserBean.getCreateUserId());
            }
        });
        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(zanUserBean.getCreateTime())));
        //设置动画效果
        setAnimation(view, position);
        return view;
    }

    private class ViewHolder{
        private ImageView mUserPic;
        private TextView mUserName;
        private TextView mTime;

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

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }
    }
}
