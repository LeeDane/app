package com.leedane.cn.financial.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.database.BaseSQLiteOpenHelper;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 记账的数据库操作类
 * Created by LeeDane on 2016/7/22.
 */
public class FinancialDataBase {
    public static final String TAG = "FinancialDataBase";
    private final BaseSQLiteOpenHelper dbHelper;

    public static final String FINANCIAL_TABLE_NAME = "financial";


    public static final String CREATE_FINANCIAL_TABLE = "CREATE TABLE " + FINANCIAL_TABLE_NAME + " ("
            + "local_id integer primary key autoincrement, " + // local_id
            "id integer, " +  //记账的ID,跟服务器保持一致的ID
            "status integer, " + // 状态,-1,草稿 1正常, 2：删除
            "model integer, " + // 模块,1:收入；2：支出
            "money float, "+ // 相关金额
            "one_level varchar(20), "+ //一级分类
            "two_level varchar(20), "+ //二级分类
            "has_img integer, " + //是否有主图, 0：表示没有, 1表示有
            "path varchar(255), " +//图像的路径
            "location varchar(255), " + //位置信息
            "longitude double, " + //经度
            "latitude double, " + //纬度
            "create_user_id integer, " + // 创建人
            "create_time varchar(25)," + // 创建时间
            "financial_desc text," + // 备注信息
            "synchronous integer," + // 是否同步
            "addition_time varchar(25) not null" + //添加时间，必须(该数据实际上的，用于统计等)
            ");";

    public FinancialDataBase(Context context) {
        dbHelper = BaseSQLiteOpenHelper.getHelper(context, ConstantsUtil.DB_NAME);
    }

