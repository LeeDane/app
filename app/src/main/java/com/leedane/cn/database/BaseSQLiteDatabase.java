package com.leedane.cn.database;

import java.util.HashMap;
import java.util.Map;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.TypeConversionUtil;

/**
 * 数据库基本的操作
 */
public class BaseSQLiteDatabase {

	public final static String TAG = "BaseSQLiteDatabase";
	private Context context;
	private BaseSQLiteOpenHelper helper;
	private int openType = 0; // 默认是getWritableDatabase()
	private int friendId;//好友ID

	// 返回的map集合提示信息，格式如{"isSuccess":true,"message":"操作成功！","other":"暂无附加信息"}
	Map<String, Object> resultMap = new HashMap<String, Object>();
	boolean isSuccess = false; // resultMap中是否成功
	String message = "保存失败"; // resultMap中提示信息,默认是"保存失败"
	String other = "无"; // resultMap中附加信息,默认是“无”

	private SQLiteDatabase db;

	public BaseSQLiteDatabase() {
	}

	public BaseSQLiteDatabase(Context context) {
		this.context = context;
		this.helper = BaseSQLiteOpenHelper.getHelper(this.context, ConstantsUtil.DB_NAME);
	}

	/**
	 *
	 * @param context
	 *            上下文
	 * @param openType
	 *            打开的类型：0表示：getWritableDatabase(),1表示:getReadableDatabase()
	 */
	public BaseSQLiteDatabase(Context context, int openType) {
		this.context = context;
		this.helper = BaseSQLiteOpenHelper.getHelper(context, ConstantsUtil.DB_NAME);
		this.openType = openType;
	}

	/**
	 * 打开数据库，就是为Database db赋值
	 * @return
	 * @throws SQLException
	 */
	public BaseSQLiteDatabase openDatebase() throws SQLException {
		if (openType == 0)
			db = this.helper.getWritableDatabase();
		else
			db = this.helper.getReadableDatabase();
		return this;
	}
	/**
	 * 关闭helper对象
	 */
	public void closeDataBase() {
		if (helper != null) {
			helper.close();
		}
	}

	/**
	 * 基本的插入操作(支持单表)
	 *
	 * @param tableName
	 *            数据库中对应的表名，字符串表示！有关联的表，目前只支持填写主表名称
	 * @param params key表示在数据表所对应的字段名称，value表示插入该字段所对应的值
	 * @return
	 */
	public Map<String, Object> insert(String tableName,
									  Map<String, Object> params) throws SQLException {
		if (tableName != null && !params.isEmpty()) {
			try {
				ContentValues values = TypeConversionUtil.changeMapParamsToContentValues(params);

				if(values != null){
					this.openDatebase(); //打开数据库连接
					db.beginTransaction();  //开始事务
					Long v = db.insert(tableName, null, values); //执行插入操作
					db.setTransactionSuccessful();//设置是为了将事务标记为成功，当结束事务时就会提交事务，不设将直接回滚
					Log.i(TAG, tableName +" insert return value = " + v);
					this.isSuccess = true;
					this.message = "保存成功";//设置保存成功
				}else{
					resultMap.put("message", "params转化成ContentValues有误!");
					resultMap.put("isSuccess", this.isSuccess);
					return resultMap;
				}
			} catch (SQLException e) {
				Log.i(TAG, "保存"+ tableName +"出错：" + e.getMessage()); // 打印异常信息
			}finally{
				db.endTransaction();  //结束事务
				this.closeDataBase(); //关闭数据库
			}
		}
		resultMap.put("isSuccess", this.isSuccess);
		resultMap.put("message", this.message);
		return resultMap;
	}

