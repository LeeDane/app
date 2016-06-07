package com.leedane.cn.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.MySettingBean;
import com.leedane.cn.util.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的设置数据库操作类
 * Created by LeeDane on 2016/6/6.
 */
public class MySettingDataBase {
    public static final String TAG = "MySettingDataBase";
    private final BaseSQLiteOpenHelper dbHelper;
    public static final String MY_SETTING_TABLE_NAME = "my_setting";
    public static final String CREATE_MY_SETTING_TABLE = "CREATE TABLE " + MY_SETTING_TABLE_NAME + " ("
            + "ID integer primary key autoincrement, " + // id
            "sid integer," +//设置项的ID(初始化的时候固定，不能重复)
            "key varchar(20), " +  //设置的键的值
            "value varchar(20), "+ // 设置的项的值
            "create_user_id integer, " + // 创建人
            "create_time varchar(25)" + // 创建时间
            ");";
    public MySettingDataBase(Context context) {
        dbHelper = new BaseSQLiteOpenHelper(context, ConstantsUtil.DB_NAME);
    }

    /**
     * 初始化我的设置项目
     */
    public static List<MySettingBean> initMySetting(){
        List<MySettingBean> mySettingBeans = new ArrayList<>();
        MySettingBean mySettingBean1 = new MySettingBean(1, "load_image", "1");
        MySettingBean mySettingBean2 = new MySettingBean(2, "no_notification", "0");
        MySettingBean mySettingBean3 = new MySettingBean(3, "first_load", "10");
        MySettingBean mySettingBean4 = new MySettingBean(4, "other_load", "5");
        MySettingBean mySettingBean5 = new MySettingBean(5, "double_click_out", "0");
        MySettingBean mySettingBean6 = new MySettingBean(6, "cache_blog", "1");
        MySettingBean mySettingBean7 = new MySettingBean(7, "cache_mood", "1");
        MySettingBean mySettingBean8 = new MySettingBean(8, "chat_text_size", "16");
        MySettingBean mySettingBean9 = new MySettingBean(9, "chat_delete", "只删除本地记录");
        MySettingBean mySettingBean10 = new MySettingBean(10, "chat_send_enter", "0");
        MySettingBean mySettingBean11 = new MySettingBean(11, "cache_gallery", "1");

        mySettingBeans.add(mySettingBean1);
        mySettingBeans.add(mySettingBean2);
        mySettingBeans.add(mySettingBean3);
        mySettingBeans.add(mySettingBean4);
        mySettingBeans.add(mySettingBean5);
        mySettingBeans.add(mySettingBean6);
        mySettingBeans.add(mySettingBean7);
        mySettingBeans.add(mySettingBean8);
        mySettingBeans.add(mySettingBean9);
        mySettingBeans.add(mySettingBean10);
        mySettingBeans.add(mySettingBean11);
        MySettingDataBase mySettingDataBase = new MySettingDataBase(BaseApplication.newInstance());
        for(MySettingBean mySettingBean: mySettingBeans){
            mySettingDataBase.insert(mySettingBean);
        }
        mySettingDataBase.destroy();
        return mySettingBeans;
    }

    /**
     * 新增
     * @param data
     * @return true表示成功插入，false表示不成功插入
     */
    public boolean insert(MySettingBean data) {
        String sql = "insert into " + MY_SETTING_TABLE_NAME;
        sql += "(sid,key,value,create_user_id,create_time) values(?, ?, ?, ?, ?)";
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] {data.getId() +"", data.getKey(), data.getValue(),  data.getCreateUserId() +"", data.getCreateTime() });
        sqlite.close();
        Log.i(TAG, "数据插入成功:" + data.getId());
        return true;
    }

    /**
     * 获得总记录数
     * @param userId
     * @return
     */
    public int getTotal(int userId){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        Cursor cursor = sqlite.rawQuery("select count(sid) from " +MY_SETTING_TABLE_NAME +" where create_user_id=?" , new String[]{String.valueOf(userId)});
        int total = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            total = cursor.getInt(0);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        return total;
    }

    /**
     * 删掉全部记录
     */
    public void deleteAll() {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + MY_SETTING_TABLE_NAME );
        sqlite.execSQL(sql);
        sqlite.close();
    }

    /**
     * 改
     * @param data
     */
    public void update(MySettingBean data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + MY_SETTING_TABLE_NAME + " set value=? where sid=?");
        sqlite.execSQL(sql, new String[] {data.getValue(), data.getId() +""});
        sqlite.close();
    }

    public List<MySettingBean> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<MySettingBean> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<MySettingBean> datas = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select sid,key,value,create_user_id,create_time  from "
                + MY_SETTING_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            MySettingBean mySettingBean = new MySettingBean();
            mySettingBean.setId(cursor.getInt(0));
            mySettingBean.setKey(cursor.getString(1));
            mySettingBean.setValue(cursor.getString(2));
            mySettingBean.setCreateUserId(cursor.getInt(3));
            mySettingBean.setCreateTime(cursor.getString(4));
            datas.add(mySettingBean);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();

        return datas;
    }

    /**
     * 重置
     * @param datas
     */
    public void reset(List<MySettingBean> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + MY_SETTING_TABLE_NAME);
            // 重新添加
            for (MySettingBean data : datas) {
                insert(data);
            }
            sqlite.close();
        }
    }
    public void destroy() {
        dbHelper.close();
    }
}
