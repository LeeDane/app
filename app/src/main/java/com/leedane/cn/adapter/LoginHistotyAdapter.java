package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.LoginHistoryBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;

import java.util.List;

/**
 * 登录历史列表数据展示的adapter对象
 * Created by LeeDane on 2016/5/5.
 */
public class LoginHistotyAdapter extends BaseListAdapter<LoginHistoryBean>{

    public LoginHistotyAdapter(Context context, List<LoginHistoryBean> loginHistoryBeans) {
        super(context, loginHistoryBeans);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        LoginHistoryBean loginHistoryBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_login_history_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.method = (TextView) view.findViewById(R.id.login_history_method);
            viewHolder.ip = (TextView) view.findViewById(R.id.login_history_ip);
            TextView textView = (TextView)view.findViewById(R.id.login_history_browser);
            textView.setSelected(true);
            viewHolder.browser = textView;
            viewHolder.time = (TextView) view.findViewById(R.id.login_history_time);
            viewHolder.status = (TextView)view.findViewById(R.id.login_history_status);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(loginHistoryBean.getCreateTime())));
        viewHolder.method.setText(loginHistoryBean.getMethod());
        viewHolder.ip.setText(loginHistoryBean.getIp());
        viewHolder.browser.setText(loginHistoryBean.getBrowser());
        viewHolder.status.setText("状态:" + getStatusText(loginHistoryBean.getStatus()));
        //设置动画效果
        setAnimation(view, position);
        return view;
    }

    /**
     * 处理状态信息
     * @param status
     * @return
     */
    private String getStatusText(int status){
        //状态，0：失败，1：正常，2：未知异常
        switch (status){
            case 0:
                return "失败";
            case 1:
                return "正常";
        }
        return "未知异常";
    }

    static class ViewHolder{
        TextView method;
        TextView ip;
        TextView browser;
        TextView time;
        TextView status;
    }
}