    /**
     * 新增
     * @param data
     * @return true表示成功插入,false表示不成功插入
     */
    public boolean insert(FinancialBean data) {

        if(isExists(data.getLocalId())){
            Log.i(TAG, "数据已经存在:"+data.getLocalId());
            return false;
        }
        
        String sql = "insert into " + FINANCIAL_TABLE_NAME;

        sql += "(id, status, model, money, one_level, two_level, has_img, path,location, longitude,latitude,financial_desc,synchronous,create_user_id, create_time, addition_time) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] {
                data.getId() + "", data.getStatus() +"", data.getModel() +"", data.getMoney() +"",
                data.getOneLevel(), data.getTwoLevel(), StringUtil.changeTrueOrFalseToInt(data.isHasImg()) +"", data.getPath()
                , data.getLocation(), data.getLongitude() +"", data.getLatitude() +"",
                data.getFinancialDesc(), StringUtil.changeTrueOrFalseToInt(data.isSynchronous()) +"", data.getCreateUserId() +"", data.getCreateTime(), data.getAdditionTime() });
        if(sqlite.isOpen())
            sqlite.close();

        Log.i(TAG, "数据插入成功:" + data.getId());
        return true;
    }

    /**
     * 判断记录是否存在
     * @param localId
     * @return
     */
    private boolean isExists(int localId){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        int id = 0;
        Cursor cursor = sqlite.rawQuery("select local_id from "
                + FINANCIAL_TABLE_NAME + " where local_id = ?", new String[]{localId + ""});
        if(!cursor.isClosed())
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                id = cursor.getInt(0);
            }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        if(sqlite.isOpen())
            if(sqlite.isOpen())
                sqlite.close();
        return id > 0 ;
    }


    /**
     * 新增
     * @param data
     * @return true表示成功插入,false表示不成功插入
     */
    public boolean insertServer(FinancialBean data) {

        if(isExistsServer(data.getId())){
            Log.i(TAG, "数据已经存在:"+data.getId());
            return false;
        }

        String sql = "insert into " + FINANCIAL_TABLE_NAME;

        sql += "(id, status, model, money, one_level, two_level, has_img, path,location, longitude,latitude,financial_desc,synchronous,create_user_id, create_time, addition_time) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[]{
                data.getId() + "", data.getStatus() + "", data.getModel() + "", data.getMoney() + "",
                data.getOneLevel(), data.getTwoLevel(), StringUtil.changeTrueOrFalseToInt(data.isHasImg()) + "", data.getPath()
                , data.getLocation(), data.getLongitude() + "", data.getLatitude() + "",
                data.getFinancialDesc(), StringUtil.changeTrueOrFalseToInt(data.isSynchronous()) + "", data.getCreateUserId() + "", data.getCreateTime(), data.getAdditionTime()});
        if(sqlite.isOpen())
            sqlite.close();

        Log.i(TAG, "数据插入成功:" + data.getId());
        return true;
    }

    /**
     * 判断记录是否存在
     * @param id
     * @return
     */
    private boolean isExistsServer(int id){
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        int i = 0;
        Cursor cursor = sqlite.rawQuery("select local_id from "
                + FINANCIAL_TABLE_NAME + " where id = ?", new String[]{id + ""});
        if(!cursor.isClosed())
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                i = cursor.getInt(0);
            }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        if(sqlite.isOpen())
            sqlite.close();
        return i > 0 ;
    }


    /**
     * 删掉指定一条记录
     * @param localId
     */
    public void delete(int localId) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + FINANCIAL_TABLE_NAME + " where local_id=?");
        sqlite.execSQL(sql, new Integer[]{localId});
        if(sqlite.isOpen())
            sqlite.close();
    }

    /**
     * 删掉指定一条记录(多用户的情况下)
     * @param userId
     */
    public void deleteByUser(int userId) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + FINANCIAL_TABLE_NAME + " where create_user_id=?");
        sqlite.execSQL(sql, new Integer[]{userId});
        if(sqlite.isOpen())
            sqlite.close();
    }

    /**
     * 删掉全部记录
     */
    public void deleteAll() {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + FINANCIAL_TABLE_NAME );
        sqlite.execSQL(sql);
        if(sqlite.isOpen())
            sqlite.close();
    }

    /**
     * 更新记录
     * @param data
     */
    public void update(FinancialBean data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + FINANCIAL_TABLE_NAME + " set id=?, status=?, model=?, money=?, one_level=?, two_level=?, has_img=?,path=?," +
                "location=?, longitude=?,latitude=?,financial_desc=?,synchronous=?, create_user_id=?, create_time=?, addition_time=? where local_id=?");
        sqlite.execSQL(sql, new Object[]{
                data.getId() + "", data.getStatus() + "", data.getModel() + "", data.getMoney() + "",
                data.getOneLevel(), data.getTwoLevel(), StringUtil.changeTrueOrFalseToInt(data.isHasImg()) + "", data.getPath()
                , data.getLocation(), data.getLongitude() + "", data.getLatitude() + "",
                data.getFinancialDesc(), StringUtil.changeTrueOrFalseToInt(data.isSynchronous()) + "",
                data.getCreateUserId() + "", data.getCreateTime(), data.getAdditionTime(), data.getLocalId()});
        if(sqlite.isOpen())
            sqlite.close();
    }

    /**
     * 更新同步状态信息
     * @param localId  本地ID
     * @param id  服务器ID
     * @param status 状态
     */
    public void updateSynchronousInfo(int localId, int id, int status){
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + FINANCIAL_TABLE_NAME + " set synchronous=?, id=? where local_id=?");
        sqlite.execSQL(sql, new Object[]{status , id, localId});
        if(sqlite.isOpen())
            sqlite.close();
    }

    /**
     * 更新所有的数据同步状态为非同步
     */
    public void updateAllNoSynchronous(){
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + FINANCIAL_TABLE_NAME + " set synchronous=0, id=0");
        sqlite.execSQL(sql);
        if(sqlite.isOpen())
            sqlite.close();
    }

    public List<FinancialBean> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<FinancialBean> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<FinancialBean> data = new ArrayList<>();
        Cursor cursor = sqlite.rawQuery("select local_id, id, status, model, money, one_level, two_level, has_img, path, " +
                "location, longitude, latitude, financial_desc, synchronous, create_user_id, create_time, addition_time  from "
                + FINANCIAL_TABLE_NAME + where, null);
        if(!cursor.isClosed())
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                FinancialBean financialBean = new FinancialBean();
                financialBean.setLocalId(cursor.getInt(0));
                financialBean.setId(cursor.getInt(1));
                financialBean.setStatus(cursor.getInt(2));
                financialBean.setModel(cursor.getInt(3));
                financialBean.setMoney(cursor.getFloat(4));
                financialBean.setOneLevel(cursor.getString(5));
                financialBean.setTwoLevel(cursor.getString(6));
                financialBean.setHasImg(StringUtil.changeIntToTrueOrFalse(cursor.getInt(7)));
                financialBean.setPath(cursor.getString(8));
                financialBean.setLocation(cursor.getString(9));
                financialBean.setLongitude(cursor.getLong(10));
                financialBean.setLatitude(cursor.getLong(11));
                financialBean.setFinancialDesc(cursor.getString(12));
                financialBean.setSynchronous(StringUtil.changeIntToTrueOrFalse(cursor.getInt(13)));
                financialBean.setCreateUserId(cursor.getInt(14));
                financialBean.setCreateTime(cursor.getString(15));
                financialBean.setAdditionTime(cursor.getString(16));
                data.add(financialBean);
            }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        if(sqlite.isOpen())
            sqlite.close();

        return data;
    }



    /**
     * 重置
     *
     * @param datas
     */
    public void reset(List<FinancialBean> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + FINANCIAL_TABLE_NAME);
            // 重新添加
            for (FinancialBean data : datas) {
                insert(data);
            }
            if(sqlite.isOpen())
                sqlite.close();
        }
    }

    /**
     * 本月收入
     * @return
     */
    public int thisMonthInCome(){
        int total = 0;

        return total;
    }

    /**
     * 本月支出
     * @return
     */
    public int thisMonthSpend(){
        int total = 0;

        return total;
    }

    /**
     * 保存一条数据到本地(若已存在则直接覆盖)
     *
     * @param data
     */
    public void save(FinancialBean data) {
        List<FinancialBean> datas = query(" where local_id=" + data.getLocalId());
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
