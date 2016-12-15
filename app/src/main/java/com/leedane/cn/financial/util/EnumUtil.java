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
     * 记账图标
     * value为展示的文字
     */
    public enum FinancialIcons{
        银行卡("银行卡"),
        功课("功课"),
        支付("支付"),
        交通("交通"),
        教育("教育"),
        旅店("旅店"),
        蛋糕("蛋糕"),
        电影("电影"),
        现金("现金"),
        购物("购物"),
        住房("住房"),
        购物车("购物车"),
        包包("包包"),
        礼物("礼物"),
        手机("手机"),
        话费("话费"),
        笔记本("笔记本"),
        商店("商店"),
        出租车("出租车"),
        收入("收入"),
        支出("支出"),
        饮料("饮料"),
        买菜做饭("买菜做饭"),
        地铁("地铁"),
        话费充值("话费充值"),
        上网费("上网费"),
        快递("快递"),
        运动("运动");
        FinancialIcons(String value) {
            this.value = value;
        }
        public final String value;
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
