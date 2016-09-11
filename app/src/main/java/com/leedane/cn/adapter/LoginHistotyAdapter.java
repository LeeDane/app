package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.LoginHistoryBean;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;

import java.util.List;

/**
 * 登录历史列表数据展示的adapter对象
 * Created by LeeDane on 2016/5/5.
 */
public class LoginHistotyAdapter extends BaseRecyclerViewAdapter<LoginHistoryBean> {

    public LoginHistotyAdapter(Context context, List<LoginHistoryBean> loginHistoryBeans) {
        super(context, loginHistoryBeans);
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_login_history_listview, parent, false);
            return new ContentHolder(layout);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == TYPE_HEADER)
            return;
        if(viewType == TYPE_FOOTER){
            return;
        }

        if(viewHolder instanceof ContentHolder && !CommonUtil.isEmpty(mDatas)) {
            Log.i("ViewAdapter", "position=" + position);
            final int pos = getRealPosition(viewHolder);
            final LoginHistoryBean loginHistoryBean = mDatas.get(pos);
            ContentHolder holder = ((ContentHolder) viewHolder);

            holder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(loginHistoryBean.getCreateTime())));
            holder.method.setText(loginHistoryBean.getMethod());
            holder.ip.setText(loginHistoryBean.getIp());
            holder.browser.setText(loginHistoryBean.getBrowser());
            holder.status.setText("状态:" + getStatusText(loginHistoryBean.getStatus()));
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder{
        TextView method;
        TextView ip;
        TextView browser;
        TextView time;
        TextView status;
        public ContentHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView || itemView == mFooterView)
                return;
            method = (TextView) itemView.findViewById(R.id.login_history_method);
            ip = (TextView) itemView.findViewById(R.id.login_history_ip);
            TextView textView = (TextView)itemView.findViewById(R.id.login_history_browser);
            textView.setSelected(true);
            browser = textView;
            time = (TextView) itemView.findViewById(R.id.login_history_time);
            status = (TextView)itemView.findViewById(R.id.login_history_status);
        }
    }
}
