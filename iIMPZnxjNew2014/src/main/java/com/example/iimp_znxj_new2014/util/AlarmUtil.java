package com.example.iimp_znxj_new2014.util;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.iimp_znxj_new2014.activity.CallActivity;
import com.example.iimp_znxj_new2014.receiver.BootCompletedReceiver;

/*
 * 闹钟（定时操作）
 */
public class AlarmUtil {
	
	private static final String TAG = "AlarmUtil";
	private static final int ONE_HOUR = 60 * 1000 * 60; 
	private static final int ONE_MINUTE = 1000 * 60;
	private static final int ONE_SECONDS = 1000;
	public static int tag = 0;

	/*
	 * 取消定时定名
	 */
	public static void cancelAlarm(Context context) {
		Log.i(TAG,"cancelAlarm,接收到新的值班时间安排，取消上一个值班");
		Intent intent = new Intent(context, CallActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context,0, intent, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}

	/*
	 * 定时定名的
	 */
	public static void  startAlarm(Context context,int hour,int mininue,int interval) {
		Log.i(TAG,"startAlarm,hour="+hour+",mininue="+mininue+",interval="+interval);
		Calendar currentTime = Calendar.getInstance();///
		Calendar startTime = Calendar.getInstance();   //设置开始闹钟时间
		startTime.set(Calendar.HOUR_OF_DAY,hour);
		startTime.set(Calendar.MINUTE,mininue);
		startTime.set(Calendar.SECOND,0);
		startTime.set(Calendar.MILLISECOND,0);
		
		Intent intent = new Intent(context,CallActivity.class);
	    PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
		
		if (currentTime.getTimeInMillis() > startTime.getTimeInMillis()) {
			Log.i(TAG,"当前时间的比较,暂不开启");
			startTime.add(Calendar.DATE, 1);       
			am.setRepeating(AlarmManager.RTC_WAKEUP,startTime.getTimeInMillis(), interval * ONE_HOUR,pi);
		}else{
			Log.i(TAG, "当前时间的比较,开启定时");
			am.setRepeating(AlarmManager.RTC_WAKEUP,startTime.getTimeInMillis(), interval * ONE_HOUR,pi);
//			am.setRepeating(AlarmManager.RTC_WAKEUP,startTime.getTimeInMillis(), ONE_HOUR,pi); //1000 = 1s
		}
	}
	
	/*
	 * 设置定时
	 */
	public static void  setAlarm(Context context,int hour,int mininue,int interval) {
		Log.i(TAG,"startAlarm,hour="+hour+",mininue="+mininue+",interval="+interval);
		
		Calendar currentTime = Calendar.getInstance();///
		Calendar startTime = Calendar.getInstance();   //设置开始闹钟时间
		startTime.set(Calendar.HOUR_OF_DAY,hour);
		startTime.set(Calendar.MINUTE,mininue);
		startTime.set(Calendar.SECOND,0);
		startTime.set(Calendar.MILLISECOND,0);
		
//		tag = Math.abs((int)currentTime.getTimeInMillis());
		
		Intent intent = new Intent(context,CallActivity.class);
		intent.putExtra("current_stage", tag);
		//intent.setAction(String.valueOf(System.currentTime ...
	    PendingIntent pi = PendingIntent.getActivity(context, tag, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
		
		if (currentTime.getTimeInMillis() > startTime.getTimeInMillis()) {
			Log.i(TAG,"延后一天开启定时");
			startTime.set(Calendar.DAY_OF_YEAR,startTime.get(Calendar.DAY_OF_YEAR) + 1);
			am.setRepeating(AlarmManager.RTC_WAKEUP,startTime.getTimeInMillis(),interval * ONE_HOUR,pi);
			
		}else{
			Log.i(TAG,"开启定时");
			am.setRepeating(AlarmManager.RTC_WAKEUP,startTime.getTimeInMillis(),interval * ONE_HOUR,pi);
		}
	}
	
	/*
	 * 取消设置定时
	 */
	public static void cancelSetAlarm(Context context) {
		Log.i(TAG,"cancelStopAlarm");
		for(int i = 0; i < 10;i++){
			Intent intent = new Intent(context, CallActivity.class);
			PendingIntent pi = PendingIntent.getActivity(context,i, intent, 0);
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			am.cancel(pi);
		}
	}
	
	/*
	 * 开始定时提醒广播
	 */
	public static void startBroadCastAlarm(Context context) {
		Intent intent = new Intent(context, BootCompletedReceiver.class);
		intent.setAction("android.intent.action.DUTY_REMINDER");
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+ONE_MINUTE * 15,15 * ONE_MINUTE,pi);
	}
	
	/*
	 * 关闭定时提醒广播
	 */
	public static void stopBroadCastAlarm(int hour,int mininue,Context context) {
		
		Calendar startTime = Calendar.getInstance();   //设置开始闹钟时间
		startTime.set(Calendar.HOUR_OF_DAY,hour);
		startTime.set(Calendar.MINUTE,mininue);
		startTime.set(Calendar.SECOND,0);
		startTime.set(Calendar.MILLISECOND,0);
		
		Intent intent = new Intent(context, BootCompletedReceiver.class);
		intent.setAction("android.intent.action.CANCEL_REMINDER");
		PendingIntent pi = PendingIntent.getBroadcast(context, tag, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), pi);
	}
	
