package com.leedane.cn.adapter;

import android.support.v7.widget.RecyclerView;

import com.leedane.cn.bean.ContactBean;
import com.leedane.cn.bean.MyFriendsBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * 我的好友列表展示
 * Created by LeeDane on 2016/4/21.
 */
public abstract class MyFriendsRecyclerviewBaseAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>{

    private ArrayList<MyFriendsBean> items = new ArrayList<>();

    public MyFriendsRecyclerviewBaseAdapter() {
        setHasStableIds(true);
    }

    public void add(MyFriendsBean object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, MyFriendsBean object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<MyFriendsBean> collection) {
        if (collection != null) {
            items.clear();
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(MyFriendsBean... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(MyFriendsBean object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public MyFriendsBean getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
