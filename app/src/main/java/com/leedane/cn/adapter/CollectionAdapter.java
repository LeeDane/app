package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.CollectionBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 收藏列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/6.
 */
public class CollectionAdapter extends BaseAdapter{
    private Context mContext;
    private List<CollectionBean> mCollectionBeans;

    public CollectionAdapter(Context context, List<CollectionBean> collectionBeans) {
        this.mContext = context;
        this.mCollectionBeans = collectionBeans;
    }

    @Override
    public int getCount() {
        return mCollectionBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mCollectionBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        CollectionBean collectionBean = mCollectionBeans.get(position);
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
        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(collectionBean.getCreateTime())));
        /*viewHolder.getmUserName().setText(collectionBean.getAccount());
        if(collectionBean.getUserPicPath() != null)
            ImageCacheManager.loadImage(collectionBean.getUserPicPath(), viewHolder.getmUserPic(), 30, 30);*/

        if(StringUtil.isNotNull(collectionBean.getSource())){
            viewHolder.getmSource().setText(collectionBean.getSource());
            viewHolder.getmSource().setVisibility(View.VISIBLE);
        }else{
            viewHolder.getmSource().setVisibility(View.GONE);
        }
        return view;
    }

    public void refreshData(List<CollectionBean> collectionBeans){
        this.mCollectionBeans.clear();
        this.mCollectionBeans.addAll(collectionBeans);
        this.notifyDataSetChanged();
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
