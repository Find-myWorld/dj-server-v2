package com.example.iimp_znxj_new2014.selfconsume;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
 * 说明：操作类，对数据的增删改查
 */
public class GoodsDao
{
    private static final String DBNAME = "commodity.db";   //数据库名，注意保持一致
    private static final int VERSION = 1;
    private static final String TAG = "DBOpenHelper";
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private static final String DAILY_TABLE = "daily_t";
    private static final String LABOR_TABLE = "labor_t";
    private static final String FOOD_TABLE = "food_t";
    private static final String COUNT_TABLE = "count_t";

    public GoodsDao(Context context)
    {
        helper = new DBOpenHelper(context, DBNAME, null, VERSION);
        db = helper.getWritableDatabase();
    }

    //插入操作 insert into pic_t (name,description,price,num,pic) values ('ssm','very good',12,1,os);
    public void addData(String tablename,Goods data)//不带插入图片
    {
        db.execSQL("insert into "+tablename+"(id,name,description,price) values(?,?,?,?)",
                new Object[]{data.getId(),data.getName(),data.getDescrip(),data.getPrice()});
    }
    public void addDailyPic(Goods data)
    {
		/*	db.execSQL("insert into "+DAILY_TABLE+"(pic) values(?)",
		    new Object[]{data.getPic()});    */
        db.execSQL("insert into "+DAILY_TABLE+"(id,name,description,price,pic) values(?,?,?,?,?)",
                new Object[]{data.getId(),data.getName(),data.getDescrip(),data.getPrice(),data.getPic()});
    }

    //获取某个表中数据的数目
    public int getTableCount(String tablename)  //Long类型 --> int类型
    {
        Log.i(TAG,"表名为："+tablename);
        cursor = db.rawQuery("select count(*) from "+tablename,null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        Log.i(TAG,"当前表中数量为："+count);
        return count;
    }

    //根据某个属性，查询数据
    public Cursor queryTableAndShow(String tablename)
    {
        cursor = db.query(DAILY_TABLE, new String[]{"name","description","price","pic"}, null, null, null, null, null);
        return cursor;
    }

    //根据某个属性，查询数据
    public Cursor idQueryTable(String id,String tablename)
    {
        cursor = db.query(tablename, new String[]{"name","description","price","pic"}, "id = ?", new String[]{id}, null, null, null);//idQuery(String _id),根据id查询
        return cursor;
    }

    public void addLaborPic(Goods data)
    {
        db.execSQL("insert into "+LABOR_TABLE+"(id,name,description,price,pic) values(?,?,?,?,?)",
                new Object[]{data.getId(),data.getName(),data.getDescrip(),data.getPrice(),data.getPic()});
    }
    //根据某个属性，查询数据
    public Cursor idQueryLabor(String id)
    {
        cursor = db.query(LABOR_TABLE, new String[]{"name","description","price","pic"}, "id = ?", new String[]{id}, null, null, null);//idQuery(String _id),根据id查询
        return cursor;
    }
    //查询数据库中数据，显示到数组中
    public Cursor queryTable(String tableName)
    {
        cursor = db.query(tableName, new String[]{"id","price","name","description","pic"}, null, null, null, null, null);
        return cursor;
    }
    public void addFoodPic(Goods data)
    {
        db.execSQL("insert into "+FOOD_TABLE+"(id,name,description,price,pic) values(?,?,?,?,?)",
                new Object[]{data.getId(),data.getName(),data.getDescrip(),data.getPrice(),data.getPic()});
    }
    //根据某个属性，查询数据
    public Cursor idQueryFood(String id)
    {
        cursor = db.query(FOOD_TABLE, new String[]{"name","description","price","pic"}, "id = ?", new String[]{id}, null, null, null);//idQuery(String _id),根据id查询
        return cursor;
    }

    //更新数据。 update pic_t set name='name' where id = id;
    public void update(String name,int id)
    {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.update(DAILY_TABLE, values, "id="+id, null);
    }

    //update count_t set key = 'value' where id = 1;
    public void updateCount(String key,String value)
    {
        ContentValues values = new ContentValues();
        values.put(key, value);
        db.update(COUNT_TABLE, values, null, null);
    }

    //获取count_t表中的某个字段key的值
    public Cursor idQueryCount(String key)
    {
        cursor = db.query(COUNT_TABLE, new String[]{key}, "id = 1", null, null, null, null);
        return cursor;
    }

    //根据id，更新图片
    public void updateDataPic(byte[] pic,int id,String tablename)
    {
        ContentValues values = new ContentValues();
        values.put("pic", pic);
        db.update(tablename, values, "id="+id, null);
    }

    //把count_t表置空
    public void setEmpty(){
        String sql = "update count_t set dailyCount='0',laborCount='0',foodCount='0' where id = 1;";
        db.execSQL(sql);
    }

    //删除数据   delete from pic_t where _id = id;
    public void delete(int id)
    {
        db.execSQL("delete from "+DAILY_TABLE+" where id= "+id);
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

    public void deleteTable(String tablename)
    {
//		db.execSQL("Drop table "+tablename);//删除表(包括字段)
        db.execSQL("delete from "+tablename);//删除表(仅删除表中内容)
    }

    public void createTable(String tablename)
    {
        db.execSQL("create table if not exists "+tablename+"(id integer primary key ,name varchar(20),description varchar(64),price double,pic blob);");
    }
}
