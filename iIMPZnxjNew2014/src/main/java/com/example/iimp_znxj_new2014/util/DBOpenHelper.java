package com.example.iimp_znxj_new2014.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * 数据库用于存储定时下载任务
 */
public class DBOpenHelper extends SQLiteOpenHelper {
	
	private static final String DBNAME = "downplan.db";  
	private static final int VERSION = 1;
	
	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//String alarmTable = "create table if not exists alarm_t (id text primary key,url text not null,filename text not null);"; 
		String alarmTable = "create table if not exists alarm_t (id integer primary key,url text not null,filename text not null);"; 
		//playplan_t表中url字段无意义，主要目的保持表结构和alarm_t表一样
		String playPlanTable = "create table if not exists playplan_t (id integer primary key,url text not null,filename text not null);"; 
		db.execSQL(alarmTable);
		db.execSQL(playPlanTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(newVersion > oldVersion){
			db.execSQL("Drop table if existis alarm_t");
			db.execSQL("Drop table if existis playplan_t");
		}else{
			return;
		}
		onCreate(db);
	}
	
	
	
}













