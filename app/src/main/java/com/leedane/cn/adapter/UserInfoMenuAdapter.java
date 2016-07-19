package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.MenuBean;

import java.util.List;

/**
 * 用户中心菜单展示的adapter对象
 * Created by LeeDane on 2016/4/15.
 */
public class UserInfoMenuAdapter extends BaseAdapter{
    private Context mContext;
    private List<MenuBean> mMenuBeans;
    private boolean showUserInfo;

    public UserInfoMenuAdapter(Context context, List<MenuBean> menuBeans) {
        this.mContext = context;
        this.mMenuBeans = menuBeans;
    }

    @Override
    public int getCount() {
        return mMenuBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenuBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final MenuBean menuBean = mMenuBeans.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_menu_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmIcon((ImageView) view.findViewById(R.id.recyclerview_icon));
            viewHolder.setmTitle((TextView) view.findViewById(R.id.recyclerview_title));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmIcon().setImageResource(menuBean.getIconId());
        viewHolder.getmTitle().setText(menuBean.getTitle());
        return view;
    }

    public void refreshData(List<MenuBean> menuBeans){
        this.mMenuBeans.clear();
        this.mMenuBeans.addAll(menuBeans);
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        private ImageView mIcon;
        private TextView mTitle;

        public ImageView getmIcon() {
            return mIcon;
        }

        public void setmIcon(ImageView mIcon) {
            this.mIcon = mIcon;
        }

        public TextView getmTitle() {
            return mTitle;
        }

        public void setmTitle(TextView mTitle) {
            this.mTitle = mTitle;
        }
    }
}
