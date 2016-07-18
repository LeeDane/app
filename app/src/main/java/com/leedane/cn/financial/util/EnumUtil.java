package com.leedane.cn.financial.util;

/**
 * 枚举工具类
 * Created by LeeDane on 2016/7/13.
 */
public class EnumUtil {

    /**
     * 记账模块的类型
     */
    public enum FinancialModel{
        今日(1), 昨日(2), 本周(3), 本月(4), 本年(5);
        FinancialModel(int value) {
            this.value = value;
        }

        public final int value;
    }

    /**
     * 根据值获取FinancialModel的key值
     * @param value
     * @return
     */
    public static String getFinancialModelValue(int value){
        for(FinancialModel fm: FinancialModel.values()){
            if(fm.value == value){
                return fm.name();
            }
        }
        return "";
    }
}
