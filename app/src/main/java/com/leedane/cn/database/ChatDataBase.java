package com.leedane.cn.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatBean;
import com.leedane.cn.bean.ChatDetailBean;
import com.leedane.cn.bean.HttpResponseMyFriendsBean;
import com.leedane.cn.bean.MyFriendsBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天的数据库操作类
 * Created by LeeDane on 2016/5/11.
 */
public class ChatDataBase {
    public static final String TAG = "ChatDataBase";
    private final BaseSQLiteOpenHelper dbHelper;

    public static final String CHAT_TABLE_NAME = "chat";


    public static final String CREATE_CHAT_TABLE = "CREATE TABLE " + CHAT_TABLE_NAME + " ("
            + //"ID integer primary key autoincrement, " + // id
            "cid integer, " +  //聊天的ID
            "content text, " + // 聊天内容
            "_type integer, "+ // 类型
            "to_user_id integer, " + // 接收聊天信息的用户ID
            //"accout varchar(20), " + // 接收聊天信息的用户名称
            "create_user_id integer, " + // 创建人
            "read integer, " + // 是否读取, 0：表示未读， 1表示已经读取
            "code integer, " + // 与当前用户聊天的人的ID
            "create_time varchar(25)" + // 创建时间

            ");";
    public ChatDataBase(Context context) {
        dbHelper = BaseSQLiteOpenHelper.getHelper(context, ConstantsUtil.DB_NAME);
    }

