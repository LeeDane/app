package com.leedane.cn.adapter;

import android.support.v7.widget.RecyclerView;

import com.leedane.cn.bean.ContactBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by LeeDane on 2016/4/21.
 */
public abstract class RecyclerviewBaseAdapter <VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>{

    private ArrayList<ContactBean.MembersEntity> items = new ArrayList<>();

    public RecyclerviewBaseAdapter() {
        setHasStableIds(true);
    }

    public void add(ContactBean.MembersEntity object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, ContactBean.MembersEntity object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<ContactBean.MembersEntity> collection) {
        if (collection != null) {
            items.clear();
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(ContactBean.MembersEntity... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(ContactBean.MembersEntity object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public ContactBean.MembersEntity getItem(int position) {
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
