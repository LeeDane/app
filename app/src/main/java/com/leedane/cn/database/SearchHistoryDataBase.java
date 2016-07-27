package com.leedane.cn.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.bean.search.SearchHistoryBean;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 搜索历史的数据库操作类
 * Created by LeeDane on 2016/5/22.
 */
public class SearchHistoryDataBase {
    public static final String TAG = "SearchHistoryDataBase";
    private final BaseSQLiteOpenHelper dbHelper;

    public static final String SEARCH_TABLE_NAME = "search";


    public static final String CREATE_SEARCH_TABLE = "CREATE TABLE " + SEARCH_TABLE_NAME + " ("
            + "ID integer primary key autoincrement, " + // id
            "search_type varchar(5), " +  //搜索的类型
            "search_key varchar(50), " + // 搜索的内容
            "create_time varchar(25)" + // 创建时间
            ");";
    public SearchHistoryDataBase(Context context) {
        dbHelper = BaseSQLiteOpenHelper.getHelper(context, ConstantsUtil.DB_NAME);
    }

    /**
     * 新增搜索记录
     * 前提：判断是否有记录存在，有的话先删除旧的记录后插入新的记录
     * @param data
     * @return true表示成功插入，false表示不成功插入
     */
    public boolean insert(SearchHistoryBean data) {

        if(data.getId() > 0 ){
            Log.i(TAG, "数据已经存在:"+data.getId());
            delete(data.getId());
        }else{
            //判断内容是否存在
            if(isExists(data) != null){
                Log.i(TAG, "内容相同:key:"+data.getSearchKey() +",type:"+data.getSearchType());
                delete(data.getSearchType(), data.getSearchKey());//删掉旧的内容的数据
            }
        }

        String sql = "insert into " + SEARCH_TABLE_NAME +"(search_type, search_key, create_time) values(?, ?, ?)";

        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] { data.getSearchType(), data.getSearchKey(), DateUtil.DateToString(new Date())});
        sqlite.close();
        return true;
    }

    private SearchHistoryBean isExists(SearchHistoryBean searchHistoryBean){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<SearchHistoryBean> data = new ArrayList<SearchHistoryBean>();
        Cursor cursor = sqlite.rawQuery("select id from " + SEARCH_TABLE_NAME + " where search_type = ? and search_key=? ", new String[]{searchHistoryBean.getSearchType(), searchHistoryBean.getSearchKey()});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            SearchHistoryBean historyBean = new SearchHistoryBean();
            searchHistoryBean.setId(cursor.getInt(0));
            data.add(historyBean);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();

        return data.size() > 0 ? data.get(0) : null ;
    }

    /**
     * 删掉指定一条记录
     * @param id
     */
    public void delete(int id) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + SEARCH_TABLE_NAME + " where id=?");
        sqlite.execSQL(sql, new Integer[]{id});
        sqlite.close();
    }

    /**
     * 删掉指定一条记录
     * @param type
     * @param key
     */
    public void delete(String type, String key) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + SEARCH_TABLE_NAME + " where search_type=? and search_key=? ");
        sqlite.execSQL(sql, new String[]{type, key});
        sqlite.close();
    }

    /**
     * 删掉全部记录
     */
    public void deleteAll() {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + SEARCH_TABLE_NAME );
        sqlite.execSQL(sql);
        sqlite.close();
    }

    public List<SearchHistoryBean> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<SearchHistoryBean> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<SearchHistoryBean> data = new ArrayList<SearchHistoryBean>();
        Cursor cursor = sqlite.rawQuery("select id, search_type, search_key, create_time  from "
                + SEARCH_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            SearchHistoryBean searchHistoryBean = new SearchHistoryBean();
            searchHistoryBean.setId(cursor.getInt(0));
            searchHistoryBean.setSearchType(cursor.getString(1));
            searchHistoryBean.setSearchKey(cursor.getString(2));
            searchHistoryBean.setCreateTime(cursor.getString(3));
            data.add(searchHistoryBean);
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
    public void reset(List<SearchHistoryBean> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + SEARCH_TABLE_NAME);
            // 重新添加
            for (SearchHistoryBean data : datas) {
                insert(data);
            }
            sqlite.close();
        }
    }
    public void destroy() {
        dbHelper.close();
    }
}
