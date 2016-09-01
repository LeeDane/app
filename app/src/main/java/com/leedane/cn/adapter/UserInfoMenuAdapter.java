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
            viewHolder.setmIcon((ImageView) view.findViewById(R.id.recyclerview_icon));
            viewHolder.setmTitle((TextView) view.findViewById(R.id.recyclerview_title));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmIcon().setImageResource(menuBean.getIconId());
        viewHolder.getmTitle().setText(menuBean.getTitle());
        return view;
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
