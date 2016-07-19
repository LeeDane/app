package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.activity.AddressListActivity;
import com.leedane.cn.adapter.expandRecyclerviewadapter.StickyRecyclerHeadersAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.ContactBean;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.widget.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据当前权限进行判断相关的滑动逻辑
 * Created by LeeDane on 2016/4/21.
 */
public class ContactAdapter extends RecyclerviewBaseAdapter<ContactAdapter.ContactViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

        /**
         * 当前处于打开状态的item
         */
        private List<SwipeItemLayout> mOpenedSil = new ArrayList<>();

        private List<ContactBean.MembersEntity> mLists;

        private Context mContext;
        //private int mPermission;
        //private String createrID;
        //private boolean isCreator;


        public static final String OWNER = "1";
        public static final String CREATER = "1";
        //public static final String STUDENT = "student";

        public static interface OnRecyclerViewListener {
                void onItemClick(int position);
                boolean onItemLongClick(int position);
        }

        private OnRecyclerViewListener onRecyclerViewListener;
        public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
                this.onRecyclerViewListener = onRecyclerViewListener;
        }

        public ContactAdapter(Context context, List<ContactBean.MembersEntity> lists/*, int permission, String createrID*/) {
                this.mLists = lists;
                mContext = context;
                // mPermission = permission;
                this.addAll(mLists);
                /*this.createrID = createrID;
                if (createrID.equals(CREATER)) {
                        isCreator = true;
                } else {
                        isCreator = false;
                }*/
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_contact, parent, false);
                return new ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, final int position) {
                SwipeItemLayout swipeRoot = holder.mRoot;
                holder.position = position;
                /*if (getItem(position).getId().equals(OWNER)) {
                        swipeRoot.setSwipeAble(false);
                }*//* else if (isCreator) {
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

                                        ((AddressListActivity) mContext).deleteUser(position);
                                }
                        });
                } else {*/
                        /*if (mPermission == CommonString.PermissionCode.TEACHER) {*/
                               /* if (position != 0) {
                                        if (getItem(position).getProfession().equals(STUDENT)) {*/

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

                                ((AddressListActivity) mContext).deleteUser(position);
                        }
                });
                holder.mTest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                ToastUtil.success(mContext);
                        }
                });
                                       /* } else {
                                                swipeRoot.setSwipeAble(false);
                                        }
                                } else {
                                        swipeRoot.setSwipeAble(false);
                                }*/
                        /*} else {
                                swipeRoot.setSwipeAble(false);
                        }*/
                /*}*/
                TextView textView = holder.mName;
                textView.setText(getItem(position).getUsername());

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
                /*if ("$".equals(showValue)) {
                        textView.setText("群主");
                } else if ("%".equals(showValue)) {
                        textView.setText("系统管理员");

                } else {*/
                textView.setText(showValue);
                /*}*/

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

        public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

                public TextView mName;
                public SwipeItemLayout mRoot;
                public TextView mDelete;
                public TextView mTest;
                public int position;
                public ContactViewHolder(View itemView) {
                        super(itemView);
                        mName = (TextView) itemView.findViewById(R.id.item_contact_title);
                        mRoot = (SwipeItemLayout) itemView.findViewById(R.id.item_contact_swipe_root);
                        mDelete = (TextView) itemView.findViewById(R.id.item_contact_delete);
                        mTest = (TextView)itemView.findViewById(R.id.item_contact_test);
                        mRoot.setOnClickListener(this);
                        mRoot.setOnLongClickListener(this);
                }

                @Override
                public void onClick(View v) {
                        if (null != onRecyclerViewListener) {
                                onRecyclerViewListener.onItemClick(position);
                        }

                }

                @Override
                public boolean onLongClick(View v) {
                        if(null != onRecyclerViewListener){
                                return onRecyclerViewListener.onItemLongClick(position);
                        }
                        return false;
                }
        }
}
