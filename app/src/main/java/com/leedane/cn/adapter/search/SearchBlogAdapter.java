package com.leedane.cn.adapter.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.search.SearchBlogBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 搜索博客列表的适配器
 * Created by LeeDane on 2016/5/22.
 */
public class SearchBlogAdapter extends BaseAdapter{

    public static final String TAG = "SearchBlogAdapter";

    public List<SearchBlogBean> mSearchBlogBeans;  //所有聊天列表
    private Context mContext; //上下文对象

    public SearchBlogAdapter(Context context, List<SearchBlogBean> searchBlogBeans){
        super();
        this.mSearchBlogBeans = searchBlogBeans;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mSearchBlogBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mSearchBlogBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshData(List<SearchBlogBean> searchBlogBeans){
        this.mSearchBlogBeans.clear();
        this.mSearchBlogBeans.addAll(searchBlogBeans);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_blog_listview, null);
            myHolder = new MyHolder();
            myHolder.setmCreateTime((TextView) convertView.findViewById(R.id.search_blog_time));
            myHolder.setmAccount((TextView) convertView.findViewById(R.id.search_blog_user_name));
            myHolder.setmPicPath((ImageView) convertView.findViewById(R.id.search_blog_user_pic));
            myHolder.setmSource((TextView) convertView.findViewById(R.id.search_blog_source));
            myHolder.setmTitle((TextView) convertView.findViewById(R.id.search_blog_title));
            myHolder.setmDigest((TextView) convertView.findViewById(R.id.search_blog_digest));
            myHolder.setmMainImg((ImageView) convertView.findViewById(R.id.search_blog_img_main));
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }
        SearchBlogBean searchBlogBean = mSearchBlogBeans.get(position);

        String createTime = searchBlogBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            myHolder.getmCreateTime().setText("");
        }else{
            myHolder.getmCreateTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        myHolder.getmAccount().setText(StringUtil.changeNotNull(searchBlogBean.getAccount()));
        if(StringUtil.isNotNull(searchBlogBean.getUserPicPath()))
            ImageCacheManager.loadImage(searchBlogBean.getUserPicPath(), myHolder.getmPicPath(), 30, 30);
        myHolder.getmSource().setText(StringUtil.changeNotNull(searchBlogBean.getSource()));
        myHolder.getmTitle().setText(StringUtil.changeNotNull(searchBlogBean.getTitle()));
        myHolder.getmDigest().setText(StringUtil.changeNotNull(searchBlogBean.getDigest()) +"...");
        if(StringUtil.isNotNull(searchBlogBean.getImgUrl())){
            myHolder.getmMainImg().setVisibility(View.VISIBLE);
            ImageCacheManager.loadImage(searchBlogBean.getImgUrl(), myHolder.getmMainImg(), 100, 50);
        }else{
            myHolder.getmMainImg().setVisibility(View.GONE);
        }
        return convertView;
    }

    private class MyHolder{
        /**
         * 搜索博客的用户的账号
         */
        private TextView mAccount;

        /**
         * 搜索心情的用户的头像路径
         */
        private ImageView mPicPath;

        /**
         * 搜索博客的来自
         */
        private TextView mSource;

        /**
         * 搜索博客的创建时间
         */
        private TextView mCreateTime;

        /**
         * 博客的标题
         */
        private TextView mTitle;

        /**
         * 博客的内容
         */
        private TextView mDigest;

        /**
         * 搜索博客的主图
         */
        private ImageView mMainImg;

        public TextView getmCreateTime() {
            return mCreateTime;
        }

        public void setmCreateTime(TextView mCreateTime) {
            this.mCreateTime = mCreateTime;
        }

        public ImageView getmPicPath() {
            return mPicPath;
        }

        public void setmPicPath(ImageView mPicPath) {
            this.mPicPath = mPicPath;
        }

        public TextView getmAccount() {
            return mAccount;
        }

        public void setmAccount(TextView mAccount) {
            this.mAccount = mAccount;
        }

        public TextView getmDigest() {
            return mDigest;
        }

        public void setmDigest(TextView mDigest) {
            this.mDigest = mDigest;
        }

        public TextView getmSource() {
            return mSource;
        }

        public void setmSource(TextView mSource) {
            this.mSource = mSource;
        }

        public ImageView getmMainImg() {
            return mMainImg;
        }

        public void setmMainImg(ImageView mMainImg) {
            this.mMainImg = mMainImg;
        }

        public TextView getmTitle() {
            return mTitle;
        }

        public void setmTitle(TextView mTitle) {
            this.mTitle = mTitle;
        }
    }
}
