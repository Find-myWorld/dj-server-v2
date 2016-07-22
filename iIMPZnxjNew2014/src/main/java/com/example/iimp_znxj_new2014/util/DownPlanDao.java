package com.example.iimp_znxj_new2014.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
 * 定时下载的工具类
 */
public class DownPlanDao {
	private static final String DBNAME = "downplan.db";
	private static final int VERSION = 1;
	private DBOpenHelper helper;
	private SQLiteDatabase db;
	
	public DownPlanDao(Context context){
		helper = new DBOpenHelper(context, DBNAME, null, VERSION);
		db = helper.getWritableDatabase();
	}
	
	//插入数据
	public void addData(int id,String url,String filename,String table){
		db.execSQL("insert into "+table+"(id,url,filename) values("+id+",'"+url+"','"+filename+"');");
		Log.i("Msg","addData");
	}
	
	//读取数据（根据ID）
	public Cursor idQuery(int id,String table){
		Cursor cs = db.query(table, new String[]{"url", "filename"}, "id = ?", new String[]{id+""}, null,null, null);
		return cs;
	}
	
	//删除数据（根据ID）
	public void delete(int id,String table){
		db.execSQL("delete from "+table+" where id = "+id);
	}
	
	//删除表
	public void deleteTable(String tableName){
		String sql = "DROP TABLE IF EXISTS "+tableName;
		db.execSQL(sql);
	}
	
	//新建表
	public void createTable(String tableName){
		db.execSQL("create table if not exists "+tableName+"(id integer primary key,url text not null,filename text not null);");
	}
	
	
	//查询数据库中数据
	public Cursor queryTable(String tableName)
	{
		Cursor cursor = db.query(tableName, new String[]{"id"}, null, null, null, null, null);
		return cursor;
	}
	
	//判断当前时间是否有下载任务，如果有则覆盖
	public boolean checkId(int id,String table){
		Cursor cs = db.query(table, null, "id = ?", new String[]{id+""}, null, null, null);
		int ids = cs.getColumnIndex("id");
		Log.i("Msg","checkId");
		while(cs.moveToNext()){
			int getId = cs.getInt(ids);
			if(id == getId){
				return false;
			}
		}
		return true;
	}
	
	
	
}
