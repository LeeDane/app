package com.leedane.cn.financial.util;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.TwoLevelCategory;

import java.util.HashMap;
import java.util.Map;

/**
 * 二级分类图标类
 * Created by LeeDane on 2016/12/6.
 */
public class IconUtil {
    private static IconUtil iconUtil;

    public static Map<String, Integer> mapIcons;

    private IconUtil(){
        mapIcons = new HashMap<>();
        mapIcons.put(EnumUtil.FinancialIcons.银行卡.value, R.drawable.ic_bank_card_purple_200_18dp);//银行卡ic_card_giftcard
        mapIcons.put(EnumUtil.FinancialIcons.功课.value, R.drawable.ic_assignment_purple_200_18dp);//功课
        mapIcons.put(EnumUtil.FinancialIcons.支付.value, R.drawable.ic_payment_purple_200_18dp);//支付
        mapIcons.put(EnumUtil.FinancialIcons.交通.value, R.drawable.ic_directions_car_purple_200_18dp);//交通
        mapIcons.put(EnumUtil.FinancialIcons.教育.value, R.drawable.ic_school_purple_200_18dp); //教育
        mapIcons.put(EnumUtil.FinancialIcons.旅店.value, R.drawable.ic_location_city_purple_200_18dp);//旅店
        mapIcons.put(EnumUtil.FinancialIcons.蛋糕.value, R.drawable.ic_cake_purple_200_18dp);//蛋糕
        mapIcons.put(EnumUtil.FinancialIcons.电影.value, R.drawable.ic_movie_filter_purple_200_18dp);//电影
        mapIcons.put(EnumUtil.FinancialIcons.现金.value, R.drawable.ic_monetization_on_purple_200_18dp);//现金
        mapIcons.put(EnumUtil.FinancialIcons.购物.value, R.drawable.ic_add_shopping_cart_purple_200_18dp);//购物
        mapIcons.put(EnumUtil.FinancialIcons.住房.value, R.drawable.ic_home_purple_200_18dp);//住房
        mapIcons.put(EnumUtil.FinancialIcons.购物车.value, R.drawable.ic_shopping_cart_purple_200_18dp);//购物车
        mapIcons.put(EnumUtil.FinancialIcons.包包.value, R.drawable.ic_shopping_basket_purple_200_18dp); //包包
        mapIcons.put(EnumUtil.FinancialIcons.礼物.value, R.drawable.ic_redeem_purple_200_18dp);//礼物
        mapIcons.put(EnumUtil.FinancialIcons.手机.value, R.drawable.ic_phone_iphone_purple_200_18dp);//手机
        mapIcons.put(EnumUtil.FinancialIcons.话费.value, R.drawable.ic_phone_purple_200_18dp);//话费
        mapIcons.put(EnumUtil.FinancialIcons.笔记本.value, R.drawable.ic_computer_purple_200_18dp);//笔记本
        mapIcons.put(EnumUtil.FinancialIcons.商店.value, R.drawable.ic_store_purple_200_18dp);//商店
        mapIcons.put(EnumUtil.FinancialIcons.出租车.value, R.drawable.ic_drive_eta_purple_200_18dp); //滴滴，Uber，出租车
        mapIcons.put(EnumUtil.FinancialIcons.收入.value, R.drawable.ic_attach_money_purple_200_18dp);//收入
        mapIcons.put(EnumUtil.FinancialIcons.支出.value, R.drawable.ic_money_off_purple_200_18dp);//支出
        mapIcons.put(EnumUtil.FinancialIcons.饮料.value, R.drawable.ic_free_breakfast_purple_200_18dp);//喝茶、饮料
        mapIcons.put(EnumUtil.FinancialIcons.买菜做饭.value, R.drawable.ic_kitchen_purple_200_18dp);//厨房(买菜做饭)
        mapIcons.put(EnumUtil.FinancialIcons.地铁.value, R.drawable.ic_train_purple_200_18dp);//地铁，高铁，火车
        mapIcons.put(EnumUtil.FinancialIcons.话费充值.value, R.drawable.ic_contact_phone_purple_200_18dp);//话费充值
        mapIcons.put(EnumUtil.FinancialIcons.上网费.value, R.drawable.ic_laptop_mac_purple_200_18dp);//上网费
        mapIcons.put(EnumUtil.FinancialIcons.快递.value, R.drawable.ic_mail_purple_200_18dp);//快递
        mapIcons.put(EnumUtil.FinancialIcons.运动.value, R.drawable.ic_directions_run_purple_200_18dp);//运动
    }

    public static synchronized IconUtil getInstance(){
        if(iconUtil == null){
            synchronized (IconUtil.class){
                if(iconUtil == null)
                    iconUtil = new IconUtil();
            }
        }
        return iconUtil;
    }

    /**
     * 获取图标
     * @param name
     * @return
     */
    public int getIcon(String name){
        return mapIcons.get(name) == null ? TwoLevelCategory.DEFAULT_SUB_CATEGORY_ICON : mapIcons.get(name);
    }
}
