package com.leedane.cn.adapter.expandRecyclerviewadapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by LeeDane on 2016/4/21.
 */
public interface OrientationProvider {

  public int getOrientation(RecyclerView recyclerView);

  public boolean isReverseLayout(RecyclerView recyclerView);
}
