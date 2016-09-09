package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.ZanUserBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 点赞用户列表数据展示的adapter对象
 * Created by LeeDane on 2016/5/21.
 */
public class ZanUserAdapter extends BaseListAdapter<ZanUserBean>{
    public ZanUserAdapter(Context context, List<ZanUserBean> zanUserBeans) {
        super(context, zanUserBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final ZanUserBean zanUserBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_zan_user_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) view.findViewById(R.id.zan_user_time);
            viewHolder.userName = (TextView) view.findViewById(R.id.zan_user_name);
            viewHolder.userPic = (ImageView) view.findViewById(R.id.zan_user_pic);

            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();

        viewHolder.userName.setText(zanUserBean.getAccount());
        viewHolder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startPersonalActivity(mContext, zanUserBean.getCreateUserId());
            }
        });

        if(StringUtil.isNotNull(zanUserBean.getUserPicPath()))
            ImageCacheManager.loadImage(zanUserBean.getUserPicPath(), viewHolder.userPic, 40, 40);
        else{
            viewHolder.userPic.setImageResource(R.drawable.no_pic);
        }

        viewHolder.userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startPersonalActivity(mContext, zanUserBean.getCreateUserId());
            }
        });
        viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(zanUserBean.getCreateTime())));
        //设置动画效果
        setAnimation(view, position);
        return view;
    }

    static class ViewHolder{
        ImageView userPic;
        TextView userName;
        TextView time;
    }
}