	/*
	 * 清空定时提醒广播
	 */
	public static void clearBroadCastAlarm(Context context) {
		
		Intent intent = new Intent(context, BootCompletedReceiver.class);
		intent.setAction("android.intent.action.DUTY_REMINDER");
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
//		am.setRepeating(AlarmManager.RTC_WAKEUP,startTime.getTimeInMillis(),15 * ONE_MINUTE,pi);
		am.cancel(pi);
	}
	
	
	//发送关闭视频的命令
	public static void  sendStopAlarm(Context context,int year,int month,int day,int hour,int mininue) {
		Log.i(TAG,"sendStopAlarm1:"+year+"-"+month+"-"+day+"  "+hour+":"+mininue);
		Calendar endTime = Calendar.getInstance();   //发送结束播放视频的命令(时间格式：2015-1-31 22:30:00)//月份从零开始算的：0~11月
		endTime.set(Calendar.YEAR,year);
		endTime.set(Calendar.MONTH,month);  //0-11
		endTime.set(Calendar.DAY_OF_MONTH,day);
		endTime.set(Calendar.HOUR_OF_DAY,hour);
		endTime.set(Calendar.MINUTE,mininue);
		endTime.set(Calendar.SECOND,0);
		endTime.set(Calendar.MILLISECOND,0); 
	//	endTime.set(2015,2,6,15,41,0);
		
		Intent intent = new Intent("com.iimphouseintouch.endplayvideo");
	    PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
	//    Intent intent = new Intent(context,CallActivity.class);    //启动Activity可用，其它不可用
	//    PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
	    
		AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP,endTime.getTimeInMillis(),pi); 
		
		Log.i(TAG,"sendStopAlarm2");
	}
	
	public static void startAlarmClock(String durationTime,Context context){
		String hour,minute;
//		ArrayList<String> timeList = new ArrayList<String>();
		String[] split = durationTime.split(",");
		String[] split1 = null;
		String[] split2 = null;
		for(String str:split){
			tag++;
			
			String[] t_split = str.split("-");
			Log.i("TEST","t_split.length="+t_split.length);
			
			if(t_split.length == 2){
			    split1 = t_split[0].split(":");
				Log.i("TEST","split1.length="+split1.length);
				
				split2 = t_split[1].split(":");
				Log.i("TEST","split2.length="+split2.length);
			}
			
			if(split1.length == 2 && t_split.length == 2){
				hour = split1[0];
				minute = split1[1];
				//根据获取的时间，计算提前5分钟的时间节点
				if(hour.equals("00") || hour.equals("0")){
					hour = "23";
				}else{
					hour = Integer.parseInt(hour)-1+"";
				}
				if(Integer.parseInt(minute) < 5 && Integer.parseInt(minute) >= 0){
					minute = 55+Integer.parseInt(minute)+"";
				}else if(minute.equals("05") || minute.equals("5")){
					hour = split1[0];
					minute = "0";
				}else{
					hour = split1[0];
					minute = Integer.parseInt(minute)-5 + "";
				}
				setAlarm(context, Integer.parseInt(hour),  //开启定时
						Integer.parseInt(minute), 24); 
			}else{
//				Toast.makeText(context,"值班时间格式有误!",Toast.LENGTH_SHORT).show();
			}
			if(split2.length == 2){
				hour = split2[0];
				minute = split2[1];
				stopBroadCastAlarm(Integer.parseInt(hour),Integer.parseInt(minute), context);
				Log.i("TEST","到该点："+hour+":"+minute+" 取消定时！");
			}
		}
	}
	
}

