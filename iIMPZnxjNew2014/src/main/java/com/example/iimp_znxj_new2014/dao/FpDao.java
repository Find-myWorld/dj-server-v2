package com.example.iimp_znxj_new2014.dao;

import com.example.iimp_znxj_new2014.util.DBOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.res.Resources;

/*
 * ˵���������࣬����ݵ���ɾ�Ĳ�
 */
public class FpDao 
{
	private static final String DBNAME = "commodity.db";   //��ݿ���ע�Ᵽ��һ��
	private static final int VERSION = 1;
	private static final String TAG = "FpDao";
	private DBOpenHelper helper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private static final String FP_TABLE = "fingerp_t";
	
	public FpDao(Context context)
	{
		helper = new DBOpenHelper(context, DBNAME, null, VERSION);
		db = helper.getWritableDatabase();
	}
	
	//������� ,ָ�Ʊ������Ա���
	public void addData(String fpNum,String rybhNum)//�������ͼƬ
	{
		db.execSQL("insert into fingerp_t(fpNum,rybh) values('"+fpNum+"','"+rybhNum+"');");
//	    db.execSQL("insert into "+FP_TABLE+"(fpNum,rybh) values(?,?)", 
//			new Object[]{data.getFpNum(),data.getRybhNum()});  
	}
	
	
/*	//��ȡĳ��������ݵ���Ŀ
	public int getTableCount(String tablename)  //Long���� --> int����
	{
		Log.i(TAG,"����Ϊ��"+tablename);
	    cursor = db.rawQuery("select count(*) from "+tablename,null);
	    cursor.moveToFirst();
	    int count = cursor.getInt(0);
	    cursor.close();
	    Log.i(TAG,"��ǰ��������Ϊ��"+count);  
	    return count;
	}*/
	
	/*//���ĳ�����ԣ���ѯ���
	public Cursor queryTableAndShow(String tablename)
	{
		cursor = db.query(FP_TABLE, new String[]{"fid","rybh"}, null, null, null, null, null);
		return cursor;
	}*/
	
	//���ָ�Ʊ�Ż�ȡ��Ӧ����Ա���
	public String getRybh(String fpNum)
	{
		int id = 0;
		String rybh = null;
		Cursor cs = db.rawQuery("select fid,rybh from fingerp_t where fpNum = "+fpNum, null);
		while (cs.moveToNext()) {
			id = cs.getInt(0);
			rybh = cs.getString(1);
			Log.i(TAG,"��Ա��ţ�"+rybh+",id="+id);
		}
		cs.close();
	    return rybh;
	}

	/*//������ݡ� update pic_t set name='name' where id = id;
	public void update(String name,int id)
	{
		ContentValues values = new ContentValues();
		values.put("name", name);
		db.update(FP_TABLE, values, "fid="+id, null);
	}*/
	
	//ɾ�����   delete from pic_t where _id = id;
	public void delete(int id)
	{
		db.execSQL("delete from "+FP_TABLE+" where fid= "+id);
	}
	
	public void close()
	{
		if(!cursor.isClosed()){
			cursor.close();
		}
		if(db != null)
		{
			db.close();
			db = null;
		}
		if(helper != null)
		{
			helper.close();
			helper = null;
		}
	}
	
	public void clearTable(String tablename)
	{
//		db.execSQL("Drop table "+tablename);//ɾ���(�����ֶ�)
		db.execSQL("delete from "+tablename);//ɾ���(��ɾ���������)
	}
}
