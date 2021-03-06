package com.leedane.cn.financial.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.database.BaseSQLiteOpenHelper;
import com.leedane.cn.financial.bean.OneLevelCategory;
import com.leedane.cn.financial.bean.TwoLevelCategory;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 记账一级分类的数据库操作类
 * Created by LeeDane on 2016/7/24.
 */
public class TwoLevelCategoryDataBase {
    public static final String TAG = "TwoCategoryDataBase";
    private final BaseSQLiteOpenHelper dbHelper;

    public static final String TWO_LEVEL_CATEGORY_TABLE_NAME = "two_level_category";

    public static final String CREATE_TWO_LEVEL_CATEGORY_TABLE = "CREATE TABLE " + TWO_LEVEL_CATEGORY_TABLE_NAME + " ("
            + "id integer primary key autoincrement, " + // local_id
            "one_level_id integer, " +  //一级分类的ID
            "order_ integer, " +  //分类的排序ID,从1开始
            "status integer, " + // 状态, 0禁用 1正常,
            "value varchar(20), " + // 展示的大类名称
            "icon varchar(10), "+ // 显示的图标
            "budget float, " + //一级分类的预算
            "is_default integer, " +//是否是默认的分类, 0：表示没有, 1表示有
            "create_user_id integer, " + // 创建人
            "create_time varchar(25)" + // 创建时间
            ");";

    public TwoLevelCategoryDataBase(Context context) {
        dbHelper = BaseSQLiteOpenHelper.getHelper(context, ConstantsUtil.DB_NAME);
    }

