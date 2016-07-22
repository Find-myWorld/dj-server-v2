package com.example.iimp_znxj_new2014;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.iimp_znxj_new2014.activity.IndexActivity;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;

public class DianJiaoApplication extends Application {
	/** 打开的activity **/
	private List<Activity> activities = new ArrayList<Activity>();
	/** 应用实例 **/
	private static DianJiaoApplication instance;
	private static Context context;
	PendingIntent restartIntent;
	private List<IndexActivity> indexActivities = new ArrayList<IndexActivity>();

	// 串口键盘
	private final int mKey_Enter = 69;

	private final int mKey_Esc = 81;

	private final int mKey_Up = 85;// 19;
	private final int mKey_Down = 68;// 20;
	private final int mKey_Left = 76;// 21;
	private final int mKey_Right = 82;// 22;

	private final int mKey_0 = 48;
	private final int mKey_1 = 49;
	private final int mKey_2 = 50;
	private final int mKey_3 = 51;
	private final int mKey_4 = 52;
	private final int mKey_5 = 53;
	private final int mKey_6 = 54;
	private final int mKey_7 = 55;
	private final int mKey_8 = 56;
	private final int mKey_9 = 57;

	/*
	 //USB键盘  
	 private final int mKey_Enter=66;  
	
	 private final int mKey_Esc=111;  
	 
	 private final int mKey_Up=19;//19;   
	 private final int mKey_Down=20;//20;   
	 private final int mKey_Left=21;//21;
	 private final int mKey_Right=22;//22;
	 
	 private final int mKey_0=7;
	 private final int mKey_1=8;
	 private final int mKey_2=9;
	 private final int mKey_3=10;
	 private final int mKey_4=11;
	 private final int mKey_5=12;
	 private final int mKey_6=13;
	 private final int mKey_7=14;
	 private final int mKey_8=15;
	 private final int mKey_9=16; */

	/**
	 * 获得实例
	 * 
	 * @return
	 */
	public static DianJiaoApplication getInstance() {
		return instance;
	}

	/**
	 * 新建了一个activity
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		Log.d("jiayy", "addActivity name = " + activity);
		activities.add(activity);
	}

	/**
	 * 结束指定的Activity
	 * 
	 * @param activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			this.activities.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 应用退出，结束所有的activity
	 */
	public void exit() {
		for (Activity activity : activities) {
			if (activity != null) {
				activity.finish();
			}
		}
	}

	@Override
	public void onCreate() {
		instance = this;
		context = getApplicationContext();

		Intent intent = new Intent();
		intent.setClassName("com.example.iimp_znxj_new2014", Constant.INDEX_ACTIVITY_NAME);// "com.example.iimp_znxj_new2014.IndexActivity"
		restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		Thread.setDefaultUncaughtExceptionHandler(restartHandler);
		Log.i("TS", "restartHandler1");
		Calendar startTime = Calendar.getInstance();   //设置开始闹钟时间
		startTime.set(Calendar.HOUR_OF_DAY,23);
		startTime.set(Calendar.MINUTE,59);
		startTime.set(Calendar.SECOND,59);
		startTime.set(Calendar.MILLISECOND,0);
		Intent reBootIn=new Intent();//设置重启闹钟
		reBootIn.setAction("reboot");
		PendingIntent reBootPI=PendingIntent.getBroadcast(context, 0, reBootIn, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP,startTime.getTimeInMillis() , 24*3600*1000, reBootPI);
		
	}

	// 程序出现未捕获到的异常，1s后重启回到主界面
	public UncaughtExceptionHandler restartHandler = new UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread arg0, Throwable arg1){
			// TODO Auto-generated method stub
			Log.i("ERROR", "报错：" + arg1.getMessage());
			JingMoOrder.postErrorLog3(getApplicationContext(), arg1.getMessage(),Constant.ERROR_MSG);
			AlarmManager amr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			amr.set(AlarmManager.RTC, System.currentTimeMillis() + 800, restartIntent);
			System.exit(0); //
		}
	};

	/*
	 * 在任何页面获取context对象
	 */
	public static Context getContextObject() {
		return context;
	}

	public int getmKey_Enter() {
		return mKey_Enter;
	}

	public int getmKey_Up() {
		return mKey_Up;
	}

	public int getmKey_Down() {
		return mKey_Down;
	}

	public int getmKey_Right() {
		return mKey_Right;
	}

	public int getmKey_Left() {
		return mKey_Left;
	}

	public int getmKey_Esc() {
		return mKey_Esc;
	}

	public int getmKey_0() {
		return mKey_0;
	}

	public int getmKey_1() {
		return mKey_1;
	}

	public int getmKey_2() {
		return mKey_2;
	}

	public int getmKey_3() {
		return mKey_3;
	}

	public int getmKey_4() {
		return mKey_4;
	}

	public int getmKey_5() {
		return mKey_5;
	}

	public int getmKey_6() {
		return mKey_6;
	}

	public int getmKey_7() {
		return mKey_7;
	}

	public int getmKey_8() {
		return mKey_8;
	}

	public int getmKey_9() {
		return mKey_9;
	}

	// IndexActivities集合
	public void addIndexActivities(IndexActivity indexActivity) {
		indexActivities.add(indexActivity);
	}

	// finish所有IndexActivities集合
	public void finishIndexActivities() {
		for (Activity activity : activities) {
			activity.finish();
		}
	}
}
