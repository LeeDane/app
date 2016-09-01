package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.LocationBean;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 位置列表列表数据展示的adapter对象
 * Created by LeeDane on 2016/6/15.
 */
public class LocationAdapter extends BaseListAdapter<LocationBean>{

    public LocationAdapter(Context context, List<LocationBean> locationBeans) {
        super(context, locationBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        LocationBean locationBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_location_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmName((TextView) view.findViewById(R.id.location_item_name));
            TextView addr = (TextView) view.findViewById(R.id.location_item_addr);
            addr.setSelected(true);
            viewHolder.setmAddr(addr);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        if(StringUtil.isNotNull(locationBean.getName())){
            viewHolder.getmName().setVisibility(View.VISIBLE);
            viewHolder.getmName().setText(locationBean.getName());
        }else{
            viewHolder.getmName().setVisibility(View.GONE);
        }

        viewHolder.getmAddr().setText(locationBean.getAddrStr());
        return view;
    }

    private class ViewHolder{
        private TextView mAddr;
        private TextView mName;

        public TextView getmAddr() {
            return mAddr;
        }

        public void setmAddr(TextView mAddr) {
            this.mAddr = mAddr;
        }

        public TextView getmName() {
            return mName;
        }

        public void setmName(TextView mName) {
            this.mName = mName;
        }
    }
}
