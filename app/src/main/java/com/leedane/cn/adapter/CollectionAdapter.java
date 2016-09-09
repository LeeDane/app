package com.leedane.cn.adapter;

import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.CollectionBean;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 收藏列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/6.
 */
public class CollectionAdapter extends BaseListAdapter<CollectionBean>{

    public CollectionAdapter(Context context, List<CollectionBean> collectionBeans) {
        super(context, collectionBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        CollectionBean collectionBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_collection_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmTime((TextView) view.findViewById(R.id.collection_time));
            /*viewHolder.setmUserName((TextView) view.findViewById(R.id.collection_user_name));
            viewHolder.setmUserPic((ImageView) view.findViewById(R.id.collection_user_pic));*/
            viewHolder.setmSource((TextView)view.findViewById(R.id.collection_source));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();

        viewHolder.getmTime().setTypeface(typeface);
        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(collectionBean.getCreateTime())));
        /*viewHolder.getmUserName().setText(collectionBean.getAccount());
        if(collectionBean.getUserPicPath() != null)
            ImageCacheManager.loadImage(collectionBean.getUserPicPath(), viewHolder.getmUserPic(), 30, 30);*/

        if(StringUtil.isNotNull(collectionBean.getSource())){
            viewHolder.getmSource().setVisibility(View.VISIBLE);
            Spannable spannable= AppUtil.textviewShowImg(mContext, collectionBean.getSource());
            viewHolder.getmSource().setText(spannable);
        }else{
            viewHolder.getmSource().setVisibility(View.GONE);
        }
        //设置动画效果
        setAnimation(view, position);
        return view;
    }

    private class ViewHolder{
        /*private ImageView mUserPic;
        private TextView mUserName;*/
        private TextView mTime;
        private TextView mSource;

        /*public ImageView getmUserPic() {
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
        }*/

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
    }
}
