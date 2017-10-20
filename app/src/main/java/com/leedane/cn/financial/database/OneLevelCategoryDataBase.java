package com.leedane.cn.financial.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.database.BaseSQLiteOpenHelper;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.OneLevelCategory;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 记账一级分类的数据库操作类
 * Created by LeeDane on 2016/7/24.
 */
public class OneLevelCategoryDataBase {
    public static final String TAG = "OneCategoryDataBase";
    private final BaseSQLiteOpenHelper dbHelper;

    public static final String ONE_LEVEL_CATEGORY_TABLE_NAME = "one_level_category";


    public static final String CREATE_ONE_LEVEL_CATEGORY_TABLE = "CREATE TABLE " + ONE_LEVEL_CATEGORY_TABLE_NAME + " ("
            + "id integer primary key autoincrement, " + // id
            "order_ integer, " +  //分类的排序ID,从1开始
            "status integer, " + // 状态, 0禁用 1正常,
            "value varchar(20), " + // 展示的大类名称
            "icon integer, "+ // 显示的图标
            "model integer, "+ //模型，1收入，2支出
            "budget float, " + //一级分类的预算
            "is_default integer, " +//是否是默认的分类, 0：表示没有, 1表示有
            "create_user_id integer, " + // 创建人
            "create_time varchar(25)" + // 创建时间
            ");";

    public OneLevelCategoryDataBase(Context context) {
        dbHelper = BaseSQLiteOpenHelper.getHelper(context, ConstantsUtil.DB_NAME);
    }

    /**
     * 第一次使用初始化一级分类
     */
    public static List<OneLevelCategory> initData(){
        List<OneLevelCategory> oneLevelCategories = new ArrayList<>();

        Date date = new Date();

        OneLevelCategory oneLevelCategory1 = new OneLevelCategory();
        oneLevelCategory1.setOrder(101);
        oneLevelCategory1.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory1.setValue("食品酒水");
        oneLevelCategory1.setIsDefault(true);
        oneLevelCategory1.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory1.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory1.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory1);