    /**
     * 新增
     * @param data
     * @return true表示成功插入，false表示不成功插入
     */
    public boolean insert(ChatDetailBean data) {

        if(isExists(data.getId())){
            Log.i(TAG, "数据已经存在:"+data.getId());
            return false;
        }
        int code = 0;
        //自己
        if(data.getCreateUserId() == BaseApplication.getLoginUserId()){
            code = data.getToUserId();
        }else{
            code = data.getCreateUserId();
        }
        String sql = "insert into " + CHAT_TABLE_NAME;

        sql += "(cid, content, _type, to_user_id, create_user_id, create_time, code, read) values(?, ?, ?, ?, ?, ?, ?, ?)";

        int read = 0;
        if(data.isRead()){
            read = 1;
        }
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] {
                data.getId() + "", data.getContent() + "", data.getType() +"",
                data.getToUserId() +"", data.getCreateUserId() +"", data.getCreateTime(), code +"", read +"" });
        sqlite.close();

        Log.i(TAG, "数据插入成功:" + data.getId());
        return true;
    }

    private boolean isExists(int cid){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<ChatDetailBean> data = new ArrayList<ChatDetailBean>();
        Cursor cursor = sqlite.rawQuery("select cid from "
                + CHAT_TABLE_NAME + " where cid = ?", new String[]{cid + ""});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ChatDetailBean person = new ChatDetailBean();
            person.setId(cursor.getInt(0));
            data.add(person);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();

        return data.size() > 0 ;
    }

    /**
     * 删掉指定一条记录
     * @param id
     */
    public void delete(int id) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + CHAT_TABLE_NAME + " where cid=?");
        sqlite.execSQL(sql, new Integer[] { id });
        sqlite.close();
    }

    /**
     * 删掉指定一条记录
     * @param userId
     */
    public void deleteByUser(int userId) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + CHAT_TABLE_NAME + " where code=?");
        sqlite.execSQL(sql, new Integer[]{userId});
        sqlite.close();
    }

    /**
     * 删掉全部记录
     */
    public void deleteAll() {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + CHAT_TABLE_NAME );
        sqlite.execSQL(sql);
        sqlite.close();
    }

    /**
     * 改
     *
     * @param data
     */
    public void update(ChatDetailBean data) {
        int read = 0;
        if(data.isRead()){
            read = 1;
        }
        data.setContent("呵呵");
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + CHAT_TABLE_NAME + " set cid=?, content=?, _type=?, to_user_id=?, create_user_id=?, create_time=?, code=?, read=? where cid=?");
        sqlite.execSQL(sql,
                new Object[] { data.getId(), data.getContent() + "",
                        data.getType() +"", data.getToUserId() +"",
                        data.getCreateUserId() +"", data.getCreateTime(), data.getCode(), read, data.getId() });
        sqlite.close();

        /*List<ChatDetailBean> ss = query(" where cid=" +data.getId());
        Log.i(TAG, "cid---------->是："+ss.get(0).isRead());*/
    }

    /**
     * 将对应用户的聊天记录全部未读状态改为已读
     * @param toUserId
     */
    public void updateAllForRead(int toUserId) {

        List<ChatDetailBean> chatDetailBeans = query(" where read='0' and code ="+String.valueOf(toUserId));
        if(chatDetailBeans != null && chatDetailBeans.size() > 0){
            for(ChatDetailBean chatDetailBean: chatDetailBeans){
                chatDetailBean.setRead(true);
                update(chatDetailBean);
            }
        }
    }

    public List<ChatDetailBean> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<ChatDetailBean> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<ChatDetailBean> data = null;
        data = new ArrayList<ChatDetailBean>();
        Cursor cursor = sqlite.rawQuery("select cid, content, _type, to_user_id, create_user_id, create_time, code, read  from "
                + CHAT_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ChatDetailBean chatDetailBean = new ChatDetailBean();
            chatDetailBean.setId(cursor.getInt(0));
            chatDetailBean.setContent(cursor.getString(1));
            chatDetailBean.setType(cursor.getInt(2));
            chatDetailBean.setToUserId(cursor.getInt(3));
            chatDetailBean.setCreateUserId(cursor.getInt(4));
            chatDetailBean.setCreateTime(cursor.getString(5));
            chatDetailBean.setCode(cursor.getInt(6));
            chatDetailBean.setRead(false);
            int read = cursor.getInt(7);
            if(read == 1){
                chatDetailBean.setRead(true);
            }
            data.add(chatDetailBean);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();

        return data;
    }

    /**
     * 查聊天首页信息的展示
     * @param context
     * @param myFriendsBeans
     * @return
     */
    public List<ChatBean> queryChatHome(Context context, List<MyFriendsBean> myFriendsBeans) {
        ArrayList<ChatBean> data = new ArrayList<ChatBean>();
        if(myFriendsBeans != null && myFriendsBeans.size() > 0) {
            String sql = "select cid, content, _type, to_user_id, create_user_id, create_time, code, " +
                    "(SELECT count(*) from " + CHAT_TABLE_NAME + " where code = o.code and read = 0) no_read_number " +
                    "from " + CHAT_TABLE_NAME + " o  " +
                    "where not EXISTS(" +
                    "select 0 from " + CHAT_TABLE_NAME + " o1  where o1.code = o.code and o1.cid > o.cid" +
                    ") order by datetime(create_time) desc";
            SQLiteDatabase sqlite = dbHelper.getReadableDatabase();

            Cursor cursor = sqlite.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ChatBean chatBean = new ChatBean();
                chatBean.setAccount(getAccountByFriend(myFriendsBeans, cursor.getInt(6)));
                chatBean.setCreateTime(cursor.getString(5));
                chatBean.setContent(cursor.getString(1));
                chatBean.setToUserId(cursor.getInt(6));
                chatBean.setCreateUserId(cursor.getInt(4));
                chatBean.setId(cursor.getInt(0));
                chatBean.setNoReadNumber(cursor.getInt(7));
                chatBean.setType(0);
                chatBean.setUserPicPath(getUserPicPathByFriend(myFriendsBeans, cursor.getInt(6)));
                data.add(chatBean);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            sqlite.close();
        }
        return data;
    }

    private String getAccountByFriend(List<MyFriendsBean> friendsBeans, int userId){
        if(friendsBeans != null && friendsBeans.size() > 0){
            for(MyFriendsBean myFriendsBean: friendsBeans){

                if(myFriendsBean.getId() == userId){
                    return myFriendsBean.getAccount();
                }
            }
        }
        return null;
    }

    private String getUserPicPathByFriend(List<MyFriendsBean> friendsBeans, int userId){
        if(friendsBeans != null && friendsBeans.size() > 0){
            for(MyFriendsBean myFriendsBean: friendsBeans){

                if(myFriendsBean.getId() == userId){
                    return myFriendsBean.getUserPicPath();
                }
            }
        }
        return null;
    }

    /**
     * 重置
     *
     * @param datas
     */
    public void reset(List<ChatDetailBean> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + CHAT_TABLE_NAME);
            // 重新添加
            for (ChatDetailBean data : datas) {
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
    public void save(ChatDetailBean data) {
        List<ChatDetailBean> datas = query(" where cid=" + data.getId());
        if (datas != null && !datas.isEmpty()) {
            update(data);
        } else {
            insert(data);
        }
    }

    //
    // /**
    // * 合并一条数据到本地(通过更新时间判断仅保留最新)
    // *
    // * @param data
    // * @return 数据是否被合并了
    // */
    // public boolean merge(NotebookData data) {
    // Cursor cursor = sqlite.rawQuery(
    // "select * from " + DatabaseHelper.CHAT_TABLE_NAME
    // + " where _id=" + data.getId(), null);
    // NotebookData localData = new NotebookData();
    // // 本循环其实只执行一次
    // for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
    // localData.setId(cursor.getInt(0));
    // localData.setIid(cursor.getInt(1));
    // localData.setUnixTime(cursor.getString(2));
    // localData.setDate(cursor.getString(3));
    // localData.setContent(cursor.getString(4));
    // localData.setColor(cursor.getInt(5));
    // }
    // // 是否需要合这条数据
    // boolean isMerge = localData.getUnixTime() < data.getUnixTime();
    // if (isMerge) {
    // save(data);
    // }
    // return isMerge;
    // }

    public void destroy() {
        if(dbHelper != null)
            dbHelper.close();
    }
}
