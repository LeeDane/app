package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.LoginHistoryBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;

import java.util.List;

/**
 * 登录历史列表数据展示的adapter对象
 * Created by LeeDane on 2016/5/5.
 */
public class LoginHistotyAdapter extends BaseAdapter{
    private Context mContext;
    private List<LoginHistoryBean> mLoginHistoryBeans;

    public LoginHistotyAdapter(Context context, List<LoginHistoryBean> loginHistoryBeans) {
        this.mContext = context;
        this.mLoginHistoryBeans = loginHistoryBeans;
    }

    @Override
    public int getCount() {
        return mLoginHistoryBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mLoginHistoryBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        LoginHistoryBean loginHistoryBean = mLoginHistoryBeans.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_login_history_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmMethod((TextView) view.findViewById(R.id.login_history_method));
            viewHolder.setmIp((TextView) view.findViewById(R.id.login_history_ip));
            TextView textView = (TextView)view.findViewById(R.id.login_history_browser);
            textView.setSelected(true);
            viewHolder.setmBrowser(textView);
            viewHolder.setmTime((TextView) view.findViewById(R.id.login_history_time));
            viewHolder.setmStatus((TextView)view.findViewById(R.id.login_history_status));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(loginHistoryBean.getCreateTime())));
        viewHolder.getmMethod().setText(loginHistoryBean.getMethod());
        viewHolder.getmIp().setText(loginHistoryBean.getIp());
        viewHolder.getmBrowser().setText(loginHistoryBean.getBrowser());
        viewHolder.getmStatus().setText("状态:" +getStatusText(loginHistoryBean.getStatus()));
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

    public void refreshData(List<LoginHistoryBean> loginHistoryBeans){
        this.mLoginHistoryBeans.clear();
        this.mLoginHistoryBeans.addAll(loginHistoryBeans);
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        private TextView mMethod;
        private TextView mIp;
        private TextView mBrowser;
        private TextView mTime;
        private TextView mStatus;

        public TextView getmBrowser() {
            return mBrowser;
        }

        public void setmBrowser(TextView mBrowser) {
            this.mBrowser = mBrowser;
        }

        public TextView getmIp() {
            return mIp;
        }

        public void setmIp(TextView mIp) {
            this.mIp = mIp;
        }

        public TextView getmMethod() {
            return mMethod;
        }

        public void setmMethod(TextView mMethod) {
            this.mMethod = mMethod;
        }

        public TextView getmStatus() {
            return mStatus;
        }

        public void setmStatus(TextView mStatus) {
            this.mStatus = mStatus;
        }

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }
    }
}
