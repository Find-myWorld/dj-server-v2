package com.example.iimp_znxj_new2014.selfconsume;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * 功能：建立数据库+3张表，用于存储商品信息
 */
public class DBOpenHelper extends SQLiteOpenHelper {
	
	private static final String DBNAME = "commodity.db";  //商品数据库，库中有三张表
	private static final int VERSION = 1;
	private static final String TAG = "DBOpenHelper";
	
	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// TODO Auto-generated method stub  //新建一个表存储字段count,三个字段count1,count2,count3,永远只用一条数据，每次都是更新数据
		//表中字段：Id（编号），Name（名称），Description（说明）， Price（图片，二进制）， Pic（价格）
		String sqlDailys = "create table if not exists daily_t (id integer primary key ,name varchar(20) not null,description varchar(64),price double,pic blob);";
		String sqlLabors = "create table if not exists labor_t (id integer primary key ,name varchar(20) not null,description varchar(64),price double not null,pic blob);";
		String sqlFoods  = "create table if not exists food_t (id integer primary key ,name varchar(20) not null,description varchar(64),price double not null,pic blob);";
		//sqlCounts表结构说明：id，编号；dailyCount、laborCount、foodCount:分别标识商品xml表格的变化；flag，接收临时播报的数目；content,临时播报的内容（#号隔开）colorNum,临时播报颜色变化参数（#号隔开）；countNum,值班循环的次数;cardNum,要发送的值班表人员编号
		String sqlCounts = "create table if not exists count_t (id integer primary key ,dailyCount int,laborCount int,foodCount int,flag int,content text,colorNum text,countNum int,cardNum text,tsflag int);";  //tsflag:提审通知的数目
		db.execSQL(sqlDailys);
		db.execSQL(sqlLabors);
		db.execSQL(sqlFoods);
		db.execSQL(sqlCounts);
		db.execSQL("insert into count_t (id,dailyCount,laborCount,foodCount,flag,colorNum,countNum,tsflag) values (1,0,0,0,0,'',0,0);");//isfirst:1首次，2非首次
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(newVersion > oldVersion){
			db.execSQL("Drop table if existis daily_t");
			db.execSQL("Drop table if existis labor_t");
			db.execSQL("Drop table if existis food_t");
			db.execSQL("Drop table if existis count_t");
		}else{
			return;
		}
		onCreate(db);
	}
	
}


























