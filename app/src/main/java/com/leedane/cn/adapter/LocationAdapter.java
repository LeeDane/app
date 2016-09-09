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
            viewHolder.name = (TextView) view.findViewById(R.id.location_item_name);
            TextView addr = (TextView) view.findViewById(R.id.location_item_addr);
            addr.setSelected(true);
            viewHolder.addr = addr;
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        if(StringUtil.isNotNull(locationBean.getName())){
            viewHolder.name.setVisibility(View.VISIBLE);
            viewHolder.name.setText(locationBean.getName());
        }else{
            viewHolder.name.setVisibility(View.GONE);
        }

        viewHolder.addr.setText(locationBean.getAddrStr());
        return view;
    }

    static class ViewHolder{
        TextView addr;
        TextView name;
    }
}