        OneLevelCategory oneLevelCategory14 = new OneLevelCategory();
        oneLevelCategory14.setOrder(102);
        oneLevelCategory14.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory14.setValue("日常购物");
        oneLevelCategory14.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory14.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory14.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory14);

        OneLevelCategory oneLevelCategory2 = new OneLevelCategory();
        oneLevelCategory2.setOrder(103);
        oneLevelCategory2.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory2.setValue("衣服饰品");
        oneLevelCategory2.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory2.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory2.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory2);

        OneLevelCategory oneLevelCategory3 = new OneLevelCategory();
        oneLevelCategory3.setOrder(104);
        oneLevelCategory3.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory3.setValue("居家物业");
        oneLevelCategory3.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory3.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory3.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory3);

        OneLevelCategory oneLevelCategory4 = new OneLevelCategory();
        oneLevelCategory4.setOrder(105);
        oneLevelCategory4.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory4.setValue("行车交通");
        oneLevelCategory4.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory4.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory4.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory4);

        OneLevelCategory oneLevelCategory5 = new OneLevelCategory();
        oneLevelCategory5.setOrder(106);
        oneLevelCategory5.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory5.setValue("交流通讯");
        oneLevelCategory5.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory5.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory5.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory5);

        OneLevelCategory oneLevelCategory6 = new OneLevelCategory();
        oneLevelCategory6.setOrder(107);
        oneLevelCategory6.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory6.setValue("休闲娱乐");
        oneLevelCategory6.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory6.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory6.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory6);

        OneLevelCategory oneLevelCategory7 = new OneLevelCategory();
        oneLevelCategory7.setOrder(108);
        oneLevelCategory7.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory7.setValue("学习进修");
        oneLevelCategory7.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory7.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory7.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory7);

        OneLevelCategory oneLevelCategory8 = new OneLevelCategory();
        oneLevelCategory8.setOrder(109);
        oneLevelCategory8.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory8.setValue("人情往来");
        oneLevelCategory8.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory8.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory8.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory8);

        OneLevelCategory oneLevelCategory10 = new OneLevelCategory();
        oneLevelCategory10.setOrder(110);
        oneLevelCategory10.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory10.setValue("金融银行");
        oneLevelCategory10.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory10.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory10.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory10);

        OneLevelCategory oneLevelCategory9 = new OneLevelCategory();
        oneLevelCategory9.setOrder(111);
        oneLevelCategory9.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory9.setValue("生活保健");
        oneLevelCategory9.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory9.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory9.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory9);

        OneLevelCategory oneLevelCategory11 = new OneLevelCategory();
        oneLevelCategory11.setOrder(112);
        oneLevelCategory11.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory11.setValue("其他杂项");
        oneLevelCategory11.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory11.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory11.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelCategories.add(oneLevelCategory11);

        //收入大类
        OneLevelCategory oneLevelCategory12 = new OneLevelCategory();
        oneLevelCategory12.setOrder(113);
        oneLevelCategory12.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory12.setValue("职业收入");
        oneLevelCategory12.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory12.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory12.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME);
        oneLevelCategories.add(oneLevelCategory12);

        OneLevelCategory oneLevelCategory13 = new OneLevelCategory();
        oneLevelCategory13.setOrder(114);
        oneLevelCategory13.setStatus(ConstantsUtil.STATUS_NORMAL);
        oneLevelCategory13.setValue("其他收入");
        oneLevelCategory13.setCreateUserId(BaseApplication.getLoginUserId());
        oneLevelCategory13.setCreateTime(DateUtil.DateToString(date));
        oneLevelCategory13.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME);
        oneLevelCategories.add(oneLevelCategory13);

        OneLevelCategoryDataBase dataBase = new OneLevelCategoryDataBase(BaseApplication.newInstance());
        for(OneLevelCategory oneLevelCategory : oneLevelCategories){
            dataBase.insert(oneLevelCategory);
        }
        dataBase.destroy();

        return oneLevelCategories;
    }

    /**
     * 新增
     * @param data
     * @return true表示成功插入,false表示不成功插入
     */
    public synchronized boolean insert(OneLevelCategory data) {

        if(isExists(data.getValue())){
            Log.i(TAG, "数据已经存在:"+data.getValue());
            return false;
        }

        String sql = "insert into " + ONE_LEVEL_CATEGORY_TABLE_NAME;

        sql += "(order_, status, value, icon, model, budget, is_default, create_user_id, create_time) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();

        // 开始事务
        sqlite.beginTransaction();
        try{
            sqlite.execSQL(sql, new String[] {
                    data.getOrder() + "", data.getStatus() +"", data.getValue(), data.getIcon() +"", data.getModel() +"", data.getBudget() + "",
                    StringUtil.changeTrueOrFalseToInt(data.isDefault()) +"", data.getCreateUserId() +"", data.getCreateTime() });
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
                + ONE_LEVEL_CATEGORY_TABLE_NAME + " where value = ?", new String[]{value});
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
        String sql = ("delete from " + ONE_LEVEL_CATEGORY_TABLE_NAME + " where id=?");
        sqlite.execSQL(sql, new Integer[]{id});
        sqlite.close();

        //同时删除二级分类
        SQLiteDatabase sqlite1 = dbHelper.getWritableDatabase();
        String sql1 = ("delete from " + TwoLevelCategoryDataBase.TWO_LEVEL_CATEGORY_TABLE_NAME + " where one_level_id=?");
        sqlite1.execSQL(sql1, new Integer[]{id});
        sqlite1.close();
    }

    /**
     * 删掉指定一条记录(多用户的情况下)
     * @param userId
     */
    public void deleteByUser(int userId) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + ONE_LEVEL_CATEGORY_TABLE_NAME + " where create_user_id=?");
        sqlite.execSQL(sql, new Integer[]{userId});
        sqlite.close();
    }

    /**
     * 删掉全部记录
     */
    public void deleteAll() {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + ONE_LEVEL_CATEGORY_TABLE_NAME );
        sqlite.execSQL(sql);
        sqlite.close();
    }

    /**
     * 更新记录
     * @param data
     */
    public void update(OneLevelCategory data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + ONE_LEVEL_CATEGORY_TABLE_NAME + " set order_=?, status=?, value=?, icon=?, model=?" +
                            ", budget=?, is_default=?, create_user_id=?, create_time=? where id=?");
        sqlite.execSQL(sql, new String[]{
                data.getOrder() + "", data.getStatus() +"", data.getValue(), data.getIcon() +"", data.getModel() +"", data.getBudget() + "",
                StringUtil.changeTrueOrFalseToInt(data.isDefault()) +"", data.getCreateUserId() +"", data.getCreateTime() , data.getId() +""});
        sqlite.close();
    }

    public List<OneLevelCategory> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<OneLevelCategory> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<OneLevelCategory> data = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select id, order_, status, value, icon, model, (select sum(t.budget) from "+ TwoLevelCategoryDataBase.TWO_LEVEL_CATEGORY_TABLE_NAME+" t where t.one_level_id = o.id) budget, is_default, create_user_id, create_time  from "
                        + ONE_LEVEL_CATEGORY_TABLE_NAME  +  " o " + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            OneLevelCategory oneLevelCategory = new OneLevelCategory();
            oneLevelCategory.setId(cursor.getInt(0));
            oneLevelCategory.setOrder(cursor.getInt(1));
            oneLevelCategory.setStatus(cursor.getInt(2));
            oneLevelCategory.setValue(cursor.getString(3));
            oneLevelCategory.setIcon(cursor.getInt(4));
            oneLevelCategory.setModel(cursor.getInt(5));
            oneLevelCategory.setBudget(cursor.getFloat(6));
            oneLevelCategory.setIsDefault(StringUtil.changeIntToTrueOrFalse(cursor.getInt(7)));
            oneLevelCategory.setCreateUserId(cursor.getInt(8));
            oneLevelCategory.setCreateTime(cursor.getString(9));
            data.add(oneLevelCategory);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        return data;
    }



    /**
     * 重置
     *
     * @param datas
     */
    public void reset(List<OneLevelCategory> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + ONE_LEVEL_CATEGORY_TABLE_NAME);
            // 重新添加
            for (OneLevelCategory data : datas) {
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
    public void save(OneLevelCategory data) {
        List<OneLevelCategory> datas = query(" where id=" + data.getId());
        if (datas != null && !datas.isEmpty()) {
            update(data);
        } else {
            insert(data);
        }
    }

    public void destroy() {
        if(dbHelper != null)
            dbHelper.close();
    }

    /**
     * 通过一级分类的名称获取一级分类的ID
     * @param value
     * @return
     */
    public static int getOneLevelIdByValue(String value){
        int id = 0;
        if(StringUtil.isNull(value))
            return id;
        List<OneLevelCategory> oneLevelCategories = BaseApplication.oneLevelCategories;
        if(!CommonUtil.isEmpty(oneLevelCategories)){
            for(OneLevelCategory category: oneLevelCategories){
                if(value.equals(category.getValue())){
                    id = category.getId();
                    break;
                }
            }
        }
        return id;
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
     * 将所有该分类的数据设置为非默认后并设置默认
     * @param model
     */
    public void resetAllNoDefault(int model){
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + ONE_LEVEL_CATEGORY_TABLE_NAME + " set is_default=? where model = ?");
        sqlite.execSQL(sql, new Object[]{0, model});
        sqlite.close();
    }

    /**
     * 判断一级分类是否是收入类型
     * @param oneLevelId
     * @return
     */
    public static boolean isIncome(int oneLevelId){
        for(OneLevelCategory oneLevelCategory: BaseApplication.oneLevelCategories){
            if(oneLevelCategory.getId() == oneLevelId)
                return oneLevelCategory.getModel() == IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME;
        }
        return false;
    }
}
