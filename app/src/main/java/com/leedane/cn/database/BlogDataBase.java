package com.leedane.cn.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.bean.BlogBean;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.MySettingConfigUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 博客的数据库操作类
 * Created by LeeDane on 2016/6/3.
 */
public class BlogDataBase {
    public static final String TAG = "BlogDataBase";
    private final BaseSQLiteOpenHelper dbHelper;
    public static final String BLOG_TABLE_NAME = "blog";
    public static final String CREATE_BLOG_TABLE = "CREATE TABLE " + BLOG_TABLE_NAME + " ("
            + "ID integer primary key autoincrement, " + // id
            "bid integer, " +  //博客的ID
            "title varchar(255), " + // 博客标题
            "content text, "+ // 博客内容
            "digest varchar(155), " + // 博客摘要
            "tag varchar(50), " + // 标签(多个用逗号隔开)
            "froms varchar(30), " + //来自
            "has_img integer, "+// 是否有主图, 0：表示没有， 1表示有
            "img_url text, " + //图片的地址
            "origin_link text, " + //原文的链接
            "source varchar(30), " + //来源
            "is_read integer, " + // 是否读取, 0：表示未读， 1表示已经读取
            "create_user_id integer, " + // 创建人
            "account varchar(30), " + // 创建人姓名
            "create_time varchar(25)" + // 创建时间

            ");";
    public BlogDataBase(Context context) {
        dbHelper = new BaseSQLiteOpenHelper(context, ConstantsUtil.DB_NAME);
    }

    /**
     * 新增
     * @param data
     * @return true表示成功插入，false表示不成功插入
     */
    public boolean insert(BlogBean data) {

        if(isExists(data.getId())){
            Log.i(TAG, "数据已经存在:"+data.getId());
            return false;
        }

        //获得总数
        int total = getTotal();
        if(total >= 25){
            Log.i(TAG, "数据超过25条");
            int minId = getMinId();
            if(data.getId() < minId){  //数据更旧，直接过滤掉
                return false;
            }else{
                //数据是新的，直接删除旧的数据
                delete(minId);
            }
        }
        String sql = "insert into " + BLOG_TABLE_NAME;
        sql += "(bid,title,content,digest,tag,froms,has_img,img_url,origin_link,source,is_read,create_user_id,account,create_time) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int read = 0;
        if(data.isRead()){
            read = 1;
        }

        int hasImg = 0;
        if(data.isHasImg()){
            hasImg = 1;
        }
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] {
                data.getId() + "", data.getTitle(), data.getContent(), data.getDigest(),
                data.getTag(), data.getFroms(), hasImg +"", data.getImgUrl(), data.getOriginLink(),
                data.getSource(), read +"", data.getCreateUserId() +"", data.getAccount(), data.getCreateTime() });
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
        Cursor cursor = sqlite.rawQuery("select count(bid) from " +BLOG_TABLE_NAME , null);
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
     * @param bid
     * @return
     */
    private boolean isExists(int bid){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<BlogBean> data = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select bid from "
                + BLOG_TABLE_NAME + " where bid = ?", new String[]{bid + ""});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            BlogBean blog = new BlogBean();
            blog.setId(cursor.getInt(0));
            data.add(blog);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();

        return data.size() > 0 ;
    }

    /**
     * 获取最旧的一条记录Id
     */
    public int getMinId() {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        Cursor cursor = sqlite.rawQuery("select min(bid) from " +BLOG_TABLE_NAME, null);
        int bid = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            bid = cursor.getInt(0);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        return bid;
    }

    /**
     * 删掉指定一条记录
     * @param id
     */
    public void delete(int id) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + BLOG_TABLE_NAME + " where bid=?");
        sqlite.execSQL(sql, new Integer[] { id });
        sqlite.close();
    }

    /**
     * 删掉全部记录
     */
    public void deleteAll() {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + BLOG_TABLE_NAME );
        sqlite.execSQL(sql);
        sqlite.close();
    }

    /**
     * 改
     * @param data
     */
    public void update(BlogBean data) {
        int read = 0;
        if(data.isRead()){
            read = 1;
        }

        int hasImg = 0;
        if(data.isHasImg()){
            hasImg = 1;
        }
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + BLOG_TABLE_NAME + " set bid=?,title=?,content=?,digest=?,tag=?,froms=?,has_img=?,img_url=?,origin_link=?,source=?,is_read=?,create_user_id=?,account=?,create_time=?");
        sqlite.execSQL(sql, new String[] {
                data.getId() + "", data.getTitle(), data.getContent(), data.getDigest(),
                data.getTag(), data.getFroms(), hasImg +"", data.getImgUrl(), data.getOriginLink(),
                data.getSource(), read +"", data.getCreateUserId() +"", data.getAccount(), data.getCreateTime() });
        sqlite.close();
    }

    public List<BlogBean> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<BlogBean> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<BlogBean> datas = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select bid,title,content,digest,tag,froms,has_img,img_url,origin_link,source,is_read,create_user_id,account,create_time  from "
                + BLOG_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            BlogBean blogBean = new BlogBean();
            blogBean.setId(cursor.getInt(0));
            blogBean.setTitle(cursor.getString(1));
            blogBean.setContent(cursor.getString(2));
            blogBean.setDigest(cursor.getString(3));
            blogBean.setTag(cursor.getString(4));
            blogBean.setFroms(cursor.getString(5));
            int hasImg = cursor.getInt(6);
            if(hasImg == 1){
                blogBean.setHasImg(true);
            }
            blogBean.setImgUrl(cursor.getString(7));
            blogBean.setOriginLink(cursor.getString(8));
            blogBean.setSource(cursor.getString(9));
            int read = cursor.getInt(10);
            if(read == 1){
                blogBean.setRead(true);
            }
            blogBean.setCreateUserId(cursor.getInt(11));
            blogBean.setAccount(cursor.getString(12));
            blogBean.setCreateTime(cursor.getString(13));
            datas.add(blogBean);
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
    public List<BlogBean> queryBlogLimit25() {
        return query(" where bid > 0 order by create_time desc limit 0,25 ");
    }

    /**
     * 重置
     * @param datas
     */
    public void reset(List<BlogBean> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + BLOG_TABLE_NAME);
            if(MySettingConfigUtil.getCacheBlog()) {
                // 重新添加
                for (BlogBean data : datas) {
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
    public void save(BlogBean data) {
        List<BlogBean> datas = query(" where bid=" + data.getId());
        if (datas != null && !datas.isEmpty()) {
            update(data);
        } else {
            if(MySettingConfigUtil.getCacheBlog()) {
                insert(data);
            }
        }
    }
    public void destroy() {
        dbHelper.close();
    }
}
