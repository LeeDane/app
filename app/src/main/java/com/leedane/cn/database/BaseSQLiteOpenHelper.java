package com.leedane.cn.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.leedane.cn.util.ConstantsUtil;

public class BaseSQLiteOpenHelper extends SQLiteOpenHelper {

	public static  final String TAG = "BaseSQLiteOpenHelper";
	public static final String TABLE_FILE = "T_FILE";
	public static final String TABLE_MOOD_DRAFT = "T_MOOD_DRAFT";

	/**
	 * 普通表创建的构造方法
	 * @param context
	 * @param name  数据库名称
	 */
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
		database.execSQL(this.createTableFile());
		database.execSQL(this.createMoodDraft());
		database.execSQL(ChatDataBase.CREATE_CHAT_TABLE);
		database.execSQL(SearchHistoryDataBase.CREATE_SEARCH_TABLE);
		database.execSQL(BlogDataBase.CREATE_BLOG_TABLE);
		database.execSQL(MoodDataBase.CREATE_MOOD_TABLE);
		database.execSQL(GalleryDataBase.CREATE_GALLERY_TABLE);
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
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE);

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
		Log.i(TAG, "执行创建TableFile表的SQL语句");
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
		Log.i(TAG, "执行创建MoodDraft表的SQL语句");
		return sql;
	}
}
