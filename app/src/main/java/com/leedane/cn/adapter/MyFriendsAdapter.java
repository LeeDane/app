package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.leedane.cn.activity.AtFriendActivity;
import com.leedane.cn.adapter.expandRecyclerviewadapter.StickyRecyclerHeadersAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.MyFriendsBean;
import com.leedane.cn.widget.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据当前权限进行判断相关的滑动逻辑
 * Created by LeeDane on 2016/4/30.
 */
public class MyFriendsAdapter extends MyFriendsRecyclerviewBaseAdapter<MyFriendsAdapter.ContactViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

        /**
         * 当前处于打开状态的item
         */
        private List<SwipeItemLayout> mOpenedSil = new ArrayList<>();

        private List<MyFriendsBean> mLists;

        private Context mContext;

        public static interface OnCheckedChangedListener {
                void select(int position, boolean isChecked);
        }

        private OnCheckedChangedListener onCheckedChangedListener;

        public void setOnCheckedBoxListener(OnCheckedChangedListener onCheckedBoxListener) {
                this.onCheckedChangedListener = onCheckedBoxListener;
        }

        public MyFriendsAdapter(Context context, List<MyFriendsBean> lists) {
                this.mLists = lists;
                mContext = context;
                this.addAll(mLists);
        }

        @Override
<<<<<<< HEAD
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
=======
        public MyFriendsAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
>>>>>>> b366facf4396f34393fb66caad51b368b58e2cc8
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_myfriends, parent, false);
                return new ContactViewHolder(view);
        }

        @Override
<<<<<<< HEAD
        public void onBindViewHolder(ContactViewHolder holder, final int position) {
=======
        public void onBindViewHolder(MyFriendsAdapter.ContactViewHolder holder, final int position) {
>>>>>>> b366facf4396f34393fb66caad51b368b58e2cc8
                SwipeItemLayout swipeRoot = holder.mRoot;
                holder.position = position;
                swipeRoot.setSwipeAble(true);
                swipeRoot.setDelegate(new SwipeItemLayout.SwipeItemLayoutDelegate() {
                        @Override
                        public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
                                closeOpenedSwipeItemLayoutWithAnim();
                                mOpenedSil.add(swipeItemLayout);
                        }

                        @Override
                        public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {
                                mOpenedSil.remove(swipeItemLayout);
                        }

                        @Override
                        public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {
                                closeOpenedSwipeItemLayoutWithAnim();
                        }
                });
                holder.mDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                ((AtFriendActivity) mContext).deleteUser(position);
                        }
                });

                TextView textView = holder.mName;
                textView.setText(getItem(position).getAccount());

        }

        @Override
        public long getHeaderId(int position) {

                return getItem(position).getSortLetters().charAt(0);

        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contact_header, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
                TextView textView = (TextView) holder.itemView;
                String showValue = String.valueOf(getItem(position).getSortLetters().charAt(0));
                textView.setText(showValue);
        }


        public int getPositionForSection(char section) {
                for (int i = 0; i < getItemCount(); i++) {
                        String sortStr = mLists.get(i).getSortLetters();
                        char firstChar = sortStr.toUpperCase().charAt(0);
                        if (firstChar == section) {
                                return i;
                        }
                }
                return -1;

        }

        public void closeOpenedSwipeItemLayoutWithAnim() {
                for (SwipeItemLayout sil : mOpenedSil) {
                        sil.closeWithAnim();
                }
                mOpenedSil.clear();
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener{

                public TextView mName;
                public SwipeItemLayout mRoot;
                public TextView mDelete;
                private CheckBox mSelect;
                public int position;
                public ContactViewHolder(View itemView) {
                        super(itemView);
                        mName = (TextView) itemView.findViewById(R.id.item_myfriends_name);
                        mRoot = (SwipeItemLayout) itemView.findViewById(R.id.item_myfriends_swipe_root);
                        mDelete = (TextView) itemView.findViewById(R.id.item_myfriends_delete);
                        mSelect = (CheckBox)itemView.findViewById(R.id.item_myfriends_select);
                        mSelect.setOnCheckedChangeListener(this);
                }

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (null != onCheckedChangedListener) {
                                onCheckedChangedListener.select(position, isChecked);
                        }
                }
        }
}
