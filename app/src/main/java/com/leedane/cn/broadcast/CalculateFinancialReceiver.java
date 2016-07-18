package com.leedane.cn.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leedane.cn.financial.bean.FinancialList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 记账统计的接收器
 * Created by LeeDane on 2016/8/17.
 */
public class CalculateFinancialReceiver extends BroadcastReceiver {
    private static final String TAG = "CalculateFinancialReceiver";

    /**
     * 用户数据有更新的接口监听器
     */
    public interface CalculateFinancialListener{
        void calculate(FinancialList financialList, int model);
    }

    private CalculateFinancialListener calculateFinancialListener;

    public void setCalculateFinancialListener(CalculateFinancialListener calculateFinancialListener) {
        this.calculateFinancialListener = calculateFinancialListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;
        try{
            FinancialList financialList = (FinancialList)intent.getSerializableExtra("data");
            int model = intent.getIntExtra("model", 0);
            if(null != calculateFinancialListener)
                calculateFinancialListener.calculate(financialList, model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
