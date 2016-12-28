package com.leedane.cn.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.bean.GalleryBean;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.MySettingConfigUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 图库数据库操作类
 * Created by LeeDane on 2016/6/3.
 */
public class GalleryDataBase {
    public static final String TAG = "GalleryDataBase";
    private final BaseSQLiteOpenHelper dbHelper;
    public static final String GALLERY_TABLE_NAME = "gallery";
    public static final String CREATE_GALLERY_TABLE = "CREATE TABLE " + GALLERY_TABLE_NAME + " ("
            + "ID integer primary key autoincrement, " + // id
            "gid integer, " +  //图库的ID
            "path text, "+ // 图片的路径
            "gallery_desc varchar(255), " + //图片的描述
            "width integer, " + //宽度
            "height integer, " + //高度
            "length long, " + //长度
            "create_user_id integer, " + // 创建人
            "account varchar(30), " + // 创建人姓名
            "create_time varchar(25)" + // 创建时间

            ");";
    public GalleryDataBase(Context context) {
        dbHelper = BaseSQLiteOpenHelper.getHelper(context, ConstantsUtil.DB_NAME);
    }

    /**
     * 新增
     * @param data
     * @return true表示成功插入，false表示不成功插入
     */
    public boolean insert(GalleryBean data) {

        if(isExists(data.getId())){
            Log.i(TAG, "数据已经存在:"+data.getId());
            return false;
        }

        //获得总数
        int total = getTotal(data.getUserId());
        if(total >= 50){
            Log.i(TAG, "数据超过50条");
            int minId = getMinId(data.getUserId());
            if(data.getId() < minId){  //数据更旧，直接过滤掉
                return false;
            }else{
                //数据是新的，直接删除旧的数据
                delete(data.getUserId(), minId);
            }
        }
        String sql = "insert into " + GALLERY_TABLE_NAME;
        sql += "(gid,path,gallery_desc,width,height,length,create_user_id,account,create_time) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] {
                data.getId() + "", data.getPath(), data.getDesc(), data.getWidth() +"",
                data.getHeight() +"", data.getLength() +"", data.getUserId() +"", data.getAccount(), data.getCreateTime() });
        sqlite.close();
        Log.i(TAG, "数据插入成功:" + data.getId());
        return true;

    }

    /**
     * 获得总记录数
     * @param userId
     * @return
     */
    private int getTotal(int userId){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        Cursor cursor = sqlite.rawQuery("select count(*) from " +GALLERY_TABLE_NAME +" where create_user_id=?" , new String[]{String.valueOf(userId)});
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
     * 获得总记录数
     * @return
     */
    public int getTotal(){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        Cursor cursor = sqlite.rawQuery("select count(gid) from " +GALLERY_TABLE_NAME , null);
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
     * @param gid
     * @return
     */
    private boolean isExists(int gid){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<GalleryBean> datas = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select gid from "
                + GALLERY_TABLE_NAME + " where gid = ?", new String[]{gid + ""});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            GalleryBean galleryBean = new GalleryBean();
            galleryBean.setId(cursor.getInt(0));
            datas.add(galleryBean);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        return datas.size() > 0 ;
    }

    /**
     * 获取最旧的一条记录ID
     * @param userId
     */
    public int getMinId(int userId) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        Cursor cursor = sqlite.rawQuery("select min(gid) from " +GALLERY_TABLE_NAME +" where create_user_id=?" , new String[]{String.valueOf(userId)});
        int mid = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            mid = cursor.getInt(0);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        return mid;
    }

    /**
     * 删掉指定一条记录
     * @param userId
     * @param id
     */
    public void delete(int userId, int id) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + GALLERY_TABLE_NAME + " where gid=? and create_user_id=?");
        sqlite.execSQL(sql, new Integer[] {id, userId});
        sqlite.close();
    }

    /**
     * 删掉全部记录
     */
    public void deleteAll() {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + GALLERY_TABLE_NAME );
        sqlite.execSQL(sql);
        sqlite.close();
    }

    /**
     * 改
     * @param data
     */
    public void update(GalleryBean data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + GALLERY_TABLE_NAME + " set gid=?,path=?,gallery_desc=?,width=?,height=?,length=?,create_user_id=?,account=?,create_time=?");
        sqlite.execSQL(sql, new String[] {
                data.getId() + "", data.getPath(), data.getDesc(), data.getWidth() +"",
                data.getHeight() +"", data.getLength() +"", data.getUserId() +"", data.getAccount(), data.getCreateTime() });
        sqlite.close();
    }

    public List<GalleryBean> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<GalleryBean> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<GalleryBean> datas = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select gid,path,gallery_desc,width,height,length,create_user_id,account,create_time  from "
                + GALLERY_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            GalleryBean galleryBean = new GalleryBean();
            galleryBean.setId(cursor.getInt(0));
            galleryBean.setPath(cursor.getString(1));
            galleryBean.setDesc(cursor.getString(2));
            galleryBean.setWidth(cursor.getInt(3));
            galleryBean.setHeight(cursor.getInt(4));
            galleryBean.setLength(cursor.getLong(5));
            galleryBean.setUserId(cursor.getInt(6));
            galleryBean.setAccount(cursor.getString(7));
            galleryBean.setCreateTime(cursor.getString(8));
            Log.i(TAG, "图库的ID是："+galleryBean.getId());
            datas.add(galleryBean);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();

        return datas;
    }

    /**
     * 首页加载25条数据
     * @return
     */
    public List<GalleryBean> queryGalleryLimit50(int userId) {
        return query(" where gid > 0 and create_user_id="+userId+" order by datetime(create_time) desc limit 0,50 ");
    }

    /**
     * 重置
     * @param datas
     */
    public void reset(List<GalleryBean> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + GALLERY_TABLE_NAME);
            if (MySettingConfigUtil.cache_gallery){
                // 重新添加
                for (GalleryBean data : datas) {
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
    public void save(GalleryBean data) {
        List<GalleryBean> datas = query(" where gid=" + data.getId());
        if (datas != null && !datas.isEmpty()) {
            update(data);
        } else {
            if (MySettingConfigUtil.cache_gallery) {
                insert(data);
            }
        }
    }
    public void destroy() {
        if(dbHelper != null)
            dbHelper.close();
    }
}
