package com.leedane.cn.financial.util;

import com.leedane.cn.financial.bean.FinancialList;

/**
 * 记账默认计算工具类
 * 保存的是全局计算好的几个默认条件的数据
 * Created by LeeDane on 2016/8/19.
 */
public class CalculateUtil {

    //private static CalculateUtil calculateUtil = null;

    //今日的记账列表
    public static FinancialList toDayList = new FinancialList();

    //昨日的记账列表
    public static FinancialList yesterDayList = new FinancialList();

    //本周的记账列表
    public static FinancialList weekList = new FinancialList();

    //本月的记账列表
    public static FinancialList monthList = new FinancialList();

    //本年的记账列表
    public static FinancialList yearList = new FinancialList();

    private CalculateUtil(){

    }

    /**
     * 初始化，全局只能有一个
     * @return
     */
    /*public synchronized static CalculateUtil newInstance(){
        if(calculateUtil == null){
            synchronized (calculateUtil){
                calculateUtil = new CalculateUtil();
            }
        }
        return calculateUtil;
    }*/


}