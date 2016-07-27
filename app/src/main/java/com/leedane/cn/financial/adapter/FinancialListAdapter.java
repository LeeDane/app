package com.leedane.cn.financial.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 记账列表数据展示的adapter对象
 * Created by LeeDane on 2016/7/23.
 */
public class FinancialListAdapter extends BaseAdapter{
    private Context mContext;
    private List<FinancialBean> mFinancialBeans;

    public FinancialListAdapter(Context context, List<FinancialBean> financialBeans) {
        this.mContext = context;
        this.mFinancialBeans = financialBeans;
    }

    @Override
    public int getCount() {
        return mFinancialBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mFinancialBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        FinancialBean financialBean = mFinancialBeans.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_financial_main_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmCategory((TextView) view.findViewById(R.id.financial_item_category));
            viewHolder.setmDesc((TextView) view.findViewById(R.id.financial_item_desc));
            viewHolder.setmImage((ImageView) view.findViewById(R.id.financial_item_img));
            viewHolder.setmMoney((TextView) view.findViewById(R.id.financial_item_money));
            viewHolder.setmType((ImageView) view.findViewById(R.id.financial_item_type));
            viewHolder.setmTime((TextView) view.findViewById(R.id.financial_item_time));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(financialBean.getCreateTime())));
        viewHolder.getmCategory().setText(financialBean.getOneLevel() +">>" +financialBean.getTwoLevel());
        viewHolder.getmDesc().setText(StringUtil.changeNotNull(financialBean.getFinancialDesc()));
        if(StringUtil.isNotNull(financialBean.getPath())){
            viewHolder.getmImage().setVisibility(View.VISIBLE);
            ImageCacheManager.loadImage(financialBean.getPath(), viewHolder.getmImage());
        }else{
            viewHolder.getmImage().setVisibility(View.GONE);
        }

        viewHolder.getmMoney().setText(String.valueOf(financialBean.getMoney()));
        if(financialBean.getModel() == 1){ //收入
            viewHolder.getmType().setBackgroundResource(R.drawable.menu_address_list);
        }else if(financialBean.getModel() == 2){ //支出
            viewHolder.getmType().setBackgroundResource(R.drawable.menu_feedback);
        }
        return view;
    }

    public void refreshData(List<FinancialBean> financialBeans){
        this.mFinancialBeans.clear();
        this.mFinancialBeans.addAll(financialBeans);
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        private ImageView mType;
        private TextView mMoney;
        private TextView mCategory;
        private TextView mDesc;
        private TextView mTime;
        private ImageView mImage;

        public ImageView getmType() {
            return mType;
        }

        public void setmType(ImageView mType) {
            this.mType = mType;
        }

        public TextView getmMoney() {
            return mMoney;
        }

        public void setmMoney(TextView mMoney) {
            this.mMoney = mMoney;
        }

        public TextView getmCategory() {
            return mCategory;
        }

        public void setmCategory(TextView mCategory) {
            this.mCategory = mCategory;
        }

        public TextView getmDesc() {
            return mDesc;
        }

        public void setmDesc(TextView mDesc) {
            this.mDesc = mDesc;
        }

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }

        public ImageView getmImage() {
            return mImage;
        }

        public void setmImage(ImageView mImage) {
            this.mImage = mImage;
        }
    }
}
