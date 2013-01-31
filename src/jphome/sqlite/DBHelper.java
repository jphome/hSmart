package jphome.sqlite;

import jphome.hsmart.gConfig;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;
	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	public DBHelper(Context context, String name, int version){
		this(context, name, null, version);
	}

	public DBHelper(Context context, String name) {
		this(context, name, VERSION);
	}
	
	// 该函数是在第一次创建数据库的时候执行，实际上是在第一次得到SQLiteDatabase对象的时候调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("create a Database");
		// execSQL函数用于执行SQL语句
		db.execSQL("create table " + gConfig.sensorTableName + "(deviceId varchar(6), " +
				"sensorId varchar(6), sensorType varchar(4), sensorName varchar(12))");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("update a Database");
	}
	

}

















