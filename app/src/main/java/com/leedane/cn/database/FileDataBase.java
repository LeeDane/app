package com.leedane.cn.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.bean.FileBean;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.MySettingConfigUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * File文件数据库操作类
 * Created by LeeDane on 2016/6/8.
 */
public class FileDataBase {
    public static final String TAG = "FileDataBase";
    private final BaseSQLiteOpenHelper dbHelper;
    public static final String FILE_TABLE_NAME = "file";
    public static final String CREATE_FILE_TABLE = "CREATE TABLE " + FILE_TABLE_NAME + " ("
            + "ID integer primary key autoincrement, " + // id
            "fid integer, " +  //文件的ID
            "path varchar(255), "+ // 文件路径
            "is_upload_qiniu integer, "+// 是否上传到七牛, 0：表示还没有， 1表示已经上传
            "table_id integer, " + //表ID
            "table_name varchar(20), " + //表的名称
            "table_uuid varchar(120), " + //表uuid
            "create_time varchar(25)" + // 创建时间

            ");";
    public FileDataBase(Context context) {
        dbHelper = new BaseSQLiteOpenHelper(context, ConstantsUtil.DB_NAME);
    }

    /**
     * 新增
     * @param data
     * @return true表示成功插入，false表示不成功插入
     */
    public boolean insert(FileBean data) {

        if(isExists(data.getId())){
            Log.i(TAG, "数据已经存在:"+data.getId());
            return false;
        }

        //获得总数
        int total = getTotal();
        if(total >= 50){
            Log.i(TAG, "数据超过50条");
            int minId = getMinId();
            if(data.getId() < minId){  //数据更旧，直接过滤掉
                return false;
            }else{
                //数据是新的，直接删除旧的数据
                delete(minId);
            }
        }
        String sql = "insert into " + FILE_TABLE_NAME;
        sql += "(fid,path,is_upload_qiniu,table_id,table_name,table_uuid,create_time) values(?, ?, ?, ?, ?, ?, ?)";
        int isUploadQiniu = 0;
        if(data.isUploadQiniu()){
            isUploadQiniu = 1;
        }
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] {
                data.getId() + "", data.getPath(), isUploadQiniu +"",data.getTableId() +"",
                data.getTableName(), data.getTableUuid(), data.getCreateTime() });
        sqlite.close();
        Log.i(TAG, "数据插入成功:" + data.getId());
        return true;
    }

    /**
     * 获得总记录数
     * @return
     */
    public int getTotal(){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        Cursor cursor = sqlite.rawQuery("select count(fid) from " +FILE_TABLE_NAME , null);
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
     * 判断记录是否存在
     * @param fid
     * @return
     */
    private boolean isExists(int fid){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<MoodBean> datas = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select fid from "
                + FILE_TABLE_NAME + " where fid = ?", new String[]{fid + ""});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            MoodBean mood = new MoodBean();
            mood.setId(cursor.getInt(0));
            datas.add(mood);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        return datas.size() > 0 ;
    }

    /**
     * 删掉最旧的一条记录
     */
    public int getMinId() {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        Cursor cursor = sqlite.rawQuery("select min(fid) from " +FILE_TABLE_NAME , null);
        int fid = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            fid = cursor.getInt(0);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        return fid;
    }

    /**
     * 删掉指定一条记录
     * @param id
     */
    public void delete(int id) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + FILE_TABLE_NAME + " where fid=?");
        sqlite.execSQL(sql, new Integer[] {id});
        sqlite.close();
    }

    /**
     * 删掉全部记录
     */
    public void deleteAll() {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + FILE_TABLE_NAME );
        sqlite.execSQL(sql);
        sqlite.close();
    }

    /**
     * 改
     * @param data
     */
    public void update(FileBean data) {
        String sql = ("update " + FILE_TABLE_NAME + " set fidv,path=?,is_upload_qiniu=?,table_id=?,table_name=?,table_uuid=?,create_time=?");
        int isUploadQiniu = 0;
        if(data.isUploadQiniu()){
            isUploadQiniu = 1;
        }
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] {
                data.getId() + "", data.getPath(), isUploadQiniu +"",data.getTableId() +"",
                data.getTableName(), data.getTableUuid(), data.getCreateTime() });
        sqlite.close();
    }

    public List<FileBean> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<FileBean> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<FileBean> datas = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select fid,path,is_upload_qiniu,table_id,table_name,table_uuid,create_time  from "
                + FILE_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            FileBean fileBean = new FileBean();
            fileBean.setId(cursor.getInt(0));
            fileBean.setPath(cursor.getString(1));
            int isUploadQiniu = cursor.getInt(2);
            if(isUploadQiniu == 1){
                fileBean.setIsUploadQiniu(true);
            }
            fileBean.setTableId(cursor.getInt(3));
            fileBean.setTableName(cursor.getString(4));
            fileBean.setTableUuid(cursor.getString(5));
            fileBean.setCreateTime(cursor.getString(6));
            datas.add(fileBean);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();

        return datas;
    }

    /**
     * 首页加载50条数据
     * @return
     */
    public List<FileBean> queryFileLimit50() {
        return query(" where fid > 0 order by create_time desc limit 0,50 ");
    }

    /**
     * 重置
     * @param datas
     */
    public void reset(List<FileBean> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + FILE_TABLE_NAME);
            if(MySettingConfigUtil.getCacheFile()) {
                // 重新添加
                for (FileBean data : datas) {
                    insert(data);
                }
            }
            sqlite.close();
        }
    }

    /**
     * 保存一条数据到本地(若已存在则直接覆盖)
     * @param data
     */
    public void save(FileBean data) {
        List<FileBean> datas = query(" where fid=" + data.getId());
        if (datas != null && !datas.isEmpty()) {
            update(data);
        } else {
            if(MySettingConfigUtil.getCacheFile()) {
                insert(data);
            }
        }
    }
    public void destroy() {
        if(dbHelper != null)
            dbHelper.close();
    }
}
