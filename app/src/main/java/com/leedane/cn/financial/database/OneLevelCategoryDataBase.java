package com.leedane.cn.financial.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.database.BaseSQLiteOpenHelper;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.OneLevelGategory;
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
    public static List<OneLevelGategory> initData(){
        List<OneLevelGategory> oneLevelGategories = new ArrayList<>();

        Date date = new Date();

        OneLevelGategory OneLevelGategory1 = new OneLevelGategory();
        OneLevelGategory1.setOrder(101);
        OneLevelGategory1.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory1.setValue("食品酒水");
        OneLevelGategory1.setIsDefault(true);
        OneLevelGategory1.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory1.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory1.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelGategories.add(OneLevelGategory1);

        OneLevelGategory OneLevelGategory2 = new OneLevelGategory();
        OneLevelGategory2.setOrder(102);
        OneLevelGategory2.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory2.setValue("衣服饰品");
        OneLevelGategory2.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory2.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory2.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelGategories.add(OneLevelGategory2);

        OneLevelGategory OneLevelGategory3 = new OneLevelGategory();
        OneLevelGategory3.setOrder(103);
        OneLevelGategory3.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory3.setValue("居家物业");
        OneLevelGategory3.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory3.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory3.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelGategories.add(OneLevelGategory3);

        OneLevelGategory OneLevelGategory4 = new OneLevelGategory();
        OneLevelGategory4.setOrder(104);
        OneLevelGategory4.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory4.setValue("行车交通");
        OneLevelGategory4.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory4.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory4.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelGategories.add(OneLevelGategory4);

        OneLevelGategory OneLevelGategory5 = new OneLevelGategory();
        OneLevelGategory5.setOrder(105);
        OneLevelGategory5.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory5.setValue("交流通讯");
        OneLevelGategory5.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory5.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory5.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelGategories.add(OneLevelGategory5);

        OneLevelGategory OneLevelGategory6 = new OneLevelGategory();
        OneLevelGategory6.setOrder(106);
        OneLevelGategory6.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory6.setValue("休闲娱乐");
        OneLevelGategory6.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory6.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory6.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelGategories.add(OneLevelGategory6);

        OneLevelGategory OneLevelGategory7 = new OneLevelGategory();
        OneLevelGategory7.setOrder(107);
        OneLevelGategory7.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory7.setValue("学习进修");
        OneLevelGategory7.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory7.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory7.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelGategories.add(OneLevelGategory7);

        OneLevelGategory OneLevelGategory8 = new OneLevelGategory();
        OneLevelGategory8.setOrder(108);
        OneLevelGategory8.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory8.setValue("人情往来");
        OneLevelGategory8.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory8.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory8.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelGategories.add(OneLevelGategory8);

        OneLevelGategory OneLevelGategory9 = new OneLevelGategory();
        OneLevelGategory9.setOrder(109);
        OneLevelGategory9.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory9.setValue("医疗保健");
        OneLevelGategory9.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory9.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory9.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelGategories.add(OneLevelGategory9);

        OneLevelGategory OneLevelGategory10 = new OneLevelGategory();
        OneLevelGategory10.setOrder(110);
        OneLevelGategory10.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory10.setValue("金融保险");
        OneLevelGategory10.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory10.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory10.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelGategories.add(OneLevelGategory10);

        OneLevelGategory OneLevelGategory11 = new OneLevelGategory();
        OneLevelGategory11.setOrder(111);
        OneLevelGategory11.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory11.setValue("其他杂项");
        OneLevelGategory11.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory11.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory11.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        oneLevelGategories.add(OneLevelGategory11);

        //收入大类
        OneLevelGategory OneLevelGategory12 = new OneLevelGategory();
        OneLevelGategory12.setOrder(112);
        OneLevelGategory12.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory12.setValue("职业收入");
        OneLevelGategory12.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory12.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory12.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME);
        oneLevelGategories.add(OneLevelGategory12);

        OneLevelGategory OneLevelGategory13 = new OneLevelGategory();
        OneLevelGategory13.setOrder(113);
        OneLevelGategory13.setStatus(ConstantsUtil.STATUS_NORMAL);
        OneLevelGategory13.setValue("其他收入");
        OneLevelGategory13.setCreateUserId(BaseApplication.getLoginUserId());
        OneLevelGategory13.setCreateTime(DateUtil.DateToString(date));
        OneLevelGategory13.setModel(IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME);
        oneLevelGategories.add(OneLevelGategory13);

        OneLevelCategoryDataBase dataBase = new OneLevelCategoryDataBase(BaseApplication.newInstance());
        for(OneLevelGategory oneLevelGategory: oneLevelGategories){
            dataBase.insert(oneLevelGategory);
        }
        dataBase.destroy();

        return oneLevelGategories;
    }

    /**
     * 新增
     * @param data
     * @return true表示成功插入,false表示不成功插入
     */
    public synchronized boolean insert(OneLevelGategory data) {

        if(isExists(data.getValue())){
            Log.i(TAG, "数据已经存在:"+data.getValue());
            return false;
        }

        String sql = "insert into " + ONE_LEVEL_CATEGORY_TABLE_NAME;

        sql += "(order_, status, value, icon, budget, is_default, create_user_id, create_time) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();

        // 开始事务
        sqlite.beginTransaction();
        try{
            sqlite.execSQL(sql, new String[] {
                    data.getOrder() + "", data.getStatus() +"", data.getValue(), data.getIcon() +"", data.getBudget() + "",
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
        sqlite.execSQL(sql, new Integer[] { id });
        sqlite.close();
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
    public void update(OneLevelGategory data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + ONE_LEVEL_CATEGORY_TABLE_NAME + " set order_=?, status=?, value=?, icon=?" +
                            ", budget=?, is_default=?, create_user_id=?, create_time=?");
        sqlite.execSQL(sql, new String[]{
                data.getOrder() + "", data.getStatus() +"", data.getValue(), data.getIcon() +"", data.getBudget() + "",
                StringUtil.changeTrueOrFalseToInt(data.isDefault()) +"", data.getCreateUserId() +"", data.getCreateTime() });
        sqlite.close();
    }

    public List<OneLevelGategory> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<OneLevelGategory> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<OneLevelGategory> data = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select id, order_, status, value, icon, budget, is_default, create_user_id, create_time  from "
                        + ONE_LEVEL_CATEGORY_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            OneLevelGategory oneLevelGategory = new OneLevelGategory();
            oneLevelGategory.setId(cursor.getInt(0));
            oneLevelGategory.setOrder(cursor.getInt(1));
            oneLevelGategory.setStatus(cursor.getInt(2));
            oneLevelGategory.setValue(cursor.getString(3));
            oneLevelGategory.setIcon(cursor.getInt(4));
            oneLevelGategory.setBudget(cursor.getFloat(5));
            oneLevelGategory.setIsDefault(StringUtil.changeIntToTrueOrFalse(cursor.getInt(6)));
            oneLevelGategory.setCreateUserId(cursor.getInt(7));
            oneLevelGategory.setCreateTime(cursor.getString(8));
            data.add(oneLevelGategory);
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
    public void reset(List<OneLevelGategory> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + ONE_LEVEL_CATEGORY_TABLE_NAME);
            // 重新添加
            for (OneLevelGategory data : datas) {
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
    public void save(OneLevelGategory data) {
        List<OneLevelGategory> datas = query(" where id=" + data.getId());
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
}
