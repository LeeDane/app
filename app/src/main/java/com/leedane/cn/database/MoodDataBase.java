package com.leedane.cn.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.MySettingConfigUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 心情数据库操作类
 * Created by LeeDane on 2016/6/3.
 */
public class MoodDataBase {
    public static final String TAG = "MoodDataBase";
    private final BaseSQLiteOpenHelper dbHelper;
    public static final String MOOD_TABLE_NAME = "mood";
    public static final String CREATE_MOOD_TABLE = "CREATE TABLE " + MOOD_TABLE_NAME + " ("
            + "ID integer primary key autoincrement, " + // id
            "mid integer, " +  //心情的ID
            "content text, "+ // 心情内容
            "froms varchar(30), " + //来自
            "has_img integer, "+// 是否有主图, 0：表示没有， 1表示有
            "imgs text, " + //多张图像的路径，多个用","分隔开
            "zan_number integer, " + //赞的数量
            "comment_number integer, " + //评论数
            "transmit_number integer, " + //转发数
            "zan_users varchar(255), " + //赞用户
            "create_user_id integer, " + // 创建人
            "account varchar(30), " + // 创建人姓名
            "user_pic_path text, " + // 创建人头像
            "create_time varchar(25)" + // 创建时间

            ");";
    public MoodDataBase(Context context) {
        dbHelper = new BaseSQLiteOpenHelper(context, ConstantsUtil.DB_NAME);
    }

    /**
     * 新增
     * @param data
     * @return true表示成功插入，false表示不成功插入
     */
    public boolean insert(MoodBean data) {

        if(isExists(data.getId())){
            Log.i(TAG, "数据已经存在:"+data.getId());
            return false;
        }

        //获得总数
        int total = getTotal(data.getCreateUserId());
        if(total >= 25){
            Log.i(TAG, "数据超过25条");
            int minId = getMinId(data.getCreateUserId());
            if(data.getId() < minId){  //数据更旧，直接过滤掉
                return false;
            }else{
                //数据是新的，直接删除旧的数据
                delete(data.getCreateUserId(), minId);
            }
        }
        String sql = "insert into " + MOOD_TABLE_NAME;
        sql += "(mid,content,froms,has_img,imgs,zan_number,comment_number,transmit_number,zan_users,create_user_id,account,user_pic_path,create_time) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int hasImg = 0;
        if(data.isHasImg()){
            hasImg = 1;
        }
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] {
                data.getId() + "", data.getContent(), data.getFroms(), hasImg +"",
                data.getImgs(), data.getZanNumber() +"", data.getCommentNumber() +"", data.getTransmitNumber()+"",
                data.getPraiseUserList(), data.getCreateUserId() +"", data.getAccount(), data.getUserPicPath(), data.getCreateTime() });
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
        Cursor cursor = sqlite.rawQuery("select count(mid) from " +MOOD_TABLE_NAME +" where create_user_id=?" , new String[]{String.valueOf(userId)});
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
        Cursor cursor = sqlite.rawQuery("select count(mid) from " +MOOD_TABLE_NAME , null);
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
     * @param mid
     * @return
     */
    private boolean isExists(int mid){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<MoodBean> datas = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select mid from "
                + MOOD_TABLE_NAME + " where mid = ?", new String[]{mid + ""});
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
     * @param userId
     */
    public int getMinId(int userId) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        Cursor cursor = sqlite.rawQuery("select min(mid) from " +MOOD_TABLE_NAME +" where create_user_id=?" , new String[]{String.valueOf(userId)});
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
        String sql = ("delete from " + MOOD_TABLE_NAME + " where mid=? and create_user_id=?");
        sqlite.execSQL(sql, new Integer[] {id, userId});
        sqlite.close();
    }

    /**
     * 删掉全部记录
     */
    public void deleteAll() {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + MOOD_TABLE_NAME );
        sqlite.execSQL(sql);
        sqlite.close();
    }

    /**
     * 改
     * @param data
     */
    public void update(MoodBean data) {
        int hasImg = 0;
        if(data.isHasImg()){
            hasImg = 1;
        }
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + MOOD_TABLE_NAME + " set mid=?,content=?,froms=?,has_img=?,imgs=?,zan_number=?,comment_number=?,transmit_number=?,zan_users=?,create_user_id=?,account=?,user_pic_path=?,create_time=?");
        sqlite.execSQL(sql, new String[] {
                data.getId() + "", data.getContent(), data.getFroms(), hasImg +"",
                data.getImgs(), data.getZanNumber() +"", data.getCommentNumber() +"", data.getTransmitNumber()+"",
                data.getPraiseUserList(), data.getCreateUserId() +"", data.getAccount(), data.getUserPicPath(), data.getCreateTime() });
        sqlite.close();
    }

    public List<MoodBean> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<MoodBean> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<MoodBean> datas = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select mid,content,froms,has_img,imgs,zan_number,comment_number,transmit_number,zan_users,create_user_id,account,user_pic_path,create_time  from "
                + MOOD_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            MoodBean moodBean = new MoodBean();
            moodBean.setId(cursor.getInt(0));
            moodBean.setContent(cursor.getString(1));
            moodBean.setFroms(cursor.getString(2));
            int hasImg = cursor.getInt(3);
            if(hasImg == 1){
                moodBean.setHasImg(true);
            }
            moodBean.setImgs(cursor.getString(4));
            moodBean.setZanNumber(cursor.getInt(5));
            moodBean.setCommentNumber(cursor.getInt(6));
            moodBean.setTransmitNumber(cursor.getInt(7));
            moodBean.setPraiseUserList(cursor.getString(8));
            moodBean.setCreateUserId(cursor.getInt(9));
            moodBean.setAccount(cursor.getString(10));
            moodBean.setUserPicPath(cursor.getString(11));
            moodBean.setCreateTime(cursor.getString(12));
            datas.add(moodBean);
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
    public List<MoodBean> queryMoodLimit25(int userId) {
        return query(" where mid > 0 and create_user_id="+userId+" order by create_time desc limit 0,25 ");
    }

    /**
     * 重置
     * @param datas
     */
    public void reset(List<MoodBean> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + MOOD_TABLE_NAME);
            if(MySettingConfigUtil.getCacheMood()) {
                // 重新添加
                for (MoodBean data : datas) {
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
    public void save(MoodBean data) {
        List<MoodBean> datas = query(" where mid=" + data.getId());
        if (datas != null && !datas.isEmpty()) {
            update(data);
        } else {
            if(MySettingConfigUtil.getCacheMood()) {
                insert(data);
            }
        }
    }
    public void destroy() {
        if(dbHelper != null)
            dbHelper.close();
    }
}
