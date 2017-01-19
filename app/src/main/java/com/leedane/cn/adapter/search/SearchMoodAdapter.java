package com.leedane.cn.adapter.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.search.SearchMoodBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 搜索心情列表的适配器
 * Created by LeeDane on 2016/5/22.
 */
public class SearchMoodAdapter extends BaseAdapter{

    public static final String TAG = "SearchMoodAdapter";

    public List<SearchMoodBean> mSearchMoodBeans;  //所有聊天列表
    private Context mContext; //上下文对象

    public SearchMoodAdapter(Context context, List<SearchMoodBean> searchMoodBeans){
        super();
        this.mSearchMoodBeans = searchMoodBeans;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mSearchMoodBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mSearchMoodBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshData(List<SearchMoodBean> searchMoodBeans){
        this.mSearchMoodBeans.clear();
        this.mSearchMoodBeans.addAll(searchMoodBeans);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.one_mood_layout, null);
            myHolder = new MyHolder();
            myHolder.createTime = (TextView) convertView.findViewById(R.id.one_mood_time);
            myHolder.account = (TextView) convertView.findViewById(R.id.one_mood_user_name);
            myHolder.picPath = (ImageView) convertView.findViewById(R.id.one_mood_user_pic);
            myHolder.froms = (TextView) convertView.findViewById(R.id.one_mood_froms);
            myHolder.content = (TextView) convertView.findViewById(R.id.one_mood_content);
            myHolder.imgContainer = (LinearLayout) convertView.findViewById(R.id.one_mood_img_container);
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }
        SearchMoodBean searchMoodBean = mSearchMoodBeans.get(position);

        String createTime = searchMoodBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            myHolder.createTime.setText("");
        }else{
            myHolder.createTime.setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        myHolder.account.setText(StringUtil.changeNotNull(searchMoodBean.getAccount()));
        if(StringUtil.isNotNull(searchMoodBean.getUserPicPath()))
            ImageCacheManager.loadImage(searchMoodBean.getUserPicPath(), myHolder.picPath, 45, 45);
        myHolder.froms.setText("来自：" + StringUtil.changeNotNull(searchMoodBean.getFroms()));
        myHolder.content.setText(StringUtil.changeNotNull(searchMoodBean.getContent()));
        if(StringUtil.isNotNull(searchMoodBean.getImgs())){
            myHolder.imgContainer.setVisibility(View.VISIBLE);
            ImageUtil.addImages(mContext, searchMoodBean.getImgs(), myHolder.imgContainer);
        }else{
            myHolder.imgContainer.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class MyHolder{
        /**
         * 搜索心情的用户的账号
         */
        private TextView account;

        /**
         * 搜索心情的用户的头像路径
         */
        private ImageView picPath;

        /**
         * 搜索心情的来自
         */
        private TextView froms;

        /**
         * 搜索心情的创建时间
         */
        private TextView createTime;

        /**
         * 心情的内容
         */
        private TextView content;

        /**
         * 搜索心情的主图
         */
        private LinearLayout imgContainer;
    }
}
