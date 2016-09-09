package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatBgSelectWebBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 聊天背景网络图片的数据展示的adapter对象
 * Created by LeeDane on 2016/6/10.
 */
public class ChatBgSelectWebAdapter extends BaseListAdapter<ChatBgSelectWebBean>{
    public ChatBgSelectWebAdapter(Context context, List<ChatBgSelectWebBean> chatBgSelectWebBeans) {
       super(context, chatBgSelectWebBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final ChatBgSelectWebBean chatBgSelectWebBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_base_gridview, null);
            viewHolder = new ViewHolder();
            ImageView imageView = (ImageView) view.findViewById(R.id.gridview_img);
            ViewGroup.LayoutParams para = imageView.getLayoutParams();
            para.height = 450;
            para.width = (BaseApplication.newInstance().getScreenWidthAndHeight()[0] -20)/3;//一屏幕显示3行;
            imageView.setLayoutParams(para);
            viewHolder.path = (imageView);
            viewHolder.icon = (ImageView) view.findViewById(R.id.gridview_icon);
            viewHolder.download = (TextView)view.findViewById(R.id.gridview_download);
            viewHolder.show = (TextView)view.findViewById(R.id.gridview_show);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        if(StringUtil.isNotNull(chatBgSelectWebBean.getPath())){
            ImageCacheManager.loadImage(chatBgSelectWebBean.getPath(), viewHolder.path);
            viewHolder.path.setVisibility(View.VISIBLE);
        }else{
            viewHolder.path.setVisibility(View.GONE);
        }

        viewHolder.show.setText(StringUtil.changeNotNull(chatBgSelectWebBean.getAccount()) + "  " + RelativeDateFormat.format(DateUtil.stringToDate(chatBgSelectWebBean.getCreateTime())));

        if(chatBgSelectWebBean.getType() == 0){
            viewHolder.icon.setImageResource(R.drawable.free);
        }else{
            viewHolder.icon.setImageResource(R.drawable.charge);
        }

        if(chatBgSelectWebBean.getCreateUserId() == BaseApplication.getLoginUserId()){
            viewHolder.download.setText("我上传的");
        }else{
            if(chatBgSelectWebBean.isDownload()){
                viewHolder.download.setText(chatBgSelectWebBean.getAccount() + "上传的,已下载过");
            }else{
                viewHolder.download.setText(chatBgSelectWebBean.getAccount() +"上传的,还未下载");
            }
        }
        return view;
    }

    static class ViewHolder{
        TextView show;
        TextView download; //是否下载过
        ImageView path;
        ImageView icon; //是否免费
    }
}
