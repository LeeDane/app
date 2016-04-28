package com.leedane.cn.database;
import com.leedane.cn.util.ConstantsUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BaseSQLiteOpenHelper extends SQLiteOpenHelper {

	public static  final String TAG = "BaseSQLiteOpenHelper";

	public static final String TABLE_FILE = "T_FILE";
	public static final String TABLE_MOOD_DRAFT = "T_MOOD_DRAFT";
	public BaseSQLiteOpenHelper(Context context, String name) {
		super(context, name, null, ConstantsUtil.DB_VERSION);
	}

	/**
	 * 当数据库不存在或者第一次执行的时候才调用 (non-Javadoc)
	 *
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		//database.execSQL(this.createTableUSER_ALL());
		//database.execSQL(this.createTableMONEY_IN());
		//database.execSQL(this.createTableMONEY_IN_TYPE());
		//database.execSQL(this.createTableOptions());
		database.execSQL(this.createTableFile());
		database.execSQL(this.createMoodDraft());
	}

	/**
	 * 当传进去的版本比上一版本高时调用的方法
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "call update");
		/** 开发阶段先不执行 **/
		/*
		 * db.execSQL("DROP TABLE IF EXISTS " + ConstantUtils.TABLE_USER_ALL);
		 * //删除USER_ALL表 db.execSQL("DROP TABLE IF EXISTS " +
		 * ConstantUtils.TABLE_MONEY_IN);//删除MONEY_IN表
		 * db.execSQL("DROP TABLE IF EXISTS " +
		 * ConstantUtils.TABLE_MONEY_IN_TYPE);//删除MONEY_IN_TYPE表
		 * db.execSQL("DROP TABLE IF EXISTS " +
		 * ConstantUtils.TABLE_OPTIONS);//删除OPTIONS表
		 *
		 * this.onCreate(db); //执行创建操作!
		 */
	}

	/**
	 * 创建文件表
	 * @return
	 */
	private String createTableFile() {
		String sql = "CREATE TABLE " + TABLE_FILE + " ("
				+ "ID integer primary key autoincrement, " + // id
				"status integer int, " + // status
				"filename varchar(255), " + // 文件名称
				"create_user_id int, " + // 创建人
				"create_time varchar(25)" + // 创建时间
				");";
		Log.i(TAG, "执行创建MoodDraft表的SQL语句");
		return sql;
	}

	/**
	 * 创建心情草稿表
	 * @return
	 */
	private String createMoodDraft() {
		String sql = "CREATE TABLE " + TABLE_MOOD_DRAFT + " ("
				+ "ID integer primary key autoincrement, " + // id
				"content varchar(255), " + // 心情的内容
				"uris text, " + // 心情的图片地址
				"create_user_id varchar(25), " + // 创建人
				"create_time varchar(25)" + // 创建时间
				");";
		Log.i(TAG, "执行创建FilePath表的SQL语句");
		return sql;
	}
}
