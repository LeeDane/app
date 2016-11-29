package com.leedane.cn.financial.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.database.BaseSQLiteOpenHelper;
import com.leedane.cn.financial.bean.FinancialLocationBean;
import com.leedane.cn.util.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 记账位置的数据库操作类
 * Created by LeeDane on 2016/11/23.
 */
public class FinancialLocationDataBase {
    public static final String TAG = "LocationDataBase";
    private final BaseSQLiteOpenHelper dbHelper;

    public static final String FINANCIAL_LOCATION_TABLE_NAME = "financial_location";


    public static final String CREATE_FINANCIAL_LOCATION_TABLE = "CREATE TABLE " + FINANCIAL_LOCATION_TABLE_NAME + " ("
            + "local_id integer primary key autoincrement, " + // local_id
            "id integer, " +  //记账的ID,跟服务器保持一致的ID
            "status integer, " + // 状态,1正常, 0：禁用
            "location varchar(255), " + //位置信息
            "location_desc varchar(255), " + //描述信息
            "create_user_id integer, " + // 创建人
            "create_time varchar(25)" + // 创建时间
            ");";

    public FinancialLocationDataBase(Context context) {
        dbHelper = BaseSQLiteOpenHelper.getHelper(context, ConstantsUtil.DB_NAME);
    }

    /**
     * 新增
     * @param data
     * @return true表示成功插入,false表示不成功插入
     */
    public boolean insert(FinancialLocationBean data) {

        if(isExists(data.getId())){
            Log.i(TAG, "数据已经存在:"+data.getId());
            return false;
        }
        
        String sql = "insert into " + FINANCIAL_LOCATION_TABLE_NAME;

        sql += "(id, status, location, location_desc, create_user_id, create_time) " +
                "values(?, ?, ?, ?, ?, ?)";
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] {
                data.getId() + "", data.getStatus() +"", data.getLocation(), data.getLocationDesc()
                , data.getCreateUserId() +"", data.getCreateTime() });
        sqlite.close();

        Log.i(TAG, "数据插入成功:" + data.getId());
        return true;
    }

    /**
     * 判断记录是否存在
     * @param id
     * @return
     */
    private boolean isExists(int id){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        Cursor cursor = sqlite.rawQuery("select id from "
                + FINANCIAL_LOCATION_TABLE_NAME + " where id = ?", new String[]{id + ""});

        int result = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        return result > 0 ;
    }

    /**
     * 删掉指定一条记录
     * @param id
     */
    public void delete(int id) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + FINANCIAL_LOCATION_TABLE_NAME + " where id=?");
        sqlite.execSQL(sql, new Integer[]{id});
        sqlite.close();
    }

    /**
     * 删掉指定一条记录(多用户的情况下)
     * @param userId
     */
    public void deleteByUser(int userId) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + FINANCIAL_LOCATION_TABLE_NAME + " where create_user_id=?");
        sqlite.execSQL(sql, new Integer[]{userId});
        sqlite.close();
    }

    /**
     * 删掉全部记录
     */
    public void deleteAll() {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + FINANCIAL_LOCATION_TABLE_NAME );
        sqlite.execSQL(sql);
        sqlite.close();
    }

    /**
     * 更新记录
     * @param data
     */
    public void update(FinancialLocationBean data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + FINANCIAL_LOCATION_TABLE_NAME + " set status=?," +
                "location=?, location_desc=?, create_user_id=?, create_time=? where id=?");
        sqlite.execSQL(sql, new Object[]{
                data.getStatus() + "",  data.getLocation(), data.getLocationDesc() + "",
                data.getCreateUserId() + "", data.getCreateTime(), data.getId()});
        sqlite.close();
    }

    public List<FinancialLocationBean> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<FinancialLocationBean> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<FinancialLocationBean> data = new ArrayList<>();
        try{
            Cursor cursor = sqlite.rawQuery("select id, status, location, location_desc, create_user_id, create_time  from "
                    + FINANCIAL_LOCATION_TABLE_NAME + where, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                FinancialLocationBean financialLocationBean = new FinancialLocationBean();
                financialLocationBean.setId(cursor.getInt(0));
                financialLocationBean.setStatus(cursor.getInt(1));
                financialLocationBean.setLocation(cursor.getString(2));
                financialLocationBean.setLocationDesc(cursor.getString(3));
                financialLocationBean.setCreateUserId(cursor.getInt(4));
                financialLocationBean.setCreateTime(cursor.getString(5));
                data.add(financialLocationBean);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        sqlite.close();

        return data;
    }



    /**
     * 重置
     *
     * @param datas
     */
    public void reset(List<FinancialLocationBean> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + FINANCIAL_LOCATION_TABLE_NAME);
            // 重新添加
            for (FinancialLocationBean data : datas) {
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
    public void save(FinancialLocationBean data) {
        List<FinancialLocationBean> datas = query(" where id=" + data.getId());
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
