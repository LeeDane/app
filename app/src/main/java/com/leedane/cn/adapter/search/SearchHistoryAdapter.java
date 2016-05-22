package com.leedane.cn.adapter.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.bean.search.SearchHistoryBean;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 搜索历史列表的适配器
 * Created by LeeDane on 2016/5/22.
 */
public class SearchHistoryAdapter extends BaseAdapter{

    public static final String TAG = "SearchHistoryAdapter";

    public List<SearchHistoryBean> mSearchHistoryBeans;  //所有聊天列表
    private Context mContext; //上下文对象

    public SearchHistoryAdapter(Context context, List<SearchHistoryBean> searchHistoryBeans){
        super();
        this.mSearchHistoryBeans = searchHistoryBeans;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mSearchHistoryBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mSearchHistoryBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshData(List<SearchHistoryBean> searchHistoryBeans){
        this.mSearchHistoryBeans.clear();
        this.mSearchHistoryBeans.addAll(searchHistoryBeans);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_history_listview, null);
            myHolder = new MyHolder();
            myHolder.setmCreateTime((TextView) convertView.findViewById(R.id.search_history_time));
            myHolder.setmType((TextView) convertView.findViewById(R.id.search_history_type));
            TextView keyTextView = (TextView) convertView.findViewById(R.id.search_history_key);
            keyTextView.setSelected(true);
            myHolder.setmKey(keyTextView);
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }
        SearchHistoryBean searchHistoryBean = mSearchHistoryBeans.get(position);

        String createTime = searchHistoryBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            myHolder.getmCreateTime().setText("");
        }else{
            myHolder.getmCreateTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        myHolder.getmType().setText("类型："+searchHistoryBean.getSearchType());
        myHolder.getmKey().setText("关键字："+searchHistoryBean.getSearchKey());
        return convertView;
    }

    private class MyHolder{
        /**
         * 搜索历史的类型
         */
        private TextView mType;

        /**
         * 搜索历史的内容
         */
        private TextView mKey;

        /**
         * 创建时间
         */
        private TextView mCreateTime;

        public TextView getmCreateTime() {
            return mCreateTime;
        }

        public void setmCreateTime(TextView mCreateTime) {
            this.mCreateTime = mCreateTime;
        }

        public TextView getmKey() {
            return mKey;
        }

        public void setmKey(TextView mKey) {
            this.mKey = mKey;
        }

        public TextView getmType() {
            return mType;
        }

        public void setmType(TextView mType) {
            this.mType = mType;
        }
    }
}
