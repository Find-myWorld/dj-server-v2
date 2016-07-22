package com.example.iimp_znxj_new2014.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.iimp_znxj_new2014.activity.AudioActivity;
import com.example.iimp_znxj_new2014.activity.PlayPlanLocalVideoActivity;
import com.example.iimp_znxj_new2014.service.DownLoadService;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;

/*
 * 未使用的工具类
 * <p>程序通过每分钟去读取一个文件，看里面的时间内容来判断是否执行定时计划)</p>
 */
public class AlarmManagerUtil
{
    private static final long TIME_REPEATING_SALES_ORDER = 24 * 60 * 60 * 1000;
    
    private static final int FIRST_SALES_ORDER_REQEUST_CODE = 0;
    
    private static final int SECOND_SALES_ORDER_REQEUST_CODE = 1;
    
    private static final int UNDO_TASK_REQEUST_CODE = 2;
    
    private static final int FIRST_SALES_ORDER_TRIGLE_TIME = 12;
    
    private static final int SECOND_SALES_ORDER_TRIGLE_TIME = 18;
    
    private static final long UNDO_TASK_TIME_REPEATING = 60 * 60 * 1000;
    
    public static void startAlarmManager(Context context)
    {
        AlarmManagerUtil.cancelAlarmIfNeeded(context, FIRST_SALES_ORDER_REQEUST_CODE);
        AlarmManagerUtil.cancelAlarmIfNeeded(context, SECOND_SALES_ORDER_REQEUST_CODE);
        AlarmManagerUtil.startAlarm(context, FIRST_SALES_ORDER_TRIGLE_TIME, FIRST_SALES_ORDER_REQEUST_CODE);
        AlarmManagerUtil.startAlarm(context, SECOND_SALES_ORDER_TRIGLE_TIME, SECOND_SALES_ORDER_REQEUST_CODE);
    }
    
    public static void cancelAlarmIfNeeded(Context context, int requestCode)
    {
        Intent intent = new Intent(context, AudioActivity.class);
        PendingIntent operation = PendingIntent.getActivity(context, requestCode, intent, 0);
        AlarmManager aManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        aManager.cancel(operation);
    }
    
    public static void startAlarm(Context context, int hour, int requestCode)
    {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
            Integer.parseInt(new SimpleDateFormat("dd", Locale.CHINA).format(System.currentTimeMillis())));
        Intent intent = new Intent(context, AudioActivity.class);
        intent.putExtra("requestCode", requestCode);
        AlarmManager aManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent operation = PendingIntent.getService(context, requestCode, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        /*
         * if (UNDO_TASK_REQEUST_CODE == requestCode) { calendar.set(Calendar.HOUR_OF_DAY, hour);
         * aManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), UNDO_TASK_TIME_REPEATING,
         * operation); } else if(FIRST_SALES_ORDER_REQEUST_CODE == requestCode || SECOND_SALES_ORDER_REQEUST_CODE ==
         * requestCode) {
         */
        Log.d("jiayy", "hour = " + hour);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (currentCalendar.getTimeInMillis() > calendar.getTimeInMillis())
        {
            calendar.add(Calendar.DATE, 1);
        }
        aManager.setRepeating(AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            TIME_REPEATING_SALES_ORDER,
            operation);
        // }
    }
    
    //播放计划：requestCode需每次都不一样才能启动多个闹钟(每日计划)
    public static void setPlayPlan(Context context, int year,int month,int day,int hour,int minute,int requestCode) 
    {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar planCalendar = Calendar.getInstance();
        Log.i("setPlayPlan","设置定时Alarm"+year+"|"+month+"|"+day+"|"+hour+"|"+minute+"|"+requestCode);
        planCalendar.set(year, month, day, hour, minute, 0);  //年月日时分秒
   //     planCalendar.set(2015, 2, 3, 13, 42, 0);   
        Intent intent = new Intent(context,PlayPlanLocalVideoActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, requestCode, intent, 0); //requestCode不同，多个闹钟才会生效
        AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        if (currentCalendar.getTimeInMillis() > planCalendar.getTimeInMillis())
        {
        	planCalendar.add(Calendar.DATE, 1);
        }else{
        	am.set(AlarmManager.RTC_WAKEUP, planCalendar.getTimeInMillis(), pi); //只播放一次
        }
       
    }
    
    //发送结束广播(带有指定年月日的)
    public static void sendEndBroadCast(Context context, int year,int month,int day,int hour,int minute,int requestCode) 
    {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar planCalendar = Calendar.getInstance();
        Log.i("End","设置定时Alarm"+year+"|"+month+"|"+day+"|"+hour+"|"+minute+"|"+requestCode);
        planCalendar.set(year, month, day, hour, minute, 0);  //年月日时分秒
      
        Intent intent = new Intent();
        intent.setAction(Constant.STOPPLAY_ACTION );
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, 0); //requestCode不同，多个闹钟才会生效
        AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        if (currentCalendar.getTimeInMillis() > planCalendar.getTimeInMillis())
        {
        	Log.i("sendEndBroadCast",">>>>>");
        	planCalendar.add(Calendar.DATE, 1);
        }else{
        	Log.i("sendEndBroadCast","正常");
        	am.set(AlarmManager.RTC_WAKEUP, planCalendar.getTimeInMillis(), pi); //只播放一次
       }
    }
    
    /*
     * 取消每日计划
     */
    public static void cancelPlayPlan(Context context, int requestCode)
    {
        Intent intent = new Intent(context, PlayPlanLocalVideoActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, requestCode, intent, 0);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    //  new Thread(new Client("canclePlayPlan:true")).start();
    }
    
    /*
     * <titel>后台下载
     * <p>requestCode需每次都不一样才能启动多个闹钟</p>
     */
    public static void setDownLoadAlarm(Context context, int year,int month,int day,int hour,int minute,int second,int requestCode) 
    {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar planCalendar = Calendar.getInstance();
        planCalendar.set(year, month, day, hour, minute, second);
      
        Intent intent = new Intent(context,DownLoadService.class);
        PendingIntent pi = PendingIntent.getService(context, requestCode, intent, 0); //requestCode不同，多个闹钟才会生效
        AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        Log.i("Msg","设置定时Alarm");
        if (currentCalendar.getTimeInMillis() > planCalendar.getTimeInMillis())
        {
        	planCalendar.add(Calendar.DATE, 1);
        }else{
        	am.set(AlarmManager.RTC_WAKEUP, planCalendar.getTimeInMillis(), pi);
        }
    }
}