    /**
     * 第一次使用初始化二级分类
     */
    public static List<TwoLevelCategory> initData(){
        
        List<TwoLevelCategory> twoLevelCategories = new ArrayList<TwoLevelCategory>();
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("食品酒水"), "早餐", false, 1, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("食品酒水"), "午餐", false, 2, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("食品酒水"), "晚餐", true, 3, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("食品酒水"), "夜宵", false, 4, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("食品酒水"), "水果", false, 5, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("食品酒水"), "零食", false, 6, EnumUtil.FinancialIcons.饮料.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("食品酒水"), "饮料", false, 7, EnumUtil.FinancialIcons.饮料.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("食品酒水"), "买菜做饭", false, 8, EnumUtil.FinancialIcons.买菜做饭.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("食品酒水"), "请客吃饭", false, 9, EnumUtil.FinancialIcons.支付.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("日常购物"), "网上商城", false, 1, EnumUtil.FinancialIcons.购物车.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("日常购物"), "超市", false, 2, EnumUtil.FinancialIcons.商店.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("日常购物"), "便利店", false, 3, EnumUtil.FinancialIcons.购物.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("日常购物"), "杂货店", false, 4, EnumUtil.FinancialIcons.购物.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("日常购物"), "地摊", false, 5, EnumUtil.FinancialIcons.购物.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("衣服饰品"), "衣服裤子", false, 1, EnumUtil.FinancialIcons.包包.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("衣服饰品"), "鞋帽包包", false, 2, EnumUtil.FinancialIcons.包包.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("衣服饰品"), "化妆饰品", false, 3, EnumUtil.FinancialIcons.支付.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("居家物业"), "日常用品", false, 1, EnumUtil.FinancialIcons.支出.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("居家物业"), "水电煤气", false, 2, EnumUtil.FinancialIcons.支出.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("居家物业"), "房租", false, 3, EnumUtil.FinancialIcons.住房.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("居家物业"), "物业管理", false, 4, EnumUtil.FinancialIcons.住房.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("居家物业"), "维修保养", false, 5, EnumUtil.FinancialIcons.支出.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("行车交通"), "羊城通", false, 1, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("行车交通"), "公共车", false, 2, EnumUtil.FinancialIcons.地铁.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("行车交通"), "地铁", false, 3, EnumUtil.FinancialIcons.地铁.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("行车交通"), "出租车", false, 4, EnumUtil.FinancialIcons.出租车.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("行车交通"), "滴滴出行", false, 5, EnumUtil.FinancialIcons.出租车.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("行车交通"), "Uber", false, 6, EnumUtil.FinancialIcons.出租车.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("行车交通"), "私家车", false, 7, EnumUtil.FinancialIcons.出租车.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("行车交通"), "其他车费", false, 8, EnumUtil.FinancialIcons.交通.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("交流通讯"), "手机", false, 1, EnumUtil.FinancialIcons.手机.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("交流通讯"), "笔记本", false, 2, EnumUtil.FinancialIcons.笔记本.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("交流通讯"), "话费充值", false, 3, EnumUtil.FinancialIcons.话费充值.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("交流通讯"), "上网费", false, 4, EnumUtil.FinancialIcons.上网费.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("交流通讯"), "快递费", false, 5, EnumUtil.FinancialIcons.快递.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("交流通讯"), "其他电子", false, 6, EnumUtil.FinancialIcons.上网费.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("休闲娱乐"), "景区门票", false, 1, EnumUtil.FinancialIcons.支出.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("休闲娱乐"), "酒店住宿", false, 2, EnumUtil.FinancialIcons.旅店.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("休闲娱乐"), "运动健身", false, 3, EnumUtil.FinancialIcons.运动.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("休闲娱乐"), "腐败聚会", false, 4, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("休闲娱乐"), "休闲玩乐", false, 5, EnumUtil.FinancialIcons.蛋糕.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("休闲娱乐"), "旅游度假", false, 6, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("休闲娱乐"), "宠物宝贝", false, 7, EnumUtil.FinancialIcons.支付.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("学习进修"), "学习资料", false, 1, EnumUtil.FinancialIcons.教育.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("学习进修"), "培训进修", false, 2, EnumUtil.FinancialIcons.教育.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("学习进修"), "数码装备", false, 3, EnumUtil.FinancialIcons.笔记本.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("人情往来"), "送礼请客", false, 1, EnumUtil.FinancialIcons.礼物.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("人情往来"), "孝敬长辈", false, 2, EnumUtil.FinancialIcons.支出.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("人情往来"), "还人钱物", false, 3, EnumUtil.FinancialIcons.支出.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("人情往来"), "慈善捐助", false, 4, EnumUtil.FinancialIcons.支出.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("金融银行"), "信用卡", false, 1, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("金融银行"), "微信红包", false, 2, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("金融银行"), "支付宝红包", false, 3, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("金融银行"), "微信转账", false, 4, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("金融银行"), "支付宝转账", false, 5, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("金融银行"), "银行手续", false, 6, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("金融银行"), "投资亏损", false, 7, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("金融银行"), "按揭还款", false, 8, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("金融银行"), "消费税收", false, 9, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("金融银行"), "利息支出", false, 10, EnumUtil.FinancialIcons.支付.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("金融银行"), "赔偿罚款", false, 11, EnumUtil.FinancialIcons.支付.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("生活保健"), "药品费", false, 1, EnumUtil.FinancialIcons.支出.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("生活保健"), "保健费", false, 2, EnumUtil.FinancialIcons.支出.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("生活保健"), "美容费", false, 3, EnumUtil.FinancialIcons.支出.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("生活保健"), "治疗费", false, 4, EnumUtil.FinancialIcons.支出.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("其他杂项"), "其他支出", false, 1, EnumUtil.FinancialIcons.支出.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("其他杂项"), "意外丢失", false, 2, EnumUtil.FinancialIcons.支出.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("其他杂项"), "烂账丢失", false, 3, EnumUtil.FinancialIcons.支出.value));

        //收入大类
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("职业收入"), "工资收入", false, 1, EnumUtil.FinancialIcons.现金.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("职业收入"), "利息收入", false, 2, EnumUtil.FinancialIcons.现金.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("职业收入"), "加班收入", false, 3, EnumUtil.FinancialIcons.现金.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("职业收入"), "奖金收入", false, 4, EnumUtil.FinancialIcons.收入.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("职业收入"), "投资收入", false, 5, EnumUtil.FinancialIcons.收入.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("职业收入"), "兼职收入", false, 6, EnumUtil.FinancialIcons.收入.value));

        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("其他收入"), "礼金收入", false, 1, EnumUtil.FinancialIcons.收入.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("其他收入"), "中奖收入", false, 2, EnumUtil.FinancialIcons.收入.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("其他收入"), "意外来钱", false, 3, EnumUtil.FinancialIcons.收入.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("其他收入"), "经营所得", false, 4, EnumUtil.FinancialIcons.收入.value));
        twoLevelCategories.add(new TwoLevelCategory(OneLevelCategoryDataBase.getOneLevelIdByValue("其他收入"), "借钱", false, 5, EnumUtil.FinancialIcons.收入.value));

        TwoLevelCategoryDataBase dataBase = new TwoLevelCategoryDataBase(BaseApplication.newInstance());
        for(TwoLevelCategory twoLevelCategory: twoLevelCategories){
            dataBase.insert(twoLevelCategory);
        }

        dataBase.destroy();
        return twoLevelCategories;
    }

    /**
     * 新增
     * @param data
     * @return true表示成功插入,false表示不成功插入
     */
    public synchronized boolean insert(TwoLevelCategory data) {

        if(isExists(data.getValue())){
            Log.i(TAG, "数据已经存在:"+data.getValue());
            return false;
        }

        String sql = "insert into " + TWO_LEVEL_CATEGORY_TABLE_NAME;

        sql += "(order_, one_level_id, status, value, icon, budget, is_default, create_user_id, create_time) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        // 开始事务
        sqlite.beginTransaction();
        try{
            sqlite.execSQL(sql, new String[]{
                    data.getOrder() + "", data.getOneLevelId() + "", data.getStatus() + "", data.getValue(), data.getIconName() + "", data.getBudget() + "",
                    StringUtil.changeTrueOrFalseToInt(data.isDefault()) + "", data.getCreateUserId() + "", data.getCreateTime()});
            sqlite.setTransactionSuccessful(); // 设置事务成功完成
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlite.endTransaction();
            sqlite.close();
        }
        Log.i(TAG, "数据插入成功:" + data.getId());
        return true;
    }

    /**
     * 判断记录是否存在(根据值来区分)
     * @param value
     * @return
     */
    private synchronized boolean isExists(String value){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        sqlite.beginTransaction();
        int id = 0;
        Cursor cursor = sqlite.rawQuery("select id from "
                + TWO_LEVEL_CATEGORY_TABLE_NAME + " where value = ?", new String[]{value});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            id = cursor.getInt(0);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.endTransaction();
        sqlite.close();
        return id > 0 ;
    }

    /**
     * 删掉指定一条记录
     * @param id
     */
    public void delete(int id) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + TWO_LEVEL_CATEGORY_TABLE_NAME + " where id=?");
        sqlite.execSQL(sql, new Integer[]{id});
        sqlite.close();
    }

    /**
     * 删掉指定一条记录(多用户的情况下)
     * @param userId
     */
    public void deleteByUser(int userId) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + TWO_LEVEL_CATEGORY_TABLE_NAME + " where create_user_id=?");
        sqlite.execSQL(sql, new Integer[]{userId});
        sqlite.close();
    }

    /**
     * 删掉全部记录
     */
    public void deleteAll() {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + TWO_LEVEL_CATEGORY_TABLE_NAME );
        sqlite.execSQL(sql);
        sqlite.close();
    }

    /**
     * 更新记录
     * @param data
     */
    public void update(TwoLevelCategory data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + TWO_LEVEL_CATEGORY_TABLE_NAME + " set order_=?, one_level_id=?, status=?, value=?, icon=?" +
                ", budget=?, is_default=?,create_user_id=?, create_time=? where id=?");
        sqlite.execSQL(sql, new String[]{
                data.getOrder() + "", data.getOneLevelId() +"", data.getStatus() +"", data.getValue(), data.getIconName() +"", data.getBudget() + "",
                StringUtil.changeTrueOrFalseToInt(data.isDefault()) +"", data.getCreateUserId() +"", data.getCreateTime(), data.getId() +""});
        sqlite.close();
    }

    public List<TwoLevelCategory> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<TwoLevelCategory> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<TwoLevelCategory> data = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select id, order_, one_level_id, status, value, icon, budget, is_default, create_user_id, create_time  from "
                + TWO_LEVEL_CATEGORY_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            TwoLevelCategory twoLevelCategory = new TwoLevelCategory();
            twoLevelCategory.setId(cursor.getInt(0));
            twoLevelCategory.setOrder(cursor.getInt(1));
            twoLevelCategory.setOneLevelId(cursor.getInt(2));
            twoLevelCategory.setStatus(cursor.getInt(3));
            twoLevelCategory.setValue(cursor.getString(4));
            twoLevelCategory.setIconName(cursor.getString(5));
            twoLevelCategory.setBudget(cursor.getFloat(6));
            twoLevelCategory.setIsDefault(StringUtil.changeIntToTrueOrFalse(cursor.getInt(7)));
            twoLevelCategory.setCreateUserId(cursor.getInt(8));
            twoLevelCategory.setCreateTime(cursor.getString(9));
            data.add(twoLevelCategory);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        return data;
    }

    /**
     * 获取一级分类的id
     * @param value
     * @return
     */
    public static int getIndexByOneLevelCategory(String value){
        int id = 0;
        List<OneLevelCategory> oneLevelCategories = BaseApplication.oneLevelCategories;
        if(!CommonUtil.isEmpty(oneLevelCategories)){
            for(OneLevelCategory oneLevelCategory : oneLevelCategories){
                if (value.equals(oneLevelCategory.getValue())){
                    id = oneLevelCategory.getId();
                    break;
                }
            }
        }
        return id;
    }

    /**
     * 重置
     *
     * @param datas
     */
    public void reset(List<TwoLevelCategory> datas, int oneLevelId) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + TWO_LEVEL_CATEGORY_TABLE_NAME + " where one_level_id = " + oneLevelId);
            // 重新添加
            for (TwoLevelCategory data : datas) {
                insert(data);
            }
            sqlite.close();
        }
    }

    /**
     * 保存一条数据到本地(若已存在则直接覆盖)
     *
     * @param data
     */
    public void save(TwoLevelCategory data) {
        List<TwoLevelCategory> datas = query(" where id=" + data.getId());
        if (datas != null && !datas.isEmpty()) {
            update(data);
        } else {
            insert(data);
        }
    }

    /**
     * 将所有该分类的数据设置为非默认后并设置默认
     * @param model
     */
    public void resetAllNoDefault(int model){
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + TWO_LEVEL_CATEGORY_TABLE_NAME + " set is_default=0 where one_level_id not in (select id from "+OneLevelCategoryDataBase.ONE_LEVEL_CATEGORY_TABLE_NAME+" where model !="+model+")");
        sqlite.execSQL(sql);
        sqlite.close();
    }

    public void destroy() {
        if(dbHelper != null)
            dbHelper.close();
    }

    /**
     * 执行sql语句
     * @param sql
     */
    public void excuteSql(String sql){
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql);
        sqlite.close();
    }

    /**
     * 获取二级分类对应一级分类的model
     * @param oneLevelId
     * @return
     */
    public static int getModel(int oneLevelId){
        for(OneLevelCategory oneLevelCategory: BaseApplication.oneLevelCategories){
            if(oneLevelCategory.getId() == oneLevelId)
                return oneLevelCategory.getModel();
        }
        return 0;
    }
}
