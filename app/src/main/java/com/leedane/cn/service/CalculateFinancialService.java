package com.leedane.cn.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * 计算记账的数据
 * 统计今日，昨日，本周，本月，本年，
 * Created by LeeDane on 2016/8/17.
 */
public class CalculateFinancialService extends Service {

    public static final int CALCULATE_FINANCIAL_CODE = 17;
    private FinancialDataBase financialDataBase;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //service被杀死后重启将没有intent，这里不做进一步处理即可
        if(intent == null){
            return super.onStartCommand(intent, flags, startId);
        }
        if(financialDataBase != null)
            financialDataBase.destroy();

        financialDataBase = new FinancialDataBase(CalculateFinancialService.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                generateNoClould();
                generateTodayData();
                generateYesterDayData();
                generateWeekData();
                generateMonthData();
                generateYearData();
            }
        }, 500);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取今日初始数据
     * @return
     */
    private void generateTodayData(){
        String startTime = DateUtil.DateToString(DateUtil.getTodayStart());
        String endTime = DateUtil.DateToString(DateUtil.getTodayEnd());
        FinancialList financialList = new FinancialList();
        financialList.setFinancialBeans(financialDataBase.query(" where status="+ConstantsUtil.STATUS_NORMAL+" and datetime(addition_time) between datetime('"+startTime+"') and datetime('"+ endTime+"') order by datetime(addition_time) desc"));
        financialList.setModel(EnumUtil.FinancialModel.今日.value);
        //使用静态的方式注册广播，可以使用显示意图进行发送广播
        Intent broadcast = new Intent("com.leedane.cn.broadcast.CalculateFinancialReceiver");
        //broadcast.putExtra("data", financialList);
        CalculateUtil.todayList = financialList;
        broadcast.putExtra("model", EnumUtil.FinancialModel.今日.value);
        sendBroadcast(broadcast, null);
    }

    /**
     * 获取昨日初始数据
     * @return
     */
    private void generateYesterDayData(){
        String startTime = DateUtil.DateToString(DateUtil.getYesTodayStart());
        String endTime = DateUtil.DateToString(DateUtil.getYesTodayEnd());
        FinancialList financialList = new FinancialList();
        financialList.setFinancialBeans(financialDataBase.query(" where status="+ConstantsUtil.STATUS_NORMAL+" and datetime(addition_time) between datetime('"+startTime+"') and datetime('"+ endTime+"') order by datetime(addition_time) desc"));
        financialList.setModel(EnumUtil.FinancialModel.昨日.value);
        //使用静态的方式注册广播，可以使用显示意图进行发送广播
        Intent broadcast = new Intent("com.leedane.cn.broadcast.CalculateFinancialReceiver");
        //broadcast.putExtra("data", financialList);
        CalculateUtil.yesterDayList = financialList;
        broadcast.putExtra("model", EnumUtil.FinancialModel.昨日.value);
        sendBroadcast(broadcast, null);
    }

    /**
     * 获取本周初始数据
     * @return
     */
    private void generateWeekData(){
        String startTime = DateUtil.DateToString(DateUtil.getThisWeekStart());
        String endTime = DateUtil.DateToString(new Date());
        FinancialList financialList = new FinancialList();
        financialList.setFinancialBeans(financialDataBase.query(" where status="+ConstantsUtil.STATUS_NORMAL+" and datetime(addition_time) between datetime('"+startTime+"') and datetime('"+ endTime+"') order by datetime(addition_time) desc"));
        financialList.setModel(EnumUtil.FinancialModel.本周.value);
        //使用静态的方式注册广播，可以使用显示意图进行发送广播
        Intent broadcast = new Intent("com.leedane.cn.broadcast.CalculateFinancialReceiver");
        //broadcast.putExtra("data", financialList);
        CalculateUtil.weekList = financialList;
        broadcast.putExtra("model", EnumUtil.FinancialModel.本周.value);
        sendBroadcast(broadcast, null);
    }
    /**
     * 获取本月初始数据
     * @return
     */
    private void  generateMonthData(){
        String startTime = DateUtil.DateToString(DateUtil.getThisMonthStart());
        String endTime = DateUtil.DateToString(new Date());
        FinancialList financialList = new FinancialList();
        financialList.setFinancialBeans(financialDataBase.query(" where status="+ ConstantsUtil.STATUS_NORMAL+" and datetime(addition_time) between datetime('" + startTime + "') and datetime('" + endTime + "') order by datetime(addition_time) desc"));
        financialList.setModel(EnumUtil.FinancialModel.本月.value);
        //使用静态的方式注册广播，可以使用显示意图进行发送广播
        Intent broadcast = new Intent("com.leedane.cn.broadcast.CalculateFinancialReceiver");
        //broadcast.putExtra("data", financialList);
        CalculateUtil.monthList = financialList;
        broadcast.putExtra("model", EnumUtil.FinancialModel.本月.value);
        sendBroadcast(broadcast, null);
    }

    /**
     * 获取本年初始数据
     * @return
     */
    private void generateYearData(){
        String startTime = DateUtil.DateToString(DateUtil.getThisYearStart());
        String endTime = DateUtil.DateToString(new Date());
        FinancialList financialList = new FinancialList();
        financialList.setFinancialBeans(financialDataBase.query(" where status="+ConstantsUtil.STATUS_NORMAL+" and datetime(addition_time) between datetime('" + startTime + "') and datetime('" + endTime + "') order by datetime(addition_time) desc"));
        financialList.setModel(EnumUtil.FinancialModel.本年.value);
        //使用静态的方式注册广播，可以使用显示意图进行发送广播
        Intent broadcast = new Intent("com.leedane.cn.broadcast.CalculateFinancialReceiver");
        //broadcast.putExtra("data", financialList);
        CalculateUtil.yearList = financialList;
        broadcast.putExtra("model", EnumUtil.FinancialModel.本年.value);
        sendBroadcast(broadcast, null);
    }

    /**
     * 获取未上传到云端的数据数量
     * @return
     */
    private void generateNoClould(){
        CalculateUtil.noClouldNumber = 0;
        List<FinancialBean> financialBeans = financialDataBase.query(" where (id = 0 or synchronous = "+ ConstantsUtil.STATUS_DISABLE +") and status !="+ ConstantsUtil.STATUS_DRAFT +" order by datetime(addition_time) asc");
        CalculateUtil.noClouldNumber = financialBeans != null && financialBeans.size() > 0 ? financialBeans.size(): 0;
    }
}
