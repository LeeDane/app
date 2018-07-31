package com.leedane.cn.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.activity.ChatActivity;
import com.leedane.cn.activity.CircleOfFriendActivity;
import com.leedane.cn.activity.FriendActivity;
import com.leedane.cn.activity.GalleryActivity;
import com.leedane.cn.activity.MainActivity;
import com.leedane.cn.activity.MoodActivity;
import com.leedane.cn.activity.NotificationActivity;
import com.leedane.cn.activity.SettingActivity;
import com.leedane.cn.activity.UserInfoActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.financial.Helper.ItemTouchHelperAdapter;
import com.leedane.cn.financial.Helper.ItemTouchHelperViewHolder;
import com.leedane.cn.financial.Helper.OnStartDragListener;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONArray;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 首页适配器的adapter对象
 * Created by LeeDane on 2018/1/18.
 */
public class HomeAdapter extends BaseRecyclerViewAdapter<Map<String, Object>> implements ItemTouchHelperAdapter {
    private Context mContext;
    private OnStartDragListener mDragStartListener;
    private boolean isLogin;
    public HomeAdapter(Context context, List<Map<String, Object>> itemList, boolean isLogin, OnStartDragListener dragStartListener){
        super(context, itemList);
        this.mContext = context;
        this.isLogin = isLogin;
        this.mDragStartListener = dragStartListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_home_gridview_item, parent, false);
        return new MyViewHolder(layout);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final Map<String, Object> data = mDatas.get(position);
        if((viewHolder instanceof MyViewHolder) && !CommonUtil.isEmpty(mDatas)) {
            final MyViewHolder holder = ((MyViewHolder) viewHolder);
            holder.img.setBackgroundResource((int)data.get("img"));
            holder.tip.setText(String.valueOf(data.get("label")));
            /*holder.root.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    switch ((int)data.get("id")) {
                        case 1: //
                            mContext.startActivity(new Intent(mContext, MainActivity.class));
                            break;
                        case 2: //
                            mContext.startActivity(new Intent(mContext, MoodActivity.class));
                            break;
                        case 3: //
                            mContext.startActivity(new Intent(mContext, com.leedane.cn.financial.activity.HomeActivity.class));
                            break;
                        case 4: //
                            mContext.startActivity(new Intent(mContext, NotificationActivity.class));
                            break;
                        case 5: //
                            mContext.startActivity(new Intent(mContext, UserInfoActivity.class));
                            break;
                        case 6: //
                            mContext.startActivity(new Intent(mContext, GalleryActivity.class));
                            break;
                        case 7: //
                            mContext.startActivity(new Intent(mContext, CircleOfFriendActivity.class));
                            break;
                        case 8: //
                            mContext.startActivity(new Intent(mContext, ChatActivity.class));
                            break;
                        case 9: //
                            mContext.startActivity(new Intent(mContext, FriendActivity.class));
                            break;
                        case 10: //
                            mContext.startActivity(new Intent(mContext, SettingActivity.class));
                            break;
                        default:
                            break;
                    }
                }
            });*/
            //holder.img.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            //holder.badge.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            int num = StringUtil.changeObjectToInt(data.get("num"));
            if(num > 0){
                BadgeView badge = new BadgeView(mContext, holder.badge);
                badge.setText(num + "");
                badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);// 设置在右上角
                badge.show();
            }

            // 对必须登录确不能登录的标记为灰色并且不能点击
            if(StringUtil.changeObjectToBoolean(data.get("mustLogin")) && !isLogin){
                holder.itemView.setBackgroundColor(Color.LTGRAY);
                /*holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                            mDragStartListener.onStartDrag(holder);
                        }
                        return true;
                    }
                });*/
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.failure(mContext, "请先登录");
                    }
                });
            }else{
                holder.itemView.setBackgroundColor(0);
                /*holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                       if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                            mDragStartListener.onStartDrag(holder);
                        }
                        return false;
                    }
                });*/
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClick(position, StringUtil.changeObjectToInt(data.get("id")));
                    }
                });
            }

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(StringUtil.isNotNull(StringUtil.changeNotNull(data.get("desc")))){
                        ToastUtil.success(mContext, StringUtil.changeNotNull(data.get("desc")));
                    }
                    return true;
                }
            });
            setAnimation(holder.root, position);
        }
    }



    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //ToastUtil.success(mContext, "onItemMove:fromPosition->"+fromPosition+",toPosition->"+toPosition);
        Collections.swap(mDatas, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        try {
            SharedPreferenceUtil.saveDesktopData(mContext, new JSONArray(mDatas).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        //ToastUtil.success(mContext, "onItemDismiss:position->"+position);
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        ImageView img;
        TextView tip;
        LinearLayout badge;
        LinearLayout root;
        public MyViewHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            badge = (LinearLayout)itemView.findViewById(R.id.grid_view_badge);
            img = (ImageView)itemView.findViewById(R.id.grid_view_img);
            tip = (TextView) itemView.findViewById(R.id.grid_view_tip);
            root = (LinearLayout)itemView.findViewById(R.id.grid_view_root);
        }

        @Override
        public void onItemSelected(int position) {
            //ToastUtil.success(mContext, "select:"+position);
            //right.setImageResource(R.drawable.ic_list_white_18dp);
            //itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear(int oldPosition , int position) {

            //right.setImageResource(R.mipmap.right_sign);
            //itemView.setBackgroundColor(0);
        }
    }
}
