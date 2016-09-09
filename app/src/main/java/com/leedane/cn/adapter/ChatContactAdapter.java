package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.adapter.expandRecyclerviewadapter.StickyRecyclerHeadersAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.MyFriendsBean;
import com.leedane.cn.widget.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据当前权限进行判断相关的滑动逻辑
 * 聊天联系人列表
 * Created by LeeDane on 2016/5/12.
 */
public class ChatContactAdapter extends MyFriendsRecyclerviewBaseAdapter<ChatContactAdapter.ContactViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

        public static interface OnRecyclerViewListener {
                void onItemClick(int position);
        }

        private OnRecyclerViewListener onRecyclerViewListener;
        public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
                this.onRecyclerViewListener = onRecyclerViewListener;
        }
        /**
         * 当前处于打开状态的item
         */
        private List<SwipeItemLayout> mOpenedSil = new ArrayList<>();

        private List<MyFriendsBean> mLists;

        private Context mContext;

        public ChatContactAdapter(Context context, List<MyFriendsBean> lists) {
                this.mLists = lists;
                mContext = context;
                this.addAll(mLists);
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_contact, parent, false);
                return new ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, final int position) {
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

                                //((ChatContactFragment) mContext).deleteUser(position);
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

        public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

                TextView mName;
                SwipeItemLayout mRoot;
                TextView mDelete;
                int position;
                public ContactViewHolder(View itemView) {
                        super(itemView);
                        mName = (TextView) itemView.findViewById(R.id.item_chat_contact_name);
                        mRoot = (SwipeItemLayout) itemView.findViewById(R.id.item_chat_contact_swipe_root);
                        mDelete = (TextView) itemView.findViewById(R.id.item_chat_contact_delete);
                        mRoot.setOnClickListener(this);
                }

                @Override
                public void onClick(View v) {
                        if (null != onRecyclerViewListener) {
                                onRecyclerViewListener.onItemClick(position);
                        }

                }
        }
}
