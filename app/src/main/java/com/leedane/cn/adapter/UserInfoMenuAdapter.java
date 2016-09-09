package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.MenuBean;

import java.util.List;

/**
 * 用户中心菜单展示的adapter对象
 * Created by LeeDane on 2016/4/15.
 */
public class UserInfoMenuAdapter extends BaseListAdapter<MenuBean>{
    public UserInfoMenuAdapter(Context context, List<MenuBean> menuBeans) {
        super(context, menuBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final MenuBean menuBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_menu_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(R.id.recyclerview_icon);
            viewHolder.title = (TextView) view.findViewById(R.id.recyclerview_title);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.icon.setImageResource(menuBean.getIconId());
        viewHolder.title.setText(menuBean.getTitle());
        return view;
    }

    static class ViewHolder{
        ImageView icon;
        TextView title;
    }
}