	/**
	 *  基本的更新操作
	 * @param tableName 要更新的表名
	 * @param params   key表示在数据表所对应的字段名称，value表示更新该字段所对应的值
	 * @param whereClause    where条件的语句，格式如"id=?,name=?"
	 * @param whereArgs  where条件语句中"?"所对应的值,注意先后顺序，类型是String[],格式是new String[]{"1","Lee"}
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> update(String tableName, Map<String, Object> params, String whereClause, String[] whereArgs) throws SQLException {
		if(tableName != null && !params.isEmpty()){
			try{
				ContentValues values  = TypeConversionUtil.changeMapParamsToContentValues(params);
				if(values != null){
					this.openDatebase();  //打开数据库
					db.beginTransaction(); //开始事务
					db.update(tableName, values, whereClause, whereArgs);
					db.setTransactionSuccessful();//设置是为了将事务标记为成功，当结束事务时就会提交事务，不设将直接回滚
				}else{
					resultMap.put("message", "params转化成ContentValues有误!");
					resultMap.put("isSuccess", this.isSuccess);
					return resultMap;
				}

			}catch(SQLException e){
				Log.i(TAG, "更新表"+ tableName +"出错：" +e.getMessage()); // 打印异常信息
			}finally{
				db.endTransaction(); //结束事务
				this.closeDataBase(); //关闭数据库连接
			}
		}
		resultMap.put("isSuccess", this.isSuccess);
		resultMap.put("message", this.message);
		return resultMap;
	}

	/**
	 * 基本的删除操作
	 * @param tableName 要删除的表名
	 * @param whereClause   where条件的语句，格式如"id=?,name=?",为null表示清空表的数据
	 * @param whereArgs    where条件语句中"?"所对应的值,注意先后顺序，类型是String[],格式是new String[]{"1","Lee"}
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> delete(String tableName, String whereClause, String[] whereArgs) throws SQLException{
		if(tableName != null){
			try{
				this.openDatebase();  //打开数据库
				db.beginTransaction(); //开始事务
				int i = db.delete(tableName, whereClause, whereArgs);
				db.setTransactionSuccessful();//设置是为了将事务标记为成功，当结束事务时就会提交事务，不设将直接回滚
			}catch(SQLException e){
				Log.i(TAG, "删除表"+ tableName +"的"+ whereClause +"数据出错：" +e.getMessage()); // 打印异常信息
			}finally{
				db.endTransaction();  //结束事务
				this.closeDataBase(); //关闭数据库连接
			}
		}
		resultMap.put("isSuccess", this.isSuccess);
		resultMap.put("message", this.message);
		return resultMap;
	}
	/**
	 * 基本查询row,返回SQL结果的行的集合
	 * @param sql  原始SQL查询条件
	 * @param args  sql中带"?"所对应的值,虽然是字符串数组,查询时候也支持将其转成其他类型
	 * @return
	 * @throws SQLException
	 */
	public Cursor rowQuery(String sql, String[] args) throws SQLException{
		try{
			this.openDatebase();  //打开数据库
			return db.rawQuery(sql, args);
		}catch(SQLException e){
			Log.i(TAG, "执行语句'" + sql + "'出错：" +e.getMessage()); // 打印异常信息
		}finally{
			//this.closeDataBase(); //关闭数据库连接
		}
		return null;
	}

	public void testdb() throws Exception {
		//db.
	}

	/**
	 * 用sql语句执行增删改操作
	 * @param sql  原始的sql语句
	 * @param args  sql语句中"?"对应的参数值
	 * @return
	 */
	public Map<String, Object> execSQLOnlyByInsertUpdateDelete(String sql, Object[] args) {
		if (sql != null) {
			try {
				this.openDatebase(); //打开数据库连接
				db.beginTransaction();  //开始事务
				db.execSQL(sql, args);
				db.setTransactionSuccessful();//设置是为了将事务标记为成功，当结束事务时就会提交事务，不设将直接回滚
			} catch (SQLException e) {
				Log.i(TAG, "执行SQL语句'"+ sql +"'出错：" + e.getMessage()); // 打印异常信息
			}finally{
				db.endTransaction();  //结束事务
				this.closeDataBase(); //关闭数据库
			}
		}
		resultMap.put("isSuccess", this.isSuccess);
		resultMap.put("message", this.message);
		return resultMap;
	}

}
